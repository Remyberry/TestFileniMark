package com.example.lendease;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class UserLogin extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_login);

        EditText user_email = findViewById(R.id.user_email);
        EditText user_password = findViewById(R.id.user_password);
        Button user_login_btn = findViewById(R.id.user_login_btn);
        TextView user_forgot_password = findViewById(R.id.user_forgot_password);
        TextView user_to_adminaccount = findViewById(R.id.user_to_adminaccount);

        user_to_adminaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserLogin.this, MainActivity.class);
                startActivity(intent);
            }
        });

        user_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserLogin.this, AdminForgotPassword.class);
                startActivity(intent);
            }
        });

        user_login_btn.setOnClickListener(v -> {
            String email = user_email.getText().toString().trim();
            String password = user_password.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(UserLogin.this, "Please fill in all fields",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            //Sign In Firebase Authentication
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            // Once Successful Need to Check the email in CustomerInfo -
                            // if exist meaning the login email and password is for Customer
                            db.collection("customerinfo")
                                    .whereEqualTo("email", email)
                                    .get()
                                    .addOnCompleteListener(innerTask -> {
                                        if (innerTask.isSuccessful()) {
                                            String documentId = "";
                                            for (DocumentSnapshot document : innerTask.getResult()) {
                                                documentId = document.getId();
//                                                Toast.makeText(UserLogin.this, "Nakita ang email ",
//                                                        Toast.LENGTH_SHORT).show();
                                                // Stop searching after finding the first document (if expected)
                                                break;
                                            }

//                                            Toast.makeText(UserLogin.this, documentId,
//                                                    Toast.LENGTH_SHORT).show();
                                            // Test walang nakitang Email or Document ID meaning Admin iyon
                                            if (!documentId.equals("")) {
                                                Intent intent = new Intent(UserLogin.this,
                                                        AdminDashboard.class);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(UserLogin.this, "Invalid Email or Password: ",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(UserLogin.this, "Login failed: " +
                                                            Objects.requireNonNull(innerTask.getException()).getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(UserLogin.this, "Login failed: " +
                                            Objects.requireNonNull(task.getException()).getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

    }
}