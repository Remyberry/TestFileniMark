package com.example.lendease;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class AdminDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);
        ImageView home_nav = findViewById(R.id.home_nav);
        ImageView info_nav = findViewById(R.id.info_nav_ci);
        ImageView collect_nav = findViewById(R.id.collect_nav_ci);
        ImageView addloan_nav = findViewById(R.id.addloan_nav_ci);
        ImageView report_nav = findViewById(R.id.report_nav_ci);
        ImageView list_nav = findViewById(R.id.list_nav);

        home_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboard.this, AdminDashboard.class));
            }
        });

        info_nav.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(AdminDashboard.this, CustomerInfo.class));
                Intent intent = new Intent(AdminDashboard.this, CustomerInfo.class);
//                intent.putExtra("key", "joshuayalung555@gmail.com");
                startActivity(intent);
            }
        }));

        addloan_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboard.this, LoanPosting.class);
                startActivity(intent);
            }
        });

        collect_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboard.this, Collection.class);
                startActivity(intent);
            }
        });


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Get a reference to the image file in Firebase Storage
        StorageReference imageRef = storageRef.child("images/lanielsicangco@gmail.com");

        // Download the image into a local file
        File localFile = null;
        try {
            localFile = File.createTempFile("image", "jpg", getCacheDir());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File finalLocalFile = localFile;
        imageRef.getFile(localFile)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image downloaded successfully, display it in the ImageView
                    Bitmap bitmap = BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath());
                    ImageView imageView = findViewById(R.id.imageView);
                    imageView.setImageBitmap(bitmap);
                })
                .addOnFailureListener(exception -> {
                    // Handle any errors
                    //Log.e("TAG", "Error downloading image: " + exception.getMessage());
                });
    }
}