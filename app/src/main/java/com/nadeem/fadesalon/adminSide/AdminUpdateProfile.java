package com.nadeem.fadesalon.adminSide;
/* class handles the admin profile data with firebase for any update */
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.nadeem.fadesalon.R;
import com.nadeem.fadesalon.models.User;

public class AdminUpdateProfile extends AppCompatActivity {

    private ImageView arrowBack;
    private EditText nameFetch,emailFetch;
    private Button saveBtn;

    User userObject;
    FirebaseFirestore firestore; // for barber reference
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_update_profile);

        arrowBack = findViewById(R.id.back_arrow);
        saveBtn = findViewById(R.id.saveBtn);
        nameFetch = findViewById(R.id.nameFetch);
        emailFetch = findViewById(R.id.emailFetch);


        /* ********************  Arrow back Button   *************** */
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminUpdateProfile.this, AdminDashboardActivity.class);
                startActivity(intent);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminUpdateProfile.this, AdminDashboardActivity.class);
                saveChange();
                Toast.makeText(AdminUpdateProfile.this, "Changes Saved. ", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        getBarber();
    }

    /* function get's the barber data from firebase */
    public User getBarber() {

        user = FirebaseAuth.getInstance().getCurrentUser();
        String CurrentId = user.getUid();
        DocumentReference reference;
        firestore = FirebaseFirestore.getInstance();

        //Document reference to users Collection using user ID
        reference = firestore.collection("barbers").document(CurrentId);
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    // if profile exist it will take the data from the Collection &
                    // String field should match the DB rows.
                    String nameResult = task.getResult().getString("FullName");
                    String emailResult = task.getResult().getString("Mail");
                    String userTypeResult = task.getResult().getString("UserType");
                    String statusResult = task.getResult().getString("Status");

                    userObject = new User(CurrentId, nameResult, emailResult, userTypeResult, statusResult);
                    nameFetch.setText(nameResult);
                    emailFetch.setText(emailResult);

                } else {
                    Toast.makeText(getApplicationContext(), "Failed fetching Barber Data", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return userObject;
    }

    /* function to save the changes of barber profile information */
    private void saveChange() {

        String name = nameFetch.getText().toString();
        String email = emailFetch.getText().toString();
        userObject = getBarber();


        final  DocumentReference saveDoc = firestore.collection("barbers").document(userObject.getUser_id());
        firestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                //DocumentSnapshot snapshot = transaction.get(saveDoc);

                //field String should match to the field in collection , name is the variable this function.
                transaction.update(saveDoc, "FullName", name);
                transaction.update(saveDoc, "Mail", email);

                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AdminUpdateProfile.this, "Change Saved Successfully", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminUpdateProfile.this, "Change failed. \n"+e, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    //to keep the app running in the back
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
