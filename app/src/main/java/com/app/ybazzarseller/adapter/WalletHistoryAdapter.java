package com.app.ybazzarseller.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.app.ybazzarseller.R;
import com.app.ybazzarseller.helper.Constant;
import com.app.ybazzarseller.model.WalletHistory;

public class WalletHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // for load more
    public final int VIEW_TYPE_ITEM = 0;
    public final int VIEW_TYPE_LOADING = 1;
    public boolean isLoading;
    Activity activity;
    ArrayList<WalletHistory> items;

    public WalletHistoryAdapter(Activity activity, ArrayList<WalletHistory> items) {
        this.activity = activity;
        this.items = items;
    }

    public void add(int position, WalletHistory item) {
        items.add(position, item);
        notifyItemInserted(position);
    }

    public void setLoaded() {
        isLoading = false;
    }


    // Create new views (invoked by the layout manager)

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.activity_wallet_history, parent, false);
            return new OrderHolderItems(view);
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

        if (holderparent instanceof OrderHolderItems) {
            OrderHolderItems holder = (OrderHolderItems) holderparent;
            final WalletHistory item = items.get(position);

            holder.tvTransactionId.setText(activity.getString(R.string.tx_id) + item.getId());
            holder.tvWalletBalance.setText(Constant.SETTING_CURRENCY_SYMBOL + item.getAmount());

            holder.tvMessage.setText(activity.getString(R.string.tx_message) + item.getMessage());
            holder.tvTransactionDate.setText(activity.getString(R.string.tx_date) + item.getDate_created());

            if (item.getStatus().equalsIgnoreCase("0")) {
                holder.tvActiveStatus.setText(activity.getString(R.string.pending));
                holder.cardActiveStatus.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.status_pending));
            } else if (item.getStatus().equalsIgnoreCase("1")) {
                holder.tvActiveStatus.setText(activity.getString(R.string.approved));
                holder.cardActiveStatus.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.status_approved));
            } else {
                holder.tvActiveStatus.setText(activity.getString(R.string.cancelled));
                holder.cardActiveStatus.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.status_cancelled));
            }

        } else if (holderparent instanceof ViewHolderLoading) {
            ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holderparent;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        WalletHistory product = items.get(position);
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
        TextView tvTransactionId, tvMessage, tvTransactionDate, tvWalletBalance, tvActiveStatus;
        CardView cardActiveStatus;

        public OrderHolderItems(View itemView) {
            super(itemView);
            tvTransactionId = itemView.findViewById(R.id.tvTransactionId);
            tvWalletBalance = itemView.findViewById(R.id.tvAmount);
            tvActiveStatus = itemView.findViewById(R.id.tvActiveStatus);
            cardActiveStatus = itemView.findViewById(R.id.cardActiveStatus);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTransactionDate = itemView.findViewById(R.id.tvTransactionDate);
        }
    }
}