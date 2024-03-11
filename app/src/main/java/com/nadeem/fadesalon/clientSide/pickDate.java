package com.nadeem.fadesalon.clientSide;
/* class to pick the date of the wished appointment */

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.nadeem.fadesalon.MainActivity;
import com.nadeem.fadesalon.R;
import com.nadeem.fadesalon.models.Appointments;
import com.nadeem.fadesalon.models.TimeOption;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class pickDate extends AppCompatActivity
        implements
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener
{

    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    int Year, Month, Day, Hour, Minute;
    int dayOfWeek=-1;
    Calendar calendar;

    ImageView arrowBack;
    TextView dateTV, timeTV;
    Button pickDateBtn, pickTimeBtn, bookBtn;
    FirebaseFirestore firebaseFirestore;
    public FirebaseAuth mAuth;
    public long selectionDate;



    public TimeOption TimeOptionObject; // to save selected service.
    public List<TimeOption> selectedRoutineList;
    public List<Map<String, Object>>[] listOfMaps;
    public Map<String, Object> appointmentMap;

    public ArrayList<Timepoint> arrayTP;
    Timepoint[] array;

    Date dateObject;

    Appointments appointment;
    String barberName; // it's the docPath !...
    String serviceName;
    int serviceLength;
    String serviceLengthAdapter="";

    int pickedHour,pickedMinute;
    private View mParentLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_date);

        // hooks
        mParentLayout = findViewById(android.R.id.content);

        dateTV = findViewById(R.id.dateTV);
        timeTV = findViewById(R.id.timeTV);
        pickDateBtn = findViewById(R.id.pickDateBtn1);
        pickTimeBtn = findViewById(R.id.pickTimeBtn);
        bookBtn = findViewById(R.id.bookBtn);
        arrowBack = findViewById(R.id.back_arrow);


        appointment = (Appointments) getIntent().getSerializableExtra("appointment");
        barberName = appointment.getBarberName();
        serviceName = appointment.getServiceName();

        serviceLengthAdapter += appointment.getServiceLength();
        serviceLength = Integer.parseInt(serviceLengthAdapter);


        calendar = Calendar.getInstance();
        Year = calendar.get(Calendar.YEAR);
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);
        Hour = calendar.get(Calendar.HOUR_OF_DAY);
        Minute = calendar.get(Calendar.MINUTE);
        dateObject = new Date();


        firebaseFirestore = FirebaseFirestore.getInstance();
        arrayTP = new ArrayList<>();
        appointmentMap = new HashMap<>();



        if(dayOfWeek == -1)
            pickTimeBtn.setEnabled(false);

        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(pickDate.this, pick_Barber_Service.class);
                startActivity(intent);
            }
        });

        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertAppointmentToFireStore(pickedHour, pickedMinute);
                makePositiveSnackBarMessage("Appointment Booked Successfully");
            }
        });



        pickDateBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                datePickerDialog = DatePickerDialog.newInstance(pickDate.this, Year, Month, Day);
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
                        makeNegativeSnackBarMessage("Datepicker Canceled");
                    }
                });

                datePickerDialog.show(getFragmentManager(), "DatePickerDialog");

