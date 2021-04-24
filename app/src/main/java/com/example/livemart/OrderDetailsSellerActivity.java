package com.example.livemart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class OrderDetailsSellerActivity extends AppCompatActivity {

    String orderId,orderBy;
    private ImageButton backBtn,editBtn;
    private RecyclerView itemsRv;
    private TextView orderIdTv,dateTv,orderStatusTv,amountTv;
    private ArrayList<ModelOrderedItem> orderedItemArrayList;
    private AdapterOrderedItem adapterOrderedItem;

    private FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_seller);

        backBtn=findViewById(R.id.backBtn);
        editBtn=findViewById(R.id.editBtn);
        itemsRv=findViewById(R.id.itemsRv);
        orderIdTv=findViewById(R.id.orderIdTv);
        dateTv=findViewById(R.id.dateTv);
        orderStatusTv=findViewById(R.id.orderStatusTv);
        amountTv=findViewById(R.id.amountTv);

        orderId=getIntent().getStringExtra("orderId");
        orderBy=getIntent().getStringExtra("orderBy");

        mauth=FirebaseAuth.getInstance();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editOrderStatusDialog();
            }
        });

        loadOrderDetails();
        loadOrderedItems();
    }

    private void editOrderStatusDialog() {
        final String[] options={"In Progress","Completed","Cancelled"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Order Status")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //get selected item
                        String selectedOption =options[which];
                        editOrderStatus(selectedOption);
                    }
                })
                .show();
    }

    private void editOrderStatus(String selectedOption) {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("OrderStatus",""+selectedOption);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(mauth.getUid()).child("Orders").child("OrderId")
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(OrderDetailsSellerActivity.this, "Order is now"+selectedOption, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OrderDetailsSellerActivity.this, "e.getMessage()", Toast.LENGTH_SHORT).show();
                    }
                });



    }

    private void loadOrderDetails() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child("RPiznTPah6M3pTaagWPLRZ0lzpf1").child("Orders").child(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String orderId =""+snapshot.child("OrderId").getValue();
                        String orderBy = ""+snapshot.child("OrderBy").getValue();
                        String orderCost = ""+snapshot.child("OrderCost").getValue();
                        String orderStatus = ""+snapshot.child("OrderStatus").getValue();
                        String orderTime = ""+snapshot.child("OrderTime").getValue();
                        String orderTo = ""+snapshot.child("Order To").getValue();


                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(orderTime));
                        String formattedDate = DateFormat.format("dd/MM/yyyy",calendar).toString();

                        System.out.println("Hello"+orderStatus);
                        System.out.println("Hello"+orderId);
                        System.out.println("Hello"+orderBy);
                        System.out.println("Hello"+orderCost);
                        System.out.println("Hello"+orderTime);

                        //change order status text color
                        if(orderStatus.equals("In Progress")){

                            orderStatusTv.setTextColor(Color.parseColor("#0000FF"));

                        }
                        else if(orderStatus.equals("Completed")){
                            orderStatusTv.setTextColor(Color.parseColor("#00FF00"));
                        }
                        else if(orderStatus.equals("Cancelled")){
                            orderStatusTv.setTextColor(Color.parseColor("#FF0000"));
                        }

                        orderIdTv.setText(orderId);
                        orderStatusTv.setText(orderStatus);
                        dateTv.setText(formattedDate);
                        amountTv.setText("Rs "+orderCost);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void loadOrderedItems()
    {
        orderedItemArrayList =new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(mauth.getUid()).child("Orders").child("orderId").child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clearList before adding
                        orderedItemArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {


                            ModelOrderedItem modelOrderedItem = ds.getValue(ModelOrderedItem.class);
                            orderedItemArrayList.add(modelOrderedItem);


                        }

                        //setup adapter
                        adapterOrderedItem = new AdapterOrderedItem(OrderDetailsSellerActivity.this, orderedItemArrayList);
                        //set adapter
                        itemsRv.setAdapter(adapterOrderedItem);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}