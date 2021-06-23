package com.app.ybazzarseller.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ybazzarseller.helper.ApiConfig;
import com.app.ybazzarseller.model.Customers;
import com.app.ybazzarseller.R;
import com.app.ybazzarseller.helper.Constant;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // for load more
    public final int VIEW_TYPE_ITEM = 0;
    public final int VIEW_TYPE_LOADING = 1;
    public boolean isLoading;
    Activity activity;
    ArrayList<Customers> items;

    String id = "0";

    public CustomerListAdapter(Activity activity, ArrayList<Customers> items) {
        this.activity = activity;
        this.items = items;
    }

    public void add(int position, Customers item) {
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
            View view = LayoutInflater.from(activity).inflate(R.layout.lyt_customer_list, parent, false);
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
            final Customers item = items.get(position);

            holder.tvName.setText(item.getName());
            holder.tvEmail.setText(ApiConfig.maskEmailAddress(item.getEmail()));
            holder.tvMobile.setText(ApiConfig.maskMobileNumber(item.getMobile()));
            holder.tvWalletBalance.setText(Constant.SETTING_CURRENCY_SYMBOL + item.getBalance());


            Picasso.get()
                    .load(item.getProfile())
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.imgUser);

            if (item.getStatus().equalsIgnoreCase("1")) {
                holder.tvActiveStatus.setText("Active");
                holder.cardActiveStatus.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.user_active));
            } else {
                holder.tvActiveStatus.setText("Deactivate");
                holder.cardActiveStatus.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.user_deactivate));
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
        Customers product = items.get(position);
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
        TextView tvName, tvEmail, tvMobile, tvWalletBalance, tvActiveStatus;
        ImageView imgUser;
        CardView cardActiveStatus;

        public OrderHolderItems(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvMobile = itemView.findViewById(R.id.tvMobile);
            tvWalletBalance = itemView.findViewById(R.id.tvWalletBalance);
            imgUser = itemView.findViewById(R.id.imgUser);
            tvActiveStatus = itemView.findViewById(R.id.tvActiveStatus);
            cardActiveStatus = itemView.findViewById(R.id.cardActiveStatus);
        }
    }
}