//                selectionDate= (long) materialDatePicker.getSelection();
//                Toast.makeText(pickDate.this, "selectionDate : "+selectionDate, Toast.LENGTH_LONG).show();
//                Object s = datePickerDialog.getSelectedDay();
//                Integer day = (int) ((TimeUnit.MILLISECONDS.toDays(Long.parseLong(s.toString())) + 4) % 7 + 1);

            }
        });



        pickTimeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //2.make it the only time that available in the time picker
                if (listOfMaps[0].size() != 0) {
                    timePickerDialog = TimePickerDialog.newInstance(pickDate.this, Hour, Minute,true);
                    timePickerDialog.setVersion(TimePickerDialog.Version.VERSION_2);
                    timePickerDialog.setThemeDark(false);
                    timePickerDialog.setTitle("Pick Appointment Time");

                    setTheAvailableRoutine(listOfMaps[0], timePickerDialog);

                    boolean flag = false;
                    int i, j = 0;
                    for (i = 0; i < 24 && !flag; i++) {
                        for (j = 0; j < 60 && !flag; j++) {
                            flag = timePickerDialog.isOutOfRange(new Timepoint(i, j));
                        }
                    }
//                    Toast.makeText(getApplicationContext(), "selectionDate Value : "+selectionDate  , Toast.LENGTH_LONG).show();
                    setAppointmentDisable(selectionDate, timePickerDialog, i - 1, j - 1);

                    // has put it like this
//                     timePickerDialog.show(getFragmentManager(), "TimePickerDialog");


                    /*  On  Cancel Press Action  */
                    timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            makeNegativeSnackBarMessage("Timepicker Canceled");
                        }
                    });

                }else {
                    makeNegativeSnackBarMessage("Pick another Day Please, at This day " + barberName + " is not working");
                }



                }


        }); // End of PickTime Button


    }// End of OnCreate





    @Override
    public void onDateSet(DatePickerDialog view, int Year, int Month, int Day) {


        dateObject = new Date(Year,Month,Day);
        selectionDate = dateObject.getTime();

        // gives the number of day  1..2..3..4..5..
        dayOfWeek = dateObject.getDay();
        Toast.makeText(pickDate.this, "dayOfWeek : "+dayOfWeek, Toast.LENGTH_LONG).show();


        if(dayOfWeek !=-1)
            pickTimeBtn.setEnabled(true);


        String date = "Date: " + Day + "/" + (Month + 1) + "/" + Year;
        makePositiveSnackBarMessage("picked Date"+date);
        dateTV.setText(date);


        getSelectedDayRoutine(barberName, dayOfWeek);

    }





    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

        String time = "Time: " + hourOfDay + "h :" + minute + "m";
        makePositiveSnackBarMessage("picked Time"+time);
        timeTV.setText(time);


        pickedHour = hourOfDay;
        pickedMinute = minute;


    }




    // function to get Routine of the selected day.
    public void getSelectedDayRoutine(String docPath, int selectedDay) {

//        Toast.makeText(getApplicationContext(), "DocName Value = " + docPath, Toast.LENGTH_SHORT).show();
        selectedRoutineList = new ArrayList<>();
        listOfMaps = new List[]{new ArrayList<Map<String, Object>>()};


        //1.get the routine hours from the routine day
        firebaseFirestore.collection("Business").document(docPath).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    switch (selectedDay) {
                        case 1:
                            listOfMaps[0] = (List<Map<String, Object>>) documentSnapshot.get("sundayRoutine");
                            break;
                        case 2:
                            listOfMaps[0] = (List<Map<String, Object>>) documentSnapshot.get("mondayRoutine");
                            break;
                        case 3:
                            listOfMaps[0] = (List<Map<String, Object>>) documentSnapshot.get("tuesdayRoutine");
                            break;
                        case 4:
                            listOfMaps[0] = (List<Map<String, Object>>) documentSnapshot.get("wednesdayRoutine");
                            break;
                        case 5:
                            listOfMaps[0] = (List<Map<String, Object>>) documentSnapshot.get("thursdayRoutine");
                            break;
                        case 6:
                            break;
                        case 0:
                            listOfMaps[0] = (List<Map<String, Object>>) documentSnapshot.get("saturdayRoutine");
                            break;
                        default:
                            break;
                    }

//                    //2.make it the only time that available in the time picker
//                    if (listOfMaps[0].size() != 0) {
//                        timePickerDialog = TimePickerDialog.newInstance(pickDate.this,true);
//                        timePickerDialog.setVersion(TimePickerDialog.Version.VERSION_2);
//                        timePickerDialog.setThemeDark(false);
//                        timePickerDialog.setTitle("Time Picker");
//
//                        setTheAvailableRoutine(listOfMaps[0], timePickerDialog);
//
//                        boolean flag = false;
//                        int i, j = 0;
//                        for (i = 0; i < 24 && !flag; i++) {
//                            for (j = 0; j < 60 && !flag; j++) {
//                                flag = timePickerDialog.isOutOfRange(new Timepoint(i, j));
//                            }
//                        }
//                        Toast.makeText(getApplicationContext(), "selectionDate Value : "+selectionDate  , Toast.LENGTH_LONG).show();
//                        setAppointmentDisable(selectionDate, timePickerDialog, i - 1, j - 1);
//                    }else {
//                        makeNegativeSnackBarMessage("Pick another Day Please, at This day " + barberName + " is not working");
//
//                    }


                }
            }
        });


    }




    //function to set the times that are not available for the business in a selected day as unavailable
    private void setTheAvailableRoutine(List<Map<String, Object>> selectedRoutineDay, TimePickerDialog tpd) {

        Timepoint[] array;
        ArrayList<Timepoint> arrayTP = new ArrayList<>();

        for (int i = 0; i < selectedRoutineDay.size(); i++) {
            Timestamp timestamp = (Timestamp) selectedRoutineDay.get(i).get("startTime");
            Date date = timestamp.toDate();
            int startHour = (int) ((TimeUnit.MILLISECONDS.toHours(date.getTime()) + 2) % 24);
            int startMinute = (int) TimeUnit.MILLISECONDS.toMinutes(date.getTime()) % 60;

            Timestamp timestamp2 = (Timestamp) selectedRoutineDay.get(i).get("endTime");
            Date date2 = timestamp2.toDate();
            int endHour = (int) (TimeUnit.MILLISECONDS.toHours(date2.getTime()) + 2) % 24;
            int endMinute = (int) TimeUnit.MILLISECONDS.toMinutes(date2.getTime()) % 60;


            for (int j = startHour; j <= endHour; j++) {


                for (int k = 0; k < 60; k++) {
                    if (j == startHour && k < startMinute) {
                        k = startMinute;
                    }

                    if (j == endHour && k == endMinute) {
                        arrayTP.add(new Timepoint(j, k));
                        break;
                    }

                    arrayTP.add(new Timepoint(j, k));
                }

            }

        }

        array = arrayTP.toArray(new Timepoint[0]);
        tpd.setSelectableTimes(array);
    }




    //function that remove the time that are available and there is appointment that booked in
    private void setAppointmentDisable(long selectionDate, TimePickerDialog tpd, int h, int m) {
        Date date = new Date();
        Date date2 = new Date();
        date.setTime(selectionDate);
        //converting to milliseconds - 60 sec 60 min 24 hours 1000 millisecond
        date2.setTime(selectionDate + 60 * 60 * 24 * 1000);

//        Toast.makeText(getApplicationContext(), "You Entered setAppointmentDisable func  "+date2  , Toast.LENGTH_SHORT).show();

        //1.bring all the documents in the selection date
        firebaseFirestore.collection("Business").document(barberName).collection("appointments")
                .whereGreaterThanOrEqualTo("startTime", date)
                .whereLessThan("startTime", date2)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                Log.d("demo"," Task size : " + task.getResult().getDocuments().size());

                    List<DocumentSnapshot> docs = task.getResult().getDocuments();
                    for (int i = 0; i < docs.size(); i++) {
                        Timestamp startTime = (Timestamp) docs.get(i).get("startTime");

                        String sLengthAdapter = "";
                        sLengthAdapter += docs.get(i).get("serviceLength");
                        long length = Long.parseLong(sLengthAdapter);

                        Log.d("demo","Long ServiceLength Value : " + length);

                        int hour = (int) (TimeUnit.SECONDS.toHours(startTime.getSeconds())) % 24;
                        int minute = (int) TimeUnit.SECONDS.toMinutes(startTime.getSeconds()) % 60;

                        for (int j = 0; j < length; j++, minute++) {

                            Log.d("demo"," length : " + length + "  j :" + j);

                            if (minute == 60) {
                                minute = 0;
                                hour++;
                            }
                            arrayTP.add(new Timepoint(hour, minute));
                        }
                    }
                    array = arrayTP.toArray(new Timepoint[0]);

                Log.d("demo","array :    " + Arrays.toString(array));

                    tpd.setDisabledTimes(array);
                    boolean res = tpd.isOutOfRange(new Timepoint(5, 5));

                Log.d("demo","boolean res : " + res);

                    setAvailableTimeMatchServiceLength(tpd, serviceLength, h, m);

                    //  (in tip)
//                    tpd.show(getFragmentManager(), "timepickeDialog");



                }
        });
    }




    //function that keep available only the times that are enough to book in it for the chosen service
    private void setAvailableTimeMatchServiceLength(TimePickerDialog timePickerDialog, Integer serviceLength, int h, int m) {
        Timepoint[] array;
        ArrayList<Timepoint> arrayTP = new ArrayList<>();

        for (int hourDay = 0; hourDay < 24; hourDay++) {
            for (int minuteDay = 0; minuteDay < 60; minuteDay++) {
                boolean flag = false;
                boolean res = timePickerDialog.isOutOfRange(new Timepoint(hourDay, minuteDay));
                if (!res) {
                    int SL = serviceLength - 1;
                    for (int currentHourDay = hourDay; currentHourDay < 24 && SL > 0 && !flag; currentHourDay++) {

                        for (int currentMinuteDay = 0; currentMinuteDay < 60 && SL > 0 && !flag; currentMinuteDay++, SL--) {

                            if (currentHourDay == hourDay && currentMinuteDay == 0) {

                                currentMinuteDay = minuteDay + 1;
                            }
                            flag = timePickerDialog.isOutOfRange(new Timepoint(currentHourDay, currentMinuteDay));
                        }
                    }
                    if (flag || SL > 0) {
                        arrayTP.add(new Timepoint(hourDay, minuteDay));


                    }
                }

            }
        }

        // arrayTP.remove(0);
        array = arrayTP.toArray(new Timepoint[0]);

        Log.d("demo","array : "+array.length);

        timePickerDialog.setDisabledTimes(array);

        if (timePickerDialog.isOutOfRange(new Timepoint(h, m))) {
            timePickerDialog.show(getFragmentManager(), "timepickeDialog");
        }
        else {
            makeNegativeSnackBarMessage("Pick another Day Please, there's no more available appointments at this day or there's' not enough time for the chosen service");
        }
    }



    // inserting the appointment info to fireBase.
    public void insertAppointmentToFireStore(int hourOfDay, int minute){
        appointmentMap = new HashMap<>();
        appointmentMap.put("cName", appointment.getClientName());
        appointmentMap.put("cMail", appointment.getClientMail());
        appointmentMap.put("cUid", appointment.getClientId());
        appointmentMap.put("type", "appointment");
        appointmentMap.put("serviceLength", serviceLength);
        appointmentMap.put("calName", barberName);
        appointmentMap.put("serviceName", serviceName);
        //start time
        Date startTime = new Date();
        Date endTime = new Date();
        Long h = new Long(hourOfDay * 3600);
        Long m = new Long(minute * 60);
        startTime.setTime(selectionDate + h * 1000 + m * 1000);
        //startTime.setTime(selectionDate);
        endTime.setTime(selectionDate + h * 1000 + m * 1000 + (serviceLength - 1) * 60 * 1000);


        //end time
        appointmentMap.put("startTime", startTime);
        appointmentMap.put("endTime", endTime);
        Date date2 = new Date();
        date2.setTime(selectionDate + 60 *  serviceLength * 1000);


        final Boolean[] flag = {true};

        firebaseFirestore.collection("Business").document(barberName).collection("appointments")
                .whereGreaterThanOrEqualTo("startTime", startTime)
                .whereLessThan("startTime", endTime).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int size = task.getResult().size();
                if (size != 0) {
//                    Toast.makeText(getApplicationContext(),"this time had been taken, try again please",Toast.LENGTH_SHORT).show();
                    flag[0] = false;
                }
            }
        });


        firebaseFirestore.collection("Business").document(barberName).collection("appointments")
                .whereGreaterThanOrEqualTo("endTime", startTime)
                .whereLessThan("endTime", endTime).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int size = task.getResult().size();
                if (size != 0 || !flag[0]) {
                    Toast.makeText(getApplicationContext(), "this time has been taken, try again please", Toast.LENGTH_SHORT).show();
                    flag[0] = false;
                }
                if (flag[0]) {
                    firebaseFirestore.collection("Business").document(barberName)
                            .collection("appointments").document().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                appointmentMap.put("docId", task.getResult().getId());

                                // Inserting Map Object to Barber's Appointments DataBase
                                firebaseFirestore.collection("Business").document(barberName)
                                        .collection("appointments").document(task.getResult().getId()).set(appointmentMap).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {

                                        // Inserting Map Object to User's Appointment DataBase
                                        firebaseFirestore.collection("users").document(appointment.getClientId())
                                                .collection("appointments").document(task.getResult().getId()).set(appointmentMap);

                                        //create avtivity to take us to the next page!!
                                        Intent intent = new Intent(pickDate.this, MainActivity.class);
//                                        Toast.makeText(getApplicationContext(), "Booking has been accreted Successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(intent);
                                        finish();
                                        sendAppointmentToCalApp(appointmentMap);
                                    }
                                });

                            }

                        }
                    });

                }
            }
        });



    }


    // function to send the appointment information to calendar App.
    private void sendAppointmentToCalApp(Map<String,Object> appointmentMap){

        String type= String.valueOf(appointmentMap.get("type"));
        String barberName= String.valueOf(appointmentMap.get("calName"));
        String cName= String.valueOf(appointmentMap.get("cName"));
        String  cMail= String.valueOf(appointmentMap.get("cMail"));
        String sName= String.valueOf(appointmentMap.get("serviceName"));
        int sLength= (int)appointmentMap.get("serviceLength");
        Date startT= (Date)appointmentMap.get("startTime");
        Date endT= (Date)appointmentMap.get("endTime");

//        Date date = startT.toDate();
        DateFormat f = new SimpleDateFormat("dd.MM");
        String d = f.format(startT);
        DateFormat f2 = new SimpleDateFormat("HH:mm aa");
        String d2 = f2.format(startT);


        String title = "Fade Salon Appointment";
        String location = "https://waze.com/ul/hsvc1ht5t2";
        String description = "Booking Details : \n"+"Service: "+sName+ "\nDate: "+d +"\nTime: "+ d2;

        Intent sendData = new Intent(Intent.ACTION_INSERT);
        sendData.setData(CalendarContract.Events.CONTENT_URI);
//        sendData.setType("vnd.android.cursor.item/event");
        sendData.putExtra(CalendarContract.Events.TITLE,title);
        sendData.putExtra(CalendarContract.Events.EVENT_LOCATION,location);
        sendData.putExtra(CalendarContract.Events.DESCRIPTION,description);
        sendData.putExtra(CalendarContract.Events.ALL_DAY, true);
        sendData.putExtra(Intent.EXTRA_EMAIL,cMail);
//        sendData.putExtra(CalendarContract.Events.DTSTART,f);
//        sendData.putExtra(CalendarContract.Events.HAS_ALARM,Hour);



        if(sendData.resolveActivity(getPackageManager()) !=null){
            startActivity(sendData);
        }else{
            makeNegativeSnackBarMessage("There is no app that can support this action");
        }

    }



    private void makePositiveSnackBarMessage(String message){
        Snackbar snackbar = Snackbar.make(mParentLayout, message, Snackbar.LENGTH_LONG);
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

}

