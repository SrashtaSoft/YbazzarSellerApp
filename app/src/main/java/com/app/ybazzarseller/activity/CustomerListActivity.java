package com.app.ybazzarseller.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.app.ybazzarseller.adapter.CustomerListAdapter;
import com.app.ybazzarseller.model.Customers;
import com.app.ybazzarseller.R;
import com.app.ybazzarseller.helper.ApiConfig;
import com.app.ybazzarseller.helper.Constant;
import com.app.ybazzarseller.helper.Session;
import com.app.ybazzarseller.helper.VolleyCallback;

public class CustomerListActivity extends AppCompatActivity {

    Activity activity;
    Session session;
    int offset = 0;
    LinearLayout lytSearchview;
    SwipeRefreshLayout swipeLayout;
    SearchView searchview;
    String query;
    private int total = 0;
    @Nullable
    private CustomerListAdapter customerListAdapter;
    private ArrayList<Customers> items;
    private RecyclerView recyclerView;
    private NestedScrollView scrollView;
    private boolean isLoadMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);
        activity = this;
        session = new Session(activity);

        recyclerView = findViewById(R.id.recyclerView);
        scrollView = findViewById(R.id.scrollView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        searchview = findViewById(R.id.searchview);
        lytSearchview = findViewById(R.id.lytSearchview);
        swipeLayout = findViewById(R.id.swipeLayout);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.customers));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            swipeLayout.setColorSchemeColors(activity.getColor(R.color.colorPrimary));
        }
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeLayout.setRefreshing(false);
                offset = 0;
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

        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                items.clear();
                GetData(newText);
                return true;
            }
        });


        GetData("");
    }

    private void GetData(final String query) {
        boolean progress = false;
        if (query.equals("")) {
            progress = true;
        }
        this.query = query;

        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.ID, session.getData(Constant.ID));
        params.put(Constant.GET_CUSTOMERS, Constant.GetVal);
        params.put(Constant.SEARCH, query);
        params.put(Constant.OFFSET, "" + offset);
        params.put(Constant.LIMIT, Constant.PRODUCT_LOAD_LIMIT);

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    customerListAdapter = null;
                    try {

                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            total = Integer.parseInt(objectbject.getString(Constant.TOTAL));

                            final JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            Gson g = new Gson();
                            items = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                if (jsonObject1 != null) {
                                    Customers item = g.fromJson(jsonObject1.toString(), Customers.class);
                                    items.add(item);
                                } else {
                                    break;
                                }

                            }
                            if (offset == 0) {
                                customerListAdapter = new CustomerListAdapter(activity, items);
                                recyclerView.setAdapter(customerListAdapter);
                                customerListAdapter.notifyDataSetChanged();
                                scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                                    @Override
                                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                                        // if (diff == 0) {
                                        if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                                            if (items.size() < total) {
                                                if (!isLoadMore) {
                                                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == items.size() - 1) {
                                                        //bottom of list!
                                                        items.add(null);
                                                        customerListAdapter.notifyItemInserted(items.size() - 1);

                                                        offset += Constant.LOAD_ITEM_LIMIT;

                                                        Map<String, String> params = new HashMap<>();
                                                        params.put(Constant.ID, session.getData(Constant.ID));
                                                        params.put(Constant.GET_CUSTOMERS, Constant.GetVal);
                                                        params.put(Constant.SEARCH, query);
                                                        params.put(Constant.LIMIT, "" + Constant.LOAD_ITEM_LIMIT);
                                                        params.put(Constant.OFFSET, "" + offset);

                                                        ApiConfig.RequestToVolley(new VolleyCallback() {
                                                            @Override
                                                            public void onSuccess(boolean result, String response) {
                                                                if (result) {
                                                                    try {
                                                                        // System.out.println("====product  " + response);
                                                                        JSONObject objectbject1 = new JSONObject(response);
                                                                        if (!objectbject1.getBoolean(Constant.ERROR)) {

                                                                            session.setData(Constant.TOTAL, objectbject1.getString(Constant.TOTAL));

                                                                            items.remove(items.size() - 1);
                                                                            customerListAdapter.notifyItemRemoved(items.size());

                                                                            JSONObject object = new JSONObject(response);
                                                                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);

                                                                            Gson g = new Gson();


                                                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                                                                if (jsonObject1 != null) {
                                                                                    Customers item = g.fromJson(jsonObject1.toString(), Customers.class);
                                                                                    items.add(item);
                                                                                } else {
                                                                                    break;
                                                                                }

                                                                            }
                                                                            customerListAdapter.notifyDataSetChanged();
                                                                            customerListAdapter.setLoaded();
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
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.MAIN_URL, params, progress);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.toolbar_search) {
            if (customerListAdapter != null) {
                items.clear();
                customerListAdapter.notifyDataSetChanged();
                lytSearchview.setVisibility(View.VISIBLE);
                searchview.setIconifiedByDefault(true);
                searchview.setFocusable(true);
                searchview.setIconified(false);
                searchview.requestFocusFromTouch();
            }
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
        menu.findItem(R.id.toolbar_search).setVisible(true);
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