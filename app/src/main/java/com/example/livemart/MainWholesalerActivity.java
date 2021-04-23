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

import java.util.ArrayList;

public class MainWholesalerActivity extends AppCompatActivity {

    private FirebaseAuth mauth;
    private ImageButton logoutBtn,addProductBtn,filterproductsBtn;
    private TextView shopNameTv,tabProductsTv,tabOrdersTv;
    private RelativeLayout productsRl,ordersRl;
    private EditText searchTv;
    private RecyclerView productsRv;
    private ArrayList<ModelProduct> productList;
    private AdapterProductWholesaler adapterProductWholesaler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wholesaler);

        shopNameTv = findViewById(R.id.shopNameTv);

        logoutBtn = findViewById(R.id.logoutBtn);
        addProductBtn = findViewById(R.id.addProductBtn);
        tabOrdersTv=findViewById(R.id.tabOrderTv);
        tabProductsTv=findViewById(R.id.tabProductTv);
        searchTv=findViewById(R.id.searchProductEt);
        productsRv=findViewById(R.id.productsRv);
        filterproductsBtn=findViewById(R.id.filterProductBtn);
        productsRl=findViewById(R.id.productsRl);
        ordersRl=findViewById(R.id.ordersRl);
        mauth=FirebaseAuth.getInstance();

        loadAllProducts();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open add product activity
                startActivity(new Intent(getApplicationContext(),AddProductActivity.class));
            }
        });

        tabProductsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show wholesaler products
                showProductsUI();
            }
        });

        tabOrdersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show wholesaler orders
                showOrdersUI();
            }
        });

            filterproductsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainWholesalerActivity.this);
                builder.setTitle("Choose Category")
                        .setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String selected = Constants.productCategories1[which];
                                if(selected.equals("All")){
                                    loadAllProducts();
                                }
                                else
                                {
                                    loadFilteredProducts(selected);
                                }
                            }
                        }).show();
            }
        });

        searchTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    adapterProductWholesaler.getFilter().filter(s);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void loadFilteredProducts(String selected) {
        productList=new ArrayList<>();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(mauth.getUid()).child("WholesalerProducts")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();
                        for(DataSnapshot ds:snapshot.getChildren())
                        {
                            String productCategory=""+ds.child("productCategory").getValue();
                            if(selected.equalsIgnoreCase(productCategory))
                            {
                                ModelProduct modelProduct=ds.getValue(ModelProduct.class);
                                productList.add(modelProduct);
                            }

                        }
                        adapterProductWholesaler=new AdapterProductWholesaler(MainWholesalerActivity.this,productList);
                        productsRv.setAdapter(adapterProductWholesaler);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadAllProducts() {
        productList=new ArrayList<>();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(mauth.getUid()).child("WholesalerProducts")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();
                        for(DataSnapshot ds:snapshot.getChildren())
                        {
                            ModelProduct modelProduct=ds.getValue(ModelProduct.class);
                            productList.add(modelProduct);
                        }
                        adapterProductWholesaler=new AdapterProductWholesaler(MainWholesalerActivity.this,productList);
                        productsRv.setAdapter(adapterProductWholesaler);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showOrdersUI() {
        productsRl.setVisibility(View.GONE);
        ordersRl.setVisibility(View.VISIBLE);
        tabOrdersTv.setBackgroundResource(R.drawable.shape_rect_purple);
        tabProductsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

//        tabOrdersTv.setBackgroundColor();
    }

    private void showProductsUI() {
        ordersRl.setVisibility(View.GONE);
        productsRl.setVisibility(View.VISIBLE);
        tabProductsTv.setBackgroundResource(R.drawable.shape_rect_purple);
        tabOrdersTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }
}