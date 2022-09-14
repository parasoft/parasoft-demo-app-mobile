package com.parasoft.demoapp.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.parasoft.demoapp.R;
import com.parasoft.demoapp.retrofitConfig.response.OrderResponse;

import java.util.List;
import lombok.NonNull;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private List<OrderResponse> mOrderList;

    public OrderAdapter(List<OrderResponse> orderList){
        mOrderList =  orderList;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_info_layout,parent,false);
        return new ViewHolder(view);
    }

    // Set values of the views in Recycler View
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        OrderResponse orderList = mOrderList.get(position);
        viewHolder.orderIndex.setText(position + 1 + "");
        viewHolder.orderNumber.setText("#" + orderList.getOrderNumber());
    }

    // Set the list size to Recycler View
    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    // Define ViewHolder，extends RecyclerView.ViewHolder to get the views in Recycler View
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderIndex;
        TextView orderNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIndex = itemView.findViewById(R.id.order_index);
            orderNumber = itemView.findViewById(R.id.order_number);
        }
    }
}