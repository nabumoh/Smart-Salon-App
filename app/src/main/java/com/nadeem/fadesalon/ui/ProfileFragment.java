package com.nadeem.fadesalon.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.nadeem.fadesalon.MainActivity;
import com.nadeem.fadesalon.R;
import com.nadeem.fadesalon.clientSide.UpdateProfile;
import com.nadeem.fadesalon.models.User;

import java.util.ArrayList;
import java.util.List;
// class handles user profile so he can see & update his details.
public class ProfileFragment extends Fragment
        implements GoogleApiClient.OnConnectionFailedListener
{

    TextView namefetch, emailfetch;
    ImageView editBtn, arrowBack;
    Button logoutBtn;


    FirebaseAuth fAuth;
    FirebaseUser user;
    String CurrentId;
    FirebaseFirestore firestore, fs1;
    DocumentReference reference;
    private View mParentLayout;
    Button verifyBtn;
    TextView verifyMsg;
    private boolean flag;

    public List<User> usersDocs;
    public String userStatus="";

    /**************************/
    public GoogleApiClient googleApiClient;
    public GoogleSignInOptions gso;

    /**************************/


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        editBtn = root.findViewById(R.id.editBtn);
        logoutBtn = root.findViewById(R.id.logoutBtn);
        arrowBack = root.findViewById(R.id.back_arrow);
        verifyBtn = root.findViewById(R.id.verifyBtn);
        verifyMsg = root.findViewById(R.id.verifyMsg);


        mParentLayout = root.getRootView();
        fs1 = FirebaseFirestore.getInstance();


        /* *************************/
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), 1, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        /* *************************/



        /* ********************  Arrow back Button   *************** */
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        /* ********************  Edit Profile Button   *************** */
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UpdateProfile.class);
                startActivity(intent);
            }
        });

        /* ********************  LogOut Profile Button   *************** */
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth = FirebaseAuth.getInstance();
                fAuth.signOut();

                /* *************************/
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                if (status.isSuccess()) {

                                } else {
                                    Toast.makeText(getActivity(), "Session not close", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                /* *************************/



                Intent intent = new Intent(getContext(), MainActivity.class);
                Toast.makeText(getContext(), "logged out Successfully", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });


        getUsers();
        return root;
    }

    /**************************/
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();

                namefetch.setText(account.getDisplayName());
                emailfetch.setText(account.getEmail());
                CurrentId = account.getId();


        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        googleApiClient.stopAutoManage(getActivity());
        googleApiClient.disconnect();
    }

    /**************************/


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        namefetch = getActivity().findViewById(R.id.nameFetch);
        emailfetch = getActivity().findViewById(R.id.emailFetch);


        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null)
            CurrentId = user.getUid();

        firestore = FirebaseFirestore.getInstance();


    }

    public void onClick(View view) {
        switch (view.getId()) {
        }
    }


    @Override
    public void onStart() {
        super.onStart();


        if (!user.isEmailVerified()) {
            verifyBtn.setVisibility(View.VISIBLE);
            verifyMsg.setVisibility(View.VISIBLE);

            verifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // send verification link
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            makePositiveSnackBarMessage("Verification Email has been sent");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            makeNegativeSnackBarMessage("Email not sent :" + e.getMessage());
                        }
                    });
                }
            });
        }// end of if

        if (user.isEmailVerified()) {
            verifyBtn.setVisibility(View.GONE);
            verifyMsg.setVisibility(View.GONE);
        }


        //Document reference to users Collection using user ID
        reference = firestore.collection("users").document(CurrentId);
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

                    User userObject = new User(CurrentId, nameResult, emailResult, userTypeResult, statusResult);
                        // it will set the result value to display in textView field.
                        namefetch.setText(userObject.getFullname());
                        emailfetch.setText(userObject.getEmail());
                        makePositiveSnackBarMessage("Data Fetched Successfully");

                } else {
                    // flag = false means no user that appears in data base ( let's check google ).
                    flag = false;
                }
            }

        });


        /* *************************/
        if (!flag) {

            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
            if (opr.isDone()) {
                GoogleSignInResult result = opr.get();
                handleSignInResult(result);
            } else {
                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                        handleSignInResult(googleSignInResult);
                    }
                });
            }

        } else {
            // else -  if there is no profile exist , it will use intent automatically
            LoginFragment loginFragment = new LoginFragment();
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.drawer_layout, loginFragment);
            transaction.addToBackStack(null); // to Avoid memory leakage.
            makeNegativeSnackBarMessage("Failed fetching User Data \nNeed to Sign In First");
            transaction.commit();
        }
        /* ************************ */


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


    private void getUsers() {

        usersDocs = new ArrayList<>();

        fs1.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {

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

                }

            }
        });
    }



}


