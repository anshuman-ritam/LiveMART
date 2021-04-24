package com.example.livemart;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterOrderUser extends RecyclerView.Adapter<AdapterOrderUser.HolderOrderUser> {

    private Context context;
    private ArrayList<ModelOrderUser> orderUserList;

    public AdapterOrderUser(Context context, ArrayList<ModelOrderUser> orderUserList){

        this.context=context;
        this.orderUserList=orderUserList;
    }

    @NonNull
    @Override
    public HolderOrderUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.now_order_user, parent, false);
        return new HolderOrderUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderUser holder, int position) {
        //getData
        ModelOrderUser modelOrderUser=orderUserList.get(position);
        String orderId = modelOrderUser.getOrderId();
        String orderBy = modelOrderUser.getOrderBy();
        String orderCost = modelOrderUser.getOrderCost();
        String orderStatus = modelOrderUser.getOrderStatus();
        String orderTime = modelOrderUser.getOrderTime();
        String orderTo = modelOrderUser.getOrderTo();

        //getShopInfo
//        loadShopInfo(modelOrderUser, holder);
        //setData
        holder.amountTv.setText("Amount Rs:"+orderCost);
        holder.statusTv.setText(orderStatus);
        holder.orderIdTv.setText("OrderID:"+orderId);
        //change order status text color
//        if(orderStatus.equals("In Progress")){
//
//            holder.statusTv.setTextColor(Color.parseColor("#0000FF"));
//
//        }
//        else if(orderStatus.equals("Completed")){
//            holder.statusTv.setTextColor(Color.parseColor("#00FF00"));
//        }
//        else if(orderStatus.equals("Cancelled")){
//            holder.statusTv.setTextColor(Color.parseColor("#FF0000"));
//        }
//
////    convert timestamp to proper format
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(Long.parseLong(orderTime));
//        String formattedDate = DateFormat.format("dd/MM/yyyy",calendar).toString();
//
//        holder.dateTv.setText(formattedDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //OPEN ORDER DETAILS
                Intent intent = new Intent(context,OrderDetailsUser.class);
                intent.putExtra("orderTo",orderTo);
                intent.putExtra("orderId",orderId);
                context.startActivity(intent);

            }
        });
    }

   /* private void loadShopInfo(ModelOrderUser modelOrderUser, HolderOrderUser holder) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(modelOrderUser.getOrderTo())
                    .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String shopName = "" + dataSnapshot.child()("shopName").getValue();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
        });
    }*/

    @Override
    public int getItemCount() {
        return orderUserList.size();
    }

    //view holder class
    class HolderOrderUser extends RecyclerView.ViewHolder{

        //views of layout
        private TextView orderIdTv,dateTv, shopNameTv, amountTv, statusTv;

        public HolderOrderUser(@NonNull View itemView){
            super(itemView);

            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            shopNameTv = itemView.findViewById(R.id.shopNameTv);
            amountTv = itemView.findViewById(R.id.amountTv);
            statusTv = itemView.findViewById(R.id.statusTv);
        }
    }

}
