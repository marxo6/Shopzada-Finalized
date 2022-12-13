package com.utcc.shopzada.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.utcc.shopzada.Listener.ItemClickListener;
import com.utcc.shopzada.R;

public class ApprovementViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView productNameLabel, productPriceLabel, productAmountLabel, productDescriptionLabel, productOwnerLabel;
    public ImageView productImageView, productOwnerImageView;
    public ConstraintLayout acceptButton, declineButton;
    public ItemClickListener itemClickListener;

    public ApprovementViewHolder(View itemView) {
        super(itemView);

        productPriceLabel = itemView.findViewById(R.id.productPriceLabel);
        productNameLabel = itemView.findViewById(R.id.productNameLabel);
        productAmountLabel = itemView.findViewById(R.id.productAmountLabel);
        productDescriptionLabel = itemView.findViewById(R.id.productDescriptionLabel);
        productOwnerLabel = itemView.findViewById(R.id.productOwnerLabel);
        productOwnerImageView = itemView.findViewById(R.id.productOwnerImageLabel);
        productImageView = itemView.findViewById(R.id.productImageView);
        acceptButton = itemView.findViewById(R.id.acceptButton);
        declineButton = itemView.findViewById(R.id.declineButton);
    }

    public void onItemClickListener(ItemClickListener  itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

}
