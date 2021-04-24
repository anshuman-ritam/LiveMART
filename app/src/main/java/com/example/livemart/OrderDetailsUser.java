package com.example.livemart;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class OrderDetailsUser extends AppCompatActivity {

    private String orderTo, orderId;

    //ui views
    private ImageButton backBtn;
    private TextView orderIdTv, dateTv, orderStatusTv,totalItemsTv,amountTv,addressTv;
    private RecyclerView itemsRv;

    private ArrayList<ModelOrderedItem> orderedItemsArrayList;
    private AdapterOrderedItem adapterOrderedItem;


    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_users);

        //init views
        backBtn = findViewById(R.id.backBtn);
        orderIdTv = findViewById(R.id.orderIdTv);
        dateTv = findViewById(R.id.dateTv);
        orderStatusTv = findViewById(R.id.orderStatusTv);
        totalItemsTv = findViewById(R.id.totalItemsTv);
         amountTv= findViewById(R.id.amountTv);
        addressTv = findViewById(R.id.addressTv);
        itemsRv = findViewById(R.id.itemsRv);



        Intent intent = getIntent();
        orderTo = intent.getStringExtra("orderTo");
        orderId = intent.getStringExtra("orderId");

        firebaseAuth = FirebaseAuth.getInstance();
//        loadShopInfo();
        loadOrderDetails();
        loadOrderedItems();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadOrderedItems() {
        orderedItemsArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderedItemsArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    //String uid = ""+ds.getRef().getKey();

                    //DatabaseReference_ref =

                    ModelOrderedItem modelOrderedItems = ds.getValue(ModelOrderedItem.class);
                    orderedItemsArrayList.add(modelOrderedItems);
                }

                //setup adapter
                adapterOrderedItem = new AdapterOrderedItem(OrderDetailsUser.this, orderedItemsArrayList);
                //set adapter
                itemsRv.setAdapter(adapterOrderedItem);
                totalItemsTv.setText(""+snapshot.getChildrenCount());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadOrderDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child("RPiznTPah6M3pTaagWPLRZ0lzpf1").child("Orders").child(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String orderBy = ""+ snapshot.child("OrderBy").getValue();
                        String orderCost = ""+ snapshot.child("OrderCost").getValue();
                        String orderId = ""+ snapshot.child("OrderId").getValue();
                        String orderStatus = ""+ snapshot.child("OrderStatus").getValue();
                        String orderTime = ""+ snapshot.child("OrderTime").getValue();
                        String orderTo = ""+ snapshot.child("Order To").getValue();
//                        String deliveryfee = ""+ snapshot.child("").getValue();

                        //convert timestamp
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(orderTime));
                        String formatedDate = DateFormat.format("dd/MM/yyyy hh:mm a",calendar).toString();

                        if(orderStatus.equals("In progress")){
                            orderStatusTv.setTextColor(Color.parseColor("#0000FF"));
                        }
                        else if(orderStatus.equals("Completed")){
                            orderStatusTv.setTextColor(Color.parseColor("#00FF00"));
                        }
                        else if(orderStatus.equals("Cancelled")){
                            orderStatusTv.setTextColor(Color.parseColor("#FF0000"));
                        }

                        //set data
                        orderIdTv.setText(orderId);
                        orderStatusTv.setText(orderStatus);
                        amountTv.setText("Rs"+orderCost+"(including delivery fee Rs"+12+"]");
                        dateTv.setText(formatedDate);
                        


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

//    private void loadShopInfo() {
//        //get shop info
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//        ref.child(orderTo)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }
}
