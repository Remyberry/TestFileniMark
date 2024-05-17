package com.example.lendease;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

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

        List<Loan> loanList = new ArrayList<>();

        // Add some sample loan data (replace with your actual data population logic)
        loanList.add(new Loan("John Doe", "2024-05-20", 100.00));
        loanList.add(new Loan("Jane Pagado", "2024-06-15", 69000.00));
        loanList.add(new Loan("Jane Smith", "2024-06-15", 150.00));
        loanList.add(new Loan("Jane Smith", "2024-06-15", 150.00));
        loanList.add(new Loan("Jane Smith", "2024-06-15", 150.00));
        loanList.add(new Loan("John Doe", "2024-05-20", 100.00));
        loanList.add(new Loan("John Doe", "2024-05-20", 100.00));
        loanList.add(new Loan("Jane Smith", "2024-06-15", 150.00));
        loanList.add(new Loan("Jerry Smith", "2024-06-15", 1.00));
        loanList.add(new Loan("Jerry Smith", "2024-06-15", 1.00));
        loanList.add(new Loan("Jerry Smith", "2024-06-15", 1.00));
        loanList.add(new Loan("Dexter Bading", "2024-06-15", 9600.00));
        loanList.add(new Loan("John Doe", "2024-05-20", 100.00));
        loanList.add(new Loan("John Doe", "2024-05-20", 100.00));
        loanList.add(new Loan("John Doe", "2024-05-20", 100.00));


        // Initialize the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewCust);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Set layout manager

        // Create the adapter and set the data
        // Your adapter class
        LoanAdapter adapter = new LoanAdapter(loanList);
        recyclerView.setAdapter(adapter);

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




//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageRef = storage.getReference();
//
//        // Get a reference to the image file in Firebase Storage
//        StorageReference imageRef = storageRef.child("images/lanielsicangco@gmail.com");
//
//        // Download the image into a local file
//        File localFile = null;
//        try {
//            localFile = File.createTempFile("image", "jpg", getCacheDir());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        File finalLocalFile = localFile;
//        imageRef.getFile(localFile)
//                .addOnSuccessListener(taskSnapshot -> {
//                    // Image downloaded successfully, display it in the ImageView
//                    Bitmap bitmap = BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath());
//                    ImageView imageView = findViewById(R.id.imageView);
//                    imageView.setImageBitmap(bitmap);                                                     ERROR HERE ACCESSING      //java.lang.NullPointerException: Attempt to invoke virtual method 'void android.widget.ImageView.setImageBitmap(android.graphics.Bitmap)' on a null object referencejava.lang.NullPointerException: Attempt to invoke virtual method 'void android.widget.ImageView.setImageBitmap(android.graphics.Bitmap)' on a null object reference
//                })
//                .addOnFailureListener(exception -> {
//                    // Handle any errors
//                    //Log.e("TAG", "Error downloading image: " + exception.getMessage());
//                });
//
//
    }

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
}