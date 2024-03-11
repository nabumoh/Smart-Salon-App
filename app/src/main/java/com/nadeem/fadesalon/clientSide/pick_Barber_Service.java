package com.nadeem.fadesalon.clientSide;
/* class to make  pick barber & service using spinner */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.nadeem.fadesalon.MainActivity;
import com.nadeem.fadesalon.R;
import com.nadeem.fadesalon.models.Appointments;
import com.nadeem.fadesalon.models.Business;
import com.nadeem.fadesalon.models.CalendarServices;
import com.nadeem.fadesalon.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class pick_Barber_Service extends AppCompatActivity implements
        View.OnClickListener,
//        TimePickerDialog.OnTimeSetListener,
        AdapterView.OnItemSelectedListener {

    Button btnDatePicker, btnTimePicker;
    EditText txtDate, txtTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private ImageView arrowBack;


    public Long selectionDate;
    public ArrayList<HashMap<String, Timestamp>> selectedRoutineDay = new ArrayList<>();
    public String docPathHandler;


    // for Business
    public ArrayList<String> namesArrayList;
    public List<Business> barbersDocs;
    FirebaseFirestore firestoreBusiness;
    String selectedBarber;


    // for User
    public User userObject;
    FirebaseFirestore firestoreUser;
    FirebaseUser user;
    private View mParentLayout;


    // for All
    Spinner barberSpinnerBtn, serviceSpinnerBtn;
    public TextView pickedBarberTV, pickedServiceTV;
    public String item, item2;
    Button nextBtn;
    public Appointments appointments;
    ProgressDialog progressDialog;


    // for Service
    public CalendarServices calendarServicesObject; // to save selected service.
    public String serviceName;
    public Number serviceLength;
    public List<CalendarServices> calendarServicesList;
    private List<Map<String, Object>>[] listOfMaps;
    ArrayList<String> arrayList;
    ArrayAdapter<String> serviceArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_barber_service);

        // for Barber Spinner
        pickedBarberTV = findViewById(R.id.pickedBarberTV);
        barberSpinnerBtn = findViewById(R.id.barberSpinnerBtn);
        barberSpinnerBtn.setOnItemSelectedListener(this);
        selectedBarber = pickedBarberTV.getText().toString().trim();


        // for Service Spinner
        pickedServiceTV = findViewById(R.id.pickedServiceTV);
        serviceSpinnerBtn = findViewById(R.id.serviceSpinnerBtn);
        serviceSpinnerBtn.setOnItemSelectedListener(this);



        // for All
        arrowBack = findViewById(R.id.back_arrow);
        nextBtn = findViewById(R.id.nextBtn);
        mParentLayout = findViewById(android.R.id.content);

        // for Business
        firestoreBusiness = FirebaseFirestore.getInstance();
        namesArrayList = new ArrayList<>();


        // for Service
        arrayList = new ArrayList<>();
        listOfMaps = new List[]{new ArrayList<Map<String, Object>>()};


        // for Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Wait A Moment Loading Data ...");
        progressDialog.show();


        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(pick_Barber_Service.this, MainActivity.class);
                startActivity(intent);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent send = new Intent(pick_Barber_Service.this, pickDate.class);
                send.putExtra("appointment", appointments);
                startActivity(send);
            }
        });


        // Calling Functions to OnCreate

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null ){
            getUser();
            getBusiness();
        }
        else{
            progressDialog.dismiss();
            Intent intent = new Intent(pick_Barber_Service.this, MainActivity.class);
            Toast.makeText(pick_Barber_Service.this, "You Need To Login First !", Toast.LENGTH_LONG).show();
            startActivity(intent);
        }



    }


    private void makePositiveSnackBarMessage(String message){
        Snackbar snackbar = Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(ContextCompat.getColor(this,R.color.colorGreen));
        snackbar.setTextColor(ContextCompat.getColor(this,R.color.colorBlack));
        snackbar.show();
    }
    private void makeNegativeSnackBarMessage(String message){
        Snackbar snackbar = Snackbar.make(mParentLayout, message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(ContextCompat.getColor(this,R.color.colorRed));
        snackbar.setTextColor(ContextCompat.getColor(this,R.color.colorWhite));
        snackbar.show();
    }

    //to keep the app running in the back
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onClick(View v) {

    }

    /*  Start - Spinner methods   */
    private void initializeBarberSpinner(ArrayList<String> arrayList) {

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        barberSpinnerBtn.setAdapter(arrayAdapter);
    }

    private void initializeServiceSpinner(ArrayList<String> arrayList) {

        serviceArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        serviceArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serviceSpinnerBtn.setAdapter(serviceArrayAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


        if (parent.getId() == R.id.barberSpinnerBtn) {

            // if  there's already previous values in Services spinner it will clear it.
            if (serviceArrayAdapter != null && !serviceArrayAdapter.isEmpty()) {
                serviceArrayAdapter.clear(); // clear items
                serviceArrayAdapter.notifyDataSetChanged(); // update spinner view
            }

            item = barberSpinnerBtn.getSelectedItem().toString();
            pickedBarberTV.setText(item);
            docPathHandler = item;
            if (item != null && !item.equals("")) {
                getServices(item);
                makePositiveSnackBarMessage("Please Pick Service That "+item+" Provides");
            }

        }
        if (parent.getId() == R.id.serviceSpinnerBtn) {
            item2 = serviceSpinnerBtn.getSelectedItem().toString();
            pickedServiceTV.setText(item2);
        }

        if (item != null && !item.equals("") && item2 != null && !item2.equals(""))
            saveValue(item , item2);


    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
    /*  End - Spinner methods   */


    public void saveValue(String item,String item2) {

        appointments = new Appointments();
        String [] serviceArr;
        serviceArr = item2.split("-");


        appointments.setClientId(userObject.getUser_id()); // Client ID
        appointments.setClientName(userObject.getFullname()); // Client Name
        appointments.setClientMail(userObject.getEmail()); // Client Email

        for (Business element : barbersDocs) {
            if (item.equals(element.getName())) {
                appointments.setBarberName(element.getName()); // barber name
                appointments.setDocId(element.getUid()); // Document ID in Business Collection

            }
        }

        for(CalendarServices element : calendarServicesList){
            String s = (String) element.getServiceName();
            Number l =  element.getServiceLength();
            if ( serviceArr[0].contains(s) ) {
                appointments.setServiceName(s);
                appointments.setServiceLength(l);
            }
        }


        //  need to insert startTime + EndTime +  Type
        // it will be inserted in other position such as the part of picking Date
    }


    // fetching user data from firebase.
    public User getUser() {

        user = FirebaseAuth.getInstance().getCurrentUser();
        String CurrentId = user.getUid();
        DocumentReference reference1;
        firestoreUser = FirebaseFirestore.getInstance();

        //Document reference to users Collection using user ID
        reference1 = firestoreUser.collection("users").document(CurrentId);
        reference1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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


                } else {
                    Intent intent = new Intent(pick_Barber_Service.this, MainActivity.class);
                    Toast.makeText(pick_Barber_Service.this, "Failed fetching User Data", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }
            }
        });
        return userObject;
    }

    // fetching routine days for every barber.
    public void getBusiness() {

        List<Map<String, Object>>[] services = new List[]{new ArrayList<Map<String, Object>>()};
        List<Map<String, Object>>[] sundayRoutine = new List[]{new ArrayList<Map<String, Object>>()};
        List<Map<String, Object>>[] mondayRoutine = new List[]{new ArrayList<Map<String, Object>>()};
        List<Map<String, Object>>[] tuesdayRoutine = new List[]{new ArrayList<Map<String, Object>>()};
        List<Map<String, Object>>[] wednesdayRoutine = new List[]{new ArrayList<Map<String, Object>>()};
        List<Map<String, Object>>[] thursdayRoutine = new List[]{new ArrayList<Map<String, Object>>()};
        List<Map<String, Object>>[] saturdayRoutine = new List[]{new ArrayList<Map<String, Object>>()};


        barbersDocs = new ArrayList<>();

        firestoreBusiness.collection("Business").orderBy("Name", Query.Direction.ASCENDING).
                addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                String email = dc.getDocument().getData().get("Mail").toString();
                                String name = dc.getDocument().getData().get("Name").toString();
                                String Uid = dc.getDocument().getData().get("Uid").toString();

                                services[0] = (List<Map<String, Object>>) dc.getDocument().getData().get("services");
                                sundayRoutine[0] = (List<Map<String, Object>>) dc.getDocument().getData().get("sundayRoutine");
                                mondayRoutine[0] = (List<Map<String, Object>>) dc.getDocument().getData().get("mondayRoutine");
                                tuesdayRoutine[0] = (List<Map<String, Object>>) dc.getDocument().getData().get("tuesdayRoutine");
                                wednesdayRoutine[0] = (List<Map<String, Object>>) dc.getDocument().getData().get("wednesdayRoutine");
                                thursdayRoutine[0] = (List<Map<String, Object>>) dc.getDocument().getData().get("thursdayRoutine");
                                saturdayRoutine[0] = (List<Map<String, Object>>) dc.getDocument().getData().get("saturdayRoutine");

                                Business barberRoutine = new Business(email, name, Uid, services, sundayRoutine, mondayRoutine, tuesdayRoutine, wednesdayRoutine, thursdayRoutine, saturdayRoutine);
                                barbersDocs.add(barberRoutine);
                                namesArrayList.add(barberRoutine.getName());
                            }
                            initializeBarberSpinner(namesArrayList);
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }

                    }
                });
    }


    // fetching services that selected barber provides.
    public void getServices(String docPath) {

//        Toast.makeText(getApplicationContext(), "DocName Value = " + docPath, Toast.LENGTH_SHORT).show();
        calendarServicesList = new ArrayList<>();

        firestoreBusiness.collection("Business").document(docPath).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    listOfMaps[0] = (List<Map<String, Object>>) documentSnapshot.get("services");

                    for (int i = 0; i < listOfMaps[0].size(); i++) {

                        serviceName = (String) listOfMaps[0].get(i).get("serviceName");
                        serviceLength = (Number)listOfMaps[0].get(i).get("serviceLength");

                        // arrayList used to show in spinner of services
                        arrayList.add(serviceName + " - " + serviceLength + " Minutes");
                        calendarServicesObject = new CalendarServices(serviceName,serviceLength);
                        calendarServicesList.add(calendarServicesObject);

                    }

                    initializeServiceSpinner(arrayList);
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();

                }
                else{
                    makeNegativeSnackBarMessage("Failed Fetching Services");
                }
            }
        });


    }



}


