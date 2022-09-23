package com.parasoft.demoapp.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.parasoft.demoapp.R;
import com.parasoft.demoapp.retrofitConfig.response.OrderResponse;
import com.parasoft.demoapp.retrofitConfig.response.OrderStatus;

import java.util.List;
import lombok.NonNull;

public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private OnItemClickListener listener;
    private List<OrderResponse> mOrderList;
    private Context context;
    private static boolean canStart = true;

    private final int ITEM_TYPE = 0;
    private final int FOOTER_TYPE = 1;
    public static final int LOADING = 0;
    public static final int LOAD_FINISH = 1;
    public static final int LOAD_END = 2;
    private int loadState = LOAD_FINISH;

    public OrderAdapter(List<OrderResponse> orderList, OnItemClickListener customListener){
        mOrderList =  orderList;
        listener = customListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == FOOTER_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.order_list_footer_layout,parent,false);
            return new FooterViewHolder(view);
        } else if (viewType == ITEM_TYPE){
            View view = LayoutInflater.from(context).inflate(R.layout.order_list_info_layout,parent,false);
            return new ItemViewHolder(view);
        }
        return null;
    }

    // Set values of the views in Recycler View
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ItemViewHolder) {
            OrderResponse orderList = mOrderList.get(position);
            ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
            itemViewHolder.orderNewStatus.setVisibility(View.GONE);
            if (!orderList.getReviewedByAPV()) {
                itemViewHolder.orderNewStatus.setVisibility(View.VISIBLE);
            }
            itemViewHolder.orderNumber.setText("#" + orderList.getOrderNumber());
            itemViewHolder.orderDetailDate.setText(orderList.getSubmissionDate().substring(0,orderList.getSubmissionDate().indexOf('T')));
            itemViewHolder.orderDetailTime.setText(orderList.getSubmissionDate().substring(orderList.getSubmissionDate().indexOf('T')+1,
                    orderList.getSubmissionDate().lastIndexOf('.')));
            itemViewHolder.orderDetailRequestedBy.setText(orderList.getRequestedBy());
            itemViewHolder.orderStatus.setText(parseOrderStatus(itemViewHolder, orderList.getStatus()));
            itemViewHolder.bind(orderList, listener);
        } else if (viewHolder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
            switch (loadState) {
                case LOADING:
                    footerViewHolder.mProgressBar.setVisibility(View.VISIBLE);
                    footerViewHolder.bottomBlank.setVisibility(View.GONE);
                    break;
                case LOAD_FINISH:
                    footerViewHolder.mProgressBar.setVisibility(View.GONE);
                    footerViewHolder.bottomBlank.setVisibility(View.GONE);
                    break;
                case LOAD_END:
                    footerViewHolder.mProgressBar.setVisibility(View.GONE);
                    footerViewHolder.bottomBlank.setVisibility(View.VISIBLE);
            }
        }
    }

    // Set the list size to Recycler View
    @Override
    public int getItemCount() {
        return mOrderList == null ? 0 : mOrderList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return FOOTER_TYPE;
        }
        return ITEM_TYPE;
    }

    // add item when slide-down fresh data
    public void addHeaderItem(List<OrderResponse> items) {
        mOrderList.clear();
        mOrderList.addAll(items);
        notifyDataSetChanged();
    }

    // add item when slide-up load data
    public void addFooterItem(List<OrderResponse> items) {
        mOrderList.addAll(items);
        notifyDataSetChanged();
    }

    public void setLoadState(int loadState) {
        this.loadState = loadState;
        notifyDataSetChanged();
    }

    public int getLoadState() {
        return this.loadState;
    }

    // Define FooterViewHolder，extends RecyclerView.ViewHolder to get the views in Recycler View
    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        ProgressBar mProgressBar;
        TextView bottomBlank;

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            mProgressBar = itemView.findViewById(R.id.progress_bar);
            bottomBlank = itemView.findViewById(R.id.bottom_blank);
        }
    }

    // Define ItemViewHolder，extends RecyclerView.ViewHolder to get the views in Recycler View
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView orderNumber;
        TextView orderDetailDate;
        TextView orderDetailTime;
        TextView orderDetailRequestedBy;
        TextView orderStatus;
        TextView orderNewStatus;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            orderNumber = itemView.findViewById(R.id.order_number);
            orderDetailDate = itemView.findViewById(R.id.order_detail_date);
            orderDetailTime = itemView.findViewById(R.id.order_detail_time);
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

    private String parseOrderStatus(@NonNull ItemViewHolder viewHolder, OrderStatus orderStatus) {
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