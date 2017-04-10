package com.highfive.highfive.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.highfive.highfive.R;
import com.highfive.highfive.model.Order;
import com.highfive.highfive.model.OrderTypeList;
import com.highfive.highfive.model.SubjectList;

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

    public OrderListAdapter(List<Order> list, OnItemClickListener listener,
                            SubjectList subjectList, OrderTypeList orderTypeList) {
        this.orderList = list;
        this.listener = listener;
        this.subjectList = subjectList;
        this.orderTypeList = orderTypeList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_order_list_item, parent, false);
        return new ViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.theme.setText(order.getTheme());
        holder.subject.setText(subjectList.getSubjectNameById(order.getSubjectId()));
        holder.orderType.setText(orderTypeList.getOrderTypeNameByTypeId(order.getType()));
        holder.price.setText(order.getOffer() + " Р");

        //DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
        holder.date.setText("до " + order.getdeadLine());
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

        public ViewHolder(View itemView,  OnItemClickListener listener) {
            super(itemView);
            this.listener = listener;
            theme = (TextView) itemView.findViewById(R.id.order_list_item_theme);
            subject = (TextView) itemView.findViewById(R.id.order_list_item_subject);
            orderType = (TextView) itemView.findViewById(R.id.order_list_item_orderType);
            date = (TextView) itemView.findViewById(R.id.order_list_item_date);
            price = (TextView) itemView.findViewById(R.id.order_list_item_price);
            bidNum = (TextView) itemView.findViewById(R.id.order_list_item_bid_num);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(getAdapterPosition());
        }
    }
}
