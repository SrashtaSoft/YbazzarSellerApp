package com.app.ybazzarseller.adapter;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.app.ybazzarseller.R;
import com.app.ybazzarseller.helper.ApiConfig;
import com.app.ybazzarseller.helper.Constant;
import com.app.ybazzarseller.helper.Session;
import com.app.ybazzarseller.helper.VolleyCallback;

public class ProductImagesAdapter extends RecyclerView.Adapter<ProductImagesAdapter.ImageHolder> {

    final Activity activity;
    final ArrayList<String> images;
    final Session session;
    String from;
    String productID;

    public ProductImagesAdapter(Activity activity, ArrayList<String> images, String from, String productID) {
        this.activity = activity;
        this.images = images;
        this.from = from;
        this.productID = productID;
        session = new Session(activity);
    }

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_image_list, null);
        return new ImageHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NotNull final ImageHolder holder, int position) {

        final String image = images.get(position);
        if (from.equals("api")) {
            Picasso.get().
                    load(image)
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.imgProductImage);
        } else {
            holder.imgProductImage.setImageBitmap(BitmapFactory.decodeFile(image));
        }

        holder.imgProductImageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productID.equals("0")) {
                    images.remove(image);
                    notifyDataSetChanged();
                } else {
                    removeImage(activity, "" + position, image);
                }
            }
        });
    }

    public void removeImage(final Activity activity, String position, String image) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        // Setting Dialog Message
        alertDialog.setTitle(R.string.remove_image);
        alertDialog.setMessage(R.string.remove_image_msg);
        alertDialog.setCancelable(false);
        final AlertDialog alertDialog1 = alertDialog.create();

        // Setting OK Button
        alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Constant.SELLER_ID, session.getData(Constant.ID));
                params.put(Constant.DELETE_OTHER_IMAGES, Constant.GetVal);
                params.put(Constant.PRODUCT_ID, productID);
                params.put(Constant.IMAGE, position);
                ApiConfig.RequestToVolley(new VolleyCallback() {
                    @Override
                    public void onSuccess(boolean result, String response) {
                        if (result) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (!jsonObject.getBoolean(Constant.ERROR)) {
                                    images.remove(image);
                                    notifyDataSetChanged();
                                } else {
                                    dialog.dismiss();
                                }
                            } catch (JSONException e) {
                                dialog.dismiss();
                                e.printStackTrace();
                            }
                        }
                    }
                }, activity, Constant.MAIN_URL, params, false);
            }
        });
        alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog1.dismiss();
            }
        });
        // Showing Alert Message
        alertDialog.show();

    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ImageHolder extends RecyclerView.ViewHolder {
        final ImageView imgProductImage, imgProductImageDelete;

        public ImageHolder(View itemView) {
            super(itemView);

            imgProductImage = itemView.findViewById(R.id.imgProductImage);
            imgProductImageDelete = itemView.findViewById(R.id.imgProductImageDelete);
        }
    }
}