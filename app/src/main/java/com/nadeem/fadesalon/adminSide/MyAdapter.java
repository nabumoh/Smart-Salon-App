package com.nadeem.fadesalon.adminSide;
/* MyAdapter Class to adapt the fetched data from firebase into recyclerview. */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.nadeem.fadesalon.R;
import com.nadeem.fadesalon.models.User;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private ArrayList<User> userArrayList;
    private Context context;
    User user;
    private View mParentLayout;
    public boolean flag1,flag2;

    public MyAdapter(Context context, ArrayList<User> users) {
        this.userArrayList = users;
        this.context = context;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        mParentLayout = v;
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {

         user = this.userArrayList.get(position);
         holder.Fullname.setText(String.valueOf(user.getFullname()));
         holder.Email.setText(String.valueOf(user.getEmail()));
         holder.Status.setText(String.valueOf(user.getStatus()));
         holder.userId =user.getUser_id();
         holder.name =user.getFullname();
         holder.newStatus =user.getStatus();
         holder.userholder =user;


    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView Fullname, Email , Status ;
        public ImageView BlockBtn;
        public String userId,name,newStatus;
        User userholder;

        public MyViewHolder(View itemView) {
            super(itemView);
            Fullname = itemView.findViewById(R.id.list_name);
            Email = itemView.findViewById(R.id.list_email);
            Status = itemView.findViewById(R.id.list_status);
            BlockBtn = itemView.findViewById(R.id.blockBtn);


            BlockBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    AlertDialog.Builder BlockDialog = new AlertDialog.Builder(v.getContext());
                    BlockDialog.setTitle("Block Account");
                    BlockDialog.setMessage("Are you sure you want to block "+name+" permanently ?!");

                    BlockDialog.setPositiveButton("Block", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!blockUser(name,userId) && !blockBarber(name,userId)) {
                                makeNegativeSnackBarMessage("Change failed.");
                            }
                            if(blockUser(name,userId)){
                                blockUser(name,userId);
                                makePositiveSnackBarMessage(name+" has been blocked !");
                                notifyDataSetChanged();
                            } else {
                                blockBarber(name,userId);
                                deleteBarberfromBusiness(name);
                                makePositiveSnackBarMessage(name+" has been blocked !");
                                notifyDataSetChanged();
                            }
                        }
                    });
                    BlockDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do Nothing
                            // Class Dialog
                        }
                    });

                    BlockDialog.create().show();
            }
            }); // end of BlockBtn



        }
    }

    // function to change the status (block) of selected user.
    private boolean blockUser(String name, String userId) {

        DocumentReference documentReference;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String status = "blocked";
        final DocumentReference saveDoc = db.collection("users").document(userId);
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                //DocumentSnapshot snapshot = transaction.get(saveDoc);

                //field String should match to the field in collection , name is the variable this function.
                transaction.update(saveDoc, "Status", status);

                // Success
                flag1=true;
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        flag1=false;
                    }
                });
        return flag1;
    }

    // function to change the status (block) of selected barber.
    public boolean blockBarber(String name, String userId) {

        DocumentReference documentReference;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String status = "blocked";

        final DocumentReference saveDoc = db.collection("barbers").document(userId);
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                //DocumentSnapshot snapshot = transaction.get(saveDoc);

                //field String should match to the field in collection , name is the variable this function.
                transaction.update(saveDoc, "Status", status);

                // Success
                flag2=true;
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        flag2=false;
                    }
                });
          return flag2;
    }

    // function to delete the name of blocked barber from showing to prevent from showing to customers.
    public void deleteBarberfromBusiness(String name){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Business").document(name).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                 // do nothing.
            }
        });
    }


    // show snackBar message at the bottom of the screen.
    private void makePositiveSnackBarMessage(String message){
        Snackbar snackbar = Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(ContextCompat.getColor(this.context,R.color.colorGreen));
        snackbar.setTextColor(ContextCompat.getColor(this.context,R.color.colorBlack));
        snackbar.show();
    }
    private void makeNegativeSnackBarMessage(String message){
        Snackbar snackbar = Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(ContextCompat.getColor(this.context,R.color.colorRed));
        snackbar.setTextColor(ContextCompat.getColor(this.context,R.color.colorWhite));
        snackbar.show();
    }
}

