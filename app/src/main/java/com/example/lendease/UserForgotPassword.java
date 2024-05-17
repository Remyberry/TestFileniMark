package com.example.lendease;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserForgotPassword extends AppCompatActivity {
    private EditText user_EmailAddress;
    private Button user_forgotBtn;
    private FirebaseFirestore db;
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Hide navigation bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN    // Hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // Keep bars hidden
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_forgot_password);

        user_EmailAddress = findViewById(R.id.user_EmailAddress);
        user_forgotBtn = findViewById(R.id.user_forgotBtn);

        db = FirebaseFirestore.getInstance();

        user_forgotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = user_EmailAddress.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(UserForgotPassword.this, "Enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if email exists in Firestore
                db.collection("users").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Generate token and send reset email
                                String token = generateToken();
                                sendPasswordResetEmail(email, token);
                                // Save token in Firestore or any other storage
                                saveToken(email, token);
                            } else {
                                Toast.makeText(UserForgotPassword.this, "Email not found",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(UserForgotPassword.this, "Error: " +
                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private String generateToken() {
        // Generate a unique token using UUID
        return UUID.randomUUID().toString();
    }

    private void sendPasswordResetEmail(String email, String token) {
        // Construct the password reset link with the token
//        String resetLink = "https://yourdomain.com/resetpassword?token=" + token;
        String resetLink = "Use this token to reset your password = " + token;

        // Implement your logic to send the email using your email sending service
        // Here's a simplified example using Firebase Email Auth:

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UserForgotPassword.this, "Password reset email sent",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserForgotPassword.this,
                                    "Failed to send password reset email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveToken(String email, String token) {
        // Save the token in Firestore or any other storage
        // For simplicity, let's assume you have a 'password_reset_tokens' collection
        // where you store tokens associated with user emails
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);

        db.collection("password_reset_tokens").document(email)
                .set(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Token saved successfully
                        } else {
                            Toast.makeText(UserForgotPassword.this, "Error saving token: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}