package com.nadeem.fadesalon.clientSide;
// class handles any change of profile info

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.nadeem.fadesalon.MainActivity;
import com.nadeem.fadesalon.R;
import com.nadeem.fadesalon.models.User;

public class UpdateProfile extends AppCompatActivity {

    private ImageView arrowBack;
    private Button updateBtn;
    EditText nameField,emailField;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    DocumentReference documentReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String currentID;
    User user;
    private View mParentLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        this.currentID = user.getUid();
        documentReference= db.collection("users").document(currentID);

        mParentLayout = findViewById(android.R.id.content);

        arrowBack = findViewById(R.id.back_arrow);
        updateBtn = findViewById(R.id.saveBtn);
        nameField = findViewById(R.id.nameField);
        emailField = findViewById(R.id.emailField);

        /* ********************  Arrow back Button   *************** */
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfile.this, MainActivity.class);
                startActivity(intent);
            }
        });

        /* ********************  SaveChanges Button   *************** */
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChange();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // when click on update that will show the user that data inserted.

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.getResult().exists()) {
                    // if profile exist it will take the data from the Collection &
                    // String field should match the DB rows.
                    String nameResult = task.getResult().getString("FullName");
                    String emailResult = task.getResult().getString("Mail");

                    // i tried to use toObject but didn't work :(
//                    user =task.getResult().toObject(User.class);



                    // it will set the result value to display in textView field.
                    nameField.setText(nameResult);
                    emailField.setText(emailResult);
                }
                else{
                    Toast.makeText(UpdateProfile.this, "No Profile exist", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    // function to update the changed profile info into fireBase.
    private void saveChange() {

        String name = nameField.getText().toString();
        String email = emailField.getText().toString();


        final  DocumentReference saveDoc = db.collection("users").document(this.currentID);
        db.runTransaction(new Transaction.Function<Void>() {
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
                makePositiveSnackBarMessage("Change Saved Successfully.");
//                Toast.makeText(UpdateProfile.this, "Change Saved Successfully", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeNegativeSnackBarMessage("Change failed. \n"+e);
//                        Toast.makeText(UpdateProfile.this, "Change failed. \n"+e, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void makePositiveSnackBarMessage(String message){
        Snackbar snackbar = Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(ContextCompat.getColor(this,R.color.colorGreen));
        snackbar.setTextColor(ContextCompat.getColor(this,R.color.colorBlack));
        snackbar.show();
    }
    private void makeNegativeSnackBarMessage(String message){
        Snackbar snackbar = Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(ContextCompat.getColor(this,R.color.colorRed));
        snackbar.setTextColor(ContextCompat.getColor(this,R.color.colorWhite));
        snackbar.show();
    }

    //to keep the app running in the back
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
