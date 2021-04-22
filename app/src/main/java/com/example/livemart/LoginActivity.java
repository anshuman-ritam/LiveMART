package com.example.livemart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText username,passwordText;
    private Button Btn;
    private ProgressBar progressbar;
    private FirebaseAuth mAuth;
//    private LoginButton fbButton;
    private SignInButton googlebtn;
    private static int SIGN_IN=1;
    private GoogleApiClient googleApiClient;

//    CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // initialising all views through id defined above
        username = findViewById(R.id.emailEt);
        passwordText = findViewById(R.id.passwordEt);
        Btn = findViewById(R.id.loginBtn);
//        fbButton=findViewById(R.id.fb_login);

        // Set on Click Listener on Sign-in button
        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                loginUserAccount();
            }
        });

        //Go to register page
        Button register=(Button) findViewById(R.id.registerBtn);
        register.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent j=new Intent(LoginActivity.this, Register.class);
                startActivity(j);
            }
        });

//        Handling facebook login
//        callbackManager = CallbackManager.Factory.create();
//        fbButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                startActivity(new Intent(getApplicationContext(),MainDashboard.class));
//                finish();
//            }
//
//            @Override
//            public void onCancel() {
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//
//            }
//        });


//        //Handling google sign in
//        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
//        googleApiClient=new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();
//        googlebtn=findViewById(R.id.google_login);
//        googlebtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i=Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
//                startActivityForResult(i,SIGN_IN);
//
//            }
//        });
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode==SIGN_IN)
//        {
//            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            if(result.isSuccess())
//            {
//                startActivity(new Intent(getApplicationContext(),MainDashboard.class));
//                finish();
//            }
//        }
//        callbackManager.onActivityResult(requestCode,resultCode,data);
//    }


    private void loginUserAccount()
    {


        // Take the value of two edit texts in Strings
        String user, password;
        user = username.getText().toString();
        password = passwordText.getText().toString();

        // validations for input email and password
        if (TextUtils.isEmpty(user)) {
            Toast.makeText(getApplicationContext(),
                    "Please enter Username!",
                    Toast.LENGTH_LONG)
                    .show();
            return;
        }

        if (TextUtils.isEmpty(password) || password.length()<6) {
            Toast.makeText(getApplicationContext(),
                    "Password should be greater than 5 characters!",
                    Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // signin existing user
        mAuth.signInWithEmailAndPassword(user, password)
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(
                                    @NonNull Task<AuthResult> task)
                            {
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(getApplicationContext(),EmailVerifyActivity.class));
                                }

                                else {

                                    // sign-in failed
                                    Toast.makeText(getApplicationContext(),
                                            "Please enter correct username and password",
                                            Toast.LENGTH_LONG)
                                            .show();


                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });;
    }


//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }
}