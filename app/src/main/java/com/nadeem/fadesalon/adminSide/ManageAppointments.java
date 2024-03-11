package com.nadeem.fadesalon.adminSide;
/* class to show all the booked appointments for current barber according to selected date. */

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.nadeem.fadesalon.R;
import com.nadeem.fadesalon.models.Appointments;
import com.nadeem.fadesalon.models.User;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/************
 * Related classes --> ManageAppointments.java + MyAdapter_Appointments.java + listItem_appointments.xml + activityManageAppointments.xml
 * Class purpose --> class  handles the Appointments of the Customers  in RecycleView for the Admin ....
 * *************/
public class ManageAppointments extends AppCompatActivity
        implements
        DatePickerDialog.OnDateSetListener
{

    private static final String TAG = "ManageAppointments";
    RecyclerView recyclerView;
    public ArrayList<Appointments> appointmentsArrayList;
    public Appointments appointments;
    MyAdapter_Appointments myAdapter;
    public FirebaseFirestore firestore1,firestore2;
    ProgressDialog progressDialog;
    AdminProfileActivity adminProfileActivity;
    private View mParentLayout;

    DatePickerDialog datePickerDialog;
    ImageView pickDateBtn;
    int Year, Month, Day;
    int dayOfWeek=0;
    Calendar calendar;
    FirebaseFirestore firebaseFirestore;
    public FirebaseAuth mAuth;
    TextView dateTV;
    Date dateObject;


    public User barberObject;
    String nameResult;
    FirebaseUser barber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_appointments);

        // hooks
        mParentLayout = findViewById(android.R.id.content);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Wait A Moment getting Data ...");
        progressDialog.show();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        firestore2 = FirebaseFirestore.getInstance();
        appointmentsArrayList = new ArrayList<>();
        myAdapter = new MyAdapter_Appointments(ManageAppointments.this, appointmentsArrayList);
        recyclerView.setAdapter(myAdapter);


        firebaseFirestore = FirebaseFirestore.getInstance();
        dateObject = new Date();
        calendar = Calendar.getInstance();
        Year = calendar.get(Calendar.YEAR);
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);
        dateTV = findViewById(R.id.datetv2);
        pickDateBtn = findViewById(R.id.pickdateBtn);



        pickDateBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                datePickerDialog = DatePickerDialog.newInstance(ManageAppointments.this, Year, Month, Day);
                datePickerDialog.setThemeDark(false);
                datePickerDialog.showYearPickerFirst(false);
                datePickerDialog.setTitle("Date Picker");


                // Setting Min Date to today date
                Calendar min_date_c = Calendar.getInstance();
                datePickerDialog.setMinDate(min_date_c);


                // Setting Max Date to next 7 Days
                Calendar max_date_c = Calendar.getInstance();
                max_date_c.set(Year,Month,Day+7);
                datePickerDialog.setMaxDate(max_date_c);


                //Disable all SUNDAYS and SATURDAYS between Min and Max Dates
                for (Calendar loopdate = min_date_c; min_date_c.before(max_date_c); min_date_c.add(Calendar.DATE, 1), loopdate = min_date_c) {
                    int disableDay = loopdate.get(Calendar.DAY_OF_WEEK);
                    if (disableDay == Calendar.FRIDAY) {
                        Calendar[] disabledDays = new Calendar[1];
                        disabledDays[0] = loopdate;
                        datePickerDialog.setDisabledDays(disabledDays);
                    }
                }

                datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                    }
                });


                datePickerDialog.show(getFragmentManager(), "DatePickerDialog");

            }

        });

        getBarber();

    } // End of On Create


    @Override
    public void onDateSet(DatePickerDialog view, int Year, int Month, int Day) {

        dateObject = new Date(Year,Month,Day);
        // gives the number of day  1..2..3..4..5..
        dayOfWeek = dateObject.getDay();
//        Toast.makeText(getApplicationContext(), "dayOfWeek : "+dayOfWeek, Toast.LENGTH_LONG).show();
//        Toast.makeText(getApplicationContext(), "barber name : "+nameResult, Toast.LENGTH_LONG).show();


        String date = Day + "/" + (Month + 1) + "/" + Year;
        Toast.makeText(getApplicationContext(), "picked Date  "+date, Toast.LENGTH_LONG).show();
//        makeSnackBarMessage("picked Date"+date);
        dateTV.setText(date);

        getAppointmentBySelectedDate(nameResult,dayOfWeek);
    }

    // function get's the current barber data from firebase.
    public User getBarber() {

        barber = FirebaseAuth.getInstance().getCurrentUser();
        String CurrentId = barber.getUid();
        DocumentReference reference;
        firestore1 = FirebaseFirestore.getInstance();

        //Document reference to users Collection using user ID
        reference = firestore1.collection("barbers").document(CurrentId);
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    // if profile exist it will take the data from the Collection &
                    // String field should match the DB rows.
                    nameResult = task.getResult().getString("FullName");
                    String emailResult = task.getResult().getString("Mail");
                    String userTypeResult = task.getResult().getString("UserType");
                    String statusResult = task.getResult().getString("Status");

                    barberObject = new User(CurrentId, nameResult, emailResult, userTypeResult, statusResult);
                    makeSnackBarMessage("Succeed fetching Barber Data :)");

//                    EventChangeListener(nameResult);

                    progressDialog.dismiss();
                } else {
                    makeSnackBarMessage("Failed fetching Barber Data");
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            }
        });
        return barberObject;
    }


    // function get's the appointment according to by selected date.
    public void getAppointmentBySelectedDate(String barberName,int dayOfWeek) {


        appointmentsArrayList.clear();
        makeSnackBarMessage("day of week :"+dayOfWeek);

        firestore2.collection("Business").document(barberName).collection("appointments")
                .orderBy("startTime", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                String clientId = dc.getDocument().getData().get("cUid").toString();
                                String clientName = dc.getDocument().getData().get("cName").toString();
                                String clientMail = dc.getDocument().getData().get("cMail").toString();
                                String docId = dc.getDocument().getData().get("docId").toString();
                                String barberName = dc.getDocument().getData().get("calName").toString();
                                String serviceName = dc.getDocument().getData().get("serviceName").toString();
                                Timestamp startTime = (Timestamp) dc.getDocument().getData().get("startTime");
                                Timestamp endTime = (Timestamp) dc.getDocument().getData().get("endTime");
                                Number serviceLength = (Number) dc.getDocument().getData().get("serviceLength");
                                String type = dc.getDocument().getData().get("type").toString();


                                Date d2= startTime.toDate();
                                int day = d2.getDay();

                                if(day == dayOfWeek){
                                    appointments = new Appointments(clientId,clientName,clientMail,docId,barberName,serviceName,startTime,endTime,serviceLength,type);
                                    appointmentsArrayList.add(appointments);
                                }

//                                Toast.makeText(getApplicationContext(), "dayOfWeek : "+dayOfWeek, Toast.LENGTH_SHORT).show();
                            }

                            myAdapter.notifyDataSetChanged();
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                    }
                });


    }

    private void makeSnackBarMessage(String message){
        Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT).show();
    }
}
