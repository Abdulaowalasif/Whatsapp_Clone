package com.whatsappclone.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.whatsappclone.R;
import com.whatsappclone.models.MessageModel;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter {
ArrayList<MessageModel> msgModel;
Context context;
int SENDER_VIEW=1;
int RECEIVER_VIEW=2;

    public ChatAdapter(ArrayList<MessageModel> msgModel, Context context) {
        this.msgModel = msgModel;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      if (viewType==SENDER_VIEW){
          View view= LayoutInflater.from(context).inflate(R.layout.sender_sample,parent,false);
          return new SenderViewHolder(view);
      }else {
          View view= LayoutInflater.from(context).inflate(R.layout.reciever_sample,parent,false);
          return new ReceiverViewHolder(view);
      }
    }

    @Override
    public int getItemViewType(int position) {
        if (msgModel.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())){
            return SENDER_VIEW;
        }
        else {
            return RECEIVER_VIEW;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel=msgModel.get(position);
        if (holder.getClass()==SenderViewHolder.class){
            ((SenderViewHolder)holder).senderMsg.setText(messageModel.getMessage());
        }else{
            ((ReceiverViewHolder)holder).receiveMsg.setText(messageModel.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return msgModel.size();
    }

    public static class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMsg,senderTime;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg=itemView.findViewById(R.id.sender_msg);
            senderTime=itemView.findViewById(R.id.sender_time);
        }
    }
    public static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView receiveMsg,receiveTime;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiveMsg=itemView.findViewById(R.id.receiver_msg);
            receiveTime=itemView.findViewById(R.id.receiver_time);
        }
    }
}
