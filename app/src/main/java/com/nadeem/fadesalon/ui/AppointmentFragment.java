package com.nadeem.fadesalon.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.nadeem.fadesalon.clientSide.CancelAnAppointment;
import com.nadeem.fadesalon.clientSide.pick_Barber_Service;
import com.nadeem.fadesalon.models.User;
// class handles the all the checking before starting a booking also shows booked appointment.
public class AppointmentFragment extends Fragment {

    private Button bookBtn;
    private ImageView arrowBack;
    private Button cancelBooking;

    //    FirebaseUser user;
    private View mParentLayout;

    // for User
    public User userObject;
    public User user1;
    FirebaseFirestore firestoreUser, firebaseFirestore;
    FirebaseUser user, user2;
    private boolean mailedFlag;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_appointment, container, false);

        bookBtn = root.findViewById(R.id.BookBtn);
        arrowBack = root.findViewById(R.id.back_arrow);
        cancelBooking = root.findViewById(R.id.cancelBooking);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mParentLayout = root.getRootView();
        firebaseFirestore = FirebaseFirestore.getInstance();

        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (user != null && !mailedFlag) {
                    Intent intent = new Intent(getActivity(), pick_Barber_Service.class);
                    startActivity(intent);
                } else if (user == null) {
                    makeNegativeSnackBarMessage("You have to Sign in first.");
                } else {
                    makeNegativeSnackBarMessage("You have booked an appointment already.");
                }
            }
        });

        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        cancelBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CancelAnAppointment.class);
                startActivity(intent);
            }
        });

        // checking if user has already booked appointment
        if (user != null) {
            String CurrentId = user.getUid();
            DocumentReference reference1;
            firestoreUser = FirebaseFirestore.getInstance();

            //Document reference to users Collection using user ID
            reference1 = firestoreUser.collection("users").document(CurrentId);
            reference1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult().exists()) {

//                    hasAnAppointments(CurrentId);

                        firebaseFirestore.collection("users").document(CurrentId).collection("appointments").addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                if (error != null) {
                                    Log.e("FireStore error", error.getMessage());
                                    return;
                                }

                                for (DocumentChange dc : value.getDocumentChanges())
                                    if (dc.getType() == DocumentChange.Type.ADDED)
                                        mailedFlag = true;


                                if (value.getDocumentChanges().isEmpty())
                                    mailedFlag = false;


                            }
                        });

                    } else {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        Toast.makeText(getActivity(), "Failed fetching User Data", Toast.LENGTH_LONG).show();
                        startActivity(intent);
                    }
                }
            });
        }

        return root;
    }


    private void makePositiveSnackBarMessage(String message) {
        Snackbar snackbar = Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(ContextCompat.getColor(getActivity(), R.color.colorGreen));
        snackbar.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorBlack));
        snackbar.show();
    }
    private void makeNegativeSnackBarMessage(String message) {
        Snackbar snackbar = Snackbar.make(mParentLayout, message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(ContextCompat.getColor(getActivity(), R.color.colorRed));
        snackbar.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
        snackbar.show();
    }



}
