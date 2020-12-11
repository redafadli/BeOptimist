package com.optibe.android.beoptimist;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdateEmail extends AppCompatActivity {

    private EditText current_email, new_email;
    private Button update_email;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private ValidateInput validateInput;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);
        back = findViewById(R.id.back);
        current_email = findViewById(R.id.current_email);
        new_email = findViewById(R.id.new_email);
        update_email = findViewById(R.id.update_email);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        validateInput = new ValidateInput(UpdateEmail.this, new_email);
        setCurrent_email();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        update_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean emailVerified = validateInput.ValidateEmail();
                if (emailVerified && mUser != null) {
                    String myNewEmail = new_email.getText().toString().trim();
                    mUser.updateEmail(myNewEmail);
                    Toast.makeText(UpdateEmail.this, "Email address updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UpdateEmail.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setCurrent_email() {
        if (mUser != null) {
            current_email.setText(mUser.getEmail());
            current_email.setEnabled(false);
        }
    }
}