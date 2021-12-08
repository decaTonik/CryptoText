package com.example.cryptotext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ProfileActivity extends AppCompatActivity {

    CircleImageView profileImageView;
    EditText inputUsername, inputCity, inputAge;
    Button btnUpdate;

    DatabaseReference mUserRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ProgressDialog mLoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImageView=findViewById(R.id.circleImageView);
        inputUsername=findViewById(R.id.inputUsername);
        inputAge=findViewById(R.id.inputCity);
        inputCity=findViewById(R.id.inputAge);
        btnUpdate=findViewById(R.id.btnUpdate);
        mLoadingBar = new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        mUserRef= FirebaseDatabase.getInstance().getReference().child("Users");


        mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String profileImageUrl=snapshot.child("profileImage").getValue().toString();
                    String city=snapshot.child("city").getValue().toString();
                    String age=snapshot.child("age").getValue().toString();
                    String username=snapshot.child("username").getValue().toString();

                    Picasso.get().load(profileImageUrl).into(profileImageView);
                    inputCity.setText(city);
                    inputAge.setText(age);
                    inputUsername.setText(username);

                    btnUpdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UpdateData();
                        }
                    });


                }
                else
                {
                    Toast.makeText(ProfileActivity.this, "Data not exist", Toast.LENGTH_SHORT).show();
                }
            }

            private void UpdateData() {


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, ""+error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}