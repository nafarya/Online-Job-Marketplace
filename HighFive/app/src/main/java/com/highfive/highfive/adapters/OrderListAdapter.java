package com.highfive.highfive.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.highfive.highfive.R;
import com.highfive.highfive.model.Order;
import com.highfive.highfive.model.OrderTypeList;
import com.highfive.highfive.model.Profile;
import com.highfive.highfive.model.SubjectList;


import java.util.List;

/**
 * Created by dan on 02.12.16.
 */

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int item);
        void changeStatusButton(int item, String newStatus);
    }

    private List<Order> orderList;
    private OnItemClickListener listener;
    private SubjectList subjectList;
    private OrderTypeList orderTypeList;
    private String curTab;
    private Profile profile;

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    public OrderListAdapter(List<Order> list, OnItemClickListener listener,
                            SubjectList subjectList, OrderTypeList orderTypeList, String curTab,
                            Profile profile) {
        this.orderList = list;
        this.listener = listener;
        this.subjectList = subjectList;
        this.orderTypeList = orderTypeList;
        this.curTab = curTab;
        this.profile = profile;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_order_list_item, parent, false);
        return new ViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Order order = orderList.get(position);
        if (TextUtils.isEmpty(order.getTheme())) {
            holder.theme.setText(order.getTitle());
        } else {
            holder.theme.setText(order.getTheme());
        }
        holder.subject.setText(subjectList.getSubjectNameById(order.getSubject()));
        holder.orderType.setText(orderTypeList.getOrderTypeNameByTypeId(order.getType()));
        holder.price.setText(order.getOffer() + " Р");
        holder.bidNum.setText(order.getNumOfBids() + " Ставок");
        holder.date.setText("до " + order.getdeadLine());

        switch (curTab) {
            case "lenta":
                holder.changeStatusBtn.setVisibility(View.GONE);
                break;
            case "active":
                holder.changeStatusBtn.setText("Отменить");
                break;
            case "in work":
                holder.changeStatusBtn.setVisibility(View.GONE);
                break;
            case "waiting for author":
                holder.changeStatusBtn.setText("Отменить");
                break;
            case "cancelled":
                holder.changeStatusBtn.setVisibility(View.GONE);
                break;
            case "on guarantee":
                holder.changeStatusBtn.setVisibility(View.GONE);
                break;
            case "in rework":
                holder.changeStatusBtn.setVisibility(View.GONE);
                break;
            case "closed":
                holder.changeStatusBtn.setVisibility(View.GONE);
                break;
            case "chat":
                if (profile.getType().equals("teacher")) {
                    holder.changeStatusBtn.setText("Завершить");
                } else {
                    holder.changeStatusBtn.setVisibility(View.GONE);
                }
                break;
            default:
                holder.changeStatusBtn.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView theme;
        private TextView subject;
        private TextView orderType;
        private TextView date;
        private TextView price;
        private TextView bidNum;
        private OnItemClickListener listener;
        private ImageView marker;
        private Button changeStatusBtn;

        public ViewHolder(View itemView,  OnItemClickListener listener) {
            super(itemView);
            this.listener = listener;
            theme = (TextView) itemView.findViewById(R.id.order_list_item_theme);
            subject = (TextView) itemView.findViewById(R.id.order_list_item_subject);
            orderType = (TextView) itemView.findViewById(R.id.order_list_item_orderType);
            date = (TextView) itemView.findViewById(R.id.order_list_item_date);
            price = (TextView) itemView.findViewById(R.id.order_list_item_price);
            bidNum = (TextView) itemView.findViewById(R.id.order_list_item_bid_num);
            marker = (ImageView) itemView.findViewById(R.id.order_list_item_marker);
            changeStatusBtn = (Button) itemView.findViewById(R.id.change_status_button);
            changeStatusBtn.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.change_status_button) {
                switch (curTab) {
                    case "active":
                        listener.changeStatusButton(getAdapterPosition(), "cancel");
                        break;
                    case "waiting for author":
                        listener.changeStatusButton(getAdapterPosition(), "cancel");
                        break;
                    case "in rework":
                        listener.changeStatusButton(getAdapterPosition(), "cancel");
                        break;
                    case "chat":
                        listener.changeStatusButton(getAdapterPosition(), "complete");
                        break;
                }
            } else {
                listener.onItemClick(getAdapterPosition());
            }
        }
    }
}
