package com.example.lendease;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class AdminForgotPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        EditText admin_EmailAddress = findViewById(R.id.user_EmailAddress);
        Button admin_forgotBtn = findViewById(R.id.user_forgotBtn);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        admin_forgotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(admin_EmailAddress.getText().toString())) {
                    Toast.makeText(AdminForgotPassword.this, "Please fill up the email address!",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                mAuth.sendPasswordResetEmail(admin_EmailAddress.getText().toString())
                        .addOnCompleteListener(AdminForgotPassword.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AdminForgotPassword.this, "Successfully submit, please check your email!",
                                            Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                        });
            }
        });


    }
}