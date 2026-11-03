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
import com.nextgenius.snapfix.api.AuthenticationRetrofitClient;
import com.nextgenius.snapfix.api.AuthenticationApi;
import com.nextgenius.snapfix.Model.UserRoleResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput;
    private EditText passwordInput;

    private Button loginButton;
    private TextView forgotPasswordText;
    private TextView registerText;

    private FirebaseAuth firebaseAuth;
    private AuthenticationApi authenticationApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        authenticationApi =
                AuthenticationRetrofitClient.getApi();

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);

        loginButton = findViewById(R.id.loginButton);
        forgotPasswordText =
                findViewById(R.id.forgotPasswordText);
        registerText =
                findViewById(R.id.registerText);

        passwordInput.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        loginButton.setOnClickListener(
                v -> loginUser()
        );

        forgotPasswordText.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    ForgotPasswordActivity.class
            );

            startActivity(intent);
        });

        registerText.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    SignupActivity.class
            );

            startActivity(intent);
        });
    }

    private void loginUser() {

        String email =
                emailInput.getText().toString().trim();

        String password =
                passwordInput.getText().toString().trim();

        if (email.isEmpty()) {

            emailInput.setError("Email required");
            emailInput.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()) {

            emailInput.setError(
                    "Enter a valid email"
            );

            emailInput.requestFocus();
            return;
        }

        if (password.isEmpty()) {

            passwordInput.setError(
                    "Password required"
            );

            passwordInput.requestFocus();
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("AUTHENTICATING...");

        firebaseAuth
                .signInWithEmailAndPassword(
                        email,
                        password
                )
                .addOnCompleteListener(this, task -> {

                    if (!task.isSuccessful()) {

                        loginButton.setEnabled(true);
                        loginButton.setText(
                                "AUTHENTICATE & INGRESS  →"
                        );

                        String message =
                                "Authentication failed";

                        if (task.getException() != null) {
                            message =
                                    task.getException()
                                            .getMessage();
                        }

                        Toast.makeText(
                                LoginActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    // --------------------------------
                    // FIREBASE LOGIN SUCCESS
                    // --------------------------------

                    if (firebaseAuth.getCurrentUser() == null) {

                        loginButton.setEnabled(true);
                        loginButton.setText(
                                "AUTHENTICATE & INGRESS  →"
                        );

                        Toast.makeText(
                                LoginActivity.this,
                                "User session unavailable",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    // --------------------------------
                    // CHECK EMAIL VERIFICATION
                    // --------------------------------

                    if (!firebaseAuth
                            .getCurrentUser()
                            .isEmailVerified()) {

                        loginButton.setEnabled(true);
                        loginButton.setText(
                                "AUTHENTICATE & INGRESS  →"
                        );

                        Toast.makeText(
                                LoginActivity.this,
                                "Please verify your email first",
                                Toast.LENGTH_LONG
                        ).show();

                        firebaseAuth.signOut();

                        return;
                    }

                    // --------------------------------
                    // FETCH ROLE FROM API
                    // --------------------------------

                    fetchUserRole(email);
                });
    }

    private void fetchUserRole(String email) {

        loginButton.setText("CHECKING CLEARANCE...");

        authenticationApi
                .getUserRole(email)
                .enqueue(new Callback<UserRoleResponse>() {

                    @Override
                    public void onResponse(
                            Call<UserRoleResponse> call,
                            Response<UserRoleResponse> response) {

                        loginButton.setEnabled(true);
                        loginButton.setText(
                                "AUTHENTICATE & INGRESS  →"
                        );

                        if (!response.isSuccessful()
                                || response.body() == null) {

                            Toast.makeText(
                                    LoginActivity.this,
                                    "Unable to fetch user role",
                                    Toast.LENGTH_LONG
                            ).show();

                            firebaseAuth.signOut();

                            return;
                        }

                        UserRoleResponse roleResponse =
                                response.body();

                        if (!roleResponse.isSuccess()) {

                            Toast.makeText(
                                    LoginActivity.this,
                                    "User role unavailable",
                                    Toast.LENGTH_LONG
                            ).show();

                            firebaseAuth.signOut();

                            return;
                        }

                        String role =
                                roleResponse
                                        .getRole();

                        if (role == null) {

                            Toast.makeText(
                                    LoginActivity.this,
                                    "Invalid user role",
                                    Toast.LENGTH_LONG
                            ).show();

                            firebaseAuth.signOut();

                            return;
                        }

                        // --------------------------------
                        // ADMIN
                        // --------------------------------

                        if (role.equalsIgnoreCase("admin")) {

                            Intent intent =
                                    new Intent(
                                            LoginActivity.this,
                                            MainActivity.class
                                    );

                            intent.setFlags(
                                    Intent.FLAG_ACTIVITY_NEW_TASK |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                            );

                            startActivity(intent);

                            finish();

                        }

                        // --------------------------------
                        // NORMAL USER
                        // --------------------------------

                        else if (role.equalsIgnoreCase("user")) {

                            Intent intent =
                                    new Intent(
                                            LoginActivity.this,
                                            UserActivity.class
                                    );

                            intent.setFlags(
                                    Intent.FLAG_ACTIVITY_NEW_TASK |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                            );

                            startActivity(intent);

                            finish();

                        }

                        // --------------------------------
                        // UNKNOWN ROLE
                        // --------------------------------

                        else {

                            Toast.makeText(
                                    LoginActivity.this,
                                    "Unknown user role: " + role,
                                    Toast.LENGTH_LONG
                            ).show();

                            firebaseAuth.signOut();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<UserRoleResponse> call,
                            Throwable t) {

                        loginButton.setEnabled(true);

                        loginButton.setText(
                                "AUTHENTICATE & INGRESS  →"
                        );

                        Toast.makeText(
                                LoginActivity.this,
                                "Role API connection failed",
                                Toast.LENGTH_LONG
                        ).show();

                        firebaseAuth.signOut();
                    }
                });
    }
}