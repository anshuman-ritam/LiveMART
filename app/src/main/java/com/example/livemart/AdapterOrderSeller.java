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

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterOrderSeller extends RecyclerView.Adapter<AdapterOrderSeller.HolderOrderSeller> {

    private Context context;
    public ArrayList<ModelOrderSeller> orderSellerArrayList;

    public AdapterOrderSeller(Context context, ArrayList<ModelOrderSeller> orderSellerArrayList) {
        this.context = context;
        this.orderSellerArrayList = orderSellerArrayList;
    }

    @NonNull
    @Override
    public HolderOrderSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.now_order_seller,parent,false);
        return new HolderOrderSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderSeller holder, int position) {
        //get data

        ModelOrderSeller modelOrderSeller=orderSellerArrayList.get(position);
        String orderId = modelOrderSeller.getOrderId();
        String orderBy = modelOrderSeller.getOrderBy();
        String orderCost = modelOrderSeller.getOrderCost();
        String orderStatus = modelOrderSeller.getOrderStatus();
        String orderTime = modelOrderSeller.getOrderTime();
        String orderTo = modelOrderSeller.getOrderTo();

        //getShopInfo
//        loadShopInfo(modelOrderUser, holder);
        //setData
        holder.amountTv.setText("Amount Rs:"+orderCost);
        holder.statusTv.setText(orderStatus);
        holder.orderIdTv.setText("OrderID:"+orderId);

        //change order status text color
//        System.out.println("Hello"+orderStatus);
//        System.out.println("Hello"+orderId);
//        System.out.println("Hello"+orderBy);
//        System.out.println("Hello"+orderCost);
//        System.out.println("Hello"+orderTime);

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
//        //convert timestamp to proper format
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(Long.parseLong(orderTime));
//        String formattedDate = DateFormat.format("dd/MM/yyyy",calendar).toString();
//
//        holder.dateTv.setText(formattedDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,OrderDetailsSellerActivity.class);
                intent.putExtra("orderId",orderId);
                intent.putExtra("orderBy",orderBy);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return orderSellerArrayList.size();
    }

    class HolderOrderSeller extends RecyclerView.ViewHolder{

        private TextView orderIdTv,dateTv,amountTv,statusTv;

        public HolderOrderSeller(@NonNull View itemView) {
            super(itemView);

            orderIdTv=itemView.findViewById(R.id.orderIdTv);
            dateTv=itemView.findViewById(R.id.dateTv);
            amountTv=itemView.findViewById(R.id.amountTv);
            statusTv=itemView.findViewById(R.id.statusTv);

        }
    }
}
