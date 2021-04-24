package com.example.livemart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.BreakIterator;
import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterProductSeller extends RecyclerView.Adapter<AdapterProductSeller.HolderProductSeller> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productList, filterList;
    private FilterProduct filter;

    public AdapterProductSeller(Context context, ArrayList<ModelProduct> productList) {
        this.context =context;
        this.productList = productList;
        this.filterList = productList;
    }

    @NonNull
    @Override
    public HolderProductSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_seller, parent, false);
        return new HolderProductSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductSeller holder, int position) {
        ModelProduct modelProduct = productList.get(position);
        String id = modelProduct.getProductId();
        String uid = modelProduct.getUid();
        String productCatergory = modelProduct.getProductCategory();
        String productDescription = modelProduct.getProductDescription();
        String quantity = modelProduct.getProductQuantity();
        String title = modelProduct.getProductTitle();
        String timestamp = modelProduct.getTimestamp();
        String price = modelProduct.getProductPrice();
        String stockDetails = modelProduct.getProductStock();
        String stockAvailable = modelProduct.getProductAvailable();

        //For quantity

//        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
//        ref.orderByChild("uid").equalTo(FirebaseAuth.getInstance().getUid())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for(DataSnapshot ds:snapshot.getChildren())
//                        {
//                            String productQuantity=""+ds.child("productQuantity").getValue();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

        holder.titleTv.setText(title);
        holder.quantityTv.setText("Quantity: "+quantity);
        holder.priceTv.setText("Rs. "+price);
        holder.titleTv.setText(title);
        holder.stockTv.setText(stockDetails);
        holder.availableTv.setText(stockAvailable);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsBottomSheet(modelProduct); //here modelProduct contains detail of clicked product
            }
        });

        holder.addToCartTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add product to cart
                showQuantityDialog(modelProduct);

            }
        });

    }

    private double cost = 0,finalCost=0;
    private int quant =0;
    private void showQuantityDialog(ModelProduct modelProduct) {
        //inflate layout for dialog
        View view = LayoutInflater.from(context).inflate(R.layout.activity_add_cart, null);
        //init layout Views
        ImageView productIv = view.findViewById(R.id.productIv);
        TextView titleTv = view.findViewById(R.id.titleTv);
        TextView pQuantityTv = view.findViewById(R.id.pQuantityTv);
        TextView descriptionTv = view.findViewById(R.id.descriptionTv);
        TextView FinalTv = view.findViewById(R.id.FinalTv);
        ImageButton decrementBtn = view.findViewById(R.id.decrementBtn);
        TextView quantityTv = view.findViewById(R.id.quantityTv);
        ImageButton incrementBtn = view.findViewById(R.id.incrementBtn);
        Button continueBtn = view.findViewById(R.id.continueBtn);

        //get data from model
        String productId = modelProduct.getProductId();
        //String productCategory = modelProduct.getProductCategory();
        String productDescription = modelProduct.getProductDescription();
        String quantity = modelProduct.getProductQuantity();
        String title = modelProduct.getProductTitle();
        String timestamp = modelProduct.getTimestamp();
        String price = modelProduct.getProductPrice();

        cost = Double.parseDouble(price.replaceAll("Rs.", ""));
        finalCost = Double.parseDouble(price.replaceAll("Rs.", ""));
        quant = 1;

        //dialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setView(view);
        titleTv.setText("" + title);
        pQuantityTv.setText("" + quantity);
        descriptionTv.setText("" + productDescription);
        FinalTv.setText("Rs" + finalCost);

        android.app.AlertDialog dialog = builder.create();
        dialog.show();

        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalCost = finalCost + cost;
                quant++;

                FinalTv.setText("Rs" + finalCost);
                quantityTv.setText("" + quant);
            }
        });
        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quant > 1) {
                    finalCost = finalCost - cost;
                    quant--;

                    FinalTv.setText("$" + finalCost);
                    quantityTv.setText("" + quant);
                }
            }
        });
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = titleTv.getText().toString().trim();
                String price = FinalTv.getText().toString().trim().replace("Rs", "");
                String quantity = quantityTv.getText().toString().trim();

                addToCart(productId, title, price, quantity);
                dialog.dismiss();


            }
        });
    }

    private int itemId = 1;
    private void addToCart(String productId, String title, String price, String quantity) {
        itemId++;

        //EasyDB easyDB = EasyDB.init(context);
        EasyDB easyDB=EasyDB.init(context,"ITEMS_DB_SELLER")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id", new String[]{"text","unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text","not null"}))
                .doneTableColumn();
//        easyDB.deleteAllDataFromTable();
        Boolean b=easyDB.addData("Item_ID",itemId)
                .addData("Item_PID",productId)
                .addData("Item_Name",title)
                .addData("Item_Price",price)
                .addData("Item_Quantity",quantity)
                .doneDataAdding();

             /*   System.out.println(itemId);
                System.out.println(productId);
                System.out.println(title);
                System.out.println(price);
                System.out.println(quantity);
*/

        Toast.makeText(context,"Added to cart", Toast.LENGTH_SHORT).show();
    }

    private void detailsBottomSheet(ModelProduct modelProduct) {
        //bottom sheet
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);

        View view = LayoutInflater.from(context).inflate(R.layout.bs_product_details_retailer, null);

        bottomSheetDialog.setContentView(view);

        ImageButton backBtn = view.findViewById(R.id.backBtn);
        ImageButton deleteBtn = view.findViewById(R.id.deleteBtn);
        ImageButton editBtn = view.findViewById(R.id.editBtn);
        ImageView productIconIv = view.findViewById(R.id.productIconIv);
        TextView titleTv = view.findViewById(R.id.titleTv);
        TextView descriptionTv = view.findViewById(R.id.descriptionTv);
        TextView categoryTv = view.findViewById(R.id.categoryTv);
        TextView quantityTv = view.findViewById(R.id.quantityTv);
        TextView priceTv = view.findViewById(R.id.priceTv);
        TextView stockTv = view.findViewById(R.id.stockTv);
        TextView availableTv = view.findViewById(R.id.availableTv);


        //getdata
        String id = modelProduct.getProductId();
        String uid = modelProduct.getUid();
        String productCatergory = modelProduct.getProductCategory();
        String productDescription = modelProduct.getProductDescription();
        String quantity = modelProduct.getProductQuantity();
        String title = modelProduct.getProductTitle();
        String timestamp = modelProduct.getTimestamp();
        String price = modelProduct.getProductPrice();
        String stock = modelProduct.getProductStock();
        String available = modelProduct.getProductAvailable();

        //set data
        titleTv.setText(title);
        descriptionTv.setText(productDescription);
        categoryTv.setText(productCatergory);
        quantityTv.setText("Qty: "+quantity);
        priceTv.setText("Rs" + price);
        stockTv.setText(stock);
        availableTv.setText(available);

        //show dialog
        bottomSheetDialog.show();

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                //show delete confirm dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete Product " + title + " ?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //deleted
                                deleteProduct(id);
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //cancelled
                                dialog.dismiss();
                            }
                        })
                        .show();
                notifyDataSetChanged();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                //pass id of product
                Intent intent = new Intent(context, EditProductActivity.class);
                intent.putExtra("productId", id);
                context.startActivity(intent);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dismiss bottom sheet
                bottomSheetDialog.dismiss();
            }
        });

    }

    private void deleteProduct(String id) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products").child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //deleted
                        Toast.makeText(context, "Product Deleted...", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterProduct(this, filterList);
        }
        return filter;
    }

    class HolderProductSeller extends RecyclerView.ViewHolder{

        //holds views of recyclerview

        private ImageView productIconIv;
        private TextView titleTv, quantityTv, priceTv, stockTv, availableTv,addToCartTv;

        public HolderProductSeller(@NonNull View itemView) {
            super(itemView);

            productIconIv = itemView.findViewById(R.id.retailer_productIconIv);
            titleTv = itemView.findViewById(R.id.retailertitleTv);
            quantityTv = itemView.findViewById(R.id.retailer_quantityTv);
            priceTv = itemView.findViewById(R.id.retailer_priceTv);
            stockTv = itemView.findViewById(R.id.retailer_stockTv);
            availableTv = itemView.findViewById(R.id.retailer_availableTv);
            addToCartTv = itemView.findViewById(R.id.addToCartTv);

        }
    }

}