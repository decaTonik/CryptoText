package com.example.cryptotext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewFriendActivity extends AppCompatActivity {

    DatabaseReference mUserRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    CircleImageView profileImage;
    TextView name;

    String ImageUrl, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String userID = getIntent().getStringExtra("userKey");
        setContentView(R.layout.activity_view_friend);

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        profileImage = findViewById(R.id.profileImageF);
        name = findViewById(R.id.usernameF);

        LoadUser();

    }

    private void LoadUser() {
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    ImageUrl = snapshot.child("ImageUrl").getValue().toString();
                    username = snapshot.child("name").getValue().toString();

                    Picasso.get().load(ImageUrl).into(profileImage);
                    name.setText(username);
                    
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewFriendActivity.this, "Data Not Found", Toast.LENGTH_SHORT).show();
            }
        });
    }
}