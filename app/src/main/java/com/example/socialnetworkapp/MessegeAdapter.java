package com.example.socialnetworkapp;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessegeAdapter extends RecyclerView.Adapter<MessegeAdapter.MessegeViewHolder>{

    private List<Message> usermessegelost;
    private FirebaseAuth Mauth;
    private DatabaseReference MuserDatabase;

    public MessegeAdapter(List<Message> usermessegelost) {
        this.usermessegelost = usermessegelost;
    }

    @NonNull
    @Override
    public MessegeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sample_message_desian, viewGroup, false);
        Mauth = FirebaseAuth.getInstance();
        return new MessegeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessegeViewHolder messegeViewHolder, int i) {

        String messegesenderID = Mauth.getCurrentUser().getUid();
        Message message = usermessegelost.get(i);

        String messageUserID = message.getFrom();
        String frommessegetype = message.getType();

        MuserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(messageUserID);

        MuserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    if(dataSnapshot.hasChild("downloadurl")){
                        String imagepath = dataSnapshot.child("downloadurl").getValue().toString();
                        Glide.with(messegeViewHolder.reciverimage.getContext()).load(imagepath).placeholder(R.drawable.default_image).into(messegeViewHolder.reciverimage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MessegeViewHolder extends RecyclerView.ViewHolder {

        View Mview;
        public TextView sendermessege, recivermessege;
        public CircleImageView reciverimage;

        public MessegeViewHolder(@NonNull View itemView) {
            super(itemView);

            Mview = itemView;
            sendermessege = Mview.findViewById(R.id.SenderMessegeID);
            recivermessege = Mview.findViewById(R.id.ReciverMessegeID);
            reciverimage = Mview.findViewById(R.id.MessegePicID);
        }
    }
}





