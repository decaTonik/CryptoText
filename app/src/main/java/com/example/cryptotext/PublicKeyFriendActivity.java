package com.example.cryptotext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cryptotext.Utills.Friends;
import com.example.cryptotext.Utills.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.security.PublicKey;

import de.hdodenhof.circleimageview.CircleImageView;

public class PublicKeyFriendActivity extends AppCompatActivity {

    FirebaseRecyclerOptions<Friends>options;
    FirebaseRecyclerAdapter<Friends, FindFriendViewHlder>adapter;
    Toolbar toolbar;
    RecyclerView recyclerView;
    FirebaseAuth mAuth;
    DatabaseReference mRef, mRef1, mRef2;
    FirebaseUser mUser;
    String publicKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_key_friend);

        toolbar=findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Friend");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        mRef= FirebaseDatabase.getInstance().getReference().child("Friends");
        mRef1 = FirebaseDatabase.getInstance().getReference().child("Friends").child(mUser.getUid());
        mRef2 = FirebaseDatabase.getInstance().getReference().child("Friends").child(mRef1.getKey().toString());


        LoadFriends("");
    }

    private void LoadFriends(String s) {
        Query query=mRef.child(mUser.getUid()).orderByChild("name").startAt(s).endAt(s+"\uf8ff");
        options=new FirebaseRecyclerOptions.Builder<Friends>().setQuery(query,Friends.class).build();
        adapter=new FirebaseRecyclerAdapter<Friends, FindFriendViewHlder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHlder holder, int position, @NonNull Friends model) {
                Picasso.get().load(model.getImageUrl()).into(holder.ImageUrl);
                holder.name.setText(model.getName());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mRef2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists())
                                {
                                    publicKey = snapshot.getValue().toString();
                                    Toast.makeText(PublicKeyFriendActivity.this, "hello", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

            }

            @NonNull
            @Override
            public FindFriendViewHlder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_friend,parent,false);
                return new FindFriendViewHlder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

}