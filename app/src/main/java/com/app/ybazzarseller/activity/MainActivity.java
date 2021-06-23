package com.app.ybazzarseller.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.app.ybazzarseller.R;
import com.app.ybazzarseller.adapter.ItemsAdapter;
import com.app.ybazzarseller.adapter.OrderListAdapter;
import com.app.ybazzarseller.adapter.TrackerAdapter;
import com.app.ybazzarseller.helper.ApiConfig;
import com.app.ybazzarseller.helper.AppController;
import com.app.ybazzarseller.helper.Constant;
import com.app.ybazzarseller.helper.Session;
import com.app.ybazzarseller.helper.VolleyCallback;
import com.app.ybazzarseller.model.OrderTracker;

@SuppressWarnings("ALL")
public class MainActivity extends DrawerActivity {
    public static ArrayList<OrderTracker> orderTrackerArrayList;
    public static OrderListAdapter orderListAdapter;
    public static OrderTracker orderLists;
    @Nullable
    public Session session;
    boolean doubleBackToExitPressedOnce = false;
    TextView tvTitleWeeklySales;
    TextView tvWeeklySales;
    TextView tvOrdersCount;
    TextView tvProductsCount;
    static TextView tvBalance;
    TextView tvSoldOutCount;
    TextView tvLowStockCount;
    CardView lytOrders, lytProducts, lytCustomers, lytSoldOut, lytLowStock;
    RecyclerView recyclerView;
    ItemsAdapter itemsAdapter;
    Toolbar toolbar;
    Activity activity;
    SwipeRefreshLayout swipeRefresh;
    NestedScrollView scrollView;
    int total = 0;
    boolean isLoadMore = false;
    int offset = 0;
    //    SearchView searchview;
    LinearLayout lyt_order_detail, lyt_stock_detail;//;,lytSearchview;
    String filterBy;
    int filterIndex;
    CardView lytSales;
    TrackerAdapter trackerAdapter;
    private String query;

    @SuppressWarnings("deprecation")
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_main, frameLayout);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        activity = MainActivity.this;
        session = new Session(activity);


        orderTrackerArrayList = new ArrayList<>();

        filterIndex = 0;

        lytOrders = findViewById(R.id.lytOrders);
        lytProducts = findViewById(R.id.lytProducts);
        lytCustomers = findViewById(R.id.lytCustomers);
        lytSoldOut = findViewById(R.id.lytSoldOut);
        lytLowStock = findViewById(R.id.lytLowStock);

        tvProductsCount = findViewById(R.id.tvProductsCount);
        tvOrdersCount = findViewById(R.id.tvOrdersCount);
        tvBalance = findViewById(R.id.tvBalance);
        tvSoldOutCount = findViewById(R.id.tvSoldOutCount);
        tvLowStockCount = findViewById(R.id.tvLowStockCount);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        scrollView = findViewById(R.id.scrollView);
//        searchview = findViewById(R.id.searchview);
//        lytSearchview = findViewById(R.id.lytSearchview);
        lyt_stock_detail = findViewById(R.id.lyt_stock_detail);
        lyt_order_detail = findViewById(R.id.lyt_order_detail);
        tvTitleWeeklySales = findViewById(R.id.tvTitleWeeklySales);
        tvWeeklySales = findViewById(R.id.tvWeeklySales);
        lytSales = findViewById(R.id.lytSales);

        recyclerView = findViewById(R.id.recyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);

        getFinancialStatistics();

        lytOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, OrderListActivity.class));
            }
        });

        lytProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProductListActivity.class).putExtra("from", "all_stock"));
            }
        });

        lytCustomers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, WalletTransactionsListActivity.class));
            }
        });

        lytSoldOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProductListActivity.class).putExtra("from", "out_stock"));
            }
        });

        lytLowStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProductListActivity.class).putExtra("from", "low_stock"));
            }
        });

