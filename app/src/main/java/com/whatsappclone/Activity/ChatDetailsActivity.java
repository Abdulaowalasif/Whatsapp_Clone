package com.whatsappclone.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.whatsappclone.Adapters.ChatAdapter;
import com.whatsappclone.R;
import com.whatsappclone.databinding.ActivityChatDetailsBinding;
import com.whatsappclone.models.MessageModel;
import java.util.ArrayList;
import java.util.Date;

public class ChatDetailsActivity extends AppCompatActivity {
    ActivityChatDetailsBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        final String senderId = auth.getUid();
        String receiverId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String profile = getIntent().getStringExtra("profilePic");

        binding.chatUserName.setText(userName);
        Picasso.get().load(profile).placeholder(R.drawable.user).into(binding.chatProfile);


        binding.chatBack.setOnClickListener(view -> startActivity(new Intent(ChatDetailsActivity.this, HomeActivity.class)));

        final ArrayList<MessageModel> messageModels = new ArrayList<>();
        final ChatAdapter adapter = new ChatAdapter(messageModels, this);

        binding.chatRecycleView.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.chatRecycleView.setLayoutManager(linearLayoutManager);

        final String senderRoom = senderId + receiverId;
        final String receiverRoom = receiverId + senderId;

        database.getReference().child("Chats").child(senderRoom).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageModels.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    MessageModel model = snapshot1.getValue(MessageModel.class);
                    messageModels.add(model);
                }
                adapter.notifyDataSetChanged();
                binding.chatRecycleView.scrollToPosition(messageModels.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.chatBtnSend.setOnClickListener(view -> {
            String msg = binding.chatEditSend.getText().toString();
            if (!msg.equals("")) {
                final MessageModel messageModel = new MessageModel(msg, senderId);
                messageModel.setTime(new Date().getTime());
                binding.chatEditSend.setText("");

                database.getReference().child("Chats").child(senderRoom).push().setValue(messageModel).addOnSuccessListener(unused ->
                        database.getReference().child("Chats").child(receiverRoom).push().setValue(messageModel).addOnSuccessListener(unused1 ->
                        {

                }));
            }
        });
    }
}