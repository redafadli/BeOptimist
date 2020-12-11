package com.optibe.android.beoptimist;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {


    private ValidateInput validateInput;
    private EditText sign_in_email, sign_in_password;
    private FirebaseAuth mAuth;
    private Button log_in;
    private TextView sign_up;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sign_in_email = findViewById(R.id.email);
        sign_in_password = findViewById(R.id.password);
        log_in = findViewById(R.id.log_in);
        sign_up = findViewById(R.id.sign_up);

        validateInput = new ValidateInput(MainActivity.this, sign_in_email, sign_in_password);

        mAuth = FirebaseAuth.getInstance();

        log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String loginEmail = sign_in_email.getText().toString().trim();
                String loginPass = sign_in_password.getText().toString().trim();

                if (!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPass)) {

                    mAuth.signInWithEmailAndPassword(loginEmail, loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                Intent intent = new Intent(MainActivity.this, Home.class);
                                startActivity(intent);
                            }

                            else {
                                Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

                }else{
                    Toast.makeText(MainActivity.this, "Email or Password can't be empty", Toast.LENGTH_SHORT).show();
                    if(TextUtils.isEmpty(loginEmail)){
                        sign_in_email.getError();
                    }else if(TextUtils.isEmpty(loginPass)){
                        sign_in_password.getError();
                    }else{
                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                    }
                }

            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, sign_up.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            Intent intent = new Intent(MainActivity.this, Home.class);
            startActivity(intent);
        }
    }

    public void signInAccount() {

        String email = sign_in_email.getText().toString().trim();
        String password = sign_in_password.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(MainActivity.this, Home.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}