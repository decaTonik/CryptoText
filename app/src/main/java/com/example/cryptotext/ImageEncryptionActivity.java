package com.example.cryptotext;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


import android.content.Intent;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class    ImageEncryptionActivity extends AppCompatActivity {

    private final int req = 1;
    ImageView imageEncryptionInput;
    Button enc_btn, add_btn;
    ImageButton main_enc, main_dec, main_enc_orange, main_dec_orange, send_btn, cpy_btn;
    ConstraintLayout downC, UPc;
    ImageView output;
    EditText encryptedImage;
    TextView input_TV, output_TV, encryptedImageText;
    String password = "qwerty";
    Uri uri;


    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
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
        add_btn = findViewById(R.id.imageEncryption_add);
        cpy_btn = findViewById(R.id.imageEncryption_copy);
        send_btn = findViewById(R.id.imageEncryption_send);
        downC = findViewById(R.id.imageEncryption_constraintLayout);
        input_TV = findViewById(R.id.imageEncryption_inputTV);
        output_TV = findViewById(R.id.imageEncryption_outputTV);



        main_enc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main_enc.setVisibility(View.INVISIBLE);
                main_enc_orange.setVisibility(View.VISIBLE);
                main_dec.setVisibility(View.VISIBLE);
                main_dec_orange.setVisibility(View.INVISIBLE);
                send_btn.setVisibility(View.INVISIBLE);
                cpy_btn.setVisibility(View.INVISIBLE);
                enc_btn.setText("ENCRYPT");
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
                            String EncryptedImage = aes_Encrypt(imageBytes,password );
                            encryptedImageText.setText(EncryptedImage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
                add_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent,req);

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
                enc_btn.setText("DECRYPT");

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

}
