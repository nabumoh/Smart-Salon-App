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

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.nadeem.fadesalon.MainActivity;
import com.nadeem.fadesalon.R;
import com.nadeem.fadesalon.clientSide.RegisterActivity;
import com.nadeem.fadesalon.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.nadeem.fadesalon.GoogleSignInActivity;

// class handles the user login so he can have full access to the app
public class LoginFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    public TextView signUpTv, resetPass;
    private ImageView arrowBack;
    private EditText inputEmail, inputPassword;
    private Button btnSignIn, googleBtn;
    private ProgressDialog progressDialog;
    private ConnectivityManager connectivityManager;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    public FirebaseFirestore firestore; // for user reference
    private View mParentLayout;
    public boolean flag;

    public List<User> usersDocs;
    public String userStatus = "";

    /**************************/
    /* ***** Adding tag for logging and RC_SIGN_IN for an activity result ***** */
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "GoogleAUTH";
    GoogleSignInClient mGoogleSignInClient; // Adding Google sign-in client
    //    private SignInButton googleBtn;
    private GoogleApiClient googleApiClient;
    String name, email;
    String idToken;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    /**************************/


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_login, container, false);
        mParentLayout = root.getRootView();

        firebaseAuth = com.google.firebase.auth.FirebaseAuth.getInstance();

        /*--------------------- Hooks ------------------------*/
        signUpTv = root.findViewById(R.id.signUpTV);
        arrowBack = root.findViewById(R.id.back_arrow);
        inputEmail = root.findViewById(R.id.emailField);
        inputPassword = root.findViewById(R.id.passField);
        btnSignIn = root.findViewById(R.id.ContinueBtn);
        googleBtn = root.findViewById(R.id.googleBtn);
        resetPass = root.findViewById(R.id.resetPassTv);


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        /* ************************ */
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait a moment...");
        progressDialog.setTitle("checking");
        progressDialog.setCanceledOnTouchOutside(false);



        /* *************************/
//        //this is where we start the Auth state Listener to listen for whether the user is signed in or not
        saveGoogleUserToDB();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))//you can also use R.string.default_web_client_id
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        /* *************************/


        /*---------------------SignUp TextView Intent------------------------*/
        signUpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        /* ********************  Arrow back Button   *************** */
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainActivity.class);
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

        /* *******************    Google Button   ************** */
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });

        resetPass.setOnClickListener(new View.OnClickListener() {
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
                                makeNegativeSnackBarMessage("Mail doesn't exist. OR \nMail doesn't Valid" + e.getMessage());

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

        onStart();
        getUsers();
        return root;
    }


    /* ********************  Google Authentication   *************** */

    public void saveGoogleUserToDB(){
        //this is where we start the Auth state Listener to listen for whether the user is signed in or not
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Get signedIn user
//                FirebaseUser user = firebaseAuth.getCurrentUser();

                //if user is signed in, we call a helper method to save the user details to Firebase
//                if (user != null) {
                if (mUser != null) {
                    // User is signed in
                    // you could place other firebase code
                    //logic to save the user details to Firebase

                    String userID = mUser.getUid();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("UserID", userID);
                    userMap.put("FullName", name);
                    userMap.put("Mail", email);
                    userMap.put("UserType", "user");
                    userMap.put("Status", "active");

                    Log.d("demo","userMap Value:"+userMap.get("UserID"));

                    DocumentReference documentReference = db.collection("users").document(userID);
                    documentReference.set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) { // do nothing
                        }
                    });

                    Log.d(TAG, "onAuthStateChanged:signed_in:" + mUser.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {

            GoogleSignInAccount account = result.getSignInAccount();
            idToken = account.getIdToken();
            name = account.getDisplayName();
            email = account.getEmail();


            /* *******        Checking if user status blocked     ************/
            for (User element : usersDocs) {
                if (email.equals(element.getEmail())) {
                    userStatus = element.getStatus();
                }
            }
            if (userStatus != null) {
                if (userStatus.equals("blocked")) {
                    makeNegativeSnackBarMessage("Your Account Has Been Blocked !");
                    Auth.GoogleSignInApi.signOut(googleApiClient);
                } else if (userStatus.equals("active")) { //*****
                    // you can store user data to SharedPreference
                    AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
                    firebaseAuthWithGoogle(credential);

                } else if (userStatus.equals("")) {

                    // you can store user data to SharedPreference
                    AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
                    firebaseAuthWithGoogle(credential);
                }
            }

        } else {
            // Google Sign In failed, update UI appropriately
            Log.d("demo", "Login Unsuccessful. " + result);
            Toast.makeText(getActivity(), "Login Unsuccessful", Toast.LENGTH_SHORT).show();

        }
    }

    private void firebaseAuthWithGoogle(AuthCredential credential) {

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            Toast.makeText(getContext(), "Welcome " + name, Toast.LENGTH_SHORT).show();
                            // this  will stop come back to this activity when user successfully register.
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            onDestroy();
                            startActivity(intent);
                        } else {
                            Log.w(TAG, "signInWithCredential" + task.getException().getMessage());
                            task.getException().printStackTrace();
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    @Override
    public void onStart() {
        super.onStart();
        if (authStateListener != null) {
            FirebaseAuth.getInstance().signOut();
        }
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        googleApiClient.stopAutoManage(getActivity());
//        googleApiClient.disconnect();
//    }


    // to avoid leaked Windows...
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    /**************************/







    /* ********************  using Email & password Authentication   *************** */
    private void performAuth() {

        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        /* ********************  Checking Network Connectivity   *************** */
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).
                getState()!= NetworkInfo.State.CONNECTED && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).
                getState()!=NetworkInfo.State.CONNECTED)
        {
            Toast.makeText(getContext(), "No internet conection", Toast.LENGTH_SHORT).show();
            return;
        }
        /* ********************  checking if any of the fields are empty   *************** */
        if (email.isEmpty() || password.isEmpty()) {

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

        if (userStatus.equals("blocked")) {
            makeNegativeSnackBarMessage("Your Account Has Been Blocked !");
            progressDialog.dismiss();
        } else if (userStatus.equals("active")) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        makePositiveSnackBarMessage("Signed in Successfully");
                        Intent intent = new Intent(getContext(), MainActivity.class);

                        // this  will stop come back to this activity when user successfully SignIn.
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        makeNegativeSnackBarMessage("Neither your Email or Password are Not Correct!");
                        //progressBar.setVisibility(View.GONE);
                        progressDialog.dismiss();

                    }
                }
            });
        } // End of else if
        else {
            progressDialog.dismiss();
            makeNegativeSnackBarMessage("user doesn't exist !");
        }

    } // End of  perform Auth

    private void getUsers() {


        usersDocs = new ArrayList<>();

        firestore.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                        String emailResult = (String) dc.getDocument().getData().get("Mail");
                        String userTypeResult = (String) dc.getDocument().getData().get("UserType");
                        String statusResult = (String) dc.getDocument().getData().get("Status");

                        User userObject = new User(userId, nameResult, emailResult, userTypeResult, statusResult);
                        usersDocs.add(userObject);
                    }
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }

            }
        });
    }


    private void makePositiveSnackBarMessage(String message) {
        Snackbar snackbar = Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(ContextCompat.getColor(getActivity(), R.color.colorGreen));
        snackbar.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorBlack));
        snackbar.show();
    }

    private void makeNegativeSnackBarMessage(String message) {
        Snackbar snackbar = Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(ContextCompat.getColor(getActivity(), R.color.colorRed));
        snackbar.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
        snackbar.show();
    }

}
