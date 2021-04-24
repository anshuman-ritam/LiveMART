package com.example.livemart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EditProductActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private ImageView productIconIv;
    private EditText titleEt, descriptionEt, quantityEt, priceEt, availableEt;
    private TextView categoryTv, stockTv;
    private Button updateProductBtn;

    private String productId;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        //init ui views
        backBtn = findViewById(R.id.backBtn);
        productIconIv = findViewById(R.id.productIconIv);
        titleEt = findViewById(R.id.titleEt);
        descriptionEt = findViewById(R.id.descriptionEt);
        categoryTv = findViewById(R.id.categoryTv);
        stockTv = findViewById(R.id.stockTv);
        quantityEt = findViewById(R.id.quantityEt);
        priceEt = findViewById(R.id.priceEt);
        availableEt = findViewById(R.id.availableEt);
        updateProductBtn = findViewById(R.id.updateProductBtn);

        //get id of the product from intent
        productId = getIntent().getStringExtra("productId");

        firebaseAuth = FirebaseAuth.getInstance();
        loadProductDetails(); //to set on views

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait!");
        progressDialog.setCanceledOnTouchOutside(false);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
                ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot ds:snapshot.getChildren())
                                {
                                    String accountType=""+ds.child("accountType").getValue();
                                    if(accountType.equalsIgnoreCase("retailer"))
                                    {
                                        startActivity(new Intent(getApplicationContext(),MainRetailerActivity.class));
                                        finish();
                                    }
                                    else if(accountType.equalsIgnoreCase("wholesaler"))
                                    {
                                        startActivity(new Intent(getApplicationContext(),MainWholesalerActivity.class));
                                        finish();
                                    }
                                    else if(accountType.equalsIgnoreCase("customer"))
                                    {
                                        startActivity(new Intent(getApplicationContext(),UserProductDetail.class));
                                        finish();
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

            }
        });

        categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog();
            }
        });

        stockTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stockDialog();
            }
        });

        updateProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });
    }

    private void loadProductDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products").child(productId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get data
                        String productId = ""+snapshot.child("productId").getValue();
                        String productTitle = ""+snapshot.child("productTitle").getValue();
                        String productDescription = ""+snapshot.child("productDescription").getValue();
                        String productQuantity = ""+snapshot.child("productQuantity").getValue();
                        String productCategory = ""+snapshot.child("productCategory").getValue();
                        String productPrice = ""+snapshot.child("productPrice").getValue();
                        String productStock = ""+snapshot.child("productStock").getValue();
                        String productAvailable = ""+snapshot.child("productAvailable").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();
                        String uid = ""+snapshot.child("uid").getValue();

                        //set data to views
                        titleEt.setText(productTitle);
                        descriptionEt.setText(productDescription);
                        categoryTv.setText(productCategory);
                        priceEt.setText(productPrice);
                        stockTv.setText(productStock);
                        availableEt.setText(productAvailable);
                        quantityEt.setText(productQuantity);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private String productTitle, productDescription, productCategory, productQuantity, productPrice, productStock, productAvailable;

    private void inputData() {

        productTitle = titleEt.getText().toString().trim();
        productDescription = descriptionEt.getText().toString().trim();
        productCategory = categoryTv.getText().toString().trim();
        productQuantity = quantityEt.getText().toString().trim();
        productPrice = priceEt.getText().toString().trim();
        productStock = stockTv.getText().toString().trim();
        productAvailable = availableEt.getText().toString().trim();

        if(TextUtils.isEmpty(productTitle)) {
            Toast.makeText(this, "Title is required...", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(productCategory)) {
            Toast.makeText(this, "Category is required...", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(productPrice)) {
            Toast.makeText(this, "Price is required...", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(productStock)) {
            Toast.makeText(this, "Stock detail is required...", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(productAvailable)) {
            Toast.makeText(this, "Availability Detail is required...", Toast.LENGTH_SHORT).show();
            return;
        }

        updateProduct();

    }

    private void updateProduct() {
        //show progress
        progressDialog.setMessage("Updating Product...");
        progressDialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("productTitle", ""+productTitle);
        hashMap.put("productDescription", ""+productDescription);
        hashMap.put("productCategory", ""+productCategory);
        hashMap.put("productPrice", ""+productPrice);
        hashMap.put("productQuantity",""+productQuantity);
        hashMap.put("productStock", ""+productStock);
        hashMap.put("productAvailable", ""+productAvailable);

        //update to db
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products").child(productId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(EditProductActivity.this, "Updated...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void stockDialog() {
        String stockInfo[] = {
                "In stock",
                "Not in stock"
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Stock Details")
                .setItems(stockInfo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String stock = stockInfo[which];
                        stockTv.setText(stock);
                    }
                })
                .show();
    }

    private void categoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Product Category")
                .setItems(Constants.productCategories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String category = Constants.productCategories[which];
                        categoryTv.setText(category);
                    }
                })
                .show();
    }

}