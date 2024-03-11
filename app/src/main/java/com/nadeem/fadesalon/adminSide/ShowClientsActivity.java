package com.nadeem.fadesalon.adminSide;
/* Unused Class */

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nadeem.fadesalon.R;
import com.nadeem.fadesalon.models.User;

import java.util.ArrayList;

/************
 * Class works in list view but it only show document id for now
 * xml only -->  showClientsActivity.xml
 * Unused class
 * *************/

public class ShowClientsActivity extends AppCompatActivity {

    ListView listClients; // list view for clients
    ArrayAdapter arrayAdapter; // for clients array list
    ArrayList<User> clients = new ArrayList<>(); // for all clients

    final Context context = this;
    FirebaseFirestore db = FirebaseFirestore.getInstance(); // get firebase firestore
    private static String TAG = ShowClientsActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_clients);

        listClients = findViewById(R.id.list_clients);
        ShowDataList();
    }

    public void ShowDataList() {
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //Loop the list and add user documents to our ArrayList
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String userID = document.getId();
                        String fullname = document.getData().get("FullName").toString();
                        String email = document.getData().get("Mail").toString();
                        String status = document.getData().get("Status").toString();
                        String userType = document.getData().get("UserType").toString();

                        User user = new User(userID, fullname, email, userType, status);
                        clients.add(user);
                    }
                    arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, clients);
                    listClients.setAdapter(arrayAdapter);
                } else {
                    Log.d(TAG, "Error getting documents : ", task.getException());
                }
            }
        });
    }

    public void ShowDataList2() {
        db.collection("users")
                .get().
                addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            User u = document.toObject(User.class);
//                            clients.add(u);
                            Log.d(TAG, u.getFullname() + " " + u.getEmail());
                        }
                        arrayAdapter.addAll(clients);
                    }
                });
    }
}
