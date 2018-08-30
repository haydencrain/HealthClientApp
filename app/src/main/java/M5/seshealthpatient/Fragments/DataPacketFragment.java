package M5.seshealthpatient.Fragments;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import M5.seshealthpatient.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DataPacketFragment extends Fragment {

    private static Button btn = null;
    private FragmentManager manager;
    private FragmentTransaction ft;

    private String str;
    public DataPacketFragment() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);// Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_data_packet, container, false);

        btn = (Button) v.findViewById( R.id.btnSHR );


        btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                manager = getFragmentManager();
                HeartRateFragment myJDEditFragment = new HeartRateFragment();
                ft = manager.beginTransaction();
                ft.replace(R.id.fragment_container, myJDEditFragment);
                ft.addToBackStack(null);
                ft.commit();

            }
            
        } );


        TextView tvObj = (TextView)v.findViewById(R.id.fm01tvid);

        str = (String)getArguments().get("str");

        tvObj.setText( str );

        return v;
    }

}
