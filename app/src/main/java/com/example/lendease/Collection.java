package com.example.lendease;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class Collection extends AppCompatActivity {
    private EditText idEditText, collectNoEditText, collectDateEditText,
            amortizationDateEditText, addPaidAmountEdittext ;
    private ImageView photoImageViewCollect;
    private Spinner loanNoChoiceSpinner;
    private TextView nameTextView , totalLoanAmountTextView, amountPaidTextView, loanBalanceTextView,
            emailTextView;
    private Button updateBtn;
    private Spinner loanChoiceSpinner;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_collection);
        collectNoEditText = findViewById(R.id.collectNoEditText);
        collectDateEditText = findViewById(R.id.collectDateEditText);
        increment_collectno();
        loanNoChoiceSpinner = findViewById(R.id.loanNoChoiceSpinner);
        idEditText = findViewById(R.id.idEditText);
        nameTextView = findViewById(R.id.nameTextView);
        photoImageViewCollect = findViewById(R.id.photoImageViewCollect);
        totalLoanAmountTextView = findViewById(R.id.totalLoanAmountTextView);
        amountPaidTextView = findViewById(R.id.amountPaidTextView);
        loanBalanceTextView = findViewById(R.id.loanBalanceTextView);
        amortizationDateEditText = findViewById(R.id.amortizationDateEditText);
        addPaidAmountEdittext = findViewById(R.id.addPaidAmountEdittext);
        emailTextView = findViewById(R.id.emailTextView);
        updateBtn = findViewById(R.id.updateBtn);

        //ID search the loan list with balance
        idEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String userInput = s.toString();
                getLoanNoList(userInput);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loanNoChoiceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLoanNo = parent.getItemAtPosition(position).toString();
                displayLoan(Long.parseLong(selectedLoanNo));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


