package com.utcc.shopzada.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.utcc.shopzada.Listener.ItemClickListener;
import com.utcc.shopzada.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView userTextLabel;
    public ImageView verifiedSymbol;
    public CircleImageView userImageView;
    public ItemClickListener itemClickListener;

    public UserViewHolder(View itemView) {
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
