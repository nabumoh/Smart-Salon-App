package com.nadeem.fadesalon.adminSide;
/*  Class handles the Admin profile page  */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nadeem.fadesalon.MainActivity;
import com.nadeem.fadesalon.R;
import com.nadeem.fadesalon.models.User;


public class AdminProfileActivity extends AppCompatActivity {

    private ImageView arrowBack,joinBtn,blockBtn,editBtn;
    private EditText nameFetch,emailFetch;
    private Button logoutBtn;

    User userObject;
    FirebaseFirestore firestore; // for barber reference
    FirebaseUser user;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        arrowBack = findViewById(R.id.back_arrow);
        logoutBtn = findViewById(R.id.logoutBtn);
        joinBtn = findViewById(R.id.joinBtn);
        editBtn = findViewById(R.id.editBtn);
        blockBtn = findViewById(R.id.blockBtn);
        nameFetch = findViewById(R.id.nameFetch);
        emailFetch = findViewById(R.id.emailFetch);


        /* ********************  Arrow back Button   *************** */
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminProfileActivity.this, AdminDashboardActivity.class);
                startActivity(intent);
            }
        });

        /* ********************  Join new Barber Button   *************** */
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminProfileActivity.this, adminRegisteraionActivity.class);
                startActivity(intent);
            }
        });

        /* ********************  Update Barber Button   *************** */
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminProfileActivity.this, AdminUpdateProfile.class);
                startActivity(intent);
            }
        });

        /* ********************  Block Barber Button   *************** */
        blockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminProfileActivity.this, BlockAdmins.class);
                startActivity(intent);
            }
        });

        /* ********************  LogOut Profile Button   *************** */
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth = FirebaseAuth.getInstance();
                fAuth.signOut();
                Toast.makeText(getApplicationContext(), "logged out Successfully", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AdminProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        getBarber();

    }

    /* function get's the barber data from firebase */
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
                    nameFetch.setText(userObject.getFullname());
                    emailFetch.setText(userObject.getEmail());

                } else {
                    Toast.makeText(getApplicationContext(), "Failed fetching Barber Data", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return userObject;
    }
    //to keep the app running in the back
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
