package com.optibe.android.beoptimist;

import android.content.Context;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

class ValidateInput {

    private Context context;
    private EditText email, password, passwordRepeat;
    String emailInput, passwordInput, passwordRepeatInput;

    ValidateInput(Context myContext, EditText myNewEmail){
        context = myContext;
        email = myNewEmail;
    }

    ValidateInput(Context myContext, EditText myEmail, EditText myPassword) {
        context = myContext;
        email = myEmail;
        password = myPassword;

    }

    ValidateInput(Context myContext, EditText myEmail, EditText myPassword, EditText myPasswordRepeat) {
        context = myContext;
        email = myEmail;
        password = myPassword;
        passwordRepeat = myPasswordRepeat;
    }

    boolean ValidateEmail() {
        emailInput = email.getText().toString().trim();

        if (emailInput.isEmpty()) {
            Toast.makeText(context, "Enter your email address please", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            Toast.makeText(context, "Invalid email address", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    boolean ValidatePassword() {
        passwordInput = password.getText().toString().trim();

        if (passwordInput.isEmpty()) {
            Toast.makeText(context, "Please enter your password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (passwordInput.length() < 6) {
            Toast.makeText(context, "Password too small", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    boolean ValidatePasswordRepeat() {
        passwordRepeatInput = passwordRepeat.getText().toString().trim();
        if (passwordRepeatInput.isEmpty()){
            Toast.makeText(context, "Please enter the confirmation password", Toast.LENGTH_SHORT).show();
            return false;
        }else if(!passwordRepeatInput.equals(passwordInput)){
            Toast.makeText(context, "Passwords don't match", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }
}