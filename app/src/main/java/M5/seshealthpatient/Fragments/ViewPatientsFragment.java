package M5.seshealthpatient.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import M5.seshealthpatient.Models.BaseUser;
import M5.seshealthpatient.Models.DataPacket;
import M5.seshealthpatient.R;


public class ViewPatientsFragment extends Fragment implements AdapterView.OnItemClickListener {

    ListView patientsListView;
    private FirebaseUser mUser;
    private DatabaseReference mUserDb;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("View Patients");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.fragment_view_patients, container, false);
        patientsListView = view.findViewById(R.id.patientsListView);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        // get doctor ID
        String docId = mUser.getUid();
        mUserDb = FirebaseDatabase.getInstance().getReference("Users/");

        mUserDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                BaseUser baseUser = dataSnapshot.getValue(BaseUser.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        patientsListView.setOnItemClickListener(this);

        return view;



    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
