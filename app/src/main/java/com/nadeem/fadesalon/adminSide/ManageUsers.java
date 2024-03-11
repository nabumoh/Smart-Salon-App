package com.nadeem.fadesalon.adminSide;
/* class to manage users in recyclerView if there's any need to block existing user */

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.nadeem.fadesalon.R;
import com.nadeem.fadesalon.models.User;

import java.util.ArrayList;
/************
 * Related classes --> ManageUsers.java + MyAdapter.java + listItem.xml + activityManageUsers.xml
 * Class purpose --> class  handles the Customers RecycleView for the Admin to Block them ....
 * *************/
public class ManageUsers extends AppCompatActivity {

    private static final String TAG = "ManageUsers";
    RecyclerView recyclerView;
    ArrayList<User> userArrayList;
    MyAdapter myAdapter;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    private View mParentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        // hooks
        mParentLayout = findViewById(android.R.id.content);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Wait A Moment getting Users ...");
        progressDialog.show();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        firestore = FirebaseFirestore.getInstance();
        userArrayList = new ArrayList<>();
        myAdapter = new MyAdapter(ManageUsers.this, userArrayList);
        recyclerView.setAdapter(myAdapter);

        EventChangeListener();

    }

    // function get's all the users from firebase DB.
    private void EventChangeListener() {
        firestore.collection("users").orderBy("FullName", Query.Direction.ASCENDING).
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
                                String userID = dc.getDocument().getId();
                                String fullname = dc.getDocument().getData().get("FullName").toString();
                                String email = dc.getDocument().getData().get("Mail").toString();
                                String userType = dc.getDocument().getData().get("UserType").toString();
                                String status = dc.getDocument().getData().get("Status").toString();

                                User user = new User(userID,fullname, email,userType,status);
                                userArrayList.add(user);
                                makeSnackBarMessage("Query Successed. Check Logs.");
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
