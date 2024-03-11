package com.nadeem.fadesalon.adminSide;
/*  Class handles the Admins Registration Page */
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nadeem.fadesalon.MainActivity;
import com.nadeem.fadesalon.R;

import java.util.HashMap;
import java.util.Map;

public class adminRegisteraionActivity extends AppCompatActivity {

    private ImageView arrowBack;
    private EditText inputFullname, inputEmail, inputPassword, inputCConformPassword;
    private Button btnRegister;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;//Creating member variable for FirebaseAuth
    FirebaseUser mUser;
    ConnectivityManager connectivityManager;
    private FirebaseFirestore fStore;
    String userID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_registeraion);

        /* ********************  Hooks   *************** */
        arrowBack = findViewById(R.id.back_arrow);
        inputFullname = findViewById(R.id.nameField);
        inputEmail = findViewById(R.id.emailField);
        inputPassword = findViewById(R.id.passField);
        inputCConformPassword = findViewById(R.id.ReTypePassField);
        btnRegister = findViewById(R.id.ContinueBtn);
        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);




        /* ********************  Arrow back Button   *************** */
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(adminRegisteraionActivity.this, AdminProfileActivity.class);
                startActivity(intent);
            }
        });

        /* ********************  When Pressing on Register Button   *************** */
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performeAuth();
            }
        });

    }

    /* function to Authenticate the the signed information for the admin registration before adding it to firebase */
    private void performeAuth() {
        String fullname = inputFullname.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String confirmPassword = inputCConformPassword.getText().toString().trim();

        /* ********************  Checking Connectivity   *************** */
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).
                getState() != NetworkInfo.State.CONNECTED && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).
                getState() != NetworkInfo.State.CONNECTED) {
            Toast.makeText(adminRegisteraionActivity.this, "No internet conection", Toast.LENGTH_SHORT).show();
            return;
        }
        /* ********************  checking if any of the fields are empty   *************** */
        if (fullname.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            if (TextUtils.isEmpty(fullname)) inputFullname.setError("Name required");
            if (TextUtils.isEmpty(email)) inputEmail.setError("Email is required");
            if (TextUtils.isEmpty(password)) inputPassword.setError("Passwords required");
            if (TextUtils.isEmpty(confirmPassword))
                inputCConformPassword.setError("Confirm Password required");
        }
        /* ********************  checking if email uses the Required pattern   *************** */
        if (!email.matches(emailPattern)) {
            inputEmail.setError("Enter Correct Email");
        }
        /* ********************  Checking password if empty or less than 6 figures  *************** */
        else if (password.length() < 6) {
            inputPassword.setError("Enter Password is short than six figures");
        }
        /* ********************  checking if passwords are equal   *************** */
        else if (!password.equals(confirmPassword)) {
            inputCConformPassword.setError("password does not match !");
        }
        /* ********************  otherwise everything is perfect & performing a Registration   *************** */
        else {
            progressDialog.setMessage("Please Wait Registration...");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        userID = mAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = fStore.collection("barbers").document(userID);
                        Map<String, Object> userMap = new HashMap<>();

                        userMap.put("UserID", userID);
                        userMap.put("FullName", fullname);
                        userMap.put("Mail", email);
                        userMap.put("UserType", "barber");
                        userMap.put("Status", "active");

                        progressDialog.dismiss();
                        documentReference.set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(adminRegisteraionActivity.this, fullname + ", Wellcome to our Salon Family", Toast.LENGTH_SHORT).show();
                                SendUserToNextPage();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                String error = e.getMessage();
                                Toast.makeText(adminRegisteraionActivity.this, "Error :" + error, Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(adminRegisteraionActivity.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /* function to intent from this page to main activity */
    private void SendUserToNextPage() {
        Intent intent = new Intent(adminRegisteraionActivity.this, MainActivity.class);

        // this  will stop come back to this activity when user successfully register.
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //to keep the app running in the back
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
