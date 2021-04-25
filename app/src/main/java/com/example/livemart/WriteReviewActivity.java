package com.example.livemart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class WriteReviewActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private ImageView profileTv;
    private TextView shopNameTv;
    private RatingBar ratingBar;
    private EditText reviewEt;
    private Button submitBtn;
    private String shopUid;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        //init ui views
        backBtn = findViewById(R.id.backBtn);
        profileTv = findViewById(R.id.profileIv);
        shopNameTv = findViewById(R.id.shopNameTv);
        ratingBar=findViewById(R.id.ratingBar);
        reviewEt=findViewById(R.id.reviewEt);
        submitBtn=findViewById(R.id.submitBtn);

        //get shop uid from intent
        shopUid = getIntent().getStringExtra("shopUid");

        firebaseAuth = FirebaseAuth.getInstance();

        //if user has added review to this shop then load it
        loadMyReview();

        //go back to previous activity
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ////input data
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });
    }

    private void loadMyReview() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child("Ratings").child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            //my review is available in this shop

                            //get review details
                            String uid=""+snapshot.child("uid").getValue();
                            String ratings=""+snapshot.child("ratings").getValue();
                            String review=""+snapshot.child("review").getValue();
                            String timestamp=""+snapshot.child("timestamp").getValue();

                            //set review detials to our ui
                            float myRating = Float.parseFloat(ratings);
                            ratingBar.setRating(myRating);
                            reviewEt.setText(review);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void inputData() {

        String ratings=""+ratingBar.getRating();
        String review = reviewEt.getText().toString().trim();

        //for time of review
        String timestamp = ""+System.currentTimeMillis();

        //setup data in hashmap
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("uid",""+firebaseAuth.getUid());
        hashMap.put("ratings",""+ratings);
        hashMap.put("review",""+review);
        hashMap.put("timestamp",""+timestamp);

        //put to db>Users>Ratings
        DatabaseReference ref =  FirebaseDatabase.getInstance().getReference();

        ref.child(firebaseAuth.getUid()).child("Ratings").updateChildren(hashMap)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //review added to db
                Toast.makeText(WriteReviewActivity.this, "Review published successfully....", Toast.LENGTH_SHORT).show();
            }
        })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //failed adding review to db
                    Toast.makeText(WriteReviewActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }
}