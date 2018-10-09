package M5.seshealthpatient.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import M5.seshealthpatient.Models.Comment;
import M5.seshealthpatient.Models.DataPacket;
import M5.seshealthpatient.R;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedbackActivity extends BaseActivity {
    private String patientId;
    private String dataPacketId;
    private String dataPacketTitle;
    private String feedbackType;
    private LinkedList<Comment> mComments;

    DatabaseReference mCommentsDb;

    private RelativeLayout mAddMessageWrapper;
    private TextView mMessageTV;
    private ListView mListView;
    private EditText addMessageTxt;

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

        if (!isUserDoctor())
            mAddMessageWrapper.setVisibility(View.GONE);

        String title = "";
        switch (feedbackType) {
            case "QUERY":
                addCommentListener("queryComments");
                title = " - Query Feedback";
                break;
            case "HEART_RATE":
                addCommentListener("heartRateComments");
                title = " - Heart Rate Feedback";
                break;
            case "LOCATION":
                addCommentListener("locationComments");
                title = " - Location Feedback";
        }
        setTitle(dataPacketTitle + title);
    }

    public void getDataFromIntent() {
        patientId = (String)getIntent().getSerializableExtra("PATIENT_ID");
        dataPacketId = (String)getIntent().getSerializableExtra("DATA_PACKET_ID");
        dataPacketTitle = (String)getIntent().getSerializableExtra("DATA_PACKET_TITLE");
        feedbackType = (String)getIntent().getSerializableExtra("FEEDBACK_TYPE");
    }

    public void bindViewComponents() {
        mAddMessageWrapper = findViewById(R.id.addMessageWrapper);
        mListView = findViewById(R.id.commentsListView);
        mMessageTV = findViewById(R.id.messageTV);
        addMessageTxt = findViewById(R.id.addMessageTxt);
    }

    @OnClick(R.id.addMessageBtn)
    public void onAddMessageClick(View view) {
        hideKeyboard();
        addComment();
    }

    public void addComment() {
        String message = addMessageTxt.getText().toString();
        Comment comment = new Comment(message, getUserId(), new Date().getTime());
        String key = mCommentsDb.push().getKey();
        mCommentsDb.child(key).setValue(comment);
        addMessageTxt.setText("");
        Toast.makeText(FeedbackActivity.this, "Feedback Sent Succesfully", Toast.LENGTH_LONG).show();
    }

    public void addCommentListener(String commentsPath) {
        mCommentsDb = FirebaseDatabase.getInstance()
                .getReference("Users/" + patientId)
                .child("Queries")
                .child(dataPacketId)
                .child(commentsPath);
        mCommentsDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                createCommentList(dataSnapshot.getChildren());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

                text1.setTextSize(16);

                Comment comment = mComments.get(position);
                Date date = comment.getSentDate();
                String dateString = String.format(Locale.ENGLISH, "%1$s %2$tr %2$te %2$tb %2$tY", "at:", date);
                String bottomText = String.format("%s %s", comment.getCommenterId(), dateString);

                text1.setText(comment.getMessage());
                text2.setText(bottomText);

                return view;
            }
        };
        mListView.setAdapter(adapter);
    }
}
