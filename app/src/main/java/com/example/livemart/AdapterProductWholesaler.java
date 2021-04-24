package com.example.livemart;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdapterProductWholesaler extends RecyclerView.Adapter<AdapterProductWholesaler.HolderProductWholesaler> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> wholesalerProductList,filterList;
    private FilterWholesalerProduct filter;

    public AdapterProductWholesaler(Context context, ArrayList<ModelProduct> wholesalerProductList) {
        this.context = context;
        this.wholesalerProductList = wholesalerProductList;
        this.filterList=wholesalerProductList;
    }



    @NonNull
    @Override
    public HolderProductWholesaler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_wholesaler,parent,false);
        return new HolderProductWholesaler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductWholesaler holder, int position) {
        ModelProduct modelProduct=wholesalerProductList.get(position);
        String id=modelProduct.getProductId();
        String productCategory=modelProduct.getProductCategory();
        String productPrice=modelProduct.getProductPrice();
        String productTitle=modelProduct.getProductTitle();
        String quantity=modelProduct.getProductQuantity();
        String stockDetails = modelProduct.getProductStock();
        String stockAvailable = modelProduct.getProductAvailable();

        holder.quantityTv.setText("Quantity: "+quantity);
        holder.titleTv.setText(productTitle);
        holder.priceTv.setText("Rs. "+productPrice);
        holder.stockTv.setText(stockDetails);
        holder.availableTv.setText(stockAvailable);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsBottomSheet(modelProduct); //here modelProduct contains detail of clicked product
            }
        });
    }


    private void detailsBottomSheet(ModelProduct modelProduct) {
        //bottom sheet
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);

        View view = LayoutInflater.from(context).inflate(R.layout.bs_product_details_wholesaler, null);

        bottomSheetDialog.setContentView(view);

        ImageButton backBtn = view.findViewById(R.id.backBtn);
        ImageButton deleteBtn = view.findViewById(R.id.deleteBtn);
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
        quantityTv.setText("Quantity: "+quantity);
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
        reference.child(firebaseAuth.getUid()).child("WholesalerProducts").child(id).removeValue()
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
        return wholesalerProductList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null)
        {
            filter=new FilterWholesalerProduct(this,filterList);
        }
        return filter;
    }


    class HolderProductWholesaler extends RecyclerView.ViewHolder{

        private ImageView productIconTv;
        private TextView titleTv,quantityTv,priceTv, stockTv, availableTv;

        public HolderProductWholesaler(@NonNull View itemView) {
            super(itemView);

            productIconTv=itemView.findViewById(R.id.productIconIv);
            titleTv=itemView.findViewById(R.id.titleTv);
            quantityTv=itemView.findViewById(R.id.quantityTv);
            priceTv=itemView.findViewById(R.id.priceTv);
            stockTv = itemView.findViewById(R.id.retailer_stockTv);
            availableTv = itemView.findViewById(R.id.retailer_availableTv);
        }
    }
}
