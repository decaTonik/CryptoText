package com.example.cryptotext;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendViewHlder extends RecyclerView.ViewHolder{
    CircleImageView profileImageUrl;
    TextView username;
    public FindFriendViewHlder(@NonNull View itemView) {
        super(itemView);
        profileImageUrl= itemView.findViewById((R.id.profileImage));
        username=itemView.findViewById((R.id.username));
    }
}
