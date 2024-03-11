package com.nadeem.fadesalon.clientSide;
// class to show user appointment & with option to cancel.

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
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
import com.nadeem.fadesalon.models.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CancelAnAppointment extends AppCompatActivity {

    TextView barberName, time, service, noBookingTV;
    Button cancelAppointmentBtn;
    RelativeLayout appointmentLayout;

    // for User
    public User userObject;
    public User user1;
    FirebaseFirestore firestoreUser, firebaseFirestore;
    FirebaseUser user;
    public boolean mailedFlag;

    private ImageView arrowBack;

    ProgressDialog progressDialog;
    private View mParentLayout;
    public List<User> usersDocs;

    String docId, barber, CurrentId, serviceName;
    boolean flagy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_an_appointment);

        barberName = findViewById(R.id.barberName);
        time = findViewById(R.id.Date);
        service = findViewById(R.id.service);
        cancelAppointmentBtn = findViewById(R.id.cancelAppointmentBtn);
        appointmentLayout = findViewById(R.id.appointmentsLayout);
        noBookingTV = findViewById(R.id.noBookingTV);
        arrowBack = findViewById(R.id.back_arrow);
        mParentLayout = findViewById(android.R.id.content);


        firebaseFirestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Fetching Appointment");
        progressDialog.setMessage("Wait A Moment Loading Data ...");
        progressDialog.show();


        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        cancelAppointmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAppointment(docId, barber);
                Log.d("demo","docId: "+docId+" --- barber: "+barber);
            }
        });


        if (user != null) {
            noBookingTV.setVisibility(View.GONE);
            getUser();
            progressDialog.dismiss();
        } else {
            appointmentLayout.setVisibility(View.GONE);
            noBookingTV.setVisibility(View.VISIBLE);
            progressDialog.dismiss();
        }


    }


    // function to get user data from firebase
    public User getUser() {

        user = FirebaseAuth.getInstance().getCurrentUser();
        CurrentId = user.getUid();
        DocumentReference reference1;
        firestoreUser = FirebaseFirestore.getInstance();

        //Document reference to users Collection using user ID
        reference1 = firestoreUser.collection("users").document(CurrentId);
        reference1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {

                    getUserAppointment(CurrentId);

                } else {
                    Intent intent = new Intent(CancelAnAppointment.this, MainActivity.class);
                    Toast.makeText(CancelAnAppointment.this, "Failed fetching User Data", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }
            }
        });
        return userObject;
    }


    // getting the appointment from firebase according to current user.
    private void getUserAppointment(String userId) {

        firebaseFirestore.collection("users").document(userId).collection("appointments").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                        docId = (String) dc.getDocument().getData().get("docId");
                        barber = (String) dc.getDocument().getData().get("calName");
                        serviceName = (String) dc.getDocument().getData().get("serviceName");

                        Timestamp startTime = (Timestamp) dc.getDocument().getData().get("startTime");
                        Date date = startTime.toDate();

                        DateFormat f = new SimpleDateFormat("dd.MM");
                        String d = f.format(date);
                        DateFormat f2 = new SimpleDateFormat("HH:mm aa");
                        String d2 = f2.format(date);

                        barberName.setText(barber);
                        service.setText(serviceName);
                        time.setText(d + " at " + d2);


                    }
                }
                if (progressDialog.isShowing())
                    progressDialog.dismiss();


                if (value.getDocumentChanges().isEmpty()) {
                    appointmentLayout.setVisibility(View.GONE);
                    noBookingTV.setVisibility(View.VISIBLE);
                }

            }

        });
    }

    // deleting the appointment from firebase in case of cancelling it.
    private void deleteAppointment(String docId, String barberName) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        flagy = true;


        db.collection("Business").document(barberName).collection("appointments")
                .document(docId).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // do nothing
            }else flagy = false;
        });

        db.collection("users").document(CurrentId).collection("appointments")
                .document(docId).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // do nothing
            }else flagy = false;
        });

        if(flagy){
            Toast.makeText(CancelAnAppointment.this, "Appointment has been Cancelled successfully !", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CancelAnAppointment.this, MainActivity.class);
            startActivity(intent);
        }


    }


    private void makePositiveSnackBarMessage(String message) {
        Snackbar snackbar = Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.colorGreen));
        snackbar.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
        snackbar.show();
    }
    private void makeNegativeSnackBarMessage(String message) {
        Snackbar snackbar = Snackbar.make(mParentLayout, message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.colorRed));
        snackbar.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
        snackbar.show();
    }

}






