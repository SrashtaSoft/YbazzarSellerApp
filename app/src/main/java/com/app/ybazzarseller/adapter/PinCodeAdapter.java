package com.app.ybazzarseller.adapter;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import com.app.ybazzarseller.R;
import com.app.ybazzarseller.model.PinCode;

public class PinCodeAdapter extends RecyclerView.Adapter<PinCodeAdapter.PinCodeItemHolder> {

    final Activity activity;
    final ArrayList<PinCode> pinCodes;

    public PinCodeAdapter(Activity activity, ArrayList<PinCode> pinCodes) {
        this.activity = activity;
        this.pinCodes = pinCodes;
    }

    @Override
    public PinCodeItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_pincode, null);
        return new PinCodeItemHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NotNull final PinCodeItemHolder holder, int position) {
        final PinCode pinCode = pinCodes.get(position);
        holder.tvCityName.setText(pinCode.getPincode());
        holder.imgRemovePinCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinCodes.remove(pinCode);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return pinCodes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class PinCodeItemHolder extends RecyclerView.ViewHolder {
        final TextView tvCityName;
        final ImageView imgRemovePinCode;

        public PinCodeItemHolder(View itemView) {
            super(itemView);
            imgRemovePinCode = itemView.findViewById(R.id.imgRemovePinCode);
            tvCityName = itemView.findViewById(R.id.tvCityName);
        }
    }
}