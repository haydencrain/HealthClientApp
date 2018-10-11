package M5.seshealthpatient.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.ArrayList;

import M5.seshealthpatient.Models.BaseUser;
import M5.seshealthpatient.Models.DataPacket;
import M5.seshealthpatient.Models.PatientUser;
import M5.seshealthpatient.R;

public class ViewPatientsFragment extends Fragment implements AdapterView.OnItemClickListener {

    View view;

    ListView patientsListView;
    private FirebaseUser mUser;
    private DatabaseReference mUserDb;
    String docId;

    LinkedList<PatientUser> patients;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("View Patients");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         view = inflater.inflate(R.layout.fragment_view_patients, container, false);
        patientsListView = view.findViewById(R.id.patientsListView);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        // get doctor ID
         docId = mUser.getUid();
        mUserDb = FirebaseDatabase.getInstance().getReference("Users/");

        mUserDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //this goes over all users in the db and checks whos a patient
                findPatients(dataSnapshot);
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

    void findPatients(DataSnapshot ds)
    {
        patients = new LinkedList<>();
        for (DataSnapshot snapshot : ds.getChildren()) {

            BaseUser baseUser = snapshot.getValue(BaseUser.class);
            if(!baseUser.getIsDoctor())
            {
                PatientUser patient = snapshot.getValue(PatientUser.class);
                if(patient.getDoctorID().equals(docId))
                {
                    patients.add(patient);
                }
            }
        }
        //this puts the data into the list view, this can only be called after find patients has been called
        //so i decided to include it here. Note, it wont work in the OnCreateView method, because its seems
        //to delay calling findPatients and calls preparePatients first while is empty causing the app
        //to crash. This way its guranteed findPatients is being called first.
        preparePatientsListView();
    }


    void preparePatientsListView()
    {
        ArrayList<String> patientNames = new ArrayList<String>();
        for(PatientUser patient: patients)
        {
            patientNames.add(patient.getName());
        }

        ArrayAdapter<String> namesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, patientNames);

        patientsListView = view.findViewById(R.id.patientsListView);
        patientsListView.setAdapter(namesAdapter);
    }
}
