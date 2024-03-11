package com.nadeem.fadesalon.adminSide;
/* MyAdapter Class to adapt the fetched appointments from firebase into recyclerview. */

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

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nadeem.fadesalon.R;
import com.nadeem.fadesalon.models.Appointments;
import com.nadeem.fadesalon.models.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyAdapter_Appointments extends RecyclerView.Adapter<MyAdapter_Appointments.MyViewHolder> {

    private ArrayList<Appointments> AppointmentsArrayList;
    private Context context;
    User user;
    private View mParentLayout;
    boolean flagy;

    public MyAdapter_Appointments(Context context, ArrayList<Appointments> Appointments) {
        this.AppointmentsArrayList = Appointments;
        this.context = context;
    }

    @NonNull
    @Override
    public MyAdapter_Appointments.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.list_item_appointment, parent, false);
        mParentLayout = v;
        return new MyViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull MyAdapter_Appointments.MyViewHolder holder, int position) {

        Appointments appointments = this.AppointmentsArrayList.get(position);
        holder.Fullname.setText(String.valueOf(appointments.getClientName()));
        holder.Email.setText(String.valueOf(appointments.getClientMail()));
        holder.Service.setText(String.valueOf(appointments.getServiceName()));
        holder.barberName = String.valueOf(appointments.getBarberName());
        holder.docId = String.valueOf(appointments.getDocId());
        holder.clientId = String.valueOf(appointments.getClientId());


        Timestamp timeStamp =  appointments.getStartTime();
        Date date = timeStamp.toDate();
        DateFormat f = new SimpleDateFormat("HH:mm aa");
        String d = f.format(date);
        holder.StartTime.setText(d);

//        int hour = (int) (TimeUnit.SECONDS.toHours(appointments.getStartTime().getSeconds())) % 24;
//        int minute = (int) TimeUnit.SECONDS.toMinutes(appointments.getStartTime().getSeconds()) % 60;
//        holder.StartTime.setText(hour+":"+minute);



    }

    @Override
    public int getItemCount() {
        return AppointmentsArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView Fullname, Email,Service,StartTime;
        public ImageView deleteClick;
        String barberName,docId,clientId;
//        Timestamp timeStamp;

        public MyViewHolder(View itemView) {
            super(itemView);
            Fullname = itemView.findViewById(R.id.list_name);
            Email = itemView.findViewById(R.id.list_email);
            Service = itemView.findViewById(R.id.list_service);
            StartTime = itemView.findViewById(R.id.list_startTime);
            deleteClick = itemView.findViewById(R.id.deleteClick);

            deleteClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    AlertDialog.Builder BlockDialog = new AlertDialog.Builder(v.getContext());
                    BlockDialog.setTitle("Delete Booking");
                    BlockDialog.setMessage("Are you sure you want to Delete "+barberName+" Booking ?!");

                    BlockDialog.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteAppointment(docId,barberName,clientId);
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
            });



        }
    }


    // function to delete the selected appointment.
    private void deleteAppointment(String docId, String barberName,String clientId) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        flagy = true;


        db.collection("Business").document(barberName).collection("appointments")
                .document(docId).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

            }else flagy = false;
        });

        db.collection("users").document(clientId).collection("appointments")
                .document(docId).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

            }else flagy = false;
        });

        if(flagy){
            makePositiveSnackBarMessage("Booked Appointment has been deleted successfully !");

        }

    }






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

