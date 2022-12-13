package com.utcc.shopzada.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.utcc.shopzada.Listener.ItemClickListener;
import com.utcc.shopzada.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrderStatusViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView orderId, orderDate, orderTime, orderStatus, orderAddress, orderOwner;
    public ItemClickListener itemClickListener;

    public OrderStatusViewHolder(View itemView) {
        super(itemView);

        orderId = itemView.findViewById(R.id.orderId);
        orderDate = itemView.findViewById(R.id.orderDate);
        orderTime = itemView.findViewById(R.id.orderTime);
        orderStatus = itemView.findViewById(R.id.orderStatus);
        orderAddress = itemView.findViewById(R.id.orderAddress);
        orderOwner = itemView.findViewById(R.id.orderOwner);
    }

    public void onItemClickListener(ItemClickListener  itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

}
