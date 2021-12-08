package com.example.cryptotext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    ImageButton logout, rsa, aes, imageEncrypt;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    String profileImageUrl, profileUsername;
    CircleImageView profileImageHeader;
    TextView usernameHeader;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        rsa = findViewById(R.id.rsa);
        aes = findViewById(R.id.aes);
        imageEncrypt = findViewById(R.id.image_encryption);

        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Crypto Text");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        rsa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RSA.class);
                startActivity(i);
                finish();
            }
        });

        aes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AES.class);
                startActivity(i);
                finish();
            }
        });

        imageEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ImageEncryptionActivity.class);
                startActivity(i);
                finish();
            }
        });

    }
}