package com.example.cryptotext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

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

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);

        View view = navigationView.inflateHeaderView(R.layout.drawer_header);
        profileImageHeader = view.findViewById(R.id.profileImage_header);
        usernameHeader = view.findViewById(R.id.username_header);

        navigationView.setNavigationItemSelectedListener(this);

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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.home:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.profile:
                startActivity(new Intent(SetupActivity.this, ProfileActivity.class));
                break;
            case R.id.findFriend:
                startActivity(new Intent(SetupActivity.this, FindFriendActivity.class));
                break;
            case R.id.invite:
                Toast.makeText(this, "Invite", Toast.LENGTH_SHORT).show();
                break;
            case R.id.logout:
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
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