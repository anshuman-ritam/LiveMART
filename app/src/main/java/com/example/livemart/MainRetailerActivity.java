package com.example.livemart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.cert.PolicyNode;
import java.util.ArrayList;

public class MainRetailerActivity extends AppCompatActivity {

    private FirebaseAuth mauth;
    private ImageButton logoutBtn,addProductBtn, filterProductBtn;
    private EditText searchProductEt;
    private TextView shopNameTv, tabProductsTv, tabOrderTv, filteredProductsTv;
    private RelativeLayout productsRl, ordersRl;
    private RecyclerView productsRv,ordersRv;

    private ArrayList<ModelProduct> productList;
    private AdapterProductSeller adapterProductSeller;

    private ArrayList<ModelOrderSeller> orderSellerArrayList;
    private AdapterOrderSeller adapterOrderSeller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_retailer);

        shopNameTv = findViewById(R.id.shopNameTv);
        tabProductsTv = findViewById(R.id.tabProductTv);
        tabOrderTv = findViewById(R.id.tabOrderTv);
        filteredProductsTv = findViewById(R.id.filteredProductsTv);

        searchProductEt = findViewById(R.id.searchProductEt);

        logoutBtn = findViewById(R.id.logoutBtn);
        addProductBtn = findViewById(R.id.addProductBtn);
        filterProductBtn = findViewById(R.id.filterProductBtn);

        productsRl = findViewById(R.id.productsRl);
        ordersRl = findViewById(R.id.ordersRl);

        productsRv = findViewById(R.id.productsRv);
        ordersRv=findViewById(R.id.ordersRv);

        mauth = FirebaseAuth.getInstance();

//        tabOrderTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showOrdersUI();
//                loadAllOrders();
//            }
//        });
//
//        tabProductsTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showProductsUI();
//                loadAllProducts();
//            }
//        });

        loadAllProducts();

//        loadAllOrders();
        showProductsUI();

        //search
        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterProductSeller.getFilter().filter(s);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open add product activity
                startActivity(new Intent(getApplicationContext(),AddProductActivity.class));
                finish();
            }
        });

        tabProductsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load products
                showProductsUI();
            }
        });

        tabOrderTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load orders
                showOrdersUI();
                loadAllOrders();
            }
        });

        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainRetailerActivity.this);
                builder.setTitle("Choose Category")
                        .setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //get selected item
                                String selected = Constants.productCategories1[which];
                                filteredProductsTv.setText(selected);
                                if(selected.equals("All")) {
                                    //load all
                                    loadAllProducts();
                                }
                                else {
                                    //load filtered products
                                    loadFilteredProducts(selected);
                                }
                            }
                        })
                        .show();
            }
        });

    }

    private void loadAllOrders() {
        orderSellerArrayList = new ArrayList<>();

        //get all products
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child("RPiznTPah6M3pTaagWPLRZ0lzpf1").child("Orders")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //before gettingreset lsit
//                        orderSellerArrayList.clear();
                        for(DataSnapshot ds: snapshot.getChildren()) {
                            ModelOrderSeller modelOrderSeller= ds.getValue(ModelOrderSeller.class);

//                            System.out.println(modelOrderSeller.getOrderId());
//                            System.out.println(modelOrderSeller.getOrderCost());
//                            System.out.println(modelOrderSeller.getOrderBy());

                            orderSellerArrayList.add(modelOrderSeller);
                        }

                        //setup adapter
                        adapterOrderSeller = new AdapterOrderSeller(MainRetailerActivity.this, orderSellerArrayList);
                        //set adapter
                        ordersRv.setAdapter(adapterOrderSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadFilteredProducts(String selected) {
        productList = new ArrayList<>();

//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        //get all products
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(mauth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //before gettingreset lsit
                        for(DataSnapshot ds: snapshot.getChildren()) {

                            String productCategory =""+ds.child("productCategory").getValue();

                            //if selected category match product category then add in list
                            if(selected.equalsIgnoreCase(productCategory)) {
                                ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                                productList.add(modelProduct);
                            }


                        }

                        //setup adapter
                        adapterProductSeller = new AdapterProductSeller(MainRetailerActivity.this, productList);
                        //set adapter
                        productsRv.setAdapter(adapterProductSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadAllProducts() {
        productList = new ArrayList<>();

        //get all products
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(mauth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //before gettingreset lsit
                        for(DataSnapshot ds: snapshot.getChildren()) {
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productList.add(modelProduct);
                        }

                        //setup adapter
                        adapterProductSeller = new AdapterProductSeller(MainRetailerActivity.this, productList);
                        //set adapter
                        productsRv.setAdapter(adapterProductSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showProductsUI() {
        productsRl.setVisibility(View.VISIBLE);
        ordersRl.setVisibility(View.GONE);

        tabProductsTv.setBackgroundResource(R.drawable.shape_rect_purple);
        tabOrderTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showOrdersUI() {
        productsRl.setVisibility(View.GONE);
        ordersRl.setVisibility(View.VISIBLE);

        tabOrderTv.setBackgroundResource(R.drawable.shape_rect_purple);
        tabProductsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }
}
