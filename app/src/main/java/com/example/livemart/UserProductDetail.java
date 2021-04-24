package com.example.livemart;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class UserProductDetail extends AppCompatActivity {
    // declare UI views
    private EditText userSearchProductEt;
    private ImageButton userFilterProductBtn,addProductBtn, CartBtn;
    private RecyclerView userProdRv;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelProduct> productsList;
    private AdapterProductUser adapterProductUser;

    //cart
    private ArrayList<ModelCartItem> cartItemList;
    private AdapterCartItem adapterCartItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_products);

        //init ui
        userFilterProductBtn = findViewById(R.id.userFilterProductBtn);
        userSearchProductEt = findViewById(R.id.userSearchProductEt);
        userProdRv = findViewById(R.id.userProdRv);
        addProductBtn = findViewById(R.id.addProductBtn);
        CartBtn = findViewById(R.id.CartBtn);
        firebaseAuth = FirebaseAuth.getInstance();

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
            }
        });

        CartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Opened");
                showCartDialog();
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

    public double allTotalPrice = 0.00;
    public TextView sTotalTv, dFeeTv, allTotalPriceTv;

    public void showCartDialog() {

        //init list
        cartItemList = new ArrayList<>();

        //inflate cart layout
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_cart, null);
        //init views
        RecyclerView cartItemsRv= view.findViewById(R.id.cartItemsRv);
        //TextView sTotalLabelTv = view.findViewById(R.id.sTotalLabelTv);
        sTotalTv = view.findViewById(R.id.sTotalTv);
        //TextView dFeeLabelTv = view.findViewById(R.id.dFeeLabelTv);
        dFeeTv = view.findViewById(R.id.dFeeTv);
        allTotalPriceTv = view.findViewById(R.id.totalTv);
        Button checkoutBtn = view.findViewById(R.id.checkoutBtn);

        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitOrder();
                startActivity(new Intent(getApplicationContext(),MainCustomerActivity.class));
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //set view to dialog
        builder.setView(view);

        EasyDB easyDB=EasyDB.init(
                this,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id", "text","unique"))
                .addColumn(new Column("Item_PID", "text","not null"))
                .addColumn(new Column("Item_Name", "text","not null"))
                .addColumn(new Column("Item_Price", "text","not null"))
                .addColumn(new Column("Item_Quantity", "text","not null"))
                .doneTableColumn();

        //easyDB.deleteAllDataFromTable();

        //get all records from db
        Cursor res = easyDB.getAllData();
        while (res.moveToNext()){
            String id = res.getString(1);
            String pId = res.getString(2);
            String name = res.getString(3);
            String price = res.getString(4);
            String quantity = res.getString(5);

            System.out.println(id);
            System.out.println(pId);
            System.out.println(name);
            System.out.println(price);
            System.out.println(quantity);
            allTotalPrice = allTotalPrice + Double.parseDouble(price);

            ModelCartItem modelCartItem = new ModelCartItem(""+id,""+pId,""+name,""+price,""+quantity);

            cartItemList.add(modelCartItem);
        }

        //setup adapter
        adapterCartItem = new AdapterCartItem(this, cartItemList);
        //set to recycler view
        cartItemsRv.setAdapter(adapterCartItem);
        sTotalTv.setText("Rs "+allTotalPrice);
        dFeeTv.setText("Rs 12");
        allTotalPriceTv.setText("Rs "+(allTotalPrice+12));
        //show dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                allTotalPrice = 0.00;

            }
        });

        //startActivity(new Intent(MainCustomerActivity.this,UserProductDetail.class));

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

    private void submitOrder() {

        //show progress dialog
//        progressDialog.setMessage("Placing Order.....");
//        progressDialog.show();

        //for order id and order time
        String timestamp = ""+System.currentTimeMillis();

        String cost = allTotalPriceTv.getText().toString().trim().replace("Rs", "");

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("OrderId",""+timestamp);
        hashMap.put("OrderTime",""+timestamp);
        hashMap.put("OrderStatus","In Progress");
        hashMap.put("OrderCost",""+cost);
        hashMap.put("OrderBy", ""+firebaseAuth.getUid());
        hashMap.put("Order To","RPiznTPah6M3pTaagWPLRZ0lzpf1");

        //add to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo("RPiznTPah6M3pTaagWPLRZ0lzpf1")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ref.child("RPiznTPah6M3pTaagWPLRZ0lzpf1").child("Orders").child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //orderinfo added now add order items
                                for (int i=0; i<cartItemList.size();i++){
                                    String pId = cartItemList.get(i).getpId();
                                    String id = cartItemList.get(i).getId();
                                    String name = cartItemList.get(i).getName();
                                    String price = cartItemList.get(i).getPrice();
                                    String quantity = cartItemList.get(i).getQuantity();

                                    HashMap<String,String> hashMap1 = new HashMap<>();
                                    hashMap1.put("pId",pId);
                                    hashMap1.put("pId",name);
                                    hashMap1.put("price",price);
                                    hashMap1.put("quantity",quantity);

                                    ref.child("RPiznTPah6M3pTaagWPLRZ0lzpf1").child("Orders").child("Items").child(pId).setValue(hashMap1);


                                }

//                                progressDialog.dismiss();
                                Toast.makeText(UserProductDetail.this, "Order Placed Successfully...", Toast.LENGTH_SHORT).show();

                                //OPEN ORDER DETAILS
                                Intent intent = new Intent(UserProductDetail.this,MapsActivity.class);
//                                intent.putExtra("orderTo","RPiznTPah6M3pTaagWPLRZ0lzpf1");
//                                intent.putExtra("orderId",timestamp);
                                startActivity(intent);

                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //failure placing order
//                                        progressDialog.dismiss();
                                        Toast.makeText(UserProductDetail.this, "e.getMessage()", Toast.LENGTH_SHORT).show();

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });





    }



}