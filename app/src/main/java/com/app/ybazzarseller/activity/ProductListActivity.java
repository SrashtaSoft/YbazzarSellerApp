package com.app.ybazzarseller.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.app.ybazzarseller.R;
import com.app.ybazzarseller.adapter.ProductLoadMoreAdapter;
import com.app.ybazzarseller.helper.ApiConfig;
import com.app.ybazzarseller.helper.AppController;
import com.app.ybazzarseller.helper.Constant;
import com.app.ybazzarseller.helper.Session;
import com.app.ybazzarseller.helper.VolleyCallback;
import com.app.ybazzarseller.model.Product;

public class ProductListActivity extends AppCompatActivity {
    public static ArrayList<Product> productArrayList;
    public static ProductLoadMoreAdapter mAdapter;
    Toolbar toolbar;
    int total;
    NestedScrollView scrollView;
    Activity activity;
    int offset = 0;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeLayout;
    String sortBy, filterBy;
    int sortIndex, filterIndex;
    TextView tvAlert;
    boolean isSort = false, isLoadMore = false;
    SearchView searchview;
    LinearLayout lytSearchview;
    String query = "", from;
    Session session;
    FloatingActionButton fabAddProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        toolbar = findViewById(R.id.toolbar);

        from = getIntent().getStringExtra("from");

        setSupportActionBar(toolbar);

        sortIndex = -1;
        filterIndex = -1;

