package com.example.notesapp;

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

import com.example.notesapp.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
//    private EditText emailET, passET;
//    private Button signUp, loginBtn;
    ActivityMainBinding mainBinding;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Data Binding
        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        //Login
        mainBinding.btnLogin.setOnClickListener(v-> {
            // onClick(View v) {
            logEmailPassUser(mainBinding.editText.getText().toString().trim(),
                    mainBinding.editText2.getText().toString().trim());

        });

        //FireBase
        firebaseAuth = FirebaseAuth.getInstance();



        //Sign Up page redirection
        mainBinding.button2.setOnClickListener(v -> {
            //onClick()
            Intent i = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(i);
        });
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
        mainBinding.editText.setOnEditorActionListener(editorActionListener);
        mainBinding.editText2.setOnEditorActionListener(editorActionListener);
    }

    private void logEmailPassUser(String email, String pass) {
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)){
            firebaseAuth.signInWithEmailAndPassword(email,pass)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user1 = firebaseAuth.getCurrentUser();

                            Intent intent = new Intent(MainActivity.this,
                                    JournalActivity.class);
                            startActivity(intent);
                        }

            });
        }
    }
}