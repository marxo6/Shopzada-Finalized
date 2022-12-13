package com.utcc.shopzada.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.utcc.shopzada.Listener.ItemClickListener;
import com.utcc.shopzada.R;

public class UserShowViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView productNameLabel, productPriceLabel;
    public CardView cardHolder;
    public ImageView productImageView;
    public ItemClickListener itemClickListener;

    public UserShowViewHolder(View itemView) {
        super(itemView);

        productImageView = itemView.findViewById(R.id.productImageView);
        productNameLabel = itemView.findViewById(R.id.productNameLabel);
        productPriceLabel = itemView.findViewById(R.id.productPriceLabel);
        cardHolder = itemView.findViewById(R.id.cardHolder);
    }

    public void onItemClickListener(ItemClickListener  itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
