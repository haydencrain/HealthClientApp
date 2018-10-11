package M5.seshealthpatient.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import M5.seshealthpatient.Models.DoctorUser;
import M5.seshealthpatient.Models.PatientUser;
import M5.seshealthpatient.R;

public class ViewDoctorFragment extends Fragment {

    private FirebaseUser mUser;
    private DatabaseReference mUsersDb;
    private ListView mDoctorInformationList;
    private ValueEventListener mUserEvent;
    private String mDoctorId;
    private DoctorUser mDoctor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Your Doctor");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_doctor_information, container, false);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUsersDb = FirebaseDatabase.getInstance().getReference("Users");
        mDoctorInformationList = view.findViewById(R.id.doctorInformation);
        return view;
    }

    @Override
    public void onStart() {
        mUserEvent = getFirebaseUserEvent();
        mUsersDb.addListenerForSingleValueEvent(mUserEvent);
        super.onStart();
    }

    @Override
    public void onStop() {
        mUsersDb.removeEventListener(mUserEvent);
        super.onStop();
    }

    public ValueEventListener getFirebaseUserEvent() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                setDoctorInformation(dataSnapshot);
                createDoctorInformationList();
            }

            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    public void setDoctorInformation(DataSnapshot dataSnapshot) {
        mDoctorId = dataSnapshot.child(mUser.getUid()).getValue(PatientUser.class).getDoctorID();
        mDoctor = dataSnapshot.child(mDoctorId).getValue(DoctorUser.class);
    }

    public void createDoctorInformationList() {
        ArrayList<String> array = new ArrayList<>();
        array.add("Name:\t" + mDoctor.getName());
        array.add("Occupation:\t" + mDoctor.getOccupation());
        array.add("Department:\t" + mDoctor.getDepartment());
        array.add("Introduction:\t" + mDoctor.getIntroduction());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, array);
        mDoctorInformationList.setAdapter(adapter);
    }

}
