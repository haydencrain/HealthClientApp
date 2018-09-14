package M5.seshealthpatient.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;

import M5.seshealthpatient.Activities.ViewDataPacket;
import M5.seshealthpatient.Models.DataPacket;
import M5.seshealthpatient.R;

public class ViewDataPacketsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView mListView;
    private FirebaseUser mUser;
    private DatabaseReference mUserDb;
    private LinkedList<DataPacket> mDataPackets;
    private LinkedList<String> mDataPacketKeys;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("View Data Packets");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_data_packets, container, false);
        mListView = view.findViewById(R.id.dataPacketsListView);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        // get user
        mUserDb = FirebaseDatabase.getInstance().getReference("Users/" + mUser.getUid());
        mUserDb.child("Queries").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        createDataPacketList(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        mListView.setOnItemClickListener(this);

        return view;
    }

    public void createDataPacketList(DataSnapshot dataSnapshot) {
        mDataPackets = new LinkedList<>();
        mDataPacketKeys = new LinkedList<>();
        LinkedList<String> dataPacketDisplays = new LinkedList<>();
        int i = 0;
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            DataPacket dataPacket = snapshot.getValue(DataPacket.class);
            mDataPackets.add(dataPacket);
            mDataPacketKeys.add(snapshot.getKey());
            dataPacketDisplays.add("Data Packet " + ++i);
        }

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, dataPacketDisplays);
        mListView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(), ViewDataPacket.class);
        intent.putExtra("DATA_PACKET", mDataPackets.get(i));
        startActivity(intent);
    }
}
