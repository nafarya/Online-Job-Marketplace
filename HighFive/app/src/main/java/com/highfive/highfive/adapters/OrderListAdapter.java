package com.highfive.highfive.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.highfive.highfive.R;
import com.highfive.highfive.model.Order;
import com.highfive.highfive.model.OrderTypeList;
import com.highfive.highfive.model.SubjectList;

import org.jsoup.helper.StringUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by dan on 02.12.16.
 */

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int item);
    }

    private List<Order> orderList;
    private OnItemClickListener listener;
    private SubjectList subjectList;
    private OrderTypeList orderTypeList;
    private String curTab;

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    public OrderListAdapter(List<Order> list, OnItemClickListener listener,
                            SubjectList subjectList, OrderTypeList orderTypeList, String curTab) {
        this.orderList = list;
        this.listener = listener;
        this.subjectList = subjectList;
        this.orderTypeList = orderTypeList;
        this.curTab = curTab;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_order_list_item, parent, false);
        return new ViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Order order = orderList.get(position);
        if (StringUtil.isBlank(order.getTheme())) {
            holder.theme.setText(order.getTitle());
        } else {
            holder.theme.setText(order.getTheme());
        }
        holder.subject.setText(subjectList.getSubjectNameById(order.getSubjectId()));
        holder.orderType.setText(orderTypeList.getOrderTypeNameByTypeId(order.getType()));
        holder.price.setText(order.getOffer() + " Р");
        holder.bidNum.setText(order.getBidArraySize() + " Ставок");
        holder.date.setText("до " + order.getdeadLine());

        if (curTab.equals("completed")) {
            holder.marker.setBackgroundColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView theme;
        TextView subject;
        TextView orderType;
        TextView date;
        TextView price;
        TextView bidNum;
        OnItemClickListener listener;
        ImageView marker;

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
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(getAdapterPosition());
        }
    }
}
