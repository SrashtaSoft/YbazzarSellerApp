package com.app.ybazzarseller.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.app.ybazzarseller.R;
import com.app.ybazzarseller.adapter.ItemsAdapter;
import com.app.ybazzarseller.helper.ApiConfig;
import com.app.ybazzarseller.helper.Constant;
import com.app.ybazzarseller.helper.Session;
import com.app.ybazzarseller.helper.VolleyCallback;
import com.app.ybazzarseller.model.OrderTracker;

public class OrderDetailActivity extends AppCompatActivity {
    OrderTracker order;
    TextView txtorderotp, tvItemTotal, tvDeliveryCharge, tvTotal, tvPromoCode, tvPCAmount, tvWallet, tvFinalTotal, tvDPercent, tvDAmount;
    TextView txtotherdetails, txtorderid, txtorderdate;
    RecyclerView recyclerView;
    RelativeLayout relativeLyt;
    LinearLayout lytPromo, lytWallet, lytPriceDetail, lytotp;
    double totalAfterTax = 0.0;
    String id;
    ScrollView scrollView;
    Activity activity;
    Session session;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        toolbar = findViewById(R.id.toolbar);

        activity = this;
        session = new Session(activity);

        lytPriceDetail = findViewById(R.id.lytPriceDetail);
        lytPromo = findViewById(R.id.lytPromo);
        lytWallet = findViewById(R.id.lytWallet);
        tvItemTotal = findViewById(R.id.tvItemTotal);
        tvDeliveryCharge = findViewById(R.id.tvDeliveryCharge);
        tvDAmount = findViewById(R.id.tvDAmount);
        tvDPercent = findViewById(R.id.tvDPercent);
        tvTotal = findViewById(R.id.tvTotal);
        tvPromoCode = findViewById(R.id.tvPromoCode);
        tvPCAmount = findViewById(R.id.tvPCAmount);
        tvWallet = findViewById(R.id.tvWallet);
        tvFinalTotal = findViewById(R.id.tvFinalTotal);
        txtorderid = findViewById(R.id.txtorderid);
        txtorderdate = findViewById(R.id.txtorderdate);
        relativeLyt = findViewById(R.id.relativeLyt);
        txtotherdetails = findViewById(R.id.txtotherdetails);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setNestedScrollingEnabled(false);
        txtorderotp = findViewById(R.id.txtorderotp);
        lytotp = findViewById(R.id.lytotp);
        scrollView = findViewById(R.id.scrollView);

        id = getIntent().getStringExtra("id");

        if (id.equals("")) {
            order = (OrderTracker) getIntent().getSerializableExtra("model");
            id = order.getId();
            SetData(order);
        } else {
            getOrderDetails(id);
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.order_id) + id);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public void getOrderDetails(String id) {
        scrollView.setVisibility(View.GONE);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_ORDERS, Constant.GetVal);
        params.put(Constant.SELLER_ID, session.getData(Constant.ID));
        params.put(Constant.ORDER_ID, id);

        //  System.out.println("=====params " + params.toString());
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(response);
                        if (!jsonObject1.getBoolean(Constant.ERROR)) {
                            JSONObject jsonObject = jsonObject1.getJSONArray(Constant.DATA).getJSONObject(0);
                            SetData(ApiConfig.OrderTracker(jsonObject));
                        } else {
                            scrollView.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        scrollView.setVisibility(View.VISIBLE);
                    }
                }
            }
        }, activity, Constant.MAIN_URL, params, false);
    }

    @SuppressLint("SetTextI18n")
    public void SetData(OrderTracker order) {
        try {
            String[] date = order.getDate_added().split("\\s+");
            txtorderid.setText(order.getId());
            if (order.getOtp().equals("0")) {
                lytotp.setVisibility(View.GONE);
            } else {
                txtorderotp.setText(order.getOtp());
            }
            txtorderdate.setText(date[0]);
            txtotherdetails.setText(getString(R.string.name_1) + order.getUser_name() + getString(R.string.mobile_no_1) + order.getMobile() + getString(R.string.address_1) + order.getAddress());
            totalAfterTax = (Double.parseDouble(order.getTotal()) + Double.parseDouble(order.getDelivery_charge()));
            tvItemTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + ApiConfig.StringFormat(order.getTotal()));
            tvDeliveryCharge.setText("+ " + Constant.SETTING_CURRENCY_SYMBOL + ApiConfig.StringFormat(order.getDelivery_charge()));
            tvDAmount.setText("- " + Constant.SETTING_CURRENCY_SYMBOL + ApiConfig.StringFormat(order.getDiscounted_price()));
            tvTotal.setText("+ " + Constant.SETTING_CURRENCY_SYMBOL + ApiConfig.StringFormat(order.getTotal()));
            if (!order.getPromo_code().equals("")) {
                lytPromo.setVisibility(View.VISIBLE);
                tvPromoCode.setText(getString(R.string.promo_applied)+"("+order.getPromo_code()+")");
                tvPCAmount.setText("- " + Constant.SETTING_CURRENCY_SYMBOL + ApiConfig.StringFormat(order.getPromo_discount()));
            } else {
                lytPromo.setVisibility(View.GONE);
            }

            tvWallet.setText("- " + Constant.SETTING_CURRENCY_SYMBOL + ApiConfig.StringFormat(order.getWallet_balance()));
            tvFinalTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + ApiConfig.StringFormat(order.getFinal_total()));

            recyclerView.setAdapter(new ItemsAdapter(activity, order.getItems()));
            relativeLyt.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}