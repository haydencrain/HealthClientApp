package M5.seshealthpatient.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import M5.seshealthpatient.Models.BaseUser;
import M5.seshealthpatient.R;

public class BaseActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private boolean isUserDoctor;

    protected int getLayoutId() {
        // layout id is to be overwritten by the extending class
        return 0;
    }

    public boolean isUserLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public String getUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    public void ChangeTitle(String newTitle) {
        toolbar.setTitle(newTitle);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public boolean isUserDoctor() {
        return isUserDoctor;
    }

    public DatabaseReference getDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (!isUserLoggedIn())
            navigateToLogin();

        setIsUserDoctor();

        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    public void setIsUserDoctor() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("Users").child(getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                BaseUser user = dataSnapshot.getValue(BaseUser.class);
                isUserDoctor = user.getIsDoctor();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void navigateToLogin() {
        Intent in = new Intent(BaseActivity.this, LoginActivity.class);
        startActivity(in);
        finish();
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        navigateToLogin();
        Toast.makeText(this, "Signed out successfully.", Toast.LENGTH_LONG).show();
    }

    public void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
