package com.example.livemart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EmailVerifyActivity extends AppCompatActivity {
    private FirebaseAuth mauth;
    private ImageButton imgBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify);

        mauth= FirebaseAuth.getInstance();

        Button btn=findViewById(R.id.verifyEmail);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mauth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Verification email sent", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        imgBtn=findViewById(R.id.backBtn);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });

        Button toMain=findViewById(R.id.continueMain);
        toMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mauth.getCurrentUser().isEmailVerified())
                {
                    checkUserType();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please verify Email address", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void checkUserType()
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(mauth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren())
                        {
                            String accountType=""+ds.child("accountType").getValue();
                            if(accountType.equalsIgnoreCase("retailer"))
                            {
                                startActivity(new Intent(getApplicationContext(),MainRetailerActivity.class));
                                finish();
                            }
                            else if(accountType.equalsIgnoreCase("customer"))
                            {
                                startActivity(new Intent(getApplicationContext(),MainCustomerActivity.class));
                                finish();
                            }
                            else if(accountType.equalsIgnoreCase("wholesaler"))
                            {
                                startActivity(new Intent(getApplicationContext(),MainWholesalerActivity.class));
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}