//        loanNoEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                displayLoan(Integer.parseInt(loanNoEditText.getText().toString()));
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCollection();
            }
        });
    }

    private void saveCollection() {
        // Save Loan Posting Header
        Integer collectNo = Integer.parseInt(collectNoEditText.getText().toString());
        String collectDate = collectDateEditText.getText().toString();
        String loanNo = loanNoChoiceSpinner.getSelectedItem().toString();//loanNoEditText.getText().toString();
        String id = idEditText.getText().toString();
        String name = nameTextView.getText().toString();
        String email = emailTextView.getText().toString();
        String totalLoanAmount = totalLoanAmountTextView.getText().toString();
        String amountPaid = amountPaidTextView.getText().toString();
        String loanBalance = loanBalanceTextView.getText().toString();
        String amortizationDate = amortizationDateEditText.getText().toString();
        String addPaidAmount = addPaidAmountEdittext.getText().toString();

        if (TextUtils.isEmpty(loanNo) || TextUtils.isEmpty(amortizationDate) || TextUtils.isEmpty(addPaidAmount)) {
            return;
        }

        // loanNo = Integer.parseInt(loanNoEditText.getText().toString());
        double totalLoanAmountdbl = Double.valueOf(totalLoanAmount);
        double amountPaiddbl = Double.valueOf(amountPaid);
        double loanbalancedbl = Double.valueOf(loanBalance);
        double addPaidAmountdbl = Double.valueOf(addPaidAmount);

        // Create a new user
        Map<String, Object> collection = new HashMap<>();
        collection.put("collectNo", collectNo);
        collection.put("collectDate", collectDate);
        collection.put("loanNo", loanNo);
        collection.put("id", id);
        collection.put("name", name);
        collection.put("email", email);
        collection.put("totalloanAmount", totalLoanAmountdbl);
        collection.put("amountPaid", amountPaiddbl);
        collection.put("loanbalance", loanbalancedbl);
        collection.put("amortizationDate",amortizationDate);
        collection.put("addPaidAmount", addPaidAmountdbl);

        // Saving the loanpostingheader information to Firestore
        db.collection("collectionposting")
                .document(String.valueOf(collectNo))
                .set(collection)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {



                        // Search for the document in the "loanpostingheader" collection
                        DocumentReference documentRef = db.collection("loanpostingheader")
                                .document("" + loanNo);

                        documentRef.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        // Document exists, retrieve the current amountPaid, totalLoanAmount, and balance fields
                                        Double totalLoanAmount = documentSnapshot.getDouble("totalLoanAmount");
                                        Double amountPaid = documentSnapshot.getDouble("amountPaid");
                                        Double balance = documentSnapshot.getDouble("balance");

                                        // Update the amountPaid and balance fields based on certain conditions
                                        Double newAmountPaid = amountPaid + addPaidAmountdbl;
                                        Double newBalance = totalLoanAmount - newAmountPaid;

                                        // Update the "amountPaid" and "balance" fields in the document
                                        documentRef.update("amountPaid", newAmountPaid, "balance", newBalance)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("Firestore", "AmountPaid and Balance updated successfully in loanpostingheader");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("Firestore", "Error updating AmountPaid and Balance in loanpostingheader", e);
                                                    }
                                                });

                                        // Now update the "amountPaid" field in the "loanpostingdetails" subcollection
                                        updateAmountPaidInSubcollection(documentRef, addPaidAmountdbl, amortizationDate);
                                    } else {
                                        // Document with the specified ID does not exist
                                        Log.d("Firestore", "Document not found in loanpostingheader");
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle any errors
                                    Log.w("Firestore", "Error getting document from loanpostingheader", e);
                                }
                            });



                        Toast.makeText(Collection.this, "Successfully Save!",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Collection.this, "Failed!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Function to update the "amountPaid" field in the "loanpostingdetails" subcollection
    private void updateAmountPaidInSubcollection(DocumentReference documentRef, double addAmountPaid, String amortizationDate) {
        // Get a reference to the "loanpostingdetails" subcollection
        CollectionReference subCollectionRef = documentRef.collection("loanpostingdetails");

        // Get a reference to the specific document using the amortizationDate
        DocumentReference docRef = subCollectionRef.document(amortizationDate);

        // Get the current amountPaid from the document
        docRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve current amountPaid and balance
                        double currentAmountPaid = documentSnapshot.getDouble("amountPaid");
                        double currentBalance = documentSnapshot.getDouble("balance");

                        // Calculate the new amountPaid and balance
                        double newAmountPaid = currentAmountPaid + addAmountPaid;
                        double newBalance = Double.valueOf(documentSnapshot.getDouble("amount").toString()) - addAmountPaid;

                        // Update the "amountPaid" and "balance" fields in the subcollection document
                        docRef.update("amountPaid", newAmountPaid, "balance", newBalance)
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "AmountPaid and Balance updated successfully in loanpostingdetails for document ID: " + amortizationDate))
                                .addOnFailureListener(e -> Log.w("Firestore", "Error updating AmountPaid and Balance in loanpostingdetails for document ID: " + amortizationDate, e));
                    } else {
                        Log.d("Firestore", "Document does not exist for amortization date: " + amortizationDate);
                    }
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error getting document from loanpostingdetails for amortization date: " + amortizationDate, e));
    }


    private void increment_collectno() {
        SimpleDateFormat sdf =  new SimpleDateFormat("MM-dd-yyyy");
        String currentDate = sdf.format(new Date());
        collectDateEditText.setText(currentDate);

        db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("collectionposting");

        collectionRef.orderBy("collectNo", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot lastDocument = queryDocumentSnapshots.getDocuments().get(0);
                            // Access the last document data here
                            Object lastValue = lastDocument.get("collectNo");
                            int idValue = Integer.parseInt(lastValue.toString()) + 1;
                            collectNoEditText.setText(String.valueOf(idValue));
                            // Use the last value as needed
                        } else {
                            // Handle the case where no documents are found
                            collectNoEditText.setText(String.valueOf(1));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors
                        collectNoEditText.setText(String.valueOf(1));
                    }
                });
    }

    private void getLoanNoList(String idSearch) {
        // Assuming you have a Firestore instance initialized
        db = FirebaseFirestore.getInstance();

        // Assuming you have a Spinner with the ID "spinner" in your layout
        Spinner spinner = findViewById(R.id.loanNoChoiceSpinner);
        // Query Firestore for loan numbers where idNo matches and balance is greater than 0
        db.collection("loanpostingheader")
                .whereEqualTo("id", idSearch)
//                .whereGreaterThan("balance", 0)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    List<Long> loanNumbers = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot.getDouble("balance").doubleValue() > 0) {
                            Long loanNo = documentSnapshot.getLong("loanNo");
                            loanNumbers.add(loanNo);
                        }
                    }
                    // Create an ArrayAdapter using the loanNumbers list
                    ArrayAdapter<Long> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, loanNumbers);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // Apply the adapter to the spinner
                    spinner.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e("Firestore", "Error querying loan numbers with balance", e);
                });
    }

    private void displayLoan(long loanNoSearch) {

        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference loanRef = db.collection("loanpostingheader");

//        Toast.makeText(this, "" + loanNoSearch, Toast.LENGTH_LONG).show();
        // Create a query to search for documents where the field "name" equals "John"
        Query query = loanRef.whereEqualTo("loanNo", loanNoSearch);

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
                            Map<String, Object> loanData = document.getData();
//                            idEditText.setText(loanData.get("id").toString());
//                            nameTextView.setText(loanData.get("name").toString());
//                            Toast.makeText(Collection.this, "" + loanData.get("loanAmount").toString(),
//                                    Toast.LENGTH_LONG).show();
                            totalLoanAmountTextView.setText(loanData.get("totalLoanAmount").toString());
                            amountPaidTextView.setText(loanData.get("amountPaid").toString());
                            loanBalanceTextView.setText(loanData.get("balance").toString());
                            emailTextView.setText(loanData.get("email").toString());
                            displayimage(emailTextView.getText().toString());

                            //Search for the amortization not paid
                            CollectionReference parentCollectionRef = db.collection("loanpostingheader");
                            CollectionReference subCollectionRef = parentCollectionRef.document("" + loanNoSearch)
                                    .collection("loanpostingdetails");

                            subCollectionRef.orderBy(FieldPath.documentId(), Query.Direction.ASCENDING)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                                String documentId = document.getId();
                                                amortizationDateEditText.setText(documentId);
                                                // Retrieve the balance field from the document
                                                Double amountPaid = document.getDouble("amountPaid");
                                                Double balance = document.getDouble("balance");
//                                                Toast.makeText(Collection.this, balance + " " + documentId, Toast.LENGTH_SHORT).show();
                                                if (amountPaid != null && amountPaid == 0) {
                                                    // Handle documents where balance is zero
//                                                    Log.d("Document ID with Zero Balance", documentId);
//                                                    Toast.makeText(Collection.this, "" + documentId, Toast.LENGTH_SHORT).show();
                                                    addPaidAmountEdittext.setText(String.valueOf(balance));
                                                    return;
                                                }
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
                    } else {
                        // No documents found
                        Toast.makeText(Collection.this, "Loan NO. not found!", Toast.LENGTH_SHORT).show();
                        //idEditText.setText("");
                        nameTextView.setText("");
                        totalLoanAmountTextView.setText("");
                        amountPaidTextView.setText("");
                        loanBalanceTextView.setText("");
                        ImageView photoImageView = findViewById(R.id.photoImageViewCollect);
                        photoImageView.setImageBitmap(null);
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
                    ImageView imageView = findViewById(R.id.photoImageViewCollect);
                    imageView.setImageBitmap(bitmap);
                })
                .addOnFailureListener(exception -> {
                    // Handle any errors
                    //Log.e("TAG", "Error downloading image: " + exception.getMessage());
                });
    }
}