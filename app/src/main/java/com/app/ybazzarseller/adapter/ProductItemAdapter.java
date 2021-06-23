package com.app.ybazzarseller.adapter;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.app.ybazzarseller.R;
import com.app.ybazzarseller.activity.ProductDetailActivity;
import com.app.ybazzarseller.helper.ApiConfig;
import com.app.ybazzarseller.helper.Constant;
import com.app.ybazzarseller.helper.Session;
import com.app.ybazzarseller.helper.VolleyCallback;
import com.app.ybazzarseller.model.PriceVariation;
import com.app.ybazzarseller.model.Unit;

public class ProductItemAdapter extends RecyclerView.Adapter<ProductItemAdapter.ProductItemHolder> {

    final Activity activity;
    final ArrayList<PriceVariation> priceVariations;
    final Session session;
    ArrayList<String> arrayListStockStatus;
    ArrayList<Unit> arrayListUnit;
    String from;

    public ProductItemAdapter(Activity activity, ArrayList<PriceVariation> priceVariations, ArrayList<Unit> arrayListUnit, ArrayList<String> arrayListStockStatus, String from) {
        this.activity = activity;
        this.priceVariations = priceVariations;
        this.arrayListUnit = arrayListUnit;
        this.arrayListStockStatus = arrayListStockStatus;
        this.from = from;
        session = new Session(activity);
    }

