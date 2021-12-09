package com.example.cryptotext;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptotext.Utills.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSA extends SetupActivity {

    private static final int REQUEST_CODE = 101;
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

    String publicKey;


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

        main_enc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main_enc.setVisibility(View.INVISIBLE);
                main_enc_orange.setVisibility(View.VISIBLE);
                main_dec_orange.setVisibility(View.INVISIBLE);
                main_dec.setVisibility(View.VISIBLE);
                UPc.setVisibility(View.VISIBLE);

                friendbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), PublicKeyFriendActivity.class);
                        startActivityForResult(intent, REQUEST_CODE);
                    }
                });
            }
        });

        main_dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UPc.setVisibility(View.INVISIBLE);
                main_dec.setVisibility(View.INVISIBLE);
                main_enc_orange.setVisibility(View.INVISIBLE);
                main_enc.setVisibility(View.VISIBLE);
                friendbtn.setVisibility(View.INVISIBLE);
                input_TV.setText("Encrypted Message");
                output_TV.setText("Message");
                main_dec_orange.setVisibility((View.VISIBLE));
                send_btn.setVisibility(View.INVISIBLE);
                new_btn.setVisibility(View.INVISIBLE);
                cpy_btn.setVisibility(View.INVISIBLE);
                downC.setVisibility(View.VISIBLE);
                rsaInput.setText("");
                output.setText("");
                enc_btn.setText("DECRYPT");
                enc_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                        String input = rsaInput.getText().toString();
                        String clearText = Rsa_dec(input, privateKeyBytesBase64);
                        if(clearText.length() == 0){
                            Toast.makeText(RSA.this, "Incorrect Cipher Text", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            output.setText(clearText);
                        }
                    }
                });


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null)
        {
            publicKey = data.getStringExtra("RESULT_STRING");

            enc_btn.setText("ENCRYPT");
            rsaInput.setText("");
            input_TV.setText("Message");
            output_TV.setText("Encrypted Message");
            output.setText("");
            downC.setVisibility(View.VISIBLE);
            enc_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String input = rsaInput.getText().toString().trim();
                    if (input.length() == 0) {
                        Toast.makeText(RSA.this, "Enter a valid message", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                            String out = Rsa_enc(input, publicKey);
                            output.setText(out);
                            send_btn.setVisibility(View.VISIBLE);
                            cpy_btn.setVisibility(View.VISIBLE);
                            new_btn.setVisibility(View.VISIBLE);
                            send_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent sendIntent = new Intent();
                                    sendIntent.setAction(Intent.ACTION_SEND);
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, out);
                                    sendIntent.setType("text/plain");

                                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                                    startActivity(shareIntent);

                                }
                            });
                            cpy_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("public", out);
                                    clipboard.setPrimaryClip(clip);
                                    Toast.makeText(RSA.this, "Encrypted Text Copied", Toast.LENGTH_SHORT).show();
                                }
                            });
                            new_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    send_btn.setVisibility(View.INVISIBLE);
                                    cpy_btn.setVisibility(View.INVISIBLE);
                                    new_btn.setVisibility(View.INVISIBLE);
                                    userInput.setText("");
                                    rsaInput.setText("");
                                    go_orange.setVisibility(View.INVISIBLE);
                                    go_btn.setVisibility(View.VISIBLE);
                                    downC.setVisibility(View.INVISIBLE);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });


        }

    }

    private static String Rsa_enc(String message, String publicKey) throws Exception {
        KeyFactory keyF = null;
        try {
            keyF = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        KeySpec keySpec = new X509EncodedKeySpec(Base64.decode(publicKey.trim().getBytes(), Base64.DEFAULT));
        Key key = null;
        try {
            key = keyF.generatePublic(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        // get an RSA cipher object and print the provider
        final Cipher cip = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
        // encrypt the plain text using the public key
        cip.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedBytes = cip.doFinal(message.getBytes("UTF-8"));
        String encryptedBase64 = new String(Base64.encode(encryptedBytes, Base64.DEFAULT));

        return encryptedBase64.replaceAll("(\\r|\\n)", "");

    }

    private static String Rsa_dec(String message, String PrivateKey)
    {
        String decString = "";
        try {
            KeyFactory keyF = KeyFactory.getInstance("RSA");
            KeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decode(PrivateKey.trim().getBytes(), Base64.DEFAULT));
            Key key = keyF.generatePrivate(keySpec);

            // get an RSA cipher object and print the provider
            final Cipher cip = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
            // encrypt the plain text using the public key
            cip.init(Cipher.DECRYPT_MODE, key);

            byte[] encBytes = Base64.decode(message, Base64.DEFAULT);
            byte[] decBytes = cip.doFinal(encBytes);
            decString = new String(decBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return decString;

    }

}