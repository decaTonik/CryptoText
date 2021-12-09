package com.example.cryptotext;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptotext.Utills.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class RSA extends SetupActivity {

    EditText rsaInput, userInput;
    Button enc_btn, clear_btn;
    ImageButton main_enc, main_dec, go_btn, main_enc_orange, main_dec_orange, go_orange, send_btn, cpy_btn, new_btn, logout_btn;
    ConstraintLayout downC, UPc;
    TextView output, input_TV, output_TV;

    FirebaseRecyclerOptions<Users>options;
    FirebaseRecyclerAdapter<Users, FindFriendViewHolder>adapter;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mUserRef;
    RecyclerView recyclerView;
    Button friendbtn;


    @Override

    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_r_s_a);




        //recyclerView = findViewById(R.id.recyclerView);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        friendbtn = findViewById(R.id.selectFriend);
        friendbtn.setVisibility(View.VISIBLE);

        rsaInput = findViewById(R.id.rsa_inputTextarea);
        main_enc = findViewById(R.id.enc);
        main_dec = findViewById(R.id.decrypt);
        main_enc_orange = findViewById(R.id.enc_orange);
        main_dec_orange = findViewById(R.id.decrypt_orange);

        enc_btn = findViewById(R.id.rsa_encrypt_btn);
        new_btn = findViewById(R.id.rsa_new);
        clear_btn = findViewById(R.id.rsa_clear);
        cpy_btn = findViewById(R.id.rsa_copy);
        send_btn = findViewById(R.id.rsa_send);
        output = findViewById(R.id.output_decrypt);
        downC = findViewById(R.id.constraintLayout);
        UPc = findViewById(R.id.constraintLayout2);
        input_TV = findViewById(R.id.rsa_inputTV);
        output_TV = findViewById(R.id.rsa_outputTV);

        /*frindbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadUser("");
            }
        });*/


        main_enc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main_enc.setVisibility(View.INVISIBLE);
                main_enc_orange.setVisibility(View.VISIBLE);
                main_dec_orange.setVisibility(View.INVISIBLE);
                main_dec.setVisibility(View.VISIBLE);

//                main_dec.setVisibility(View.VISIBLE);
//                main_dec_orange.setVisibility(View.INVISIBLE);
//                userInput.setText("");
//                UPc.setVisibility(View.VISIBLE);
//                send_btn.setVisibility(View.INVISIBLE);
//                cpy_btn.setVisibility(View.INVISIBLE);
//                new_btn.setVisibility(View.INVISIBLE);
//                downC.setVisibility(View.INVISIBLE);
            }
            });

        main_dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main_dec.setVisibility(View.INVISIBLE);
                main_dec_orange.setVisibility(View.VISIBLE);
                main_enc_orange.setVisibility((View.INVISIBLE));
                main_enc.setVisibility(View.VISIBLE);


            }
        });

    }


    private void LoadUser(String s) {
        Query query = mUserRef.orderByChild("name").startAt(s).endAt(s+"\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<Users>().setQuery(query, Users.class).build();
        adapter = new FirebaseRecyclerAdapter<Users, FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, int position, @NonNull Users model) {
                if(!mUser.getUid().equals(getRef(position).getKey().toString()))
                {
                    Picasso.get().load(model.getImageUrl()).into(holder.profileImage);
                    holder.name.setText(model.getName());
                }
                else
                {
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }

            }

            @NonNull
            @Override
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_find_friend, parent, false);

                return new FindFriendViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

}