    @NotNull
    @Override
    public ProductItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_product_items, null);
        return new ProductItemHolder(v);
    }

    public void addItem(PriceVariation priceVariation) {
        priceVariations.add(priceVariation);
        notifyDataSetChanged();
    }

    public void removeItem(PriceVariation priceVariation) {
        priceVariations.remove(priceVariation);
        notifyDataSetChanged();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NotNull final ProductItemHolder holder, int position) {
        try {
            PriceVariation priceVariation = priceVariations.get(position);
            if ((priceVariations.size() - 1) == position) {
                holder.imgAdd.setVisibility(View.VISIBLE);

            } else {
                holder.imgAdd.setVisibility(View.GONE);
            }
            holder.stockLyt.setVisibility(ProductDetailActivity.productType.equals("loose") ? View.GONE : View.VISIBLE);
            holder.edtMeasurement.addTextChangedListener(new GeneralTextWatcher(priceVariation, "measure"));
            holder.edtOriginalPrice.addTextChangedListener(new GeneralTextWatcher(priceVariation, "price"));
            holder.edtDiscountedPrice.addTextChangedListener(new GeneralTextWatcher(priceVariation, "dis_price"));
            holder.edtStock.addTextChangedListener(new GeneralTextWatcher(priceVariation, "stock"));
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, arrayListStockStatus);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.spinnerStatus.setAdapter(arrayAdapter);
            holder.spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    priceVariation.setServe_for(arrayListStockStatus.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            UnitAdapter unitAdapter = new UnitAdapter(activity, arrayListUnit, holder, priceVariation);
            holder.spinnerUnit.setAdapter(unitAdapter);
            holder.spinnerUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    priceVariation.setMeasurement_unit_id(arrayListUnit.get(position).getId());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            StockUnitAdapter unitStockAdapter = new StockUnitAdapter(activity, arrayListUnit, holder, priceVariation);
            holder.spinnerUnitStock.setAdapter(unitStockAdapter);
            holder.spinnerUnitStock.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    priceVariation.setStock_unit_id(arrayListUnit.get(position).getId());

                    System.out.println(">>>>>>>>>" + priceVariation.getStock_unit_id());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            if (priceVariation.getServe_for() != null && priceVariation.getServe_for().equals("Available")) {
                holder.spinnerStatus.setSelection(0);
            } else {
                holder.spinnerStatus.setSelection(1);
            }

            holder.imgAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String measurement, measurementId, price, discountPrice, serveFor, stock, stockId;
                    measurement = holder.edtMeasurement.getText().toString();
                    measurementId = String.valueOf(holder.spinnerUnit.getSelectedItemId());
                    price = holder.edtOriginalPrice.getText().toString();
                    discountPrice = holder.edtDiscountedPrice.getText().toString();
                    serveFor = String.valueOf(holder.spinnerStatus.getSelectedItem());
                    stock = holder.edtStock.getText().toString();
                    stockId = String.valueOf(holder.spinnerUnitStock.getSelectedItemId());
                    if (measurement.isEmpty() || price.isEmpty() || discountPrice.isEmpty()) {
                        Toast.makeText(activity, "Field should not be empty", Toast.LENGTH_SHORT).show();
                    } else {
                        if (ProductDetailActivity.productType.equals("packet") && stock.isEmpty()) {
                            Toast.makeText(activity, "Enter stocks!!", Toast.LENGTH_SHORT).show();
                        } else {
                            PriceVariation pv = priceVariations.get(position);
                            pv.setMeasurement(measurement);
                            pv.setMeasurement_unit_id(measurementId);
                            pv.setPrice(price);
                            pv.setDiscounted_price(discountPrice);
                            pv.setServe_for(serveFor);
                            pv.setStock_unit_id(stockId);
                            addItem(new PriceVariation());
                        }
                    }


                }
            });

            if (priceVariations.size() > 1) {
                holder.imgDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (from.equals("product")) {
                            removeVariant(activity, priceVariation);
                        } else {
                            removeItem(priceVariation);
                        }
                    }
                });
            }

            holder.edtMeasurement.setText(priceVariation.getMeasurement());
            holder.edtOriginalPrice.setText(priceVariation.getPrice());
            holder.edtDiscountedPrice.setText(priceVariation.getDiscounted_price());
            holder.edtStock.setText(priceVariation.getStock());
            unitAdapter.setItem(priceVariation.getMeasurement_unit_id());
            unitStockAdapter.setItem(priceVariation.getStock_unit_id());

            System.out.println("====from " + ProductDetailActivity.productType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GeneralTextWatcher implements TextWatcher {

        PriceVariation priceVariation;
        String type;

        public GeneralTextWatcher(PriceVariation priceVariation, String type) {
            this.priceVariation = priceVariation;
            this.type = type;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() != 0)
                if (type.equals("measure")) {
                    priceVariation.setMeasurement(s.toString());
                } else if (type.equals("price")) {
                    priceVariation.setPrice(s.toString());
                } else if (type.equals("dis_price")) {
                    priceVariation.setDiscounted_price(s.toString());
                } else if (type.equals("stock")) {
                    priceVariation.setStock(s.toString());
                    System.out.println(">>>>" + s.toString());
                }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }


    }


    public void removeVariant(final Activity activity, PriceVariation priceVariation) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        // Setting Dialog Message
        alertDialog.setTitle(R.string.remove_variant);
        alertDialog.setMessage(R.string.remove_variant_msg);
        alertDialog.setCancelable(false);
        final AlertDialog alertDialog1 = alertDialog.create();

        // Setting OK Button
        alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Constant.SELLER_ID, session.getData(Constant.ID));
                params.put(Constant.DELETE_VARIANT, Constant.GetVal);
                params.put(Constant.VARIANT_ID, priceVariation.getId());
                ApiConfig.RequestToVolley(new VolleyCallback() {
                    @Override
                    public void onSuccess(boolean result, String response) {
                        if (result) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (!jsonObject.getBoolean(Constant.ERROR)) {
                                    removeItem(priceVariation);
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
        return priceVariations.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ProductItemHolder extends RecyclerView.ViewHolder {
        TextView edtMeasurement, edtOriginalPrice, edtDiscountedPrice, edtStock;
        Spinner spinnerUnit, spinnerUnitStock, spinnerStatus;
        ImageView imgDelete, imgAdd;
        LinearLayout stockLyt;

        public ProductItemHolder(View itemView) {
            super(itemView);

            edtMeasurement = itemView.findViewById(R.id.edtMeasurement);
            edtOriginalPrice = itemView.findViewById(R.id.edtOriginalPrice);
            edtDiscountedPrice = itemView.findViewById(R.id.edtDiscountedPrice);
            edtStock = itemView.findViewById(R.id.edtStock);

            spinnerUnit = itemView.findViewById(R.id.spinnerUnit);
            spinnerUnitStock = itemView.findViewById(R.id.spinnerUnitStock);
            spinnerStatus = itemView.findViewById(R.id.spinnerStatus);
            stockLyt = itemView.findViewById(R.id.stockLyt);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            imgAdd = itemView.findViewById(R.id.imgAdd);
            stockLyt.setVisibility(ProductDetailActivity.productType.equals("loose") ? View.GONE : View.VISIBLE);
        }
    }

    public static class UnitAdapter extends BaseAdapter {
        final Context context;
        final ArrayList<Unit> units;
        final LayoutInflater inflter;
        ProductItemHolder holder;
        PriceVariation priceVariation;


        public UnitAdapter(Context applicationContext, ArrayList<Unit> units, ProductItemHolder holder, PriceVariation priceVariation) {
            this.context = applicationContext;
            this.units = units;
            this.holder = holder;
            this.priceVariation = priceVariation;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return units.size();
        }

        @Override
        public Unit getItem(int i) {
            return units.get(i);
        }

        @Override
        public long getItemId(int i) {
            return Long.parseLong(units.get(i).getId());
        }

        public void setItem(String id) {
            for (int i = 0; i < getCount(); i++) {
                if (id != null && id.equals(units.get(i).getId())) {
                    holder.spinnerUnit.setSelection(i);
                }
            }
        }

        @SuppressLint({"SetTextI18n", "ViewHolder", "InflateParams"})
        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.lyt_spinner_item, null);
            TextView measurement = view.findViewById(R.id.txtmeasurement);

            Unit unit = units.get(position);
            measurement.setText(unit.getName());

            try {
                if (priceVariation.getMeasurement_unit_id().equals(unit.getId())) {
                    holder.spinnerUnit.setSelection(position);
                }
            } catch (Exception ignore) {

            }

            holder.spinnerUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    try {
                        priceVariation.setMeasurement_unit_id(unit.getId());
                    } catch (Exception ignore) {

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            return view;
        }
    }

    public static class StockUnitAdapter extends BaseAdapter {
        final Context context;
        final ArrayList<Unit> units;
        final LayoutInflater inflter;
        ProductItemHolder holder;
        PriceVariation priceVariation;


        public StockUnitAdapter(Context applicationContext, ArrayList<Unit> units, ProductItemHolder holder, PriceVariation priceVariation) {
            this.context = applicationContext;
            this.units = units;
            this.holder = holder;
            this.priceVariation = priceVariation;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return units.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public void setItem(String id) {
            for (int i = 0; i < getCount(); i++) {
                if (id != null && id.equals(units.get(i).getId())) {
                    holder.spinnerUnitStock.setSelection(i);
                }
            }
        }

        @SuppressLint({"SetTextI18n", "ViewHolder", "InflateParams"})
        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.lyt_spinner_item, null);
            TextView measurement = view.findViewById(R.id.txtmeasurement);

            Unit unit = units.get(position);
            measurement.setText(unit.getName());

            holder.spinnerUnitStock.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    try {
                        priceVariation.setStock_unit_id(unit.getId());
                    } catch (Exception ignore) {

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            return view;
        }
    }

}