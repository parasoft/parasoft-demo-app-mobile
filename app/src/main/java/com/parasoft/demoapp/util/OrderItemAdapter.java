package com.parasoft.demoapp.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parasoft.demoapp.R;
import com.parasoft.demoapp.retrofitConfig.response.OrderResponse.OrderItemInfo;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {
    private List<OrderItemInfo> mOrderItemList;
    private Context context;
    private OrderItemInfo orderItem;

    public OrderItemAdapter(List<OrderItemInfo> orderItemList){
        mOrderItemList =  orderItemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.order_items_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        orderItem = mOrderItemList.get(position);

        ImageUtil.loadImage(viewHolder.orderItemImage, orderItem.getImage());
        viewHolder.orderItemTitle.setText(orderItem.getName());
        viewHolder.orderItemQuantity.setText("x" + orderItem.getQuantity());
    }

    @Override
    public int getItemCount() {
        return mOrderItemList == null ? 0 : mOrderItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView orderItemImage;
        TextView orderItemTitle;
        TextView orderItemQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderItemImage = itemView.findViewById(R.id.order_item_img);
            orderItemTitle = itemView.findViewById(R.id.order_item_name);
            orderItemQuantity = itemView.findViewById(R.id.order_item_quantity);
        }
    }
}
