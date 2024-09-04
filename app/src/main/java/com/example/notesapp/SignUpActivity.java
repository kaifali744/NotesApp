package com.example.notesapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.example.notesapp.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {
//    private EditText emailTxt, passTxt, confPassTxt;
//    private Button registerBtn;
    ActivitySignUpBinding signUpBinding;

    //Firebase Auth
    private FirebaseAuth firebaseAuth;
    private  FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //Firebase connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        signUpBinding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();

        //Auth state change
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if (currentUser != null){
                    //User already logged in
                }else{
                    //User signed out
                }
            }
        };

        //On Editor Action Listener used to hide keyboard when hitting enter
        TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        };

        signUpBinding.editText.setOnEditorActionListener(editorActionListener);
        signUpBinding.editPass.setOnEditorActionListener(editorActionListener);
        signUpBinding.editPassConfirm.setOnEditorActionListener(editorActionListener);


        //Creating User Account
        signUpBinding.btnRegiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(signUpBinding.editText.getText().toString())
                        && !TextUtils.isEmpty(signUpBinding.editPass.getText().toString())
                        && !TextUtils.isEmpty(signUpBinding.editPassConfirm.getText().toString())){

                    String email = signUpBinding.editText.getText().toString().trim();
                    String pass = signUpBinding.editPass.getText().toString().trim();
                    String confirmPass = signUpBinding.editPassConfirm.getText().toString().trim();

                    //Creating user account
                    createUserAccount(email, pass, confirmPass);
                }else {
                    Toast.makeText(SignUpActivity.this,
                            "No empty fields allowed!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private  void createUserAccount(String email, String pass, String confirmPass) {
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)
                && !TextUtils.isEmpty(confirmPass)){
            if(pass.equals(confirmPass)){
                firebaseAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    currentUser =FirebaseAuth.getInstance().getCurrentUser();
                                    String userId = email; //Using email as userId

                                    Toast.makeText(SignUpActivity.this,
                                            "Account created succesfully!",
                                            Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(SignUpActivity.this,
                                            MainActivity.class);
                                    // Set flags to clear the back stack
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                                    // Start the MainActivity
                                    SignUpActivity.this.startActivity(intent);

                                    // Finish SecondActivity
                                    if (SignUpActivity.this instanceof Activity) {
                                        ((Activity) SignUpActivity.this).finish();
                                    }
                                }else {
                                    // Handle the error here
                                    Toast.makeText(SignUpActivity.this,
                                            "Account creation failed: " + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }else{
                Toast.makeText(this,
                        "Password and Confirmation dosen't match!",
                        Toast.LENGTH_SHORT).show();
            }

    }
    }
}