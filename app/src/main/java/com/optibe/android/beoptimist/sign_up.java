package com.optibe.android.beoptimist;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class sign_up extends AppCompatActivity {

    private EditText regemailText;
    private EditText regPasstext;
    private Button regBtn;
    private ImageView back;
    private EditText regPassConftext;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        regemailText = findViewById(R.id.email);
        regPasstext = findViewById(R.id.password);
        regPassConftext = findViewById(R.id.password_repeat);
        regBtn = findViewById(R.id.sign_up);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = regemailText.getText().toString().trim();
                String pass = regPasstext.getText().toString().trim();
                String passConf = regPassConftext.getText().toString().trim();

                if (!emailVerify(email)) {
                    Toast.makeText(sign_up.this, "Invalid email", Toast.LENGTH_SHORT).show();

                } else {

                    if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {

                        if (pass.equals(passConf)) {

                            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                //On complete and there are two possibilities success or failure
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Intent setUpIntent = new Intent(sign_up.this, Profile_edit.class);
                                        startActivity(setUpIntent);
                                        finish();
                                    } else {
                                        Toast.makeText(sign_up.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(sign_up.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(sign_up.this, "Please enter all the details", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    @Override
    protected void onStart() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            sendTomain();
        }
        super.onStart();
    }

    private boolean emailVerify(String email) {
        int flag = 0, f = 0;
        for (int i = 0; i < email.length(); i++) {
            if (email.charAt(i) == '@') {
                flag = 1;
            } else if (email.charAt(i) == '.' && flag == 1) {
                flag = 2;
            } else if ((flag == 2 && (int) email.charAt(i) >= 97 && (int) email.charAt(i) <= 122)) {
                f = 1;
            } else if (f == 1) {
                return false;
            }
        }
        return f == 1;
    }

    private void sendTomain() {
        Intent mainIntent = new Intent(sign_up.this, Home.class);
        startActivity(mainIntent);
        finish();
    }
}