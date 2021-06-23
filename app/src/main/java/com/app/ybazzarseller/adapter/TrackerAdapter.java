package com.app.ybazzarseller.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

import com.app.ybazzarseller.R;
import com.app.ybazzarseller.activity.OrderDetailActivity;
import com.app.ybazzarseller.helper.ApiConfig;
import com.app.ybazzarseller.helper.Constant;
import com.app.ybazzarseller.helper.Session;
import com.app.ybazzarseller.model.OrderTracker;

public class TrackerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // for load more
    public final int VIEW_TYPE_ITEM = 0;
    public final int VIEW_TYPE_LOADING = 1;
    final Activity activity;
    final ArrayList<OrderTracker> orderTrackerArrayList;
    final Context context;
    public boolean isLoading;


    public TrackerAdapter(Context context, Activity activity, ArrayList<OrderTracker> orderTrackerArrayList) {
        this.context = context;
        this.activity = activity;
        this.orderTrackerArrayList = orderTrackerArrayList;
    }

    public void add(int position, OrderTracker item) {
        orderTrackerArrayList.add(position, item);
        notifyItemInserted(position);
    }

    public void setLoaded() {
        isLoading = false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.lyt_trackorder, parent, false);
            return new TrackerHolderItems(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_progressbar, parent, false);
            return new ViewHolderLoading(view);
        }

        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holderparent, final int position) {

        if (holderparent instanceof TrackerHolderItems) {
            final TrackerHolderItems holder = (TrackerHolderItems) holderparent;
            final OrderTracker order = orderTrackerArrayList.get(position);
            holder.txtorderid.setText(activity.getString(R.string.order_number) + order.getId());
            String[] date = order.getDate_added().split("\\s+");
            holder.txtorderdate.setText(activity.getString(R.string.ordered_on) + date[0]);
            holder.txtorderamount.setText(activity.getString(R.string.for_amount_on) + new Session(context).getData(Constant.CURRENCY) + ApiConfig.StringFormat(order.getTotal()));

            holder.carddetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startActivity(new Intent(activity, OrderDetailActivity.class).putExtra("id", "").putExtra("model", order));
                }
            });

            ArrayList<String> items = new ArrayList<>();
            for (int i = 0; i < order.getItems().size(); i++) {
                items.add(order.getItems().get(i).getName());
            }
            holder.tvItems.setText(Arrays.toString(items.toArray()).replace("]", "").replace("[", ""));
            holder.tvTotalItems.setText(items.size() + activity.getString(R.string.item));

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
        return position;
    }

    static class ViewHolderLoading extends RecyclerView.ViewHolder {
        public final ProgressBar progressBar;

        public ViewHolderLoading(View view) {
            super(view);
            progressBar = view.findViewById(R.id.itemProgressbar);
        }
    }

    public class TrackerHolderItems extends RecyclerView.ViewHolder {
        final TextView txtorderid;
        final TextView txtorderdate;
        final TextView carddetail;
        final TextView txtorderamount;
        final TextView tvTotalItems;
        final TextView tvItems;

        public TrackerHolderItems(View itemView) {
            super(itemView);
            txtorderid = itemView.findViewById(R.id.txtorderid);
            txtorderdate = itemView.findViewById(R.id.txtorderdate);
            carddetail = itemView.findViewById(R.id.carddetail);
            txtorderamount = itemView.findViewById(R.id.txtorderamount);
            tvTotalItems = itemView.findViewById(R.id.tvTotalItems);
            tvItems = itemView.findViewById(R.id.tvItems);

        }
    }
}
