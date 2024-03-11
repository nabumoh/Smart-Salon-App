package com.nadeem.fadesalon.adminSide;
 /* Class shows the Admin Dashboard & his Abilities  */
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.nadeem.fadesalon.MainActivity;
import com.nadeem.fadesalon.R;

public class AdminDashboardActivity extends AppCompatActivity {

    private ImageView arrowBack;
    Button profileBtn, appointmentsBtn, calendarBtn, customersBtn,currentcalBtn;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        /* **************** hooks  ************ */
        profileBtn = findViewById(R.id.profileBtn);
        arrowBack = findViewById(R.id.back_arrow);
        appointmentsBtn = findViewById(R.id.appointmentsBtn);
        calendarBtn = findViewById(R.id.calendarBtn);
        customersBtn = findViewById(R.id.customersBtn);
        currentcalBtn = findViewById(R.id.currentCalBtn);

        /* ********************  Arrow back Button   *************** */
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder BlockDialog = new AlertDialog.Builder(v.getContext());
                BlockDialog.setTitle("Delete Booking");
                BlockDialog.setMessage("Are you sure you want to logout ?!");

                BlockDialog.setPositiveButton("Sure Exit!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(AdminDashboardActivity.this, MainActivity.class);
                        fAuth = FirebaseAuth.getInstance();
                        fAuth.signOut();
                        startActivity(intent);
                    }
                });
                BlockDialog.setNegativeButton("Stay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do Nothing
                        // Class Dialog
                    }
                });

                BlockDialog.create().show();

            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, AdminProfileActivity.class);
                startActivity(intent);
            }
        });

        appointmentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, ManageAppointments.class);
                startActivity(intent);
            }
        });

        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, CreateCalendar.class);
                startActivity(intent);
            }
        });

        customersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, ManageUsers.class);
                startActivity(intent);
            }
        });

        currentcalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, showCurrentCalendar.class);
                startActivity(intent);
            }
        });

    }


    //to keep the app running in the back
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