        if (from.equals("all_stock")) {
            getSupportActionBar().setTitle(getString(R.string.products));
        } else if (from.equals("out_stock")) {
            filterIndex = 1;
            filterBy = Constant.SOLDOUT;
            getSupportActionBar().setTitle(getString(R.string.sold_out_products));
        } else if (from.equals("low_stock")) {
            filterIndex = 2;
            filterBy = Constant.LOWSTOCK;
            getSupportActionBar().setTitle(getString(R.string.low_stock_products));
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        activity = ProductListActivity.this;
        session = new Session(activity);

        productArrayList = new ArrayList<>();

        swipeLayout = findViewById(R.id.swipeLayout);
        tvAlert = findViewById(R.id.tvAlert);
        searchview = findViewById(R.id.searchview);
        lytSearchview = findViewById(R.id.lytSearchview);
        scrollView = findViewById(R.id.scrollView);
        fabAddProduct = findViewById(R.id.fabAddProduct);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        if (AppController.isConnected(activity)) {
            isSort = true;
            GetData(query);
        }

        swipeLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                offset = 0;
                productArrayList.clear();
                mAdapter.notifyDataSetChanged();
                swipeLayout.setRefreshing(false);
                GetData(query);
            }
        });

        searchview.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                lytSearchview.setVisibility(View.GONE);
                offset = 0;
                GetData("");
                return false;
            }
        });

        fabAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, ProductDetailActivity.class).putExtra("from", "new").putExtra("model", ""));
            }
        });

        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                productArrayList.clear();
                GetData(newText);
                return true;
            }
        });
    }

    void GetData(final String query) {
        productArrayList = new ArrayList<>();
        boolean progress = false;
        if (query.equals("")) {
            progress = true;
        }
        this.query = query;

        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_PRODUCTS, Constant.GetVal);
        params.put(Constant.SELLER_ID, session.getData(Constant.ID));
        params.put(Constant.SEARCH, query);
        if (sortIndex != -1) {
            params.put(Constant.SORT, sortBy);
        }
        if (filterIndex != -1) {
            params.put(Constant.FILTER, filterBy);
        } else {
            params.put(Constant.LIMIT, "" + Constant.LOAD_ITEM_LIMIT);
            params.put(Constant.OFFSET, "" + offset);
        }

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {

                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            total = Integer.parseInt(objectbject.getString(Constant.TOTAL));
                            if (offset == 0) {
                                productArrayList = new ArrayList<>();
                                recyclerView.setVisibility(View.VISIBLE);
                                tvAlert.setVisibility(View.GONE);
                            }
                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    if (jsonObject != null) {
                                        Product product = new Gson().fromJson(jsonObject.toString(), Product.class);
                                        productArrayList.add(product);
                                    } else {
                                        break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (offset == 0) {
                                mAdapter = new ProductLoadMoreAdapter(activity, productArrayList, R.layout.lyt_item_list);
                                mAdapter.setHasStableIds(true);
                                recyclerView.setAdapter(mAdapter);

                                scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                                    @Override
                                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                                        if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                                            if (productArrayList.size() < total) {
                                                if (!isLoadMore) {
                                                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == productArrayList.size() - 1) {
                                                        //bottom of list!
                                                        productArrayList.add(null);
                                                        mAdapter.notifyItemInserted(productArrayList.size() - 1);

                                                        offset += Constant.LOAD_ITEM_LIMIT;
                                                        Map<String, String> params = new HashMap<>();
                                                        params.put(Constant.GET_PRODUCTS, Constant.GetVal);
                                                        params.put(Constant.SELLER_ID, session.getData(Constant.ID));
                                                        params.put(Constant.SEARCH, query);
                                                        if (sortIndex != -1) {
                                                            params.put(Constant.SORT, sortBy);
                                                        }
                                                        if (filterIndex != -1) {
                                                            params.put(Constant.FILTER, filterBy);
                                                        } else {
                                                            params.put(Constant.LIMIT, "" + Constant.LOAD_ITEM_LIMIT);
                                                            params.put(Constant.OFFSET, "" + offset);
                                                        }

                                                        ApiConfig.RequestToVolley(new VolleyCallback() {
                                                            @Override
                                                            public void onSuccess(boolean result, String response) {
                                                                if (result) {
                                                                    try {
                                                                        productArrayList.remove(productArrayList.size() - 1);
                                                                        mAdapter.notifyItemRemoved(productArrayList.size());
                                                                        JSONObject objectbject = new JSONObject(response);
                                                                        if (!objectbject.getBoolean(Constant.ERROR)) {

                                                                            JSONObject object = new JSONObject(response);
                                                                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);


                                                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                                                try {
                                                                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                                                    if (jsonObject != null) {
                                                                                        Product product = new Gson().fromJson(jsonObject.toString(), Product.class);
                                                                                        productArrayList.add(product);
                                                                                    } else {
                                                                                        break;
                                                                                    }
                                                                                } catch (JSONException e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                            mAdapter.notifyDataSetChanged();
                                                                            mAdapter.setLoaded();
                                                                            isLoadMore = false;
                                                                        }
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }
                                                        }, activity, Constant.MAIN_URL, params, false);
                                                        isLoadMore = true;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            tvAlert.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.MAIN_URL, params, progress);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_sort:
                if (isSort) {
                    if (item.getItemId() == R.id.toolbar_sort) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle(activity.getResources().getString(R.string.sortby));
                        builder.setSingleChoiceItems(Constant.sortvalues, sortIndex, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item1) {
                                sortIndex = item1;
                                switch (item1) {
                                    case 0:
                                        sortBy = Constant.NEW;
                                        break;
                                    case 1:
                                        sortBy = Constant.OLD;
                                        break;
                                    case 2:
                                        sortBy = Constant.HIGH;
                                        break;
                                    case 3:
                                        sortBy = Constant.LOW;
                                        break;
                                }
                                if (item1 != -1) {
                                    GetData(query);
                                }
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }
                break;
            case R.id.toolbar_filter:
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(activity.getResources().getString(R.string.filterby));
                builder.setSingleChoiceItems(Constant.filtervalues, filterIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item1) {
                        filterIndex = item1;
                        switch (item1) {
                            case 0:
                                filterIndex = -1;
                                break;
                            case 1:
                                filterBy = Constant.SOLDOUT;
                                break;
                            case 2:
                                filterBy = Constant.LOWSTOCK;
                                break;
                        }
                        if (item1 != -1) {
                            GetData(query);
                        }
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
            case R.id.toolbar_search:
                if (mAdapter != null) {
                    productArrayList.clear();
                    mAdapter.notifyDataSetChanged();
                    lytSearchview.setVisibility(View.VISIBLE);
                    searchview.setIconifiedByDefault(true);
                    searchview.setFocusable(true);
                    searchview.setIconified(false);
                    searchview.requestFocusFromTouch();
                }
                break;
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.toolbar_search).setVisible(true);
        menu.findItem(R.id.toolbar_sort).setVisible(true);
        menu.findItem(R.id.toolbar_filter).setVisible(true);
        invalidateOptionsMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}