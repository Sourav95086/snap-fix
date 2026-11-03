package com.nextgenius.snapfix;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;

    private Button signupButton;
    private TextView loginText;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();

        usernameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);

        signupButton = findViewById(R.id.signupButton);
        loginText = findViewById(R.id.loginText);

        passwordInput.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        confirmPasswordInput.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        signupButton.setOnClickListener(v -> createAccount());

        loginText.setOnClickListener(v -> finish());
    }

    private void createAccount() {

        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword =
                confirmPasswordInput.getText().toString().trim();

        // -----------------------------
        // VALIDATION
        // -----------------------------

        if (username.isEmpty()) {
            usernameInput.setError("Username required");
            usernameInput.requestFocus();
            return;
        }

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

        if (password.isEmpty()) {
            passwordInput.setError("Password required");
            passwordInput.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordInput.setError(
                    "Password must contain at least 6 characters"
            );
            passwordInput.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordInput.setError(
                    "Confirm your password"
            );
            confirmPasswordInput.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError(
                    "Passwords do not match"
            );
            confirmPasswordInput.requestFocus();
            return;
        }

        // -----------------------------
        // DISABLE BUTTON
        // -----------------------------

        signupButton.setEnabled(false);
        signupButton.setText("CREATING CLEARANCE...");

        // -----------------------------
        // CREATE FIREBASE ACCOUNT
        // -----------------------------

        firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        FirebaseUser firebaseUser =
                                firebaseAuth.getCurrentUser();

                        if (firebaseUser == null) {

                            signupButton.setEnabled(true);
                            signupButton.setText(
                                    "CREATE CLEARANCE  →"
                            );

                            Toast.makeText(
                                    SignupActivity.this,
                                    "Account created, but user session failed.",
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        // -----------------------------
                        // SEND EMAIL VERIFICATION
                        // -----------------------------

                        firebaseUser
                                .sendEmailVerification()
                                .addOnCompleteListener(
                                        verificationTask -> {

                                            if (verificationTask.isSuccessful()) {

                                                Toast.makeText(
                                                        SignupActivity.this,
                                                        "Verification email sent to "
                                                                + email
                                                                + ". Please verify your email before login.",
                                                        Toast.LENGTH_LONG
                                                ).show();

                                                // -----------------------------
                                                // SIGN OUT USER
                                                // -----------------------------

                                                firebaseAuth.signOut();

                                                // -----------------------------
                                                // GO TO LOGIN
                                                // -----------------------------

                                                Intent intent =
                                                        new Intent(
                                                                SignupActivity.this,
                                                                LoginActivity.class
                                                        );

                                                intent.setFlags(
                                                        Intent.FLAG_ACTIVITY_NEW_TASK |
                                                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                );

                                                startActivity(intent);

                                                finish();

                                            } else {

                                                signupButton.setEnabled(true);
                                                signupButton.setText(
                                                        "CREATE CLEARANCE  →"
                                                );

                                                String message =
                                                        "Account created, but verification email could not be sent.";

                                                if (verificationTask.getException() != null) {
                                                    message =
                                                            verificationTask
                                                                    .getException()
                                                                    .getMessage();
                                                }

                                                Toast.makeText(
                                                        SignupActivity.this,
                                                        message,
                                                        Toast.LENGTH_LONG
                                                ).show();
                                            }
                                        }
                                );

                    } else {

                        signupButton.setEnabled(true);
                        signupButton.setText(
                                "CREATE CLEARANCE  →"
                        );

                        String message =
                                "Registration failed";

                        if (task.getException() != null) {
                            message =
                                    task.getException().getMessage();
                        }

                        Toast.makeText(
                                SignupActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}