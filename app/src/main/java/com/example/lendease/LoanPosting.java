package com.example.lendease;

import static java.lang.Double.parseDouble;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LoanPosting extends AppCompatActivity {

    private EditText nameEditText, loanAmountEditText, loanInterestEditText, idEditText, totalLoanAmountEditText,
            loanNoEditText, loanDateEditText;
    private TextView emailTextView;
    private Spinner paymentOptionSpinner, loanDurationSpinner;
    private Button submitButton;
    private LinearLayout tableLayout;
    private ScrollView tableScrollView;

    private FirebaseFirestore db;
    private static final String TAG = "LoanPosting";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // Class level dateFormat
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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loan_posting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        loanNoEditText = findViewById(R.id.postingLoanNoEditText);
        loanNoEditText.setEnabled(false);
        loanDateEditText = findViewById(R.id.postingLoanDateEditText);
        loanDateEditText.setEnabled(false);
        idEditText = findViewById(R.id.idEditText);
        nameEditText = findViewById(R.id.nameEditText);
        nameEditText.setEnabled(false);
        loanAmountEditText = findViewById(R.id.loanAmountEditText);
        loanInterestEditText = findViewById(R.id.loanInterestEditText);
        totalLoanAmountEditText = findViewById(R.id.totalLoanAmountEditText);
        totalLoanAmountEditText.setEnabled(false);
        paymentOptionSpinner = findViewById(R.id.paymentOptionSpinner);
        loanDurationSpinner = findViewById(R.id.loanDurationSpinner);
        submitButton = findViewById(R.id.submitButton);
        tableLayout = findViewById(R.id.tableLayout);
        tableScrollView = findViewById(R.id.tableScrollView);

        increment_loanNo();
        SimpleDateFormat sdf =  new SimpleDateFormat("MM-dd-yyyy");
        String currentDate = sdf.format(new Date());
        loanDateEditText.setText(currentDate);

        idEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // Focus lost, perform actions
                String enteredText = idEditText.getText().toString();
                if (hasFocus == false) {
                    dispalyinformation(enteredText);
                }
            }
        });

        loanAmountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String userInput = s.toString();
                computeTotalLoanAmount(userInput,"LoanAmount");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loanInterestEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String userInput = s.toString();
                computeTotalLoanAmount(userInput,"Interest");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        ArrayAdapter<CharSequence> paymentAdapter = ArrayAdapter.createFromResource(this,
                R.array.payment_options_array, android.R.layout.simple_spinner_item);
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentOptionSpinner.setAdapter(paymentAdapter);

        ArrayAdapter<CharSequence> durationAdapter = ArrayAdapter.createFromResource(this,
                R.array.loan_duration_array, android.R.layout.simple_spinner_item);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        loanDurationSpinner.setAdapter(durationAdapter);


        loanDurationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                displayTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        paymentOptionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                displayTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save Loan Posting Header
                Long loanNo = Long.parseLong(loanNoEditText.getText().toString());
                String loanDate = loanDateEditText.getText().toString();
                String id = idEditText.getText().toString();
                String name = nameEditText.getText().toString();
                String email = emailTextView.getText().toString();
                String loanAmountString = loanAmountEditText.getText().toString();
                String loanInterestString = loanInterestEditText.getText().toString();
                String loanDuration = loanDurationSpinner.getSelectedItem().toString();
                String paymentOption = paymentOptionSpinner.getSelectedItem().toString();
                String totalLoanAmountString = totalLoanAmountEditText.getText().toString();

                if (TextUtils.isEmpty(id) || TextUtils.isEmpty(name) || TextUtils.isEmpty(loanAmountString)
                        || TextUtils.isEmpty(loanInterestString) || TextUtils.isEmpty(loanDuration) || TextUtils.isEmpty(paymentOption) || TextUtils.isEmpty(totalLoanAmountString)) {
                    return;
                }

                double loanAmount = Double.valueOf(loanAmountString);
                double loanInterest = Double.valueOf(loanInterestString);
                double totalLoanAmount = Double.valueOf(totalLoanAmountString);

                // Create a new user
                Map<String, Object> loanheader = new HashMap<>();
                loanheader.put("loanNo", loanNo);
                loanheader.put("loanDate", loanDate);
                loanheader.put("id", id);
                loanheader.put("name", name);
                loanheader.put("email", email);
                loanheader.put("loanAmount", loanAmount);
                loanheader.put("loanInterest", loanInterest);
                loanheader.put("totalLoanAmount", totalLoanAmount);
                loanheader.put("amountPaid", Double.valueOf("0"));
                loanheader.put("balance", totalLoanAmount);
                loanheader.put("loanDuration", loanDuration);
                loanheader.put("paymentOption", paymentOption);

                // Saving the loanpostingheader information to Firestore
                db.collection("loanpostingheader")
                        .document(String.valueOf(loanNo))
                        .set(loanheader)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // Saving the loanpostingdetails as subcollection of loanpostingheader
                                CollectionReference scrollViewDataRef = db.collection("loanpostingheader")
                                        .document(String.valueOf(loanNo))
                                        .collection("loanpostingdetails");

                                ScrollView scrollView = findViewById(R.id.tableScrollView);
                                LinearLayout scrollViewContent = (LinearLayout) scrollView.getChildAt(0);

                                for (int i = 0; i < scrollViewContent.getChildCount(); i++) {
                                    LinearLayout rowLayout = (LinearLayout) scrollViewContent.getChildAt(i);
                                    TextView dateTextView = (TextView) rowLayout.getChildAt(0);
                                    TextView amountTextView = (TextView) rowLayout.getChildAt(1);

                                    String date = dateTextView.getText().toString();
                                    double amount = Double.parseDouble(amountTextView.getText().toString());

                                    // Create a map to represent the data
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("loanNo", loanNo);
                                    data.put("date", date);
                                    data.put("amount", amount);
                                    data.put("amountPaid", Double.valueOf("0"));
                                    data.put("balance", amount);

                                    // Assign a document ID manually
                                    String documentId = date;

                                    // Save the data to Firestore with the specified document ID
                                    scrollViewDataRef.document(documentId)
                                            .set(data)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("Firestore", "Document added with ID: " + documentId);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("Firestore", "Error adding document", e);
                                                }
                                            });
                                }
                                Toast.makeText(LoanPosting.this, "Successfully Save!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoanPosting.this, "Failed!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void computeTotalLoanAmount(String userInput, String type) {
        if (!userInput.isEmpty()) {
            double result = 0;
            if (type.equals("Interest")) {
                //Test kung numeric ang Interest Amount
                String loanAmount = loanAmountEditText.getText().toString();
                if (!TextUtils.isEmpty(loanAmount)) {
                    double lAmount = parseDouble(loanAmount);
                    result = ((parseDouble(userInput) / 100) * lAmount) + lAmount;
                }
            } else {
                //Test kung numeric ang Interest Amount
                String loanInterest = loanInterestEditText.getText().toString();
                if (!TextUtils.isEmpty(loanInterest)) {
                    double interestAmount = parseDouble(loanInterest);
                    result = ((interestAmount / 100) * parseDouble(userInput)) + parseDouble(userInput);
                }
            }
            // Display the result in the TextView
            totalLoanAmountEditText.setText(String.valueOf(result));
        } else {
            totalLoanAmountEditText.setText("");
        }
    }

    private double calculateTotalAmount(double loanAmount, double loanInterest) {
        double interestDecimal = loanInterest / 100;
        double totalAmount = loanAmount * (1 + interestDecimal);
        return totalAmount;
    }

    private void displayTable() {
        String name = nameEditText.getText().toString();
        String loanAmountString = loanAmountEditText.getText().toString();
        String loanInterestString = loanInterestEditText.getText().toString();
        String loanDuration = loanDurationSpinner.getSelectedItem().toString();
        String id = idEditText.getText().toString();
        String paymentOption = paymentOptionSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(loanDuration) || TextUtils.isEmpty(loanAmountString) || TextUtils.isEmpty(loanInterestString)) {
            tableLayout.removeAllViews();
            return;
        } else {

            double loanAmount = parseDouble(loanAmountString);
            double loanInterest = parseDouble(loanInterestString);
            double totalAmount = calculateTotalAmount(loanAmount, loanInterest);
            int paymentFrequency = Integer.parseInt(loanDuration);
            double paymentAmount = totalAmount / paymentFrequency;

            Calendar calendar = Calendar.getInstance();

            if (paymentOption.equals("Weekly")) {
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
            } else if (paymentOption.equals("Monthly")) {
                calendar.add(Calendar.MONTH, 1);
            }

            tableLayout.removeAllViews(); // Clear existing views

            for (int i = 0; i < paymentFrequency; i++) {
                Date nextPaymentDate = calendar.getTime();
                String formattedDate = dateFormat.format(nextPaymentDate);

                LinearLayout rowLayout = new LinearLayout(this);
                rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);

                TextView dateTextView = new TextView(this);
                dateTextView.setLayoutParams(new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                dateTextView.setPadding(8, 8, 8, 8);
                dateTextView.setText(formattedDate);

                TextView amountTextView = new TextView(this);
                amountTextView.setLayoutParams(new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                amountTextView.setPadding(8, 8, 8, 8);
                amountTextView.setText(String.format("%.2f", paymentAmount));

                rowLayout.addView(dateTextView);
                rowLayout.addView(amountTextView);
                tableLayout.addView(rowLayout);

                if (paymentOption.equals("Weekly")) {
                    calendar.add(Calendar.WEEK_OF_YEAR, 1);
                } else if (paymentOption.equals("Monthly")) {
                    calendar.add(Calendar.MONTH, 1);
                }
            }

            tableScrollView.setVisibility(View.VISIBLE);
        }
    }

    private void saveLoanDetails() {
        String id = idEditText.getText().toString();
        String name = nameEditText.getText().toString();
        String loanAmountString = loanAmountEditText.getText().toString();
        String loanInterestString = loanInterestEditText.getText().toString();
        String loanDuration = loanDurationSpinner.getSelectedItem().toString();
        String paymentOption = paymentOptionSpinner.getSelectedItem().toString();
        String totalLoanAmountString = totalLoanAmountEditText.getText().toString();

        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(name) || TextUtils.isEmpty(loanAmountString)
                || TextUtils.isEmpty(loanInterestString) || TextUtils.isEmpty(loanDuration) || TextUtils.isEmpty(paymentOption) || TextUtils.isEmpty(totalLoanAmountString)) {
            return;
        }

        long loanAmount = Long.parseLong(loanAmountString);
        long loanInterest = Long.parseLong(loanInterestString);

        saveLoanDetailsToFirestore(id, name, loanAmount, loanInterest, loanDuration, paymentOption);
    }

    private void saveLoanDetailsToFirestore(String id, String name, long loanAmount, long loanInterest, String loanDuration, String paymentOption) {
        Map<String, Object> loanDetails = new HashMap<>();
        loanDetails.put("name", name);
        loanDetails.put("loanAmount", loanAmount);
        loanDetails.put("loanInterest", loanInterest);
        loanDetails.put("loanDuration", loanDuration);
        loanDetails.put("paymentOption", paymentOption);

        Calendar calendar = Calendar.getInstance();  // today's date as the start date

        loanDetails.put("startDate", dateFormat.format(calendar.getTime()));

        db.collection("loanDetails")
                .document(id)
                .set(loanDetails)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Loan details added with ID: " + id);
                    createPayments(id, loanAmount, loanInterest, Integer.parseInt(loanDuration), paymentOption, calendar.getTime());
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error adding loan details", e));
    }

    private void createPayments(String loanId, long loanAmount, long loanInterest, int duration, String paymentOption, Date startDate) {
        double totalLoanAmount = loanAmount * (1 + loanInterest / 100.0);
        double monthlyPaymentAmount = totalLoanAmount / duration;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        for (int i = 0; i < duration; i++) {
            Map<String, Object> paymentDetails = new HashMap<>();
            paymentDetails.put("date", dateFormat.format(calendar.getTime()));
            paymentDetails.put("amount", monthlyPaymentAmount);
            paymentDetails.put("status", "unpaid");

            db.collection("loanDetails")
                    .document(loanId)
                    .collection("payments")
                    .add(paymentDetails)
                    .addOnSuccessListener(documentReference -> Log.d(TAG, "Payment scheduled with ID: " + documentReference.getId()))
                    .addOnFailureListener(e -> Log.e(TAG, "Error scheduling payment", e));

            if (paymentOption.equals("Weekly")) {
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
            } else if (paymentOption.equals("Monthly")) {
                calendar.add(Calendar.MONTH, 1);
            }
        }
    }

    private void dispalyinformation(String idnoSearch) {
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("customerinfo");

        // Create a query to search for documents where the field "name" equals "John"
        Query query = usersRef.whereEqualTo("id", idnoSearch);

        // Perform the query
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Query successful
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        // Iterate through the documents
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
//                            // Handle each document here
                            Map<String, Object> userData = document.getData();
                            nameEditText.setText(userData.get("fullname").toString());
                            emailTextView = findViewById(R.id.emailTextView);
                            emailTextView.setText(userData.get("email").toString());
                            String email = emailTextView.getText().toString();
                            displayimage(email);
                        }
                    } else {
                        // No documents found
                        Toast.makeText(LoanPosting.this, "ID not found!", Toast.LENGTH_SHORT).show();
                        idEditText.setText("");
                        nameEditText.setText("");
                        ImageView imageView = findViewById(R.id.uploadedImageView);
                        imageView.setImageBitmap(null);
                    }
                } else {
                    // Query failed
                    Log.d("Firestore", "Error getting documents: ", task.getException());
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
                    ImageView imageView = findViewById(R.id.uploadedImageView);
                    imageView.setImageBitmap(bitmap);
                })
                .addOnFailureListener(exception -> {
                    // Handle any errors
                    //Log.e("TAG", "Error downloading image: " + exception.getMessage());
                });
    }

    private void increment_loanNo() {
        String assignloanNo = String.valueOf(System.currentTimeMillis());
        loanNoEditText.setText(assignloanNo);
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        CollectionReference collectionRef = db.collection("loanpostingheader");
//
//        collectionRef.orderBy("loanNo", Query.Direction.DESCENDING)
//                .limit(1)
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        if (!queryDocumentSnapshots.isEmpty()) {
//                            DocumentSnapshot lastDocument = queryDocumentSnapshots.getDocuments().get(0);
//                            // Access the last document data here
//                            Object lastValue = lastDocument.get("loanNo");
//                            int idValue = Integer.parseInt(lastValue.toString()) + 1;
//                            loanNoEditText.setText(String.valueOf(idValue));
//                            // Use the last value as needed
//                        } else {
//                            // Handle the case where no documents are found
//                            loanNoEditText.setText(String.valueOf(1));
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // Handle any errors
//                        loanNoEditText.setText(String.valueOf(1));
//                    }
//                });
    }
}