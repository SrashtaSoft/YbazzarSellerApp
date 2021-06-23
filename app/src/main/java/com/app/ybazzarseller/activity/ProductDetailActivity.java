package com.app.ybazzarseller.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.app.ybazzarseller.R;
import com.app.ybazzarseller.adapter.Adapter;
import com.app.ybazzarseller.adapter.PinCodeAdapter;
import com.app.ybazzarseller.adapter.ProductImagesAdapter;
import com.app.ybazzarseller.adapter.ProductItemAdapter;
import com.app.ybazzarseller.helper.AndroidMultiPartEntity;
import com.app.ybazzarseller.helper.ApiConfig;
import com.app.ybazzarseller.helper.Constant;
import com.app.ybazzarseller.helper.Session;
import com.app.ybazzarseller.helper.VolleyCallback;
import com.app.ybazzarseller.helper.album.Action;
import com.app.ybazzarseller.helper.album.Album;
import com.app.ybazzarseller.helper.album.AlbumFile;
import com.app.ybazzarseller.helper.album.api.widget.Widget;
import com.app.ybazzarseller.helper.album.impl.OnItemClickListener;
import com.app.ybazzarseller.helper.album.widget.divider.Api21ItemDivider;
import com.app.ybazzarseller.helper.album.widget.divider.Divider;
import com.app.ybazzarseller.model.Categories;
import com.app.ybazzarseller.model.OrderStatus;
import com.app.ybazzarseller.model.PinCode;
import com.app.ybazzarseller.model.PriceVariation;
import com.app.ybazzarseller.model.Product;
import com.app.ybazzarseller.model.SubCategories;
import com.app.ybazzarseller.model.Tax;
import com.app.ybazzarseller.model.Unit;

public class ProductDetailActivity extends AppCompatActivity {

    private static final int RESULT_CODE_SINGLE = 1;
    private static final int RESULT_CODE_MULTI = 2;
    static Product product;
    Activity activity;
    Session session;
    Toolbar toolbar;
    EditText edtManufacturer, edtMadeIn, edtReturnDays, edtDescription;
    TextView tvMainImageName, tvOtherImagesName, edtProductName, btnMainImage, btnOtherImages, btnEditDoneDescription;
    Spinner spinnerProductType, spinnerDeliveryPlaces, spinnerTax, spinnerTillStatus, spinnerCategory, spinnerSubCategory;
    String from;
    SwitchCompat switchIsReturnable, switchIsCancellable;
    TextInputLayout lytReturnDays;
    RelativeLayout lytTillStatus;
    ProductItemAdapter productItemAdapter;
    PinCodeAdapter pinCodeAdapter;
    RecyclerView recyclerView, recyclerViewSelectedPinCodes, recyclerViewImageGallery, recycleviewimglist;
    RadioButton rdPacket, rdLoose;
    RelativeLayout lytProgressBar;
    CategoryAdapter categoryAdapter;
    SubCategoryAdapter subCategoryAdapter;
    StatusAdapter orderStatusAdapter;
    ArrayList<String> arrayListStockStatus, arrayListDeliveryPlaces;
    ArrayList<PriceVariation> priceVariations;
    ArrayList<Unit> arrayListUnit;
    ArrayList<Categories> arrayListCategories;
    ArrayList<Tax> arrayListTax;
    ArrayList<PinCode> arrayListPinCode, arrayListSelectedPinCode;
    ArrayList<String> pinCodesIds;
    ArrayList<OrderStatus> orderStatuses;
    Button btnAddUpdate;
    Button lytSelectedPinCodes;
    ImageView imgMainImage;
    ProductImagesAdapter productImagesAdapter;
    WebView webView;
    File mainFile;
    public static String productType = "";
    LinearLayout albumLyt;
    private ArrayList<AlbumFile> mAlbumFiles;
    private Adapter mAdapter;
    LinearLayout lytStock;
    EditText edtStock;
    String looseStockId;
    Spinner spinnerMeasurement;
    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        activity = ProductDetailActivity.this;
        session = new Session(activity);

        toolbar = findViewById(R.id.toolbar);
        edtProductName = findViewById(R.id.edtProductName);
        edtManufacturer = findViewById(R.id.edtManufacturer);
        edtMadeIn = findViewById(R.id.edtMadeIn);
        edtReturnDays = findViewById(R.id.edtReturnDays);
        spinnerProductType = findViewById(R.id.spinnerProductType);
        spinnerDeliveryPlaces = findViewById(R.id.spinnerDeliveryPlaces);
        spinnerTillStatus = findViewById(R.id.spinnerTillStatus);
        spinnerTax = findViewById(R.id.spinnerTax);
        switchIsReturnable = findViewById(R.id.switchIsReturnable);
        switchIsCancellable = findViewById(R.id.switchIsCancellable);
        lytReturnDays = findViewById(R.id.lytReturnDays);
        lytTillStatus = findViewById(R.id.lytTillStatus);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerViewSelectedPinCodes = findViewById(R.id.recyclerViewSelectedPinCodes);
        rdPacket = findViewById(R.id.rdPacket);
        rdLoose = findViewById(R.id.rdLoose);
        lytProgressBar = findViewById(R.id.lytProgressBar);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerSubCategory = findViewById(R.id.spinnerSubCategory);
        btnAddUpdate = findViewById(R.id.btnAddUpdate);
        lytSelectedPinCodes = findViewById(R.id.lytSelectedPinCodes);
        btnMainImage = findViewById(R.id.btnMainImage);
        btnOtherImages = findViewById(R.id.btnOtherImages);
        tvMainImageName = findViewById(R.id.tvMainImageName);
        tvOtherImagesName = findViewById(R.id.tvOtherImagesName);
        recyclerViewImageGallery = findViewById(R.id.recyclerViewImageGallery);
        imgMainImage = findViewById(R.id.imgMainImage);
        webView = findViewById(R.id.webView);
        edtDescription = findViewById(R.id.edtDescription);
        btnEditDoneDescription = findViewById(R.id.btnEditDoneDescription);
        albumLyt = findViewById(R.id.albumLyt);
        lytStock = findViewById(R.id.lytStock);
        spinnerMeasurement = findViewById(R.id.spinnerMeasurement);
        edtStock = findViewById(R.id.edtStock);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.product_detail));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lytProgressBar.setVisibility(View.VISIBLE);
        btnEditDoneDescription.setText(getString(R.string.edit));

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(getString(R.string.select_product_type));
        arrayList.add(getString(R.string.veg));
        arrayList.add(getString(R.string.non_veg));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProductType.setAdapter(arrayAdapter);

        arrayListDeliveryPlaces = new ArrayList<>();
        arrayListDeliveryPlaces.add(getString(R.string.city_included));
        arrayListDeliveryPlaces.add(getString(R.string.city_excluded));
        arrayListDeliveryPlaces.add(getString(R.string.includes_all));
        ArrayAdapter<String> arrayAdapterDeliveryPlaces = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayListDeliveryPlaces);
        arrayAdapterDeliveryPlaces.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDeliveryPlaces.setAdapter(arrayAdapterDeliveryPlaces);

        arrayListUnit = new ArrayList<>();
        mAlbumFiles = new ArrayList<>();
        priceVariations = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewSelectedPinCodes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewImageGallery.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recycleviewimglist = findViewById(R.id.recycleviewimglist);
        recycleviewimglist.setLayoutManager(new GridLayoutManager(this, 3));
        Divider divider = new Api21ItemDivider(Color.TRANSPARENT, 10, 10);
        recycleviewimglist.addItemDecoration(divider);
        recycleviewimglist.setNestedScrollingEnabled(false);
        mAdapter = new Adapter(this, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                previewImage(position);
            }
        });

        recycleviewimglist.setAdapter(mAdapter);
        from = getIntent().getStringExtra("from");
        pinCodesIds = new ArrayList<>();
        assert from != null;
        if (from.equals("product")) {
            product = (Product) getIntent().getSerializableExtra("model");
            position = Integer.parseInt(String.valueOf(getIntent().getSerializableExtra("position")));
            priceVariations = product.getVariants();

            btnAddUpdate.setText(activity.getString(R.string.update_product));

            Picasso.get().
                    load(product.getImage())
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(imgMainImage);

            productImagesAdapter = new ProductImagesAdapter(activity, product.getOther_images(), "api", product.getId());
            recyclerViewImageGallery.setAdapter(productImagesAdapter);

            switch (product.getDelivery_places()) {
                case "0":
                    spinnerDeliveryPlaces.setSelection(0);
                    break;
                case "1":
                    spinnerDeliveryPlaces.setSelection(1);
                    break;
                case "2":
                    spinnerDeliveryPlaces.setSelection(2);
                    break;
            }

            productType = product.getType();

            edtProductName.setText(product.getName());
            edtManufacturer.setText(product.getManufacturer());
            edtMadeIn.setText(product.getMade_in());
            edtReturnDays.setText(product.getReturn_days());

            if (product.getReturn_status().equals("1")) {
                switchIsReturnable.setChecked(true);
                lytReturnDays.setVisibility(View.VISIBLE);
                edtReturnDays.setText(product.getReturn_days());
            }

            if (product.getCancelable_status().equals("1")) {
                switchIsCancellable.setChecked(true);
                lytTillStatus.setVisibility(View.VISIBLE);
            }

            if (!product.getDelivery_places().equals("2")) {
                pinCodesIds.addAll(Arrays.asList(product.getPincodes().split(",")));
            }

            webView.loadData(product.getDescription(), "text/html; charset=utf-8", "utf-8");
            edtDescription.setText(product.getDescription());

            if (product.getDelivery_places().equals("2")) {
                recyclerViewSelectedPinCodes.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
            } else {
                recyclerViewSelectedPinCodes.setBackgroundColor(ContextCompat.getColor(activity, R.color.gray));
            }


        } else {
            switchIsCancellable.setTag("off");
            switchIsReturnable.setTag("off");
            btnAddUpdate.setText(activity.getString(R.string.add_product));
            priceVariations.add(new PriceVariation());
        }
        productType = rdPacket.isChecked() ? "packet" : "loose";


        if (productType.equals("packet")) {
            lytStock.setVisibility(View.GONE);
            rdPacket.setChecked(true);
            productType = "packet";
        } else {
            lytStock.setVisibility(View.VISIBLE);
            rdLoose.setChecked(true);
            productType = "loose";
        }

        rdLoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rdLoose.setChecked(true);
                productType = "loose";
                lytStock.setVisibility(View.VISIBLE);
                productItemAdapter.notifyDataSetChanged();

            }
        });
        rdPacket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rdPacket.setChecked(true);
                productType = "packet";
                lytStock.setVisibility(View.GONE);
                productItemAdapter.notifyDataSetChanged();
            }
        });
        switchIsReturnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (from.equals("product")) {
                    if (product.getReturn_status().equals("1")) {
                        product.setReturn_status("0");
                        lytReturnDays.setVisibility(View.GONE);
                    } else if (product.getReturn_status().equals("0")) {
                        product.setReturn_status("1");
                        lytReturnDays.setVisibility(View.VISIBLE);
                        edtReturnDays.setText(product.getReturn_days());
                    }
                } else {
                    if (switchIsReturnable.getTag().equals("on")) {
                        lytReturnDays.setVisibility(View.GONE);
                        switchIsReturnable.setChecked(false);
                        switchIsReturnable.setTag("off");

                    } else {
                        lytReturnDays.setVisibility(View.VISIBLE);
                        switchIsReturnable.setChecked(true);
                        switchIsReturnable.setTag("on");
                    }
                }
            }
        });

        switchIsCancellable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (from.equals("product")) {
                    if (product.getCancelable_status().equals("1")) {
                        product.setCancelable_status("0");
                        lytTillStatus.setVisibility(View.GONE);
                    } else if (product.getCancelable_status().equals("0")) {
                        product.setCancelable_status("1");
                        lytTillStatus.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (switchIsCancellable.getTag().equals("on")) {
                        lytTillStatus.setVisibility(View.GONE);
                        switchIsCancellable.setChecked(false);
                        switchIsCancellable.setTag("off");
                    } else {
                        lytTillStatus.setVisibility(View.VISIBLE);
                        switchIsCancellable.setChecked(true);
                        switchIsCancellable.setTag("on");
                    }
                }
            }
        });

        btnEditDoneDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnEditDoneDescription.getTag().equals("edit")) {
                    btnEditDoneDescription.setTag("done");
                    btnEditDoneDescription.setText(getString(R.string.done));
                    webView.setVisibility(View.GONE);
                    edtDescription.setVisibility(View.VISIBLE);
                } else {
                    btnEditDoneDescription.setTag("edit");
                    btnEditDoneDescription.setText(getString(R.string.edit));
                    edtDescription.setVisibility(View.GONE);
                    webView.loadData(edtDescription.getText().toString(), "text/html; charset=utf-8", "utf-8");
                    webView.setVisibility(View.VISIBLE);
                }
            }
        });

        btnMainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage("single");
            }
        });

        btnOtherImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                albumLyt.setVisibility(View.VISIBLE);
                SelectImage("multi");
            }
        });

        spinnerDeliveryPlaces.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 2) {
                    recyclerViewSelectedPinCodes.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                } else {
                    recyclerViewSelectedPinCodes.setBackgroundColor(ContextCompat.getColor(activity, R.color.gray));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnAddUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifiedAddProducts();
            }
        });

        GetCategory();
    }

    public void verifiedAddProducts() {
        String pName = edtProductName.getText().toString();
        String description = edtDescription.getText().toString();
        if (pName.isEmpty()) {
            Toast.makeText(activity, getString(R.string.error_msg_product_name), Toast.LENGTH_SHORT).show();
        } else if (description.isEmpty()) {
            Toast.makeText(activity, getString(R.string.error_msg_product_description), Toast.LENGTH_SHORT).show();
        } else {
            if (productType.equals("loose") && edtStock.getText().toString().isEmpty()) {
                Toast.makeText(activity, getString(R.string.error_msg_stock), Toast.LENGTH_SHORT).show();
            } else if (spinnerDeliveryPlaces.getSelectedItemPosition() != 2 && arrayListSelectedPinCode.size() == 0) {
                Toast.makeText(activity, getString(R.string.error_msg_pincodes), Toast.LENGTH_SHORT).show();
            } else {
                if (from.equals("product")) {
                    new AddOrUpdateProduct().execute();
                } else {
                    if (mainFile == null) {
                        Toast.makeText(activity, getString(R.string.error_msg_product_image), Toast.LENGTH_SHORT).show();
                    } else if (mAlbumFiles.size() == 0) {
                        Toast.makeText(activity, getString(R.string.error_msg_extra_product_image), Toast.LENGTH_SHORT).show();
                    } else {
                        new AddOrUpdateProduct().execute();
                    }
                }
            }
        }
    }


    public void SelectImage(String type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                if (type.equals("single")) {
                    startActivityForResult(intent, RESULT_CODE_SINGLE);
                } else if (type.equals("multi")) {
                    selectOtherImage();
                }


            }
        }
    }

    private void previewImage(int position) {
        if (mAlbumFiles == null || mAlbumFiles.size() == 0) {
            Toast.makeText(this, R.string.no_selected, Toast.LENGTH_LONG).show();
        } else {
            Album.galleryAlbum(this)
                    .checkable(true)
                    .checkedList(mAlbumFiles)
                    .currentPosition(position)
                    .widget(Widget.newDarkBuilder(this)
                            .title(toolbar.getTitle().toString()).build()
                    )
                    .onResult(new Action<ArrayList<AlbumFile>>() {
                        @Override
                        public void onAction(@NonNull ArrayList<AlbumFile> result) {
                            mAlbumFiles = result;
                            mAdapter.notifyDataSetChanged(mAlbumFiles);
                            //mTvMessage.setVisibility(result.size() > 0 ? View.VISIBLE : View.GONE);
                        }
                    })
                    .start();
        }
    }

    private void selectOtherImage() {
        Album.image(activity)
                .multipleChoice()
                .camera(true)
                .columnCount(2)
                .selectCount(6)
                .checkedList(mAlbumFiles)
                .widget(Widget.newDarkBuilder(activity)
                        .title(toolbar.getTitle().toString())
                        .build()
                )
                .onResult(new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction(@NonNull ArrayList<AlbumFile> result) {
                        mAlbumFiles = result;
                        mAdapter.notifyDataSetChanged(mAlbumFiles);
                        //mTvMessage.setVisibility(result.size() > 0 ? View.VISIBLE : View.GONE);
                    }
                })
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
                        //Toast.makeText(AddBusinessActivity.this, "Cancel", Toast.LENGTH_LONG).show();
                    }
                })
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CODE_SINGLE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            System.out.println("=====filepath " + picturePath);
            cursor.close();
            mainFile = new File(picturePath);
            imgMainImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }

    public void GetCategory() {
        arrayListCategories = new ArrayList<>();
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.SELLER_ID, session.getData(Constant.ID));
        params.put(Constant.GET_CATEGORIES, Constant.GetVal);
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.getBoolean(Constant.ERROR)) {
                            for (int i = 0; i < jsonObject.getJSONArray(Constant.DATA).length(); i++) {
                                Categories categories = new Gson().fromJson(jsonObject.getJSONArray(Constant.DATA).getJSONObject(i).toString(), Categories.class);
                                arrayListCategories.add(categories);
                            }

                            categoryAdapter = new CategoryAdapter(activity, arrayListCategories, spinnerCategory, spinnerSubCategory);
                            spinnerCategory.setAdapter(categoryAdapter);

                            GetUnits();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.MAIN_URL, params, false);
    }

    public void GetUnits() {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.GET_UNITS, Constant.GetVal);
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.getBoolean(Constant.ERROR)) {
                            for (int i = 0; i < jsonObject.getJSONArray(Constant.DATA).length(); i++) {
                                Unit unit = new Unit(jsonObject.getJSONArray(Constant.DATA).getJSONObject(i).getString(Constant.ID), jsonObject.getJSONArray(Constant.DATA).getJSONObject(i).getString(Constant.NAME));
                                arrayListUnit.add(unit);

                            }

                            GetTaxes();
                            LooseStockUnitAdapter unitStockAdapter = new LooseStockUnitAdapter(activity, arrayListUnit);
                            spinnerMeasurement.setAdapter(unitStockAdapter);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.MAIN_URL, params, false);
    }

    public void GetTaxes() {
        arrayListTax = new ArrayList<>();
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.GET_TAXES, Constant.GetVal);
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Tax tax1 = new Tax("0", activity.getString(R.string.select_tax), "0");
                        arrayListTax.add(tax1);
                        if (!jsonObject.getBoolean(Constant.ERROR)) {
                            for (int i = 0; i < jsonObject.getJSONArray(Constant.DATA).length(); i++) {
                                Tax tax = new Gson().fromJson(jsonObject.getJSONArray(Constant.DATA).getJSONObject(i).toString(), Tax.class);
                                arrayListTax.add(tax);
                            }
                        }
                        TaxAdapter taxAdapter = new TaxAdapter(activity, arrayListTax, spinnerTax);
                        spinnerTax.setAdapter(taxAdapter);
                        GetPinCodes();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.MAIN_URL, params, false);
    }

    public void GetPinCodes() {
        if (pinCodesIds != null && pinCodesIds.size() <= 0) {
            pinCodesIds = new ArrayList<>();
        }
        arrayListPinCode = new ArrayList<>();
        arrayListSelectedPinCode = new ArrayList<>();
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.GET_PINCODES, Constant.GetVal);
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.getBoolean(Constant.ERROR)) {
                            for (int i = 0; i < jsonObject.getJSONArray(Constant.DATA).length(); i++) {
                                PinCode pinCode = new Gson().fromJson(jsonObject.getJSONArray(Constant.DATA).getJSONObject(i).toString(), PinCode.class);
                                arrayListPinCode.add(pinCode);
                                if (pinCodesIds != null && pinCodesIds.size() > 0) {
                                    for (int j = 0; j < pinCodesIds.size(); j++) {
                                        if (arrayListPinCode.get(i).getId().equals(pinCodesIds.get(j))) {
                                            arrayListSelectedPinCode.add(pinCode);
                                            break;
                                        }
                                    }
                                }
                            }
                            if (from.equals("product") && !product.getDelivery_places().equals("all")) {
                                pinCodeAdapter = new PinCodeAdapter(activity, arrayListSelectedPinCode);
                                recyclerViewSelectedPinCodes.setAdapter(pinCodeAdapter);
                            }

                            TaxAdapter taxAdapter = new TaxAdapter(activity, arrayListTax, spinnerTax);
                            spinnerTax.setAdapter(taxAdapter);

                            orderStatuses = new ArrayList<>();
                            orderStatuses.add(new OrderStatus(Constant.RECEIVED, activity.getString(R.string.received)));
                            orderStatuses.add(new OrderStatus(Constant.PROCESSED, activity.getString(R.string.processed)));
                            orderStatuses.add(new OrderStatus(Constant.SHIPPED, activity.getString(R.string.shipped)));
                            orderStatusAdapter = new StatusAdapter(activity, orderStatuses, spinnerTillStatus);
                            spinnerTillStatus.setAdapter(orderStatusAdapter);

                            arrayListStockStatus = new ArrayList<>();
                            arrayListStockStatus.add(getString(R.string.available));
                            arrayListStockStatus.add(getString(R.string.sold_out));

                            try {
                                taxAdapter.setItem(product.getTax_id());
                                if (!product.getTill_status().equals("")) {
                                    orderStatusAdapter.setItem(product.getTill_status());
                                }

                                categoryAdapter.setItem(product.getCategory_id(), product.getSubcategory_id());
                            } catch (Exception ignore) {

                            }

                        }
                        recyclerView.setNestedScrollingEnabled(false);
                        productItemAdapter = new ProductItemAdapter(activity, priceVariations, arrayListUnit, arrayListStockStatus, from);
                        recyclerView.setAdapter(productItemAdapter);

                        lytSelectedPinCodes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (spinnerDeliveryPlaces.getSelectedItemPosition() != 2) {
                                    String[] listItems = new String[arrayListPinCode.size()];

                                    for (int i = 0; i < arrayListPinCode.size(); i++) {
                                        listItems[i] = arrayListPinCode.get(i).getPincode();
                                    }

                                    boolean[] checkedItems = new boolean[arrayListPinCode.size()]; //this will checked the items when user open the dialog
                                    try {
                                        for (int i = 0; i < checkedItems.length; i++) {
                                            for (int j = 0; j < pinCodesIds.size(); j++) {
                                                if (arrayListPinCode.get(i).getId().equals(pinCodesIds.get(j))) {
                                                    checkedItems[i] = true;
                                                    break;
                                                }
                                            }
                                        }
                                    } catch (Exception ignore) {

                                    }

                                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);
                                    mBuilder.setTitle(activity.getString(R.string.selected_pincodes));
                                    mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                                            if (isChecked) {
                                                pinCodesIds.add(arrayListPinCode.get(position).getId());
                                                arrayListSelectedPinCode.add(arrayListPinCode.get(position));
                                            } else {
                                                pinCodesIds.remove(arrayListPinCode.get(position).getId());
                                                arrayListSelectedPinCode.remove(arrayListPinCode.get(position));
                                            }
                                        }
                                    });

                                    mBuilder.setCancelable(true);
                                    mBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int which) {
                                            pinCodeAdapter.notifyDataSetChanged();
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    pinCodeAdapter = new PinCodeAdapter(activity, arrayListSelectedPinCode);
                                    recyclerViewSelectedPinCodes.setAdapter(pinCodeAdapter);
                                    AlertDialog mDialog = mBuilder.create();
                                    mDialog.show();
                                }
                            }
                        });

                        lytProgressBar.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        lytProgressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.MAIN_URL, params, false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public static class TaxAdapter extends BaseAdapter {
        final Context context;
        final ArrayList<Tax> taxes;
        final LayoutInflater inflter;
        Spinner spinnerTax;


        public TaxAdapter(Context applicationContext, ArrayList<Tax> taxes, Spinner spinnerTax) {
            this.context = applicationContext;
            this.taxes = taxes;
            this.spinnerTax = spinnerTax;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return taxes.size();
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
                if (id.equals(taxes.get(i).getId())) {
                    spinnerTax.setSelection(i);
                }
            }
        }

        @SuppressLint({"SetTextI18n", "ViewHolder", "InflateParams"})
        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.lyt_spinner_item, null);
            TextView txtmeasurement = view.findViewById(R.id.txtmeasurement);
            TextView txtprice = view.findViewById(R.id.txtprice);

            Tax tax = taxes.get(position);
            txtmeasurement.setText(tax.getTitle());
            txtprice.setText(tax.getPercentage() + "%");

            spinnerTax.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    try {
                        product.setTax_id(tax.getId());
                        product.setTax_percentage(tax.getPercentage());
                        product.setTax_title(tax.getTitle());
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

    public static class StatusAdapter extends BaseAdapter {
        final Context context;
        final ArrayList<OrderStatus> statuses;
        final LayoutInflater inflter;
        Spinner spinnerTillStatus;

        public StatusAdapter(Context applicationContext, ArrayList<OrderStatus> statuses, Spinner spinnerTillStatus) {
            this.context = applicationContext;
            this.statuses = statuses;
            this.spinnerTillStatus = spinnerTillStatus;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return statuses.size();
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
                if (id.equals(statuses.get(i).getStatusName())) {
                    spinnerTillStatus.setSelection(i);
                }
            }
        }

        @SuppressLint({"SetTextI18n", "ViewHolder", "InflateParams"})
        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.lyt_spinner_item, null);
            TextView txtmeasurement = view.findViewById(R.id.txtmeasurement);

            OrderStatus orderStatus = statuses.get(position);
            txtmeasurement.setText(orderStatus.getDisplayName());

            spinnerTillStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    try {
                        product.setTill_status(orderStatus.getStatusName());
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

    public static class SubCategoryAdapter extends BaseAdapter {
        final Context context;
        final ArrayList<SubCategories> subCategories;
        final LayoutInflater inflter;
        Spinner spinnerSubCategory;

        public SubCategoryAdapter(Context applicationContext, ArrayList<SubCategories> subCategories, Spinner spinnerSubCategory) {
            this.context = applicationContext;
            this.subCategories = subCategories;
            this.spinnerSubCategory = spinnerSubCategory;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return subCategories.size();
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
                if (id.equals(subCategories.get(i).getId())) {
                    spinnerSubCategory.setSelection(i);
                }
            }
        }

        @SuppressLint({"SetTextI18n", "ViewHolder", "InflateParams"})
        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.lyt_spinner_item, null);
            TextView txtmeasurement = view.findViewById(R.id.txtmeasurement);

            txtmeasurement.setText(subCategories.get(position).getName());

            return view;
        }
    }

    class AddOrUpdateProduct extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            lytProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return AddOrUpdate();
        }

        @SuppressWarnings("deprecation")
        String AddOrUpdate() {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            String responseString = null;
            try {

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Constant.MAIN_URL);

                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {
                            @Override
                            public void transferred(long num) {

                            }
                        });

                StringBuilder id = new StringBuilder();
                StringBuilder measurement = new StringBuilder();
                StringBuilder measurement_unit_id = new StringBuilder();
                StringBuilder price = new StringBuilder();
                StringBuilder discounted_price = new StringBuilder();
                StringBuilder serve_for = new StringBuilder();
                StringBuilder stock = new StringBuilder();
                StringBuilder stock_unit_id = new StringBuilder();
                StringBuilder pincodes_ = new StringBuilder();

                for (int i = 0; i < priceVariations.size(); i++) {
                    PriceVariation priceVariation = priceVariations.get(i);
                    if (i == 0) {
                        if (from.equals("product")) {
                            id = priceVariation.getId() == null ? new StringBuilder("0") : new StringBuilder(priceVariation.getId());
                        }
                        if (productType.equals("packet")) {
                            stock = new StringBuilder(priceVariation.getStock());
                            stock_unit_id = new StringBuilder(priceVariation.getStock_unit_id());
                        }
                        measurement = new StringBuilder(priceVariation.getMeasurement());
                        measurement_unit_id = new StringBuilder(priceVariation.getMeasurement_unit_id());
                        price = new StringBuilder(priceVariation.getPrice());
                        discounted_price = new StringBuilder(priceVariation.getDiscounted_price());
                        serve_for = new StringBuilder(priceVariation.getServe_for());
                    } else {
                        if (from.equals("product")) {
                            id.append(",").append(priceVariation.getId() == null ? "0" : priceVariation.getId());
                        }
                        if (productType.equals("packet")) {
                            stock.append(",").append(priceVariation.getStock());
                            stock_unit_id.append(",").append(priceVariation.getStock_unit_id());
                        }
                        measurement.append(",").append(priceVariation.getMeasurement());
                        measurement_unit_id.append(",").append(priceVariation.getMeasurement_unit_id());
                        price.append(",").append(priceVariation.getPrice());
                        discounted_price.append(",").append(priceVariation.getDiscounted_price());
                        serve_for.append(",").append(priceVariation.getServe_for().trim());
                    }
                }

                if (spinnerDeliveryPlaces.getSelectedItemPosition() != 2) {
                    for (int i = 0; i < arrayListSelectedPinCode.size(); i++) {
                        if (i == 0)
                            pincodes_ = new StringBuilder(arrayListSelectedPinCode.get(i).getId());
                        else {
                            pincodes_.append(",").append(arrayListSelectedPinCode.get(i).getId());
                        }
                    }
                    entity.addPart(Constant.PINCODES, setValue(pincodes_.toString()));
                } else {
                    entity.addPart(Constant.PINCODES, setValue(""));
                }
                System.out.println(">>>>>>pincodes_" + pincodes_);
//                // Adding file data to http body
                entity.addPart(Constant.AccessKey, setValue(Constant.AccessKeyVal));
                if (from.equals("product")) {
                    entity.addPart(Constant.UPDATE_PRODUCTS, setValue(Constant.GetVal));
                    entity.addPart(Constant.ID, setValue(product.getId()));
                    entity.addPart(Constant.PRODUCT_VARIANT_ID, setValue(id.toString()));
                } else {
                    entity.addPart(Constant.ADD_PRODUCTS, setValue(Constant.GetVal));
                }
                entity.addPart(Constant.DELIVERY_PLACES, setValue("" + spinnerDeliveryPlaces.getSelectedItemPosition()));
                entity.addPart(Constant.SELLER_ID, setValue(session.getData(Constant.ID)));
                entity.addPart(Constant.NAME, setValue(edtProductName.getText().toString().trim()));
                entity.addPart(Constant.DESCRIPTION, setValue(edtDescription.getText().toString()));
                String strCateId = arrayListCategories.get(spinnerCategory.getSelectedItemPosition()).getId();
                entity.addPart(Constant.CATEGORY_ID, setValue(strCateId));
                String strSubCate = arrayListCategories.get(spinnerCategory.getSelectedItemPosition()).getSubCategories().get(spinnerSubCategory.getSelectedItemPosition()).getId();
                entity.addPart(Constant.SUB_CATEGORY_ID, strSubCate == null || strSubCate.isEmpty() ? setValue("0") : setValue(strSubCate));
                entity.addPart(Constant.SERVE_FOR, setValue(serve_for.toString()));

                if (switchIsCancellable.isEnabled()) {
                    entity.addPart(Constant.CANCELABLE_STATUS, setValue("1"));
                    entity.addPart(Constant.RETURN_DAYS, setValue(edtReturnDays.getText().toString().trim()));
                } else {
                    entity.addPart(Constant.CANCELABLE_STATUS, setValue("0"));
                }
                if (switchIsReturnable.isEnabled()) {
                    entity.addPart(Constant.RETURN_STATUS, setValue("1"));
                    entity.addPart(Constant.TILL_STATUS, setValue(orderStatuses.get(spinnerTillStatus.getSelectedItemPosition()).getStatusName()));
                } else {
                    entity.addPart(Constant.RETURN_STATUS, setValue("0"));
                }
                if (arrayListTax != null) {
                    entity.addPart(Constant.TAX_ID, setValue(arrayListTax.get(spinnerTax.getSelectedItemPosition()).getId()));
                }
                entity.addPart(Constant.MANUFACTURER, setValue(edtManufacturer.getText().toString().trim()));
                entity.addPart(Constant.MADE_IN, setValue(edtMadeIn.getText().toString().trim()));
                entity.addPart(Constant.INDICATOR, setValue("" + spinnerProductType.getSelectedItemPosition()));

                entity.addPart(Constant.MEASUREMENT, setValue(measurement.toString()));
                entity.addPart(Constant.MEASUREMENT_UNIT_ID, setValue(measurement_unit_id.toString()));
                entity.addPart(Constant.PRICE, setValue(price.toString()));
                entity.addPart(Constant.DISCOUNTED_PRICE, setValue(discounted_price.toString()));

                if (rdLoose.isChecked()) {
                    entity.addPart(Constant.TYPE, setValue("loose"));
                    entity.addPart(Constant.LOOSE_STOCK, setValue(edtStock.getText().toString()));
                    entity.addPart(Constant.LOOSE_STOCK_UNIT_ID, setValue(looseStockId));
                } else {
                    entity.addPart(Constant.TYPE, setValue("packet"));
                    entity.addPart(Constant.STOCK, setValue(stock.toString()));
                    entity.addPart(Constant.STOCK_UNIT_ID, setValue(stock_unit_id.toString()));
                }
                if (mAlbumFiles != null && mAlbumFiles.size() > 0) {
                    for (AlbumFile imageList : mAlbumFiles) {
                        entity.addPart(Constant.OTHER_IMAGES + "[]", new FileBody(new File(imageList.getPath())));
                    }
                }

                if (mainFile != null) {
                    entity.addPart(Constant.IMAGE, new FileBody(mainFile));
                }

                httppost.addHeader(Constant.AUTHORIZATION, "Bearer " + ApiConfig.createJWT("eKart", "eKart Authentication"));
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: " + statusCode;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                JSONObject jsonObject = new JSONObject(result);
                if (!jsonObject.getBoolean(Constant.ERROR)) {
                    if (!from.equals("product")) {
                        position = 0;
                        ProductListActivity.productArrayList.add(position, new Gson().fromJson(jsonObject.getJSONObject(Constant.DATA).toString(), Product.class));
                    } else {
                        ProductListActivity.productArrayList.set(position, new Gson().fromJson(jsonObject.getJSONObject(Constant.DATA).toString(), Product.class));
                    }
                    ProductListActivity.mAdapter.notifyDataSetChanged();
                    onBackPressed();
                }
                Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            lytProgressBar.setVisibility(View.GONE);
            super.onPostExecute(result);
        }

    }

    public StringBody setValue(String value) {
        return new StringBody(value, ContentType.TEXT_PLAIN);
    }

    public class CategoryAdapter extends BaseAdapter {
        final Context context;
        final ArrayList<Categories> categories;
        final LayoutInflater inflter;
        Spinner spinnerCategory, spinnerSubCategory;

        public CategoryAdapter(Context applicationContext, ArrayList<Categories> categories, Spinner spinnerCategory, Spinner spinnerSubCategory) {
            this.context = applicationContext;
            this.categories = categories;
            this.spinnerCategory = spinnerCategory;
            this.spinnerSubCategory = spinnerSubCategory;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return categories.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public void setItem(String categoryId, String SubCategoryId) {
            for (int i = 0; i < getCount(); i++) {
                if (categoryId.equals(categories.get(i).getId())) {
                    spinnerCategory.setSelection(i);
                    subCategoryAdapter.setItem(SubCategoryId);
                }
            }
        }

        @SuppressLint({"SetTextI18n", "ViewHolder", "InflateParams"})
        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.lyt_spinner_item, null);
            TextView txtmeasurement = view.findViewById(R.id.txtmeasurement);

            Categories category = categories.get(position);
            txtmeasurement.setText(category.getName());

            spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    subCategoryAdapter = new SubCategoryAdapter(context, category.getSubCategories(), spinnerSubCategory);
                    spinnerSubCategory.setAdapter(subCategoryAdapter);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            return view;
        }
    }

    public class LooseStockUnitAdapter extends BaseAdapter {
        final Context context;
        final ArrayList<Unit> units;
        final LayoutInflater inflter;


        public LooseStockUnitAdapter(Context applicationContext, ArrayList<Unit> units) {
            this.context = applicationContext;
            this.units = units;


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
                if (id.equals(units.get(i).getId())) {
                    spinnerMeasurement.setSelection(i);
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

            spinnerMeasurement.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    try {
                        looseStockId = unit.getId();
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