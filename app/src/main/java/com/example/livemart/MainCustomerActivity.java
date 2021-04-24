//package com.example.livemart;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//public class MainCustomerActivity extends AppCompatActivity {
//
//    private FirebaseAuth firebaseAuth;
//    private Button shoppingBtn;
//    private ImageButton logoutBtn;
//    private RelativeLayout ordersRl;
//    private TextView shopNameTv,tabOrderTv;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main_customer);
//
//        logoutBtn=findViewById(R.id.logoutBtn);
//        //shopNameTv=findViewById(R.id.shopNameTv);
//        //tabOrderTv=findViewById(R.id.tabOrderTv);
//        // o//rdersRl=findViewById(R.id.ordersRl);
//        shoppingBtn = findViewById(R.id.shoppingBtn);
//        firebaseAuth = FirebaseAuth.getInstance();
////        checkUser();
//
////        showOrdersUI();
//
//        logoutBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                firebaseAuth.signOut();
//                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
//                finish();
//            }
//        });
//
//        shoppingBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainCustomerActivity.this,UserProductDetail.class));
//            }
//        });
//
//    }
//
//    private void checkUser() {
//        FirebaseUser user = firebaseAuth.getCurrentUser();
//        if(user == null){
//            startActivity(new Intent(MainCustomerActivity.this,LoginActivity.class));
//            finish();
//        }
//        //else loadmyinfo part skipped
//    }
//
//
//
//    private void showOrdersUI() {
//        ordersRl.setVisibility(View.VISIBLE);
//        tabOrderTv.setTextColor(getResources().getColor(R.color.black));
//        tabOrderTv.setBackgroundColor(R.drawable.shape_rect01);
//    }
//
//
//}

package com.example.livemart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainCustomerActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private Button shoppingBtn;
    private ImageButton logoutBtn, userFilterOrderBtn;
    private RelativeLayout ordersRl;
    private TextView shopNameTv, tabOrderTv;
    private EditText userSearchOrderEt;
    private RecyclerView userOrderRv;

    //order list
    private ArrayList<ModelOrderUser> ordersList;
    private AdapterOrderUser adapterOrderUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_customer);


        //userFilterOrderBtn = findViewById(R.id.userFilterOrderBtn);
        //userSearchOrderEt = findViewById(R.id.userSearchOrderEt);
        userOrderRv = findViewById(R.id.userOrderRv);
        firebaseAuth = FirebaseAuth.getInstance();

        logoutBtn = findViewById(R.id.logoutBtn);
        //shopNameTv=findViewById(R.id.shopNameTv);
        //tabOrderTv=findViewById(R.id.tabOrderTv);
        // o//rdersRl=findViewById(R.id.ordersRl);
        shoppingBtn = findViewById(R.id.shoppingBtn);
        firebaseAuth = FirebaseAuth.getInstance();
        //checkUser();

//        showOrdersUI();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        shoppingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainCustomerActivity.this, UserProductDetail.class));
            }
        });

        loadOrderInfo();

    }


    //search


    private void loadOrderInfo() {
        ordersList = new ArrayList<>();

        //get orders
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child("RPiznTPah6M3pTaagWPLRZ0lzpf1").child("Orders");
        ref.orderByChild("OrderBy").equalTo(firebaseAuth.getUid())
            .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ordersList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    //String uid = ""+ds.getRef().getKey();

                    //DatabaseReference_ref =

                    ModelOrderUser modelOrderUser = ds.getValue(ModelOrderUser.class);
                    ordersList.add(modelOrderUser);
                }

                //setup adapter
                adapterOrderUser = new AdapterOrderUser(MainCustomerActivity.this, ordersList);
                //set adapter
                userOrderRv.setAdapter(adapterOrderUser);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}