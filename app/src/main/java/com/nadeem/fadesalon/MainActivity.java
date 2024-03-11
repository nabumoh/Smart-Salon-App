package com.nadeem.fadesalon;
// main class that handles side menu bar
import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    //Variables
    private AppBarConfiguration mAppBarConfiguration;
    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar;
    Menu menu;

    FirebaseFirestore firestore; // for user reference
    FirebaseUser user;
    FirebaseAuth fAuth;
    public boolean flag;
    String CurrentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*---------------------Hooks------------------------*/
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        fAuth = FirebaseAuth.getInstance();

        /*---------------------Tool Bar------------------------*/
        setSupportActionBar(toolbar);


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_appointment, R.id.nav_login, R.id.nav_profile, R.id.nav_admin, R.id.nav_reachUs, R.id.nav_aboutUs)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        menu = navigationView.getMenu();


        /*--------------------- Hide or show items------------------------*/
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null ){
            // user  logged in
            menu.findItem(R.id.nav_login).setVisible(false);
            menu.findItem(R.id.nav_profile).setVisible(true);
        }else{
            // user not logged in
            // it will hide profile menu item
            menu.findItem(R.id.nav_login).setVisible(true);
            menu.findItem(R.id.nav_profile).setVisible(false);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    //to keep the app running in the back
    public void onBackPressed() {
        moveTaskToBack(true);
    }


}
