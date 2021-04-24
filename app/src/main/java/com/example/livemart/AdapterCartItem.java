package com.example.livemart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterCartItem extends RecyclerView.Adapter<AdapterCartItem.HolderCartItem> {

    private Context context;
    private ArrayList<ModelCartItem> cartItems;

    public AdapterCartItem(Context context, ArrayList<ModelCartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public HolderCartItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_cartitem.xml

        View view =LayoutInflater.from(context).inflate(R.layout.row_cartitem, parent, false);
        return new HolderCartItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCartItem holder, int position) {
        //get data
        ModelCartItem modelCartItem = cartItems.get(position);

        String id = modelCartItem.getId();
        String getpId = modelCartItem.getpId();
        String title = modelCartItem.getName();
       // String cost = modelCartItem.getCost();
        String price = modelCartItem.getPrice();
        String quantity = modelCartItem.getQuantity();

        //set data
        holder.itemTitleTv.setText(""+title);
        holder.itemPriceTv.setText(""+price);
        holder.itemQuantityTv.setText("["+quantity+"]");
//        holder.itemPriceEachTv.setText(""+price);

        //handle remove click listener, delete item from cart
        holder.itemRemoveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //will create table if not exists, but in that case will must exist
                EasyDB easyDB=EasyDB.init(context,"ITEMS_DB")
                        .setTableName("ITEMS_TABLE")
                        .addColumn(new Column("Item_Id", "text","unique"))
                        .addColumn(new Column("Item_PID", "text","not null"))
                        .addColumn(new Column("Item_Name", "text","not null"))
                        .addColumn(new Column("Item_Price", "text","not null"))
                        .addColumn(new Column("Item_Quantity", "text","not null"))
                        .doneTableColumn();

                easyDB.deleteRow(1,id);
                Toast.makeText(context, "Removed from cart...", Toast.LENGTH_SHORT).show();

                //refresh list
                cartItems.remove(position);
                notifyItemChanged(position);
                notifyDataSetChanged();


            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size(); //returns number of records
    }

    class HolderCartItem extends RecyclerView.ViewHolder{

        private TextView itemTitleTv, itemPriceTv, itemPriceEachTv, itemQuantityTv,  itemRemoveTv;

        public HolderCartItem(@NonNull View itemView) {
            super(itemView);

            itemTitleTv=itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv=itemView.findViewById(R.id.itemPriceTv);
            itemPriceEachTv=itemView.findViewById(R.id.itemPriceEachTv);
            itemQuantityTv=itemView.findViewById(R.id.itemQuantityTv);
            itemRemoveTv=itemView.findViewById(R.id.itemRemoveTv);
        }
    }
}
