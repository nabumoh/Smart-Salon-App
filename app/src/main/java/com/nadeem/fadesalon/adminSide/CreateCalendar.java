package com.nadeem.fadesalon.adminSide;
/* class that gives the barber to create his own workDays Routine */

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nadeem.fadesalon.R;
import com.nadeem.fadesalon.models.CalendarServices;
import com.nadeem.fadesalon.models.TimeOption;
import com.nadeem.fadesalon.models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class CreateCalendar extends AppCompatActivity {

    public RelativeLayout sundayLayout, mondayLayout, tuesdayLayout,
            wednesdayLayout, thursdayLayout, saturdayLayout;
    public LinearLayout serviceLayout;


    public ImageView arrowBack;
    public Button createCalendarBtn, addServiceBtn;

    public Switch sundaySwitch, mondaySwitch,
            tuesdaySwitch, wednesdaySwitch,
            thursdaySwitch, saturdaySwitch;

    private CollectionReference businessesRef;
    private FirebaseFirestore firestore1; // for calendar reference
    public Date firstDate, secondDate;
    ArrayList<TimeOption> dayTimeOption;

    //***** for Barber
    User userObject;
    FirebaseFirestore firestore; // for barber reference
    FirebaseUser user;


    private View mParentLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating_calendar);
        mParentLayout = findViewById(android.R.id.content);


        // Hooks
        sundayLayout = findViewById(R.id.sundayLayout);
        mondayLayout = findViewById(R.id.mondayLayout);
        tuesdayLayout = findViewById(R.id.tuesdayLayout);
        wednesdayLayout = findViewById(R.id.wednesdayLayout);
        thursdayLayout = findViewById(R.id.thursdayLayout);
        saturdayLayout = findViewById(R.id.saturdayLayout);
        serviceLayout = findViewById(R.id.serviceLayout);
        createCalendarBtn = findViewById(R.id.calendarBtn);
        addServiceBtn = findViewById(R.id.addServiceBtn);
        arrowBack = findViewById(R.id.back_arrow2);


        // to activate timePicker function by default on the start
        addWorkTimeToLayout(sundayLayout);
        addWorkTimeToLayout(mondayLayout);
        addWorkTimeToLayout(tuesdayLayout);
        addWorkTimeToLayout(wednesdayLayout);
        addWorkTimeToLayout(thursdayLayout);
        addWorkTimeToLayout(saturdayLayout);


        sundaySwitch = (Switch) findViewById(R.id.sundaySwitch);
        sundaySwitch.setChecked(false);
        sundaySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Log.d("switch:", "On");
                // To remove child's and Hide LayOut
                sundayLayout.removeAllViews();
                sundayLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.layoutInvisible));

            } else {
                // to Add LayOut
                sundayLayout.addView(View.inflate(getApplicationContext(), R.layout.shift_times_layout, null));
                addWorkTimeToLayout(sundayLayout);
                Log.d("switch:", "Off");

            }
        });//End of sunday Switch

        mondaySwitch = (Switch) findViewById(R.id.mondaySwitch);
        mondaySwitch.setChecked(false);
        mondaySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Log.d("switch:", "On");
                // To remove child's and Hide LayOut
                mondayLayout.removeAllViews();
                mondayLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.layoutInvisible));

            } else {
                // to Add LayOut
                mondayLayout.addView(View.inflate(getApplicationContext(), R.layout.shift_times_layout, null));
                addWorkTimeToLayout(mondayLayout);
                Log.d("switch:", "Off");

            }
        });//End of monday Switch

        tuesdaySwitch = (Switch) findViewById(R.id.tuesdaySwitch);
        tuesdaySwitch.setChecked(false);
        tuesdaySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Log.d("switch:", "On");
                // To remove child's and Hide LayOut
                tuesdayLayout.removeAllViews();
                tuesdayLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.layoutInvisible));

            } else {
                // to Add LayOut
                tuesdayLayout.addView(View.inflate(getApplicationContext(), R.layout.shift_times_layout, null));
                addWorkTimeToLayout(tuesdayLayout);
                Log.d("switch:", "Off");

            }
        });//End of tuesday Switch

        wednesdaySwitch = (Switch) findViewById(R.id.wednesdaySwitch);
        wednesdaySwitch.setChecked(false);
        wednesdaySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Log.d("switch:", "On");
                // To remove child's and Hide LayOut
                wednesdayLayout.removeAllViews();
                wednesdayLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.layoutInvisible));

            } else {
                // to Add LayOut
                wednesdayLayout.addView(View.inflate(getApplicationContext(), R.layout.shift_times_layout, null));
                addWorkTimeToLayout(wednesdayLayout);
                Log.d("switch:", "Off");

            }
        });//End of wednesday Switch

        thursdaySwitch = (Switch) findViewById(R.id.thursdaySwitch);
        thursdaySwitch.setChecked(false);
        thursdaySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Log.d("switch:", "On");
                // To remove child's and Hide LayOut
                thursdayLayout.removeAllViews();
                thursdayLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.layoutInvisible));

            } else {
                // to Add LayOut
                thursdayLayout.addView(View.inflate(getApplicationContext(), R.layout.shift_times_layout, null));
                addWorkTimeToLayout(thursdayLayout);
                Log.d("switch:", "Off");

            }
        });//End of thursday Switch

        saturdaySwitch = (Switch) findViewById(R.id.saturdaySwitch);
        saturdaySwitch.setChecked(false);
        saturdaySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Log.d("switch:", "On");
                // To remove child's and Hide LayOut
                saturdayLayout.removeAllViews();
                saturdayLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.layoutInvisible));

            } else {
                // to Add LayOut
                saturdayLayout.addView(View.inflate(getApplicationContext(), R.layout.shift_times_layout, null));
                addWorkTimeToLayout(saturdayLayout);
                Log.d("switch:", "Off");

            }
        });//End of saturday Switch


        addServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addServiceToLayout();
            }
        });

        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateCalendar.this, AdminDashboardActivity.class);
                startActivity(intent);
            }
        });

        createCalendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createCalendar();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        getBarber();

    } // End of OnCreate Function


    // function that show's a snackBar in the bottom of the screen
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

    // get's the barber data from firebase
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
                } else {
                    Toast.makeText(getApplicationContext(), "Failed fetching Barber Data", Toast.LENGTH_LONG).show();
                }
            }
        });

        return userObject;
    }


    //starting the create calendar process
    private void createCalendar() throws ParseException {

        firestore1 = FirebaseFirestore.getInstance();
        businessesRef = firestore1.collection("Business");

        final Boolean flag[] = new Boolean[1];
        flag[0] = true;
        User barberUser = getBarber();
        String barberName = barberUser.getFullname();

        businessesRef
                .whereEqualTo("Name", barberName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        /* check times */
                        try {
                            flag[0] = checkShiftTimes(sundayLayout) && flag[0];
                            flag[0] = checkShiftTimes(mondayLayout) && flag[0];
                            flag[0] = checkShiftTimes(tuesdayLayout) && flag[0];
                            flag[0] = checkShiftTimes(wednesdayLayout) && flag[0];
                            flag[0] = checkShiftTimes(thursdayLayout) && flag[0];
                            flag[0] = checkShiftTimes(saturdayLayout) && flag[0];
                            flag[0] = checkServices(serviceLayout) && flag[0];
                        } catch (ParseException e) {
//                            makeNegativeSnackBarMessage("Checking Catch Error: Fill As Requested \n" + e.getMessage());
                        }
                        if (!flag[0]) {
//                            makeNegativeSnackBarMessage("Something Not Correct \n check again : services name or time or shift time");
                            return;
                        }


                        /* save to data base... */
                        final Map<String, Object> calendarMap = new HashMap<>();
                        try {
                            calendarMap.put("Name", barberName);
                            calendarMap.put("Mail", barberUser.getEmail());
                            calendarMap.put("Uid", barberUser.getUser_id());
                            calendarMap.put("sundayRoutine", insertTimesToFireStore(sundayLayout, barberName));
                            calendarMap.put("mondayRoutine", insertTimesToFireStore(mondayLayout, barberName));
                            calendarMap.put("tuesdayRoutine", insertTimesToFireStore(thursdayLayout, barberName));
                            calendarMap.put("wednesdayRoutine", insertTimesToFireStore(wednesdayLayout, barberName));
                            calendarMap.put("thursdayRoutine", insertTimesToFireStore(thursdayLayout, barberName));
                            calendarMap.put("saturdayRoutine", insertTimesToFireStore(saturdayLayout, barberName));
                            calendarMap.put("services", insertServicesToFireStore(serviceLayout, barberName));

                        } catch (ParseException e) {
//                            Toast.makeText(CreateCalendar.this, "Insertion Catch Error", Toast.LENGTH_SHORT).show();

                        }


                        // checking at least one day shouldn't be empty and one service in order to make the calendar
                        // or pick it as holiday which means not null !!
                        if (calendarMap.get("sundayRoutine") != null && calendarMap.get("mondayRoutine") != null &&
                                calendarMap.get("tuesdayRoutine") != null && calendarMap.get("wednesdayRoutine") != null &&
                                calendarMap.get("thursdayRoutine") != null && calendarMap.get("saturdayRoutine") != null &&
                                checkServices(serviceLayout)) {

                            firestore1.collection("Business").document(barberName)
                                    .set(calendarMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent intent = new Intent(CreateCalendar.this, AdminDashboardActivity.class);
                                    intent.putExtra("queName", barberName);
                                    makePositiveSnackBarMessage("Success Listener");
                                    startActivity(intent);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    makeNegativeSnackBarMessage("Error Listener: " + e.getMessage());
                                }
                            });

                        } // end of if
                        else makeNegativeSnackBarMessage("Fill Times & Services As Requested !!\n Or Pick Holiday");

                    } // End of task.getResult() Condition

                });
    }


    //convert the times fields to array list that can be uploaded to data base
    private ArrayList<TimeOption> insertTimesToFireStore(RelativeLayout layout, String barberName) throws ParseException {
        dayTimeOption = new ArrayList<>();

        java.text.DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");

        for (int i = 0; i < layout.getChildCount(); i++) {
            View raw = layout.getChildAt(i);
            EditText startShift = raw.findViewById(R.id.StartShift);
            EditText endShift = raw.findViewById(R.id.EndShift);

            String sShift = startShift.getText().toString();
            String eShift = endShift.getText().toString();


            Date StartTime = format.parse("2000-01-01 " + sShift);
            Date EndTime = format.parse("2000-01-01 " + eShift);


            TimeOption timeOption = new TimeOption();
            timeOption.setStartTime(StartTime);
            timeOption.setEndTime(EndTime);
            dayTimeOption.add(timeOption);
        }
        return dayTimeOption;
    } // End of insertTimesToFireStore Function


    //convert the services fields to array list that can be uploeaded to data base
    private ArrayList<CalendarServices> insertServicesToFireStore(LinearLayout servicesLayout, String barberName) {
        ArrayList<CalendarServices> calendarServices = new ArrayList<>();

        for (int i = 0; i < servicesLayout.getChildCount(); i++) {
            View raw = servicesLayout.getChildAt(i);

            EditText serviceName = raw.findViewById(R.id.serviceName);
            EditText serviceTime = raw.findViewById(R.id.service_time);

            String serviceField = serviceName.getText().toString();
            Integer timeField = Integer.valueOf(serviceTime.getText().toString());


            CalendarServices calendarService = new CalendarServices(serviceField, timeField);
            calendarServices.add(calendarService);
        }
        return calendarServices;
    }


    // function check's if the inserted times are correct.
    private boolean checkShiftTimes(RelativeLayout layout) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("hh:mm aa");
        boolean flag = true;

        for (int i = 0; i < layout.getChildCount(); i++) {

            View raw = layout.getChildAt(i);
            EditText startShift = raw.findViewById(R.id.StartShift);
            EditText endShift = raw.findViewById(R.id.EndShift);

            String startField = startShift.getText().toString();
            String endField = endShift.getText().toString();

            if (TextUtils.isEmpty(startField) || TextUtils.isEmpty(endField)) {
                startShift.setError("fill fields or remove them!");
                flag = false;
                continue;
            }
            Date StartTime = format.parse("" + startField);
            Date EndTime = format.parse("" + endField);
            if (StartTime.getTime() >= EndTime.getTime()) {
                startShift.setError("Start time cant be greater or equal to end time");
                flag = false;
                continue;
            } else {
                startShift.setError(null);
            }
        }
        //if every thing is okay flag == true ; else flag == false .
        return flag;
    } // End of checkShiftTimes Function


    //function to check if services inputs in valid
    private boolean checkServices(LinearLayout servicesLayout) {
        boolean flag = true;

        if (servicesLayout.getChildCount() == 0) {
            Toast.makeText(CreateCalendar.this, "you should write at least one service", Toast.LENGTH_LONG).show();
            flag = false;
            return flag;
        }

        for (int i = 0; i < servicesLayout.getChildCount(); i++) {
            View raw = servicesLayout.getChildAt(i);
            EditText serviceName = raw.findViewById(R.id.serviceName);
            EditText serviceTime = raw.findViewById(R.id.service_time);

            if (serviceName.getText().toString().isEmpty() || serviceTime.getText().toString().isEmpty()) {
                Toast.makeText(CreateCalendar.this, "fill fields or remove them!", Toast.LENGTH_LONG).show();
                flag = false;
                continue;
            }
            if (Integer.parseInt(serviceTime.getText().toString()) <= 0) {
                Toast.makeText(CreateCalendar.this, "Time Cant be minus or zero", Toast.LENGTH_LONG).show();
                flag = false;
                continue;
            }
            if ((Integer.parseInt(serviceTime.getText().toString()) % 5) != 0) {
                Toast.makeText(CreateCalendar.this, "Time has to be duplicates of 5", Toast.LENGTH_LONG).show();
                flag = false;
                continue;
            }
        }
        return flag;
    }


    // function to activate timePicker and display times on the screen.
    public void addWorkTimeToLayout(final RelativeLayout l) {

        final View shiftTimesLayout = getLayoutInflater().inflate(R.layout.shift_times_layout, null, false);
        final Button startBtn = (Button) shiftTimesLayout.findViewById(R.id.StartBtn);
        final Button endBtn = (Button) shiftTimesLayout.findViewById(R.id.EndBtn);

        final EditText startShift = (EditText) shiftTimesLayout.findViewById(R.id.StartShift);
        final EditText endShift = (EditText) shiftTimesLayout.findViewById(R.id.EndShift);


        startBtn.setOnClickListener(new View.OnClickListener() {
            int stHours, stMinute;

            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        CreateCalendar.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        stHours = hourOfDay;
                        stMinute = minute;
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(2000, 1, 1, stHours, stMinute);
                        startShift.setText(DateFormat.format("hh:mm aa", calendar));

//                        firstDate = calendar.getTime();
                    }
                }, 12, 0, true
                );
                timePickerDialog.updateTime(stHours, stMinute);
                timePickerDialog.show();
            }
        }); // End of start Shift Btn

        endBtn.setOnClickListener(new View.OnClickListener() {
            int edHours, edMinute;


            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        CreateCalendar.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        edHours = hourOfDay;
                        edMinute = minute;
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(2000, 1, 1, edHours, edMinute);
                        endShift.setText(DateFormat.format("hh:mm aa", calendar));
//                        secondDate = calendar.getTime();
                    }
                }, 12, 0, true
                );
                timePickerDialog.updateTime(edHours, edMinute);
                timePickerDialog.show();
            }
        }); // End of End Shift Btn


        l.addView(shiftTimesLayout);
    }// End of addWorkTimeToLayout Function


    //adding row to service layout
    public void addServiceToLayout() {

        final View raw = getLayoutInflater().inflate(R.layout.add_service_layout, null, false);
        final EditText serviceName = (EditText) raw.findViewById(R.id.serviceName);
        final EditText serviceTime = (EditText) raw.findViewById(R.id.service_time);
        final ImageView imageRemove = (ImageView) raw.findViewById(R.id.image_remove);

        imageRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeViewFromLayout(raw);
            }
        });
        serviceLayout.addView(raw);
    }


    //removing from from layout
    public void removeViewFromLayout(View v) {
        serviceLayout.removeView(v);
    }

}
