package com.parasoft.demoapp.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.parasoft.demoapp.R;
import com.parasoft.demoapp.retrofitConfig.response.OrderResponse;
import com.parasoft.demoapp.retrofitConfig.response.OrderStatus;

import java.util.List;
import lombok.NonNull;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private OnItemClickListener listener;
    private List<OrderResponse> mOrderList;
    private Context context;

    public OrderAdapter(List<OrderResponse> orderList, OnItemClickListener customListener){
        mOrderList =  orderList;
        listener = customListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.order_list_info_layout,parent,false);
        return new ViewHolder(view);
    }

    // Set values of the views in Recycler View
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        OrderResponse orderList = mOrderList.get(position);
        if (!orderList.getReviewedByAPV()) {
            viewHolder.orderNewStatus.setVisibility(View.VISIBLE);
        }
        viewHolder.orderIndex.setText(position + 1 + "");
        viewHolder.orderNumber.setText("#" + orderList.getOrderNumber());
        viewHolder.orderDetailDate.setText(orderList.getSubmissionDate().substring(0,orderList.getSubmissionDate().indexOf('T')));
        viewHolder.orderDetailTime.setText(orderList.getSubmissionDate().substring(orderList.getSubmissionDate().indexOf('T')+1,
                orderList.getSubmissionDate().lastIndexOf('.')));
        viewHolder.orderDetailRequestedBy.setText(orderList.getRequestedBy());
        viewHolder.orderStatus.setText(parseOrderStatus(viewHolder, orderList.getStatus()));
        viewHolder.bind(orderList, listener);
    }

    // Set the list size to Recycler View
    @Override
    public int getItemCount() {
        return mOrderList == null ? 0 : mOrderList.size();
    }

    // Define ViewHolder，extends RecyclerView.ViewHolder to get the views in Recycler View
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final int MIN_CLICK_DELAY_TIME = 1000;
        private long lastClickTime;

        TextView orderIndex;
        TextView orderNumber;
        TextView orderDetailDate;
        TextView orderDetailTime;
        TextView orderDetailRequestedBy;
        TextView orderStatus;
        TextView orderNewStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIndex = itemView.findViewById(R.id.order_index);
            orderNumber = itemView.findViewById(R.id.order_number);
            orderDetailDate = itemView.findViewById(R.id.order_detail_date);
            orderDetailTime = itemView.findViewById(R.id.order_detail_time);
            orderDetailRequestedBy = itemView.findViewById(R.id.order_detail_requested_by);
            orderStatus = itemView.findViewById(R.id.order_status);
            orderNewStatus = itemView.findViewById(R.id.order_new_status);
        }

        public void bind(final OrderResponse item, final OnItemClickListener listener) {
            itemView.setOnClickListener(v -> {
                if(System.currentTimeMillis() - lastClickTime > MIN_CLICK_DELAY_TIME) {
                    listener.onItemClick(item);
                    lastClickTime = System.currentTimeMillis();
                }
            });
        }
    }

    private String parseOrderStatus(@NonNull ViewHolder viewHolder, OrderStatus orderStatus) {
        switch (orderStatus) {
            case SUBMITTED:
                viewHolder.orderStatus.setTextColor(context.getResources().getColor(R.color.color_green));
                return context.getResources().getString(R.string.status_open);
            case APPROVED:
                return context.getResources().getString(R.string.status_approved);
            case DECLINED:
                return context.getResources().getString(R.string.status_denied);
            default:
                return "";
        }
    }

    public interface OnItemClickListener {
        void onItemClick(OrderResponse item);
    }
}