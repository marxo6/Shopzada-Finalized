package com.utcc.shopzada.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.utcc.shopzada.Listener.ItemClickListener;
import com.utcc.shopzada.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView userTextLabel;
    public ImageView verifiedSymbol;
    public ImageView userImageView;
    public ItemClickListener itemClickListener;

    public FollowViewHolder(View itemView) {
        super(itemView);

        userTextLabel = itemView.findViewById(R.id.userTextLabel);
        userImageView = itemView.findViewById(R.id.userImageView);
        verifiedSymbol = itemView.findViewById(R.id.verifiedSymbol);
    }

    public void onItemClickListener(ItemClickListener  itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

}