//        searchview.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//                lytSearchview.setVisibility(View.GONE);
//                lyt_order_detail.setVisibility(View.VISIBLE);
//                lyt_stock_detail.setVisibility(View.VISIBLE);
//                lytSales.setVisibility(View.VISIBLE);
//                offset = 0;
//                GetOrderData("");
//                return false;
//            }
//        });
//
//        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                GetOrderData(newText);
//                return true;
//            }
//        });

        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (AppController.isConnected(activity)) {
                    offset = 0;
                    GetOrderData("");
                }

                swipeRefresh.setRefreshing(false);
            }
        });


        drawerToggle = new ActionBarDrawerToggle
                (
                        this,
                        drawer, toolbar,
                        R.string.drawer_open,
                        R.string.drawer_close
                ) {
        };

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        updateFCMId();
                    }
                });
    }

    public void updateFCMId() {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.SELLER_ID, session.getData(Constant.ID));
        params.put(Constant.UPDATE_SELLER_FCM_ID, Constant.GetVal);
        params.put(Constant.FCM_ID, "" + AppController.getInstance().getDeviceToken());

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(boolean result, String response) {
            }
        }, activity, Constant.MAIN_URL, params, false);
    }

    public void getFinancialStatistics() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.SELLER_ID, session.getData(Constant.ID));
        params.put(Constant.GET_FINANCIAL_STATISTICS, Constant.GetVal);

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.getBoolean(Constant.ERROR)) {
                            tvOrdersCount.setText(jsonObject.getString(Constant.TOTAL_ORDERS));
                            tvProductsCount.setText(jsonObject.getString(Constant.TOTAL_PRODUCTS));
                            tvSoldOutCount.setText(jsonObject.getString(Constant.TOTAL_SOLD_OUT_PRODUCTS));
                            tvLowStockCount.setText(jsonObject.getString(Constant.TOTAL_LOW_STOCK_COUNT));
                            tvBalance.setText(ApiConfig.CountConvert(Float.parseFloat(jsonObject.getString(Constant.BALANCE))));
                            session.setData(Constant.BALANCE, jsonObject.getString(Constant.BALANCE));
                            Constant.SETTING_CURRENCY_SYMBOL = jsonObject.getString(Constant.CURRENCY);
                            tvWeeklySales.setText(ApiConfig.CountConvert(Float.parseFloat(jsonObject.getString(Constant.TOTAL_SALE))));
                            tvTitleWeeklySales.setText(getString(R.string.total_sale_title) + "(" + Constant.SETTING_CURRENCY_SYMBOL + ")");
                            GetOrderData("");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        GetOrderData("");
                    }
                }
            }
        }, activity, Constant.MAIN_URL, params, false);
    }

    void GetOrderData(String query) {
        recyclerView.setVisibility(View.GONE);
        orderTrackerArrayList = new ArrayList<>();

        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_ORDERS, Constant.GetVal);
        params.put(Constant.SELLER_ID, session.getData(Constant.ID));
        params.put(Constant.OFFSET, "" + offset);
        params.put(Constant.LIMIT, "" + Constant.LOAD_ITEM_LIMIT);
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            total = Integer.parseInt(objectbject.getString(Constant.TOTAL));
                            session.setData(Constant.TOTAL, String.valueOf(total));

                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                if (jsonObject1 != null) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    orderTrackerArrayList.add(ApiConfig.OrderTracker(jsonObject));
                                }
                            }
                            if (offset == 0) {
                                trackerAdapter = new TrackerAdapter(activity.getApplicationContext(), activity, orderTrackerArrayList);
                                recyclerView.setAdapter(trackerAdapter);
                                recyclerView.setVisibility(View.VISIBLE);
                                scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                                    private boolean isLoadMore;

                                    @Override
                                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                                        // if (diff == 0) {
                                        if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                                            if (orderTrackerArrayList.size() < total) {
                                                if (!isLoadMore) {
                                                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == orderTrackerArrayList.size() - 1) {
                                                        //bottom of list!
                                                        orderTrackerArrayList.add(null);
                                                        trackerAdapter.notifyItemInserted(orderTrackerArrayList.size() - 1);

                                                        offset += Constant.LOAD_ITEM_LIMIT;
                                                        Map<String, String> params = new HashMap<>();
                                                        params.put(Constant.GET_ORDERS, Constant.GetVal);
                                                        params.put(Constant.SELLER_ID, session.getData(Constant.ID));
                                                        params.put(Constant.OFFSET, "" + offset);
                                                        params.put(Constant.LIMIT, "" + Constant.LOAD_ITEM_LIMIT);

                                                        ApiConfig.RequestToVolley(new VolleyCallback() {
                                                            @Override
                                                            public void onSuccess(boolean result, String response) {

                                                                if (result) {
                                                                    try {
                                                                        // System.out.println("====product  " + response);
                                                                        JSONObject objectbject1 = new JSONObject(response);
                                                                        if (!objectbject1.getBoolean(Constant.ERROR)) {

                                                                            session.setData(Constant.TOTAL, objectbject1.getString(Constant.TOTAL));

                                                                            orderTrackerArrayList.remove(orderTrackerArrayList.size() - 1);
                                                                            trackerAdapter.notifyItemRemoved(orderTrackerArrayList.size());

                                                                            JSONObject object = new JSONObject(response);
                                                                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);

                                                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                                                                if (jsonObject1 != null) {
                                                                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                                                    orderTrackerArrayList.add(ApiConfig.OrderTracker(jsonObject));
                                                                                }
                                                                            }
                                                                            trackerAdapter.notifyDataSetChanged();
                                                                            trackerAdapter.setLoaded();
                                                                            isLoadMore = false;
                                                                        }
                                                                    } catch (JSONException e) {
                                                                        recyclerView.setVisibility(View.VISIBLE);
                                                                    }
                                                                }
                                                            }
                                                        }, activity, Constant.MAIN_URL, params, false);

                                                    }
                                                    isLoadMore = true;
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                }
            }
        }, activity, Constant.MAIN_URL, params, true);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(navigationView))
            drawer.closeDrawers();
        else
            doubleBack();
    }

    public void doubleBack() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.please_click_back_again_to_exit), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 1000);
    }

    @Override
    public void onResume() {
        try {
            if (orderTrackerArrayList.size() != 0 && orderTrackerArrayList != null) {
                orderListAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.toolbar_search) {
            if (orderListAdapter != null) {
                lyt_stock_detail.setVisibility(View.GONE);
                lyt_order_detail.setVisibility(View.GONE);
                lytSales.setVisibility(View.GONE);
                orderListAdapter.notifyDataSetChanged();
//                lytSearchview.setVisibility(View.VISIBLE);
//                searchview.setIconifiedByDefault(true);
//                searchview.setFocusable(true);
//                searchview.setIconified(false);
//                searchview.requestFocusFromTouch();
            }
        } else if (item.getItemId() == R.id.menu_logout) {
            session.logoutUserConfirmation(activity);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.toolbar_search).setVisible(false);
        menu.findItem(R.id.toolbar_filter).setVisible(false);
        invalidateOptionsMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
