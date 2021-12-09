package com.example.cryptotext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.security.PublicKey;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewFriendActivity extends AppCompatActivity {

    DatabaseReference mUserRef, mUserRef1, requestRef, friendRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    CircleImageView profileImage;
    TextView name;
    String ImageUrl, username, publicKey, ImageUrl1, username1, publicKey1;
    Button btnPerform, btnDecline;
    String currentState = "nothing_happen";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String userID = getIntent().getStringExtra("userKey");
        setContentView(R.layout.activity_view_friend);


        requestRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        friendRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        mUserRef1 = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());
        profileImage = findViewById(R.id.profileImageF);
        name = findViewById(R.id.usernameF);
        btnPerform = findViewById(R.id.btnPerform);
        btnDecline = findViewById(R.id.btnDecline);
        btnDecline.setVisibility(View.GONE);

        LoadUser();

        btnPerform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performAction(userID);
            }
        });

        CheckUserExistence(userID);

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Unfriend(userID);
            }
        });

    }

    private void Unfriend(String userID) {
        if(currentState.equals("friend"))
        {
            friendRef.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        friendRef.child(userID).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(ViewFriendActivity.this, "Friend Removed", Toast.LENGTH_SHORT).show();
                                    currentState = "nothing_happen";
                                    btnPerform.setText("Send Friend Request");
                                    btnDecline.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                }
            });
        }
        if(currentState.equals("he_sent_pending"))
        {
            HashMap hashMap = new HashMap();
            hashMap.put("status", "declined");

            requestRef.child(userID).child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ViewFriendActivity.this, "Friend Request Declined", Toast.LENGTH_SHORT).show();
                        currentState = "declined_request";
                        btnDecline.setVisibility(View.GONE);
                        btnPerform.setText("Send Friend Request");
                    }
                }
            });
        }
    }

    private void CheckUserExistence(String userID) {
        friendRef.child(mUser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    currentState = "friend";
                    btnPerform.setText("Friends");
                    btnDecline.setText("Unfriend");
                    btnDecline.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        friendRef.child(userID).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    currentState = "friend";
                    btnPerform.setText("Friends");
                    btnDecline.setText("Unfriend");
                    btnDecline.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        requestRef.child(mUser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    if(snapshot.child("status").getValue().toString().equals("pending"))
                    {
                        currentState = "I_sent_pending";
                        btnPerform.setText("Cancel Friend Request");
                        btnDecline.setVisibility(View.GONE);
                    }
                    if(snapshot.child("status").getValue().toString().equals("decline"))
                    {
                        currentState = "I_sent_decline";
                        btnPerform.setText("Cancel Friend Request");
                        btnDecline.setVisibility(View.GONE);
                    }
                    if(snapshot.child("status").getValue().toString().equals("declined"))
                    {
                        currentState = "nothing_happen";
                        btnPerform.setText("Send Friend Request");
                        btnDecline.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        requestRef.child(userID).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    if(snapshot.child("status").getValue().toString().equals("pending"))
                    {
                        currentState = "he_sent_pending";
                        btnPerform.setText("Accept Friend Request");
                        btnDecline.setText("Decline Friend Request");
                        btnDecline.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if(currentState.equals("nothing_happen"))
        {
            currentState = "nothing_happen";
            btnPerform.setText("Send Friend Request");
            btnDecline.setVisibility(View.GONE);
        }
    }

    private void performAction(String userID) {
        if(currentState.equals("nothing_happen"))
        {
            HashMap hashMap = new HashMap();
            hashMap.put("status", "pending");
            requestRef.child(mUser.getUid()).child(userID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ViewFriendActivity.this, "Friend Request Sent!", Toast.LENGTH_SHORT).show();
                        btnDecline.setVisibility(View.GONE);
                        currentState = "I_sent_pending";
                        btnPerform.setText("Cancel Friend Request");
                    }
                    else
                    {
                        Toast.makeText(ViewFriendActivity.this, ""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
        if(currentState.equals("I_sent_pending") || currentState.equals("I_sent_decline") || currentState.equals("declined"))
        {
            requestRef.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ViewFriendActivity.this, "Friend Request Cancelled", Toast.LENGTH_SHORT).show();
                        btnPerform.setText("Send Friend Request");
                        btnDecline.setVisibility(View.GONE);
                        currentState = "nothing_happen";
                    }
                    else
                    {
                        Toast.makeText(ViewFriendActivity.this, ""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if(currentState.equals("he_sent_pending"))
        {
            requestRef.child(userID).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        HashMap hashMap = new HashMap();
                        hashMap.put("status", "friend");
                        hashMap.put("name", username);
                        hashMap.put("ImageUrl", ImageUrl);
                        hashMap.put("public", publicKey);
                        friendRef.child(mUser.getUid()).child(userID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful())
                                {
                                    HashMap hashMap1 = new HashMap();
                                    hashMap1.put("status", "friend");
                                    hashMap1.put("name", username1);
                                    hashMap1.put("ImageUrl", ImageUrl1);
                                    hashMap1.put("public", publicKey1);
                                    friendRef.child(userID).child(mUser.getUid()).updateChildren(hashMap1).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            Toast.makeText(ViewFriendActivity.this, "Friend Added", Toast.LENGTH_SHORT).show();
                                            currentState = "friend";
                                            btnPerform.setClickable(false);
                                            btnPerform.setText("Friends");
                                            btnDecline.setText("Unfriend");
                                            btnDecline.setVisibility(View.VISIBLE);
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void LoadUser() {
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    ImageUrl = snapshot.child("ImageUrl").getValue().toString();
                    username = snapshot.child("name").getValue().toString();
                    publicKey = snapshot.child("publicKey").getValue().toString();

                    Picasso.get().load(ImageUrl).into(profileImage);
                    name.setText(username);
                    
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewFriendActivity.this, "Data Not Found", Toast.LENGTH_SHORT).show();
            }
        });

        mUserRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    ImageUrl1 = snapshot.child("ImageUrl").getValue().toString();
                    username1 = snapshot.child("name").getValue().toString();
                    publicKey1 = snapshot.child("publicKey").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}