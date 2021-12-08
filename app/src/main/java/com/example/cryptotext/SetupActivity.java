package com.example.cryptotext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity{

    private static final int REQUEST_CODE = 101;
    CircleImageView profileImageView;
    EditText inputName, inputCity;
    EditText inputAge;
    Button btnSave;
    Uri imageUri;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mRef;
    StorageReference StorageRef;
    ProgressDialog mLoadingBar;

    Toolbar toolbar;

    String privateKeyBytesBase64;
    String publicKeyBytesBase64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        toolbar=findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Set Profile");

        profileImageView = findViewById(R.id.profile_image);
        inputName = findViewById(R.id.inputName);
        inputCity = findViewById(R.id.inputCity);
        inputAge = findViewById(R.id.inputAge);
        btnSave = findViewById(R.id.btnSave);
        mLoadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users");
        StorageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages");

        KeyPair keyP = null;
        try {
            keyP = getKeypair();
        } catch (Exception e) {
            e.printStackTrace();
        }
        PublicKey pubKey = keyP.getPublic();
        byte[] publicKeyBytes = pubKey.getEncoded();

        publicKeyBytesBase64 = new String(Base64.encode(publicKeyBytes, Base64.DEFAULT));

        PrivateKey privateKey = keyP.getPrivate();
        byte[] privateKeyBytes = privateKey.getEncoded();
        privateKeyBytesBase64 = new String(Base64.encode(privateKeyBytes, Base64.DEFAULT));

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveData();
            }
        });
    }

    private KeyPair getKeypair() {
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        return kp;
    }

    private void SaveData() {
        String name = inputName.getText().toString();
        String city = inputCity.getText().toString();
        String age = inputAge.getText().toString();

        if(name.isEmpty())
        {
            showError(inputName, "Name cannot be empty");
        }
        else if(city.isEmpty() || city.length() < 3)
        {
            showError(inputCity, "City Not valid");
        }
        else if(age.isEmpty())
        {
            showError(inputAge, "Age is not Valid");
        }else if(imageUri == null) {
            Toast.makeText(this, "Please select an Image", Toast.LENGTH_SHORT).show();
        } else{
            mLoadingBar.setTitle("Setting Up Profile");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();
            StorageRef.child(mUser.getUid()).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful())
                    {

                        StorageRef.child(mUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String email = mUser.getEmail().toString();
                                String encodedKey = publicKeyBytesBase64.replaceAll("(\\r|\\n)", "");

                                HashMap hashMap = new HashMap();
                                hashMap.put("name", name);
                                hashMap.put("email", email);
                                hashMap.put("city", city);
                                hashMap.put("age", age);
                                hashMap.put("ImageUrl", uri.toString());
                                hashMap.put("publicKey", encodedKey);

                                mRef.child(mUser.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        Intent intent = new Intent(SetupActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        mLoadingBar.dismiss();
                                        Toast.makeText(SetupActivity.this, "Setup Profile Complete", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SetupActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        });
                    }
                }
            });
        }
    }

    private void showError(EditText field, String text) {
        field.setError(text);
        field.requestFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null)
        {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
        }
    }
}