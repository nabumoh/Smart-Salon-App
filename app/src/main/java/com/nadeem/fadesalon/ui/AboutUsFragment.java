package com.nadeem.fadesalon.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.nadeem.fadesalon.MainActivity;
import com.nadeem.fadesalon.R;
// class handles app protocols and privacy
public class AboutUsFragment extends Fragment {

    //Variables
    private Button shareBtn;
    private Button backBtn;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_about_us, container, false);

        /*---------------------Hooks------------------------*/
        shareBtn = root.findViewById(R.id.shareBtn);
        backBtn = root.findViewById(R.id.backBtn);

        /*---------------------Share Button------------------------*/
       shareBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        Intent myIntent =new Intent(Intent.ACTION_SEND);
        myIntent.setType("text/plain");
        String shareSub="You Can ContactUs In the In Any Social Platform ";
        String shareBody="24 Hours Availabe for you service :)";
        myIntent.putExtra(Intent.EXTRA_TEXT,shareSub);
        myIntent.putExtra(Intent.EXTRA_SUBJECT,shareBody);
        startActivity(Intent.createChooser(myIntent,"Share using"));
      }
    });

        /*---------------------Back Button------------------------*/
       backBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
         // it will take me to Home by Default attitude of mainActivity
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        }
    });



    return root;
  }
}
