package com.example.spacedefender;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean isuser;

    public UserAdapter(Context mContext, List<User> mUsers, boolean isuser)
    {
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.isuser = isuser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = mUsers.get(position);
        holder.username.setText(user.getUsername());
        System.out.println("ccccccccccccccccccccccccccccccccccccc" + user.getUsername());
        // here I have add to all of the user to be online since we have users from the old version of the app
        user.setStatus("online");

        holder.profile_image.setImageResource(R.mipmap.ic_launcher);

         if (user.getStatus().equals("online"))
         {
             holder.img_on.setVisibility(View.VISIBLE);
             holder.img_off.setVisibility(View.GONE);
         }
         else
         {
             holder.img_on.setVisibility(View.GONE);
             holder.img_off.setVisibility(View.VISIBLE);
         }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("username", user.getUsername());
                String username = user.getUsername();
                user.setUsername(username);
                System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + username);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
        }
    }
}
