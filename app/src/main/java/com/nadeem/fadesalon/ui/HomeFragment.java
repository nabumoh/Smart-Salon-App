package com.nadeem.fadesalon.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.nadeem.fadesalon.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private Button MakeAppointmentBtn;
    ImageSlider imageSlider;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        /*--------------------- Hooks ------------------------*/
        MakeAppointmentBtn = root.findViewById(R.id.appointmentBtn);
        imageSlider = root.findViewById(R.id.image_slider);

        /*---------------------Make Appointment Button Intent------------------------*/
        MakeAppointmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppointmentFragment appointmentFragment = new AppointmentFragment();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.drawer_layout,appointmentFragment);
                transaction.addToBackStack(null); // to Avoid memory leakage.
                transaction.commit();
            }
        });

        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.cut6, ScaleTypes.CENTER_INSIDE));
        slideModels.add(new SlideModel(R.drawable.cut3, ScaleTypes.CENTER_INSIDE));
        slideModels.add(new SlideModel(R.drawable.cut7, ScaleTypes.CENTER_INSIDE));
        slideModels.add(new SlideModel(R.drawable.cut8, ScaleTypes.CENTER_INSIDE));
        slideModels.add(new SlideModel(R.drawable.cut9, ScaleTypes.CENTER_INSIDE));
        slideModels.add(new SlideModel(R.drawable.cut10, ScaleTypes.CENTER_INSIDE));
        slideModels.add(new SlideModel(R.drawable.cut11, ScaleTypes.CENTER_INSIDE));
        imageSlider.setImageList(slideModels,ScaleTypes.CENTER_INSIDE); // for all images


        return root;
        }

}
