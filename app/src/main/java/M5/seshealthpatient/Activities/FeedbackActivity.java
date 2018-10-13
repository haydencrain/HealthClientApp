package M5.seshealthpatient.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import M5.seshealthpatient.Models.BaseUser;
import M5.seshealthpatient.Models.Comment;
import M5.seshealthpatient.Models.PlaceResult;
import M5.seshealthpatient.R;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedbackActivity extends BaseActivity {
    public static final int RECOMMEND_FACILITY = 10101;

    private String patientId;
    private String dataPacketId;
    private String dataPacketTitle;
    private String feedbackType;
    private boolean hasLocation;
    private LinkedList<Comment> mComments;

    DatabaseReference mUsersDb;
    DatabaseReference mCommentsDb;

    private RelativeLayout mAddMessageWrapper;
    private TextView mMessageTV;
    private ListView mListView;
    private EditText addMessageTxt;
    private RelativeLayout mLocationRecommendation;

    private ValueEventListener mCommentsEventListener;
    private ValueEventListener mUsersEventListener;

    DataSnapshot mUserDataSnapshot;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_view_feedback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDataFromIntent();
        bindViewComponents();
        ButterKnife.bind(this);
        checkIfCanAddComment();
        setTitleAndCommentDb();
        mUsersDb = FirebaseDatabase.getInstance().getReference("Users");
    }

    @Override
    public void onStart() {
        mUsersEventListener = getUsersEventListener();
        mCommentsEventListener = getCommentsEventListener();
        mCommentsDb.addValueEventListener(mCommentsEventListener);
        mUsersDb.addValueEventListener(mUsersEventListener);
        super.onStart();
    }

    @Override
    public void onStop() {
        mCommentsDb.removeEventListener(mCommentsEventListener);
        mUsersDb.removeEventListener(mUsersEventListener);
        super.onStop();
    }

    public void getDataFromIntent() {
        patientId = (String)getIntent().getSerializableExtra("PATIENT_ID");
        dataPacketId = (String)getIntent().getSerializableExtra("DATA_PACKET_ID");
        dataPacketTitle = (String)getIntent().getSerializableExtra("DATA_PACKET_TITLE");
        feedbackType = (String)getIntent().getSerializableExtra("FEEDBACK_TYPE");
        hasLocation = (boolean)getIntent().getSerializableExtra("HAS_LOCATION");
    }

    public void bindViewComponents() {
        mAddMessageWrapper = findViewById(R.id.addMessageWrapper);
        mListView = findViewById(R.id.commentsListView);
        mMessageTV = findViewById(R.id.messageTV);
        addMessageTxt = findViewById(R.id.addMessageTxt);
        mLocationRecommendation = findViewById(R.id.locationRecommendation);
        mLocationRecommendation.setVisibility(View.GONE);
    }

    public void checkIfCanAddComment() {
        if (isUserPatient())
            mAddMessageWrapper.setVisibility(View.GONE);
    }

    public void checkIfCanRecommendLocation() {
        if (!isUserPatient() && hasLocation)
            mLocationRecommendation.setVisibility(View.VISIBLE);
    }

    public boolean isUserPatient() {
        return patientId.equals(getUserId());
    }

    public void setTitleAndCommentDb() {
        String title = "";
        switch (feedbackType) {
            case "QUERY":
                setCommentsDbPath("queryComments");
                title = " - Query Feedback";
                break;
            case "HEART_RATE":
                setCommentsDbPath("heartRateComments");
                title = " - Heart Rate Feedback";
                break;
            case "LOCATION":
                setCommentsDbPath("locationComments");
                title = " - Location Feedback";
                checkIfCanRecommendLocation();
                break;
            case "FILES":
                setCommentsDbPath("filesComments");
                title = " - Files Feedback";
                break;
        }

        if (title.isEmpty())
            title = "Untitled";

        setTitle(dataPacketTitle + title);
    }

    @OnClick(R.id.addMessageBtn)
    public void onAddMessageClick(View view) {
        hideKeyboard();
        addComment();
    }

    @OnClick(R.id.locationBtn)
    public void onLocationBtnClick(View view) {
        Intent intent = new Intent(this, RecommendFacility.class);
        intent.putExtra("DATA_PACKET_ID", dataPacketId);
        intent.putExtra("PATIENT_ID", patientId);
        startActivityForResult(intent, RECOMMEND_FACILITY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == RECOMMEND_FACILITY && resultCode == RESULT_OK) {
            PlaceResult selectedFacility = (PlaceResult)intent.getSerializableExtra("SELECTED_FACILITY");
            toastMessage(this, "Hey we did something hella cool here boysss");
        }
    }

    public void addComment() {
        String message = addMessageTxt.getText().toString();
        Comment comment = new Comment(message, getUserId(), new Date().getTime());
        String key = mCommentsDb.push().getKey();
        mCommentsDb.child(key).setValue(comment);
        addMessageTxt.setText("");
        Toast.makeText(FeedbackActivity.this, "Feedback Sent Succesfully", Toast.LENGTH_LONG).show();
    }

    public void setCommentsDbPath(String commentsPath) {
        mCommentsDb = FirebaseDatabase.getInstance()
                .getReference("Users/" + patientId)
                .child("Queries")
                .child(dataPacketId)
                .child(commentsPath);
    }

    public ValueEventListener getCommentsEventListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                createCommentList(dataSnapshot.getChildren());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    public ValueEventListener getUsersEventListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserDataSnapshot = dataSnapshot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    public void createCommentList(Iterable<DataSnapshot> commentsSnapshot) {
        final LinkedList<Comment> comments = new LinkedList<>();
        LinkedList<String> ids = new LinkedList<>();
        for (DataSnapshot commentSnapshot : commentsSnapshot) {
            Comment comment = commentSnapshot.getValue(Comment.class);
            comments.add(comment);
            ids.add(comment.getCommenterId());
        }
        mComments = comments;
        if (!mComments.isEmpty()) {
            mMessageTV.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            addCommentsToList(ids);
        } else {
            String message = "No comments have been added yet!";
            mMessageTV.setText(message);
            mListView.setVisibility(View.GONE);
        }

    }

    public void addCommentsToList(LinkedList<String> dummyValues) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, android.R.id.text1, dummyValues) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = view.findViewById(android.R.id.text1);
                TextView text2 = view.findViewById(android.R.id.text2);
                text1.setTextSize(12);
                text1.setPadding(0,0,0,5);

                Comment comment = mComments.get(position);
                String commenterName = "";
                if (mUserDataSnapshot != null) {
                    BaseUser user = mUserDataSnapshot.child(comment.getCommenterId()).getValue(BaseUser.class);
                    commenterName = user.getName();
                }

                Date date = comment.getSentDate();
                String dateString = String.format(Locale.ENGLISH, "%1$s %2$tr %2$te %2$tb %2$tY", "at", date);
                String nameAndDate = String.format("%s %s", commenterName, dateString);

                text1.setText(nameAndDate);
                text2.setText(comment.getMessage());

                return view;
            }
        };
        mListView.setAdapter(adapter);
    }
}
