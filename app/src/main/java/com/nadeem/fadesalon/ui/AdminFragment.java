package com.nadeem.fadesalon.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.nadeem.fadesalon.MainActivity;
import com.nadeem.fadesalon.R;
import com.nadeem.fadesalon.adminSide.AdminDashboardActivity;
import com.nadeem.fadesalon.models.User;

import java.util.ArrayList;
import java.util.List;

// class handles admin login so he can access admin dashboard and activities.
public class AdminFragment extends Fragment {

    private ImageView arrowBack;
    private EditText inputEmail,inputPassword;
    private Button btnSignIn;
    private ProgressDialog progressDialog;
    private ConnectivityManager connectivityManager;
    TextView resetPassTv;
    private View mParentLayout;


    private FirebaseFirestore firestore; // for barber reference
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    public List<User> usersDocs;
    public String userStatus="";



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_admin, container, false);

        mParentLayout = root.getRootView();
        arrowBack = root.findViewById(R.id.back_arrow);
        inputEmail = root.findViewById(R.id.emailField);
        inputPassword = root.findViewById(R.id.passField);
        btnSignIn = root.findViewById(R.id.ContinueBtn);
        resetPassTv = root.findViewById(R.id.resetPassTv);

        progressDialog = new ProgressDialog(getActivity());
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();


        connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);



        /* ********************  Arrow back Button   *************** */
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        /* ********************  When Pressing on Register Button   *************** */
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performAuth();

            }
        });

        /* ********************  Reset Button   *************** */
        resetPassTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password");
                passwordResetDialog.setMessage("Enter You Email To Receive Reset Link.");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // extract the email and send reset link

                        String mail = resetMail.getText().toString().trim();
                        mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                makePositiveSnackBarMessage("Reset Link Sent To Your Email.");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                makeNegativeSnackBarMessage("Mail doesn't exist. OR \nMail doesn't Valid"+e.getMessage());

                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do Nothing
                        // Class Dialog
                    }
                });
                passwordResetDialog.create().show();
            }
        });

        getUsers();
        return root;
    }


    /* ********************  using Email & password Authentication   *************** */
    private void performAuth() {

        String email=inputEmail.getText().toString().trim();
        String password=inputPassword.getText().toString().trim();

        /* ********************  Checking Network Connectivity   *************** */
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).
                getState()!= NetworkInfo.State.CONNECTED && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).
                getState()!=NetworkInfo.State.CONNECTED)
        {
            Toast.makeText(getContext(), "No internet conection", Toast.LENGTH_SHORT).show();
            return;
        }
        /* ********************  checking if any of the fields are empty   *************** */
        if(email.isEmpty() || password.isEmpty())
        {

            if (TextUtils.isEmpty(email)) inputEmail.setError("Email is required");
            if (TextUtils.isEmpty(password)) inputPassword.setError("Passwords required");
            return;
        }
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Please wait while checking you credentials");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        /* *******        Checking if user status blocked     ************/
        for (User element : usersDocs) {
            if (email.equals(element.getEmail())) {
                userStatus = element.getStatus();
            }
        }

        if(userStatus.equals("blocked")){
            progressDialog.dismiss();
            makeNegativeSnackBarMessage("Your Account Has Been Blocked !");
        }
        else if(userStatus.equals("active")) {

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        makePositiveSnackBarMessage("Signed in Successfully");
                        Intent intent = new Intent(getActivity(), AdminDashboardActivity.class);
                        // this  will stop come back to this activity when user successfully SignIn.
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        onDestroy();
                        startActivity(intent);
                    } else {
                        progressDialog.dismiss();
                        makeNegativeSnackBarMessage("Neither your Email or Password are Not Correct!");
                    }
                }
            });
            // end of else if
        }else {
            progressDialog.dismiss();
            makeNegativeSnackBarMessage("barber doesn't exist !");
        }


    }// End of  perform Auth

    private void getUsers() {

        usersDocs = new ArrayList<>();

        firestore.collection("barbers").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    Log.e("FireStore error", error.getMessage());
                    return;
                }

                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {

                        // getting document fields properties and make new Object to Our User Class
                        String userId = dc.getDocument().getId();
                        String nameResult = (String) dc.getDocument().getData().get("FullName");
                        String emailResult = (String)dc.getDocument().getData().get("Mail");
                        String userTypeResult =(String) dc.getDocument().getData().get("UserType");
                        String statusResult = (String)dc.getDocument().getData().get("Status");

                        User userObject = new User(userId, nameResult, emailResult, userTypeResult, statusResult);
                        usersDocs.add(userObject);
                    }
                    if (progressDialog.isShowing()){
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                }
            }
        });
    }

    // to avoid leaked Windows...
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void makePositiveSnackBarMessage(String message){
        Snackbar snackbar = Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(ContextCompat.getColor(getActivity(),R.color.colorGreen));
        snackbar.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorBlack));
        snackbar.show();
    }
    private void makeNegativeSnackBarMessage(String message){
        Snackbar snackbar = Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(ContextCompat.getColor(getActivity(),R.color.colorRed));
        snackbar.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorWhite));
        snackbar.show();
    }

}
