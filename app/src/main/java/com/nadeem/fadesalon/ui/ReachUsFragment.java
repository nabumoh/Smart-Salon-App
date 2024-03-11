package com.nadeem.fadesalon.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.nadeem.fadesalon.MainActivity;
import com.nadeem.fadesalon.R;
// class handles business contact ,work hours ,location ...
public class ReachUsFragment extends Fragment {

    Button wazeBtn;
    Button callBtn;
    Button facebookBtn;
    Button instaBtn;
    Button whatsBtn;
    private ImageView arrowBack;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_reach_us, container, false);

        wazeBtn = root.findViewById(R.id.wazeBtn);
        callBtn = root.findViewById(R.id.callBtn);
        arrowBack = root.findViewById(R.id.back_arrow);
//        facebookBtn = root.findViewById(R.id.facebookBtn);
//        instaBtn = root.findViewById(R.id.instaBtn);
//        whatsBtn = root.findViewById(R.id.whatsBtn);

        wazeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW,
                        // this will directly go to Waze locating the Barber Shop.
                        Uri.parse("https://waze.com/ul/hsvc1ht5t2"));
                startActivity(intent);
            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:0507486336"));
                startActivity(callIntent);
            }
        });

        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

//        facebookBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(Intent.ACTION_VIEW,
//                        // this will directly go to Facebook Page of the Barber Shop.
//                        Uri.parse("https://www.facebook.com/Barbershop.cut.and.shave/"));
//                startActivity(intent);
//            }
//        });
//
//        instaBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(Intent.ACTION_VIEW,
//                        // this will directly go to instagram Page of the Barber Shop.
//                        Uri.parse("https://instagram.com/barbershop.cut_and_shave?utm_medium=copy_link"));
//                startActivity(intent);
//            }
//        });
//
//        whatsBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent sendIntent = new Intent();
//                sendIntent.setAction(Intent.ACTION_SEND);
//                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
//                sendIntent.setType("text/plain");
//                sendIntent.setPackage("com.whatsapp");
//                startActivity(Intent.createChooser(sendIntent, "שלום אני מעוניין לדבר עם ספר לבירור אישי..."));
//                startActivity(sendIntent);
//                //opens the portfolio details class
//            }
//        });

        return root;
    }
}
