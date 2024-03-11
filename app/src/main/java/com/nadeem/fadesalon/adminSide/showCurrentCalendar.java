package com.nadeem.fadesalon.adminSide;
/* class to show the calendar of the current barber */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nadeem.fadesalon.R;
import com.nadeem.fadesalon.models.CalendarServices;
import com.nadeem.fadesalon.models.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class showCurrentCalendar extends AppCompatActivity {


    public RelativeLayout sundayLayout, mondayLayout, tuesdayLayout,
            wednesdayLayout, thursdayLayout, saturdayLayout;

    ProgressDialog progressDialog;
    private View mParentLayout;
    public ImageView arrowBack;

    public EditText startShift,endShift;
    public String startT,endT;


    // for Business
    public Timestamp startSTM, endSTM;

    FirebaseFirestore firestoreTimes;

    // for Service
    public CalendarServices calendarServicesObject; // to save selected service.
    public String serviceName;
    public Number serviceLength;
    public List<CalendarServices> calendarServicesList;
    private List<Map<String, Object>>[] listOfServices;
    FirebaseFirestore firestoreServices;

    //***** for Barber
    User userObject;
    FirebaseFirestore firestore; // for barber reference
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_current_calendar);

        // Hooks
        sundayLayout = findViewById(R.id.sundayLayout);
        mondayLayout = findViewById(R.id.mondayLayout);
        tuesdayLayout = findViewById(R.id.tuesdayLayout);
        wednesdayLayout = findViewById(R.id.wednesdayLayout);
        thursdayLayout = findViewById(R.id.thursdayLayout);
        saturdayLayout = findViewById(R.id.saturdayLayout);
        arrowBack = findViewById(R.id.back_arrow2);


        mParentLayout = findViewById(android.R.id.content);
        // for Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Wait A Moment Loading Data ...");
        progressDialog.show();

        // for Times
        firestoreTimes = FirebaseFirestore.getInstance();

        // for Service
        listOfServices = new List[]{new ArrayList<Map<String, Object>>()};
        firestoreServices = FirebaseFirestore.getInstance();


        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(showCurrentCalendar.this, AdminDashboardActivity.class);
                startActivity(intent);
            }
        });

        getBarber();
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


    //to keep the app running in the back
    public void onBackPressed() {
        moveTaskToBack(true);
    }



    // function to get barber data from firebase.
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
                    getTimes(nameResult);
                } else {
                    Toast.makeText(getApplicationContext(), "Failed fetching Barber Data", Toast.LENGTH_LONG).show();
                }
            }
        });

        return userObject;
    }


    // function to fetch times from firebase.
    public void getTimes(String docPath) {

        List<Map<String, Object>>[] sundayRoutine = new List[]{new ArrayList<Map<String, Object>>()};
        List<Map<String, Object>>[] mondayRoutine = new List[]{new ArrayList<Map<String, Object>>()};
        List<Map<String, Object>>[] tuesdayRoutine = new List[]{new ArrayList<Map<String, Object>>()};
        List<Map<String, Object>>[] wednesdayRoutine = new List[]{new ArrayList<Map<String, Object>>()};
        List<Map<String, Object>>[] thursdayRoutine = new List[]{new ArrayList<Map<String, Object>>()};
        List<Map<String, Object>>[] saturdayRoutine = new List[]{new ArrayList<Map<String, Object>>()};

        DateFormat f = new SimpleDateFormat("HH:mm aa");

        firestoreTimes.collection("Business").document(docPath).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    sundayRoutine[0] = (List<Map<String, Object>>) documentSnapshot.get("sundayRoutine");
                    mondayRoutine[0] = (List<Map<String, Object>>) documentSnapshot.get("mondayRoutine");
                    tuesdayRoutine[0] = (List<Map<String, Object>>) documentSnapshot.get("tuesdayRoutine");
                    wednesdayRoutine[0] = (List<Map<String, Object>>) documentSnapshot.get("wednesdayRoutine");
                    thursdayRoutine[0] = (List<Map<String, Object>>) documentSnapshot.get("thursdayRoutine");
                    saturdayRoutine[0] = (List<Map<String, Object>>) documentSnapshot.get("saturdayRoutine");


                    for (int i = 0; i < sundayRoutine[0].size(); i++) {
                        final View shiftTimesLayout = getLayoutInflater().inflate(R.layout.current_shift_times, null, false);
                        startShift = (EditText) shiftTimesLayout.findViewById(R.id.StartShift);
                        endShift = (EditText) shiftTimesLayout.findViewById(R.id.EndShift);

                        // fetching start Time
                        startSTM = (Timestamp) sundayRoutine[0].get(i).get("startTime");
                        Date date = startSTM.toDate();
                        String d = f.format(date);

                        // fetching end Time
                        endSTM = (Timestamp) sundayRoutine[0].get(i).get("endTime");
                        Date date2 = endSTM.toDate();
                        String d2 = f.format(date2);


                        startShift.setText(" "+d);
                        endShift.setText(" "+d2);

                        sundayLayout.addView(shiftTimesLayout);
                    } // END OF SUNDAY LOOP


                    for (int i = 0; i < mondayRoutine[0].size(); i++) {
                        final View shiftTimesLayout = getLayoutInflater().inflate(R.layout.current_shift_times, null, false);

                        startShift = (EditText) shiftTimesLayout.findViewById(R.id.StartShift);
                        endShift = (EditText) shiftTimesLayout.findViewById(R.id.EndShift);

                        // fetching start Time
                        startSTM = (Timestamp) mondayRoutine[0].get(i).get("startTime");
                        Date date = startSTM.toDate();
                        String d = f.format(date);


                        // fetching end Time
                        endSTM = (Timestamp) mondayRoutine[0].get(i).get("endTime");
                        Date date2 = endSTM.toDate();
                        String d2 = f.format(date2);

                        startShift.setText(" "+d);
                        endShift.setText(" "+d2);

                        mondayLayout.addView(shiftTimesLayout);


                    } // END OF MONDAY LOOP


                    for (int i = 0; i < tuesdayRoutine[0].size(); i++) {
                        final View shiftTimesLayout = getLayoutInflater().inflate(R.layout.current_shift_times, null, false);

                        startShift = (EditText) shiftTimesLayout.findViewById(R.id.StartShift);
                        endShift = (EditText) shiftTimesLayout.findViewById(R.id.EndShift);

                        // fetching start Time
                        startSTM = (Timestamp) tuesdayRoutine[0].get(i).get("startTime");
                        Date date = startSTM.toDate();
                        String d = f.format(date);


                        // fetching end Time
                        endSTM = (Timestamp) tuesdayRoutine[0].get(i).get("endTime");
                        Date date2 = endSTM.toDate();
                        String d2 = f.format(date2);

                        startShift.setText(" "+d);
                        endShift.setText(" "+d2);

                        tuesdayLayout.addView(shiftTimesLayout);

                    } // END OF TUESDAY LOOP


                    for (int i = 0; i < wednesdayRoutine[0].size(); i++) {
                        final View shiftTimesLayout = getLayoutInflater().inflate(R.layout.current_shift_times, null, false);

                        startShift = (EditText) shiftTimesLayout.findViewById(R.id.StartShift);
                        endShift = (EditText) shiftTimesLayout.findViewById(R.id.EndShift);

                        // fetching start Time
                        startSTM = (Timestamp) wednesdayRoutine[0].get(i).get("startTime");
                        Date date = startSTM.toDate();
                        String d = f.format(date);


                        // fetching end Time
                        endSTM = (Timestamp) wednesdayRoutine[0].get(i).get("endTime");
                        Date date2 = endSTM.toDate();
                        String d2 = f.format(date2);

                        startShift.setText(" "+d);
                        endShift.setText(" "+d2);

                        wednesdayLayout.addView(shiftTimesLayout);

                    } // END OF WEDNESDAY LOOP


                    for (int i = 0; i < thursdayRoutine[0].size(); i++) {
                        final View shiftTimesLayout = getLayoutInflater().inflate(R.layout.current_shift_times, null, false);
                        startShift = (EditText) shiftTimesLayout.findViewById(R.id.StartShift);
                        endShift = (EditText) shiftTimesLayout.findViewById(R.id.EndShift);

                        // fetching start Time
                        startSTM = (Timestamp) thursdayRoutine[0].get(i).get("startTime");
                        Date date = startSTM.toDate();
                        String d = f.format(date);


                        // fetching end Time
                        endSTM = (Timestamp) thursdayRoutine[0].get(i).get("endTime");
                        Date date2 = endSTM.toDate();
                        String d2 = f.format(date2);

                        startShift.setText(" "+d);
                        endShift.setText(" "+d2);
                        thursdayLayout.addView(shiftTimesLayout);

                    } // END OF THURSDAY LOOP


                    for (int i = 0; i < saturdayRoutine[0].size(); i++) {
                        final View shiftTimesLayout = getLayoutInflater().inflate(R.layout.current_shift_times, null, false);
                        startShift = (EditText) shiftTimesLayout.findViewById(R.id.StartShift);
                        endShift = (EditText) shiftTimesLayout.findViewById(R.id.EndShift);

                        // fetching start Time
                        startSTM = (Timestamp) saturdayRoutine[0].get(i).get("startTime");
                        Date date = startSTM.toDate();
                        String d = f.format(date);


                        // fetching end Time
                        endSTM = (Timestamp) saturdayRoutine[0].get(i).get("endTime");
                        Date date2 = endSTM.toDate();
                        String d2 = f.format(date2);


                        startShift.setText(" "+d);
                        endShift.setText(" "+d2);

                        saturdayLayout.addView(shiftTimesLayout);
                    } // END OF SATURDAY LOOP


                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    makePositiveSnackBarMessage("Success Getting " + docPath+" Work Times & Services");
                } else {
                    makeNegativeSnackBarMessage("Failed Fetching Services");
                }
            }
        });


    }


//    public void getServices(String docPath) {
//
//        Toast.makeText(getApplicationContext(), "DocName Value = " + docPath, Toast.LENGTH_SHORT).show();
//        calendarServicesList = new ArrayList<>();
//
//        firestoreServices.collection("Business").document(docPath).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//
//                if (task.isSuccessful()) {
//                    DocumentSnapshot documentSnapshot = task.getResult();
//
//                    listOfServices[0] = (List<Map<String, Object>>) documentSnapshot.get("services");
//
//                    for (int i = 0; i < listOfServices[0].size(); i++) {
//
//                        serviceName = (String) listOfServices[0].get(i).get("serviceName");
//                        serviceLength = (Number) listOfServices[0].get(i).get("serviceLength");
//
//
//                        calendarServicesObject = new CalendarServices(serviceName,serviceLength);
//                        calendarServicesList.add(calendarServicesObject);
//
//                    }
//
//
//
//
//                    if (progressDialog.isShowing())
//                        progressDialog.dismiss();
//                }
//                else{
//                    makeNegativeSnackBarMessage("Failed Fetching Services");
//                }
//            }
//        });
//
//
//    }

}
