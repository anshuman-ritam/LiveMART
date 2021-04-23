package com.example.livemart;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserProductDetail extends AppCompatActivity {
    // declare UI views
    private EditText userSearchProductEt;
    private ImageButton userFilterProductBtn,addProductBtn,backBtn,logoutBtn;
    private RecyclerView userProdRv;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelProduct> productsList;
    private AdapterProductUser adapterProductUser;
    private TextView userFilteredProductsTv,tabProductTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_products);

        //init ui
        userFilterProductBtn = findViewById(R.id.userFilterProductBtn);
        userSearchProductEt = findViewById(R.id.userSearchProductEt);
        userProdRv = findViewById(R.id.userProdRv);
        addProductBtn = findViewById(R.id.addProductBtn);
        userFilteredProductsTv=findViewById(R.id.userFilteredProductsTv);
        firebaseAuth = FirebaseAuth.getInstance();
        tabProductTv=findViewById(R.id.tabProductTv);
        backBtn=findViewById(R.id.backBtn);
        logoutBtn=findViewById(R.id.logoutBtn);

        //loadMyInfo();
        //loadShopDetails();
        loadShopProducts();



        //search
        userSearchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterProductUser.getFilter().filter(s);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Used for adding products users
        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open add product activity
                startActivity(new Intent(getApplicationContext(),AddProductActivity.class));
                finish();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainCustomerActivity.class));
            }
        });


        userFilterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserProductDetail.this);
                builder.setTitle("Choose Category")
                        .setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //get selected item
                                String selected = Constants.productCategories1[which];
                                userFilteredProductsTv.setText(selected);
                                //userFilteredProductsTv.setText(selected); -------------BUT IT'S THERE IN XML
                                if(selected.equals("All")) {
                                    //load all
                                    loadShopProducts();
                                }
                                else {
                                    //load filtered products
                                    loadFilteredProducts(selected);
//                                    adapterProductUser.getFilter().filter(selected);
                                }
                            }
                        })
                        .show();
            }
        });
    }

    private void loadFilteredProducts(String selected) {
        productsList = new ArrayList<>();

        //get all products
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.child(firebaseAuth.getUid()).child("CustomerProducts")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //before gettingreset lsit
                        for(DataSnapshot ds: snapshot.getChildren()) {

                            String productCategory =""+ds.child("productCategory").getValue();

                            //if selected category match product category then add in list
                            if(selected.equalsIgnoreCase(productCategory)) {
                                ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                                productsList.add(modelProduct);
                            }


                        }

                        //setup adapter
                        adapterProductUser = new AdapterProductUser(UserProductDetail.this, productsList);
                        //set adapter
                        userProdRv.setAdapter(adapterProductUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    //private void loadMyInfo() {
    //video9 23:54
    //function from video 8
    // }

    //private void loadShopDetails() {
    //}

    private void loadShopProducts() {
        //init list
        productsList = new ArrayList<>();

        //customer products

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("CustomerProducts")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clearList before adding
                        productsList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {


                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productsList.add(modelProduct);


                        }

                        //setup adapter
                        adapterProductUser = new AdapterProductUser(UserProductDetail.this, productsList);
                        //set adapter
                        userProdRv.setAdapter(adapterProductUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }



}