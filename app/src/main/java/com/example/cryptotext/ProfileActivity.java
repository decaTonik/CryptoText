package com.example.cryptotext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import java.net.*;

public class ProfileActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 101;
    CircleImageView profileImageView;
    EditText inputUsername, inputCity, inputAge;
    Button btnUpdate;
    Uri imageUri;

    DatabaseReference mUserRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    StorageReference StorageRef;
    ProgressDialog mLoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImageView=findViewById(R.id.circleImageView);
        inputUsername=findViewById(R.id.inputUsername);
        inputAge=findViewById(R.id.inputAge);
        inputCity=findViewById(R.id.inputCity);
        btnUpdate=findViewById(R.id.btnUpdate);
        mLoadingBar = new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        mUserRef= FirebaseDatabase.getInstance().getReference().child("Users");
        StorageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages");


        mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String profileImageUrl = snapshot.child("ImageUrl").getValue().toString();
                    String city = snapshot.child("city").getValue().toString();
                    String age = snapshot.child("age").getValue().toString();
                    String username = snapshot.child("name").getValue().toString();

                    Picasso.get().load(profileImageUrl).into(profileImageView);
                    inputCity.setText(city);
                    inputAge.setText(age);
                    inputUsername.setText(username);

                    profileImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent, REQUEST_CODE);
                        }
                    });

                    btnUpdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UpdateData(profileImageUrl);
                        }
                    });
                }
                else
                {
                    Toast.makeText(ProfileActivity.this, "Data not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, ""+error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void UpdateData(String profileUrl){
        String name = inputUsername.getText().toString();
        String city = inputCity.getText().toString();
        String age = inputAge.getText().toString();

        if(name.isEmpty())
        {
            showError(inputUsername, "Name cannot be empty");
        }
        else if(city.isEmpty() || city.length() < 3)
        {
            showError(inputCity, "City Not valid");
        }
        else if(age.isEmpty())
        {
            showError(inputAge, "Age is not Valid");
        }
        else if(imageUri == null)
        {
            mLoadingBar.setTitle("Updating Profile");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();

            HashMap hashMap = new HashMap();
            hashMap.put("name", name);
            hashMap.put("city", city);
            hashMap.put("age", age);

            mUserRef.child(mUser.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                    mLoadingBar.dismiss();
                    Toast.makeText(ProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            });

        } else{
            mLoadingBar.setTitle("Updating Profile");
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

                                HashMap hashMap = new HashMap();
                                hashMap.put("name", name);
                                hashMap.put("city", city);
                                hashMap.put("age", age);
                                hashMap.put("ImageUrl", uri.toString());

                                mUserRef.child(mUser.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        mLoadingBar.dismiss();
                                        Toast.makeText(ProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
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