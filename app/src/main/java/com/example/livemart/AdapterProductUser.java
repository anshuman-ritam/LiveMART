package com.example.livemart;

import android.app.AlertDialog;
import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterProductUser extends RecyclerView.Adapter<AdapterProductUser.HolderProductUser>implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productsList,filterList;
    private FilterProductUser filter;

    public AdapterProductUser(Context context, ArrayList<ModelProduct> productsList){
        this.context = context;
        this.productsList = productsList;
        this.filterList = productsList;
    }

    @NonNull
    @Override
    public HolderProductUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_user,parent,false);
        return new HolderProductUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductUser holder, int position) {
        //get data
        ModelProduct modelProduct = productsList.get(position);
        String productCategory = modelProduct.getProductCategory();
        String productDescription = modelProduct.getProductDescription();
        String quantity = modelProduct.getProductQuantity();
        String title = modelProduct.getProductTitle();
        String timestamp = modelProduct.getTimestamp();
        String price = modelProduct.getProductPrice();


        //set Data
        holder.titleTv.setText(title);
        holder.quantityTv.setText(quantity);
        holder.priceTv.setText(price);
        holder.titleTv.setText(title);

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
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        titleTv.setText("" + title);
        pQuantityTv.setText("" + quantity);
        descriptionTv.setText("" + productDescription);
        FinalTv.setText("Rs" + finalCost);

        AlertDialog dialog = builder.create();
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
        EasyDB easyDB=EasyDB.init(context,"ITEMS_DB")
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


    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new FilterProductUser(this,filterList);
        }
        return null;
    }


    class HolderProductUser extends RecyclerView.ViewHolder{

        //UID View
        private ImageView productIconIv,nextIv;
        private TextView titleTv,quantityTv,priceTv,addToCartTv;


        public HolderProductUser(@NonNull View itemView) {
            super(itemView);

            //init ui views
            productIconIv = itemView.findViewById(R.id.productIconIv);
            titleTv = itemView.findViewById(R.id.titleTv);
            quantityTv = itemView.findViewById(R.id.quantityTv);
            priceTv = itemView.findViewById(R.id.priceTv);
            addToCartTv = itemView.findViewById(R.id.addToCartTv);


        }
    }
}