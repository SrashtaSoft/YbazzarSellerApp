package com.app.ybazzarseller.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.app.ybazzarseller.R;
import com.app.ybazzarseller.activity.MainActivity;
import com.app.ybazzarseller.activity.OrderDetailActivity;
import com.app.ybazzarseller.helper.Constant;
import com.app.ybazzarseller.helper.Session;
import com.app.ybazzarseller.model.OrderTracker;

public class OrderListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // for load more
    public final int VIEW_TYPE_ITEM = 0;
    public final int VIEW_TYPE_LOADING = 1;
    public boolean isLoading;
    Activity activity;
    ArrayList<OrderTracker> orderTrackerArrayList;
    String id = "0";

    public OrderListAdapter(Activity activity, ArrayList<OrderTracker> orderLists) {
        this.activity = activity;
        this.orderTrackerArrayList = orderLists;
    }

    public void add(int position, OrderTracker item) {
        orderTrackerArrayList.add(position, item);
        notifyItemInserted(position);
    }

    public void setLoaded() {
        isLoading = false;
    }


    // Create new views (invoked by the layout manager)

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.lyt_order_list, parent, false);
            return new OrderHolderItems(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_progressbar, parent, false);
            return new ViewHolderLoading(view);
        }

        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holderparent, final int position) {

        if (holderparent instanceof OrderHolderItems) {
            OrderHolderItems holder = (OrderHolderItems) holderparent;
            final OrderTracker orderList = orderTrackerArrayList.get(position);
            id = orderList.getId();

            try {
                if (new Session(activity).getReadMark("order_id_" + id)) {
                    holder.lytOrderTracker.setBackground(activity.getResources().getDrawable(R.drawable.unread_card_shadow));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.tvCustomerOrderNo.setText(activity.getString(R.string.order_number) + orderList.getId());
            holder.tvCustomerOrderDate.setText(activity.getString(R.string.order_on) + orderList.getDate_added());

            holder.tvCustomerName.setText(orderList.getUser_name());
            holder.tvCustomerMobile.setText(orderList.getMobile());

            if (orderList.getPayment_method().equals("cod")) {
                holder.tvCustomerPaymentMethod.setText(activity.getString(R.string.via) + "C.O.D.");
            } else {
                holder.tvCustomerPaymentMethod.setText(activity.getString(R.string.via) + orderList.getPayment_method());
            }


            holder.lytOrderTracker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Session(activity).setReadMark("order_id_" + orderList.getId(), true);
                    Constant.Position_Value = position;
                    MainActivity.orderTrackerArrayList = orderTrackerArrayList;
                    activity.startActivity(new Intent(activity, OrderDetailActivity.class).putExtra(Constant.ORDER_ID, orderList.getId()).putExtra(Constant.FROM, "list"));

                }
            });

        } else if (holderparent instanceof ViewHolderLoading) {
            ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holderparent;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }


    }

    @Override
    public int getItemCount() {
        return orderTrackerArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return orderTrackerArrayList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        OrderTracker product = orderTrackerArrayList.get(position);
        if (product != null)
            return Integer.parseInt(product.getId());
        else
            return position;
    }

    class ViewHolderLoading extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ViewHolderLoading(View view) {
            super(view);
            progressBar = view.findViewById(R.id.itemProgressbar);
        }
    }

    public class OrderHolderItems extends RecyclerView.ViewHolder {

        TextView tvCustomerName, tvCustomerMobile, tvCustomerOrderNo, tvCustomerPaymentMethod, tvCustomerOrderDate, tvStatus;
        CardView card_view_status;
        RelativeLayout lytOrderTracker;

        public OrderHolderItems(View itemView) {
            super(itemView);

            tvCustomerOrderNo = itemView.findViewById(R.id.tvCustomerOrderNo);
            tvCustomerOrderDate = itemView.findViewById(R.id.tvCustomerOrderDate);

            tvStatus = itemView.findViewById(R.id.tvStatus);
            card_view_status = itemView.findViewById(R.id.card_view_status);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);

            tvCustomerMobile = itemView.findViewById(R.id.tvCustomerMobile);
            tvCustomerPaymentMethod = itemView.findViewById(R.id.tvCustomerPaymentMethod);

            lytOrderTracker = itemView.findViewById(R.id.lytOrderTracker);


        }
    }
}