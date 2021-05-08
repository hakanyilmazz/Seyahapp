package com.hakanyilmazz.seyahapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.hakanyilmazz.seyahapp.R;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmailAddress;
    private EditText inputPassword;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmailAddress = findViewById(R.id.loginActivity_emailAddress);
        inputPassword = findViewById(R.id.loginActivity_password);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void login(View view) {
        String emailAddress = inputEmailAddress.getText().toString();
        String password = inputPassword.getText().toString();

        boolean isInputsValidControl = isInputsValid(emailAddress, password);

        if (isInputsValidControl) {
            firebaseAuth.signInWithEmailAndPassword(emailAddress, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Lets Travel!", Toast.LENGTH_SHORT).show();
                                startPlacesActivity();
                            } else {
                                String errorMessage = task.getException().getLocalizedMessage();
                                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Please check again your email or password!", Toast.LENGTH_SHORT).show();
        }
    }

    public void signUp(View view) {
        String emailAddress = inputEmailAddress.getText().toString();
        String password = inputPassword.getText().toString();

        boolean isInputsValidControl = isInputsValid(emailAddress, password);

        if (isInputsValidControl) {
            firebaseAuth.createUserWithEmailAndPassword(emailAddress, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Lets Travel!", Toast.LENGTH_SHORT).show();
                                startPlacesActivity();
                            } else {
                                String errorMessage = task.getException().getLocalizedMessage();
                                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Please check again your email or password!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isInputsValid(String emailAddress, String password) {
        boolean isEmailValidControl = isEmailValid(emailAddress);
        return isEmailValidControl && !emailAddress.isEmpty() && !password.isEmpty();
    }

    private boolean isEmailValid(String emailAddress) {
        boolean isEmailFormatTrue = emailAddress.matches("^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
        boolean isFinishesWithTrueWord = emailAddress.endsWith(".com") || emailAddress.endsWith(".tr");

        return isEmailFormatTrue && isFinishesWithTrueWord;
    }

    private void startPlacesActivity() {
        Intent intentToPlacesActivity = new Intent(LoginActivity.this, PlacesActivity.class);
        startActivity(intentToPlacesActivity);
        finish();
    }

}