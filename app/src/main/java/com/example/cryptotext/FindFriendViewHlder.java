package com.example.cryptotext;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendViewHlder extends RecyclerView.ViewHolder{
    CircleImageView ImageUrl;
    TextView name;
    public FindFriendViewHlder(@NonNull View itemView) {
        super(itemView);
        ImageUrl= itemView.findViewById((R.id.profileImage));
        name=itemView.findViewById((R.id.username));
    }
}
