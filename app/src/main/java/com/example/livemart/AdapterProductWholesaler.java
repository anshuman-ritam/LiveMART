package com.example.livemart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        holder.quantityTv.setText(quantity);
        holder.titleTv.setText(productTitle);
        holder.priceTv.setText("Rs. "+productPrice);
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
        private TextView titleTv,quantityTv,priceTv;

        public HolderProductWholesaler(@NonNull View itemView) {
            super(itemView);

            productIconTv=itemView.findViewById(R.id.productIconIv);
            titleTv=itemView.findViewById(R.id.titleTv);
            quantityTv=itemView.findViewById(R.id.quantityTv);
            priceTv=itemView.findViewById(R.id.priceTv);
        }
    }
}
