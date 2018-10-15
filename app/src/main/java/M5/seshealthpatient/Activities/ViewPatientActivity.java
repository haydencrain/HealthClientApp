package M5.seshealthpatient.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import M5.seshealthpatient.Models.PatientUser;
import M5.seshealthpatient.R;

public class ViewPatientActivity extends BaseActivity {

    ListView patientInfoLv;
    Button viewPacketsbtn;
    PatientUser patient;
    String patientId;
    String patientName;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_view_patient;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("View Patient Information");
        patientInfoLv =(ListView)findViewById(R.id.patientInfoListView) ;

        patient = (PatientUser)getIntent().getSerializableExtra("PatientInfo");
        patientId = (String)getIntent().getSerializableExtra("PATIENT_ID");
        patientName = (String)getIntent().getSerializableExtra("PatientName");

        ArrayList<String> patientInfo = new ArrayList<>();
        patientInfo.add("Name:        " + patientName);
        patientInfo.add("Age:       " + patient.getAge());
        patientInfo.add("Sex:       " + patient.getSex());
        patientInfo.add("Number:    " + patient.getPhone());
        patientInfo.add("Weight:      " + patient.getWeight() + "kg");
        patientInfo.add("Height:      " + patient.getHeight() + "cm");
        patientInfo.add("Condition: " + patient.getMedicalCondition());

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, patientInfo);
        patientInfoLv.setAdapter(adapter);


        viewPacketsbtn = (Button) findViewById(R.id.viewPatientPacketsbtn);
        viewPacketsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewPatientActivity.this, ViewPatientDataPackets.class);

                intent.putExtra("PATIENT_ID", patientId);
                startActivity(intent);
            }
        });


    }
}
