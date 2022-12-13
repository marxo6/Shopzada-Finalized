package com.utcc.shopzada.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.utcc.shopzada.Listener.ItemClickListener;
import com.utcc.shopzada.R;

public class ReceiptDisplayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView productNameLabel, productPriceLabel, productAmountLabel;
    private ItemClickListener itemClickListener;

    public ReceiptDisplayViewHolder(View itemView) {
        super(itemView);

        productNameLabel = itemView.findViewById(R.id.nameLabel);
        productPriceLabel = itemView.findViewById(R.id.priceLabel);
        productAmountLabel = itemView.findViewById(R.id.amountLabel);
    }

    public void onItemClickListener(ItemClickListener  itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
