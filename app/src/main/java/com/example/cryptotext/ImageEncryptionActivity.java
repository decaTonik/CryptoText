package com.example.cryptotext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import de.hdodenhof.circleimageview.CircleImageView;


public class    ImageEncryptionActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final int req = 1;
    ImageView imageEncryptionInput;
    Button enc_btn, clear_btn;
    ImageButton main_enc, main_dec, main_enc_orange, main_dec_orange, send_btn, cpy_btn;
    ConstraintLayout downC, UPc;
    ImageView output;
    EditText encryptedImage;
    TextView input_TV, output_TV, encryptedImageText;
    String password = "qwerty";
    Uri uri;
    Toolbar toolbar;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mUserRef;
    String profileImageUrlV,userNameV;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    CircleImageView profileImageHeader;
    TextView usernameHeader;


    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mUser == null)
        {
            SendUserToLoginActivity();
        }
        else
        {
            mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        profileImageUrlV = snapshot.child("ImageUrl").getValue().toString();
                        userNameV = snapshot.child("name").getValue().toString();
                        Picasso.get().load(profileImageUrlV).into(profileImageHeader);
                        usernameHeader.setText(userNameV);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ImageEncryptionActivity.this, "Sorry!, Something went Wrong", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void SendUserToLoginActivity() {
        Intent intent = new Intent(ImageEncryptionActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_encryption);

        imageEncryptionInput = findViewById(R.id.imageEncryption_inputImage);
        output = findViewById(R.id.imageEncryption_output_image);
        encryptedImage = findViewById(R.id.imageEncryption_EncryptedImage);
        encryptedImageText = findViewById(R.id.imageEncryption_output);
        main_enc = findViewById(R.id.imageEncryption_encrypt);
        main_dec = findViewById(R.id.imageEncryption_decrypt);
        main_enc_orange = findViewById(R.id.imageEncryption_enc_orange);
        main_dec_orange = findViewById(R.id.imageEncryption_decrypt_orange);
        enc_btn = findViewById(R.id.imageEncryption_encrypt_btn);
        clear_btn = findViewById(R.id.imageEncryption_clear);
        cpy_btn = findViewById(R.id.imageEncryption_copy);
        send_btn = findViewById(R.id.imageEncryption_send);
        downC = findViewById(R.id.imageEncryption_constraintLayout);
        input_TV = findViewById(R.id.imageEncryption_inputTV);
        output_TV = findViewById(R.id.imageEncryption_outputTV);

        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Image Encryption");
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);

        View view = navigationView.inflateHeaderView(R.layout.drawer_header);
        profileImageHeader = view.findViewById(R.id.profileImage_header);
        usernameHeader = view.findViewById(R.id.username_header);

        navigationView.setNavigationItemSelectedListener(this);


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");


        main_enc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main_enc.setVisibility(View.INVISIBLE);
                main_enc_orange.setVisibility(View.VISIBLE);
                main_dec.setVisibility(View.VISIBLE);
                main_dec_orange.setVisibility(View.INVISIBLE);
                send_btn.setVisibility(View.INVISIBLE);
                cpy_btn.setVisibility(View.INVISIBLE);
                if(uri == null)
                    enc_btn.setText("Add image");
                else
                    enc_btn.setText("Encrypt");

                input_TV.setText("Message");
                output_TV.setText("Encrypted Message");
                imageEncryptionInput.setVisibility(View.VISIBLE);
                encryptedImage.setVisibility(View.INVISIBLE);
                output.setVisibility(View.INVISIBLE);
                encryptedImageText.setVisibility(View.VISIBLE);
                downC.setVisibility(View.VISIBLE);

                enc_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(uri != null) {
                            InputStream iStream = null;
                            try {
                                iStream = getContentResolver().openInputStream(uri);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            byte[] imageBytes = new byte[0];
                            try {
                                imageBytes = getBytes(iStream);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                String EncryptedImage = aes_Encrypt(imageBytes, password);
                                encryptedImageText.setText(EncryptedImage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            cpy_btn.setVisibility(View.VISIBLE);
                            send_btn.setVisibility(View.VISIBLE);
                        }
                        else{
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(galleryIntent,req);


                        }

                    }
                });
                clear_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int sex = 0;
                        if(enc_btn.getText().toString() == "Encrypt") {
                            uri = null;
                            imageEncryptionInput.setImageURI(null);
                            enc_btn.setText("Add Image");
                            encryptedImageText.setText("");
                            cpy_btn.setVisibility(View.INVISIBLE);
                            send_btn.setVisibility(View.INVISIBLE);
                        }
                        else if(enc_btn.getText().toString() == "Decrypt"){
                            encryptedImage.setText("");
                            output.setImageURI(null);
                        }

                    }
                });

                cpy_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("public", encryptedImageText.getText());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(ImageEncryptionActivity.this, "Encrypted Text Copied", Toast.LENGTH_SHORT).show();

                    }
                });
                send_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,encryptedImageText.getText().toString());
                        sendIntent.setType("text/plain");

                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                        startActivity(shareIntent);

                    }
                });


            }


        });


        main_dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main_dec.setVisibility(View.INVISIBLE);
                main_enc_orange.setVisibility(View.INVISIBLE);
                main_enc.setVisibility(View.VISIBLE);
                input_TV.setText("Encrypted Message");
                output_TV.setText("Message");
                main_dec_orange.setVisibility((View.VISIBLE));
                send_btn.setVisibility(View.INVISIBLE);
                cpy_btn.setVisibility(View.INVISIBLE);
                downC.setVisibility(View.VISIBLE);
                imageEncryptionInput.setVisibility(View.INVISIBLE);
                encryptedImage.setVisibility(View.VISIBLE);
                output.setVisibility(View.VISIBLE);
                encryptedImageText.setVisibility(View.INVISIBLE);
                enc_btn.setText("Decrypt");

                enc_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String EncryptedImage = encryptedImage.getText().toString().trim();
                        try {
                            Uri imageOut =Uri.parse(aes_Decrypt(EncryptedImage, password));
                            output.setImageURI(uri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });


            }
        });







    }
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == req && resultCode == RESULT_OK)
        {
            uri = data.getData();
            imageEncryptionInput.setImageURI(uri);
            enc_btn.setText("Encrypt");
        }
        else{
            Toast.makeText(ImageEncryptionActivity.this, "Add valid image", Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
    private String aes_Encrypt(byte[] image,String password_text) throws Exception {
        SecretKeySpec key=generate_AES_Key(password_text);
        Cipher cip = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cip.init(Cipher.ENCRYPT_MODE,key);
        byte[] encryptedByteValue = cip.doFinal(image);
        String encryptedValue = Base64.encodeToString(encryptedByteValue,Base64.DEFAULT);
        return encryptedValue;


    }
    private  String aes_Decrypt(String message,String password_text) throws Exception {
        SecretKeySpec key = generate_AES_Key(password_text);
        Cipher cip = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cip.init(Cipher.DECRYPT_MODE,key);
        byte[] decryptedByteValue = Base64.decode(message,Base64.DEFAULT);
        byte[] decryptedVale = cip.doFinal(decryptedByteValue);
        InputStream iStream = null;
        try {
            iStream = getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] imageBytes = new byte[0];
        try {
            imageBytes = getBytes(iStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i = 0; i<imageBytes.length; i++)
            if(imageBytes[i] != decryptedVale[i])
                Toast.makeText(ImageEncryptionActivity.this, "ajao sex kare", Toast.LENGTH_SHORT).show();

        return Base64.encodeToString(decryptedVale,Base64.DEFAULT);


    }

    private SecretKeySpec generate_AES_Key(String password_text) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password_text.getBytes("utf-8");

        digest.update(bytes,0,bytes.length);

        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.home:
                startActivity(new Intent(ImageEncryptionActivity.this, MainActivity.class));
                break;
            case R.id.profile:
                startActivity(new Intent(ImageEncryptionActivity.this, ProfileActivity.class));
                break;
            case R.id.findFriend:
                startActivity(new Intent(ImageEncryptionActivity.this, FindFriendActivity.class));
                break;
            case R.id.invite:
                invite();
                break;
            case R.id.logout:
                mAuth.signOut();
                Intent intent=new Intent(ImageEncryptionActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }

    private void invite() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://github.com/decaTonik/CryptoText");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return true;
    }
}
