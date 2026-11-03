package com.nextgenius.snapfix;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailInput;
    private Button resetButton;
    private TextView backToLoginText;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot_password);

        firebaseAuth = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.emailInput);
        resetButton = findViewById(R.id.resetButton);
        backToLoginText = findViewById(R.id.backToLoginText);

        resetButton.setOnClickListener(v -> sendResetEmail());

        backToLoginText.setOnClickListener(v -> finish());
    }

    private void sendResetEmail() {

        String email = emailInput.getText().toString().trim();

        if (email.isEmpty()) {
            emailInput.setError("Email required");
            emailInput.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()) {

            emailInput.setError("Enter a valid email");
            emailInput.requestFocus();
            return;
        }

        resetButton.setEnabled(false);
        resetButton.setText("TRANSMITTING...");

        firebaseAuth
                .sendPasswordResetEmail(email)
                .addOnCompleteListener(this, task -> {

                    resetButton.setEnabled(true);
                    resetButton.setText("SEND RESET TRANSMISSION  →");

                    if (task.isSuccessful()) {

                        Toast.makeText(
                                ForgotPasswordActivity.this,
                                "Password reset email sent",
                                Toast.LENGTH_LONG
                        ).show();

                        finish();

                    } else {

                        String message = "Unable to send reset email";

                        if (task.getException() != null) {
                            message = task.getException().getMessage();
                        }

                        Toast.makeText(
                                ForgotPasswordActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}