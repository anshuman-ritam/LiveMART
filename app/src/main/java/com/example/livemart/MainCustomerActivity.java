package com.example.livemart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainCustomerActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private Button shoppingBtn;
    private ImageButton logoutBtn;
    private RelativeLayout ordersRl;
    private TextView shopNameTv,tabOrderTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_customer);

        logoutBtn=findViewById(R.id.logoutBtn);
        //shopNameTv=findViewById(R.id.shopNameTv);
        //tabOrderTv=findViewById(R.id.tabOrderTv);
        // o//rdersRl=findViewById(R.id.ordersRl);
        shoppingBtn = findViewById(R.id.shoppingBtn);
        firebaseAuth = FirebaseAuth.getInstance();
//        checkUser();

//        showOrdersUI();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });

        shoppingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainCustomerActivity.this,UserProductDetail.class));
            }
        });

    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(MainCustomerActivity.this,LoginActivity.class));
            finish();
        }
        //else loadmyinfo part skipped
    }



    private void showOrdersUI() {
        ordersRl.setVisibility(View.VISIBLE);
        tabOrderTv.setTextColor(getResources().getColor(R.color.black));
        tabOrderTv.setBackgroundColor(R.drawable.shape_rect01);
    }


}