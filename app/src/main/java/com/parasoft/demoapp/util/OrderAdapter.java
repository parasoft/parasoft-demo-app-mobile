package com.parasoft.demoapp.util;

import android.annotation.SuppressLint;
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
    private final OnItemClickListener listener;
    private final List<OrderResponse> mOrderList;
    private Context context;
    private static boolean canStart = true;

    private LoadingState loadingState = LoadingState.FINISHED;

    public OrderAdapter(List<OrderResponse> orderList, OnItemClickListener customListener){
        mOrderList = orderList;
        listener = customListener;
    }

    @androidx.annotation.NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == ItemType.FOOTER.getType()) {
            View view = LayoutInflater.from(context).inflate(R.layout.order_list_footer_layout,parent,false);
            return new FooterViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.order_list_info_layout,parent,false);
            return new ItemViewHolder(view);
        }
    }

    // Set values of the views in Recycler View
    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull @NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ItemViewHolder) {
            OrderResponse orderList = mOrderList.get(position);
            ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
            itemViewHolder.orderNewStatus.setVisibility(View.GONE);
            if (!orderList.getReviewedByAPV()) {
                itemViewHolder.orderNewStatus.setVisibility(View.VISIBLE);
            }
            itemViewHolder.orderNumber.setText(context.getResources().getString(R.string.order_number, orderList.getOrderNumber()));
            itemViewHolder.orderDetailDate.setText(orderList.getSubmissionDate().substring(0,orderList.getSubmissionDate().indexOf('T')));
            itemViewHolder.orderDetailTime.setText(orderList.getSubmissionDate().substring(orderList.getSubmissionDate().indexOf('T')+1,
                    orderList.getSubmissionDate().lastIndexOf('.')));
            itemViewHolder.orderDetailRequestedBy.setText(orderList.getRequestedBy());
            itemViewHolder.orderStatus.setText(parseOrderStatus(itemViewHolder, orderList.getStatus()));
            itemViewHolder.bind(orderList, listener);
        } else if (viewHolder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
            switch (loadingState) {
                case LOADING:
                    footerViewHolder.mProgressBar.setVisibility(View.VISIBLE);
                    footerViewHolder.bottomBlank.setVisibility(View.GONE);
                    break;
                case FINISHED:
                    footerViewHolder.mProgressBar.setVisibility(View.GONE);
                    footerViewHolder.bottomBlank.setVisibility(View.GONE);
                    break;
                case END:
                    footerViewHolder.mProgressBar.setVisibility(View.GONE);
                    footerViewHolder.bottomBlank.setVisibility(View.VISIBLE);
            }
        }
    }

    // Set the list size to Recycler View, include list items and footer
    @Override
    public int getItemCount() {
        return mOrderList == null ? 0 : mOrderList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return ItemType.FOOTER.getType();
        }
        return ItemType.LISTITEM.getType();
    }

    public void addMoreItems(List<OrderResponse> items) {
        int insertPosition = mOrderList.size();
        mOrderList.addAll(items);
        notifyItemRangeInserted(insertPosition, items.size());
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setLoadState(LoadingState loadState) {
        this.loadingState = loadState;
        notifyDataSetChanged();
    }

    public LoadingState getLoadState() {
        return loadingState;
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

    private enum ItemType {
        LISTITEM(0), FOOTER(1);

        private final int type;

        ItemType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

    public enum LoadingState {
        LOADING, FINISHED, END
    }
}