package M5.seshealthpatient.Fragments;



import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import M5.seshealthpatient.Activities.AddDetails;
import M5.seshealthpatient.Activities.LoginActivity;
import M5.seshealthpatient.Activities.ViewInformation;
import M5.seshealthpatient.Activities.ViewPatientDataPackets;
import M5.seshealthpatient.R;

/**
 * Class: PatientInformationFragment
 * Extends: {@link Fragment}
 * Author: Carlos Tirado < Carlos.TiradoCorts@uts.edu.au> and YOU!
 * Description:
 * <p>
 * This fragment's job will be that to display patients information, and be able to edit that
 * information (either edit it in this fragment or a new fragment, up to you!)
 * <p>

 */
public class DoctorInformationFragment extends Fragment {
    public DoctorInformationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: Instead of hardcoding the title perhaps take the user name from somewhere?
        // Note the use of getActivity() to reference the Activity holding this fragment
        getActivity().setTitle("Doctor Information");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_doctor_information, container, false);



        return v;
    }



}
