package com.app.ybazzarseller.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import com.app.ybazzarseller.R;
import com.app.ybazzarseller.helper.Constant;
import com.app.ybazzarseller.helper.Session;
import com.app.ybazzarseller.ui.CircleTransform;

public class DrawerActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static TextView tvName;
    public NavigationView navigationView;
    public DrawerLayout drawer;
    public ActionBarDrawerToggle drawerToggle;
    public TextView tvMobile;
    protected FrameLayout frameLayout;
    Session session;
    LinearLayout lytProfile;
    ImageView imgProfile;
    Activity activity;

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        try {
            frameLayout = findViewById(R.id.content_frame);
            navigationView = findViewById(R.id.nav_view);
            drawer = findViewById(R.id.drawer_layout);
            View header = navigationView.getHeaderView(0);
            tvName = header.findViewById(R.id.header_name);
            tvMobile = header.findViewById(R.id.tvMobile);
            lytProfile = header.findViewById(R.id.lytProfile);
            imgProfile = header.findViewById(R.id.imgProfile);

            activity = DrawerActivity.this;

            session = new Session(activity);
            Picasso.get()
                    .load(session.getData(Constant.LOGO))
                    .fit()
                    .centerInside()
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(imgProfile);

            if (session.isUserLoggedIn()) {
                tvName.setText(session.getData(Constant.NAME));
                tvMobile.setText(session.getData(Constant.EMAIL));
                navigationView.getMenu().findItem(R.id.menu_customers).setVisible(session.getData(Constant.CUSTOMER_PRIVACY).equals("1"));
            } else {
                tvName.setText(getResources().getString(R.string.is_login));
            }
            setupNavigationDrawer();
        } catch (Exception ignore) {

        }
    }

    private void setupNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NotNull MenuItem menuItem) {
                drawer.closeDrawers();

                switch (menuItem.getItemId()) {
                    case R.id.menu_home:
                        startActivity(new Intent(DrawerActivity.this, MainActivity.class));
                        break;
                    case R.id.menu_orders:
                        startActivity(new Intent(DrawerActivity.this, OrderListActivity.class));
                        break;
                    case R.id.menu_customers:
                        startActivity(new Intent(DrawerActivity.this, CustomerListActivity.class));
                        break;
                    case R.id.wallet_transactions:
                        startActivity(new Intent(DrawerActivity.this, WalletTransactionsListActivity.class));
                        break;
                    case R.id.menu_products:
                        startActivity(new Intent(DrawerActivity.this, ProductListActivity.class).putExtra("from", "all_stock"));
                        break;
                    case R.id.menu_terms:
                        Intent terms = new Intent(DrawerActivity.this, WebViewActivity.class);
                        terms.putExtra("link", Constant.SELLER_TERMS);
                        terms.putExtra("title", getString(R.string.terms_conditions));
                        startActivity(terms);
                        break;
                    case R.id.menu_policy:
                        Intent privacy = new Intent(DrawerActivity.this, WebViewActivity.class);
                        privacy.putExtra("link", Constant.SELLER_POLICY);
                        privacy.putExtra("title", getString(R.string.privacy_policy));
                        startActivity(privacy);
                        break;
                    case R.id.menu_logout:
                        session.logoutUserConfirmation(activity);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }
}
