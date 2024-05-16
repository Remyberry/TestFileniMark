package com.example.lendease;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth. FirebaseAuth;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomerInfo extends AppCompatActivity {
    StorageReference storageReference;
    Uri image;
    ImageView imageselect, imageCamera;
    TextView id, fullName, address, contactNo, email, gender, birthday, password, reenter_password;

    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_info);
        FirebaseApp.initializeApp(CustomerInfo.this);
        storageReference = FirebaseStorage.getInstance().getReference();

        FirebaseAuth Auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        id = findViewById(R.id.customerID);
        id.setEnabled(false);
        fullName = findViewById(R.id.fullName);
        address = findViewById(R.id.address);
        contactNo = findViewById(R.id.contactNo);
        email = findViewById(R.id.email);
        gender = findViewById(R.id.gender);
        birthday = findViewById(R.id.birthday);
        password = findViewById(R.id.admin_password);
        reenter_password = findViewById(R.id.reenter_password);
        ImageView save = findViewById(R.id.save);
        ImageView home_nav_ci = findViewById((R.id.home_nav_ci));

        increment_idno();

//        Intent intent = getIntent();
//        if(intent != null) {
//            String data = intent.getStringExtra("key");
//            // Use the data
//            dispalyinformation("joshuayalung555@gmail.com");
//        }

        home_nav_ci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerInfo.this, AdminDashboard.class));
            }
        });

        imageselect = findViewById(R.id.imageSelect);
        //By Clicking the Camera
        imageCamera = findViewById(R.id.imageCamera);
        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String test_id = id.getText().toString();
                String test_fullname = fullName.getText().toString();
                String test_address = address.getText().toString();
                String test_contactNo = contactNo.getText().toString();
                String test_email = email.getText().toString();
                String test_gender = gender.getText().toString();
                String test_birthday = birthday.getText().toString();
                String test_password = password.getText().toString();
                String test_reenter_password = reenter_password.getText().toString();

                if (image == null) {
                    Toast.makeText(CustomerInfo.this, "Please upload picture!",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                //Validate TextView if blank or empty
                if (TextUtils.isEmpty(test_id) || TextUtils.isEmpty(test_fullname) || TextUtils.isEmpty(test_address)
                        || TextUtils.isEmpty(test_contactNo) || TextUtils.isEmpty(test_email) || TextUtils.isEmpty(test_gender)
                        || TextUtils.isEmpty(test_birthday) || TextUtils.isEmpty(test_password)
                        || TextUtils.isEmpty(test_reenter_password)) {
                    Toast.makeText(CustomerInfo.this, "Please fill up all fields!",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                //Validate password and retype-password if the same
                if (!test_password.equals(test_reenter_password)) {
                    Toast.makeText(CustomerInfo.this,"Password does not match!",Toast.LENGTH_LONG).show();
                    return;
                }

                // Create a new user
                Map<String, Object> customer = new HashMap<>();
                customer.put("id", test_id);
                customer.put("fullname", test_fullname);
                customer.put("address", test_address);
                customer.put("contactno", test_contactNo);
                customer.put("email", test_email);
                customer.put("gender", test_gender);
                customer.put("birthday", test_birthday);
//                customer.put("password", test_password);
                customer.put("customer_log_type", "user");

                Auth.fetchSignInMethodsForEmail(test_email) //Email enumeration protection (recommended) should be disable
                    .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            if (task.isSuccessful()) {
                                SignInMethodQueryResult result = task.getResult();
                                if (result != null && result.getSignInMethods() != null && !result.getSignInMethods().isEmpty()) {
                                    Toast.makeText(CustomerInfo.this, "Email exists for authentication", Toast.LENGTH_SHORT).show();
                                    return;
                                } else {
//                                    Toast.makeText(CustomerInfo.this, "Email does not exists for authentication", Toast.LENGTH_SHORT).show();

                                    //Authentication to save the email and password
                                    //Save Email in FireAuthentication
                                    Auth.createUserWithEmailAndPassword(test_email, test_password).
                                        addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {

                                                    //Saving the information in to firebase
                                                    DocumentReference docref = db.collection("customerinfo").document(test_email);
                                                    docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                DocumentSnapshot document = task.getResult();
                                                                if (task.getResult().exists()) {
                                                                    Toast.makeText(CustomerInfo.this, "Your chosen email is already taken.",
                                                                            Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    db.collection("customerinfo")
                                                                            .document(test_email)
                                                                            .set(customer).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
//                                                                      Toast.makeText(CustomerInfo.this,
//                                                                            "You have registered successfully.",
//                                                                            Toast.LENGTH_LONG).show();
                                                                                    uploadImage(image, test_email);
                                                                                    Toast.makeText(CustomerInfo.this, "Created Successfully",
                                                                                            Toast.LENGTH_SHORT).show();
                                                                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    Toast.makeText(CustomerInfo.this, "Failed!",
                                                                                            Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        }
                                                    });

                                                } else {
                                                    Toast.makeText(CustomerInfo.this, "Error Occurred" +
                                                                    task.getException().getMessage(),
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });






                                }
                            } else {
                                Toast.makeText(CustomerInfo.this,  "Found Error in Email Authentication", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });

            }
        });
    }

    private void uploadImage(Uri file, String imgName) {
        StorageReference ref = storageReference.child("images/" + imgName); //UUID.randomUUID().toString());
        //StorageReference ref = storageReference.child("images/" + "imagename");
        ref.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Toast.makeText(CustomerInfo.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CustomerInfo.this, "Failed!" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
//                progressIndicator.setMax(Math.toIntExact(taskSnapshot.getTotalByteCount()));
//                progressIndicator.setProgress(Math.toIntExact(taskSnapshot.getBytesTransferred()));
            }
        });
    }

    private void increment_idno() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("customerinfo");

        collectionRef.orderBy("id", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot lastDocument = queryDocumentSnapshots.getDocuments().get(0);
                            // Access the last document data here
                            Object lastValue = lastDocument.get("id");
                            int idValue = Integer.parseInt(lastValue.toString()) + 1;
                            id.setText(String.valueOf(idValue));
                            // Use the last value as needed
                        } else {
                            // Handle the case where no documents are found
                            id.setText(String.valueOf("1001"));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors
                    }
                });
    }
    
    //Display Information
    private void dispalyinformation(String emailSearch) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
       
        // Document ID to search
        final String documentId = emailSearch;

        // Document reference
        DocumentReference docRef = db.collection("customerinfo").document(documentId);

        // Get document data
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Document found, display its data
//                        String name = document.getString("name");
//                        String email = document.getString("email");
//                        long age = document.getLong("age");
//
//                        // Display data
//                        nameTextView.setText("Name: " + name);
//                        emailTextView.setText("Email: " + email);
//                        ageTextView.setText("Age: " + age);

                        displayimage(document.getString("email"));

                        id.setText(document.getString("id"));
                        fullName.setText(document.getString("fullname"));
                        address.setText(document.getString("address"));
                        contactNo.setText(document.getString("contactno"));
                        email.setText(document.getString("email"));
                        gender.setText(document.getString("gender"));
                        birthday.setText(document.getString("birthday"));

                        TextView textViewPassword = findViewById(R.id.textViewPassword);
                        TextView textViewReEnterPassword = findViewById(R.id.textViewReEnterPassword);

                        textViewPassword.setVisibility(View.GONE);
                        password.setVisibility(View.GONE);

                        textViewReEnterPassword.setVisibility(View.GONE);
                        reenter_password.setVisibility(View.GONE);

//                        ImageView save = findViewById(R.id.save);
                    } else {
//                        Log.d(TAG, "No such document");
                    }
                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void displayimage(String emailSearch) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Get a reference to the image file in Firebase Storage
        StorageReference imageRef = storageRef.child("images/" + emailSearch);
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
                    ImageView imageView = findViewById(R.id.imageSelect);
                    imageView.setImageBitmap(bitmap);
                })
                .addOnFailureListener(exception -> {
                    // Handle any errors
                    //Log.e("TAG", "Error downloading image: " + exception.getMessage());
                });
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    imageCamera.setEnabled(true);
                    image = result.getData().getData();
                    Glide.with(getApplicationContext()).load(image).into(imageselect);
                }
            } else {
                Toast.makeText(CustomerInfo.this, "Please select an image", Toast.LENGTH_SHORT).show();
            }
        }
    });
}