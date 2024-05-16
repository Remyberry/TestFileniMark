package com.example.lendease;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EditText admin_email = findViewById(R.id.admin_email);
        EditText admin_password = findViewById(R.id.admin_password);
        Button admin_login_btn = findViewById(R.id.admin_login_btn);
        TextView admin_forgot_password = findViewById(R.id.admin_forgot_password);
        TextView admin_to_useraccount = findViewById(R.id.user_to_adminaccount);

        admin_password.setText("Jiriel123*");

        admin_to_useraccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserLogin.class);
                startActivity(intent);
            }
        });

        admin_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdminForgotPassword.class);
                startActivity(intent);
            }
        });

        admin_login_btn.setOnClickListener(v -> {
            String email = admin_email.getText().toString().trim();
            String password = admin_password.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(MainActivity.this, "Please fill in all fields",
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
//                                                Toast.makeText(MainActivity.this, "Nakita ang email ",
//                                                        Toast.LENGTH_SHORT).show();
                                                // Stop searching after finding the first document (if expected)
                                                break;
                                            }

//                                            Toast.makeText(MainActivity.this, documentId,
//                                                    Toast.LENGTH_SHORT).show();
                                            // Test kapag walang nakitang Email or Document ID sa customer meaning Admin iyon
                                            if (documentId.equals("")) {
                                                Intent intent = new Intent(MainActivity.this,
                                                        AdminDashboard.class);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(MainActivity.this, "Invalid Email or Password: ",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(MainActivity.this, "Login failed: " +
                                                            Objects.requireNonNull(innerTask.getException()).getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(MainActivity.this, "Login failed: " +
                                            Objects.requireNonNull(task.getException()).getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}