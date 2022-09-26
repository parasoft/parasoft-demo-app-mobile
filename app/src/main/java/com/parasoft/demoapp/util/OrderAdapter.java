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
    private final OnItemClickListener listener;
    private final List<OrderResponse> mOrderList;
    private Context context;
    private static boolean canStart = true;

    public OrderAdapter(List<OrderResponse> orderList, OnItemClickListener customListener){
        mOrderList = orderList;
        listener = customListener;
    }

    @androidx.annotation.NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.order_list_info_layout,parent,false);
        return new ViewHolder(view);
    }

    // Set values of the views in Recycler View
    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull @NonNull ViewHolder viewHolder, int position) {
        OrderResponse orderList = mOrderList.get(position);
        if (!orderList.getReviewedByAPV()) {
            viewHolder.orderNewStatus.setVisibility(View.VISIBLE);
        }
        String submissionDate = CommonUtil.convertToLocalTime(orderList.getSubmissionDate());
        if (submissionDate != null) {
            viewHolder.orderSubmissionDate.setText(submissionDate.substring(0,submissionDate.indexOf('T')));
            viewHolder.orderSubmissionTime.setText(submissionDate.substring(submissionDate.indexOf('T')+1, submissionDate.lastIndexOf('.')));
        }
        viewHolder.orderNumber.setText(context.getResources().getString(R.string.order_number, orderList.getOrderNumber()));
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
        TextView orderNumber;
        TextView orderSubmissionDate;
        TextView orderSubmissionTime;
        TextView orderDetailRequestedBy;
        TextView orderStatus;
        TextView orderNewStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderNumber = itemView.findViewById(R.id.order_number);
            orderSubmissionDate = itemView.findViewById(R.id.order_detail_date);
            orderSubmissionTime = itemView.findViewById(R.id.order_detail_time);
            orderDetailRequestedBy = itemView.findViewById(R.id.order_detail_requested_by);
            orderStatus = itemView.findViewById(R.id.order_status);
            orderNewStatus = itemView.findViewById(R.id.order_new_status);
        }

        public void bind(final OrderResponse item, final OnItemClickListener listener) {
            itemView.setOnClickListener(v -> {
                if(canStart){
                    listener.onItemClick(item);
                    canStart = false;
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
                viewHolder.orderStatus.setTextColor(context.getResources().getColor(R.color.light_black));
                return context.getResources().getString(R.string.status_approved);
            case DECLINED:
                viewHolder.orderStatus.setTextColor(context.getResources().getColor(R.color.light_black));
                return context.getResources().getString(R.string.status_denied);
            default:
                return "";
        }
    }

    public interface OnItemClickListener {
        void onItemClick(OrderResponse item);
    }

    public void setCanStart(boolean can) {
        canStart = can;
    }
}