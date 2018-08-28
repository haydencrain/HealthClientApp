package M5.seshealthpatient.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import M5.seshealthpatient.R;

public class RegisterActivity extends AppCompatActivity {
    EditText emailTxt, passTxt,confirmTxt;

    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        emailTxt = (EditText)findViewById(R.id.email_field);
        passTxt = (EditText)findViewById(R.id.password_field);
        confirmTxt = (EditText)findViewById(R.id.confirmTxt);

        auth = FirebaseAuth.getInstance();
    }

    public void createUser(View v){
        String confirmPassword = confirmTxt.getText().toString();
        String email = emailTxt.getText().toString();
        String password = passTxt.getText().toString();

        if(emailTxt.getText().toString().equals("") && passTxt.getText().toString().equals(""))
        {
            Toast.makeText(getApplicationContext(), "Blank not allowed", Toast.LENGTH_SHORT).show();
        }
        else if(!password.equals(confirmPassword))
        {
            Toast.makeText(getApplicationContext(), "Password does not match!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Patient is registered", Toast.LENGTH_SHORT).show();
                        }
                        else{
                                Toast.makeText(getApplicationContext(),"Patient cannot be registered", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }

    }


}