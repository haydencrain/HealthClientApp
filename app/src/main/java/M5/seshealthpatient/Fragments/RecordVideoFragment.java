package M5.seshealthpatient.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import M5.seshealthpatient.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordVideoFragment extends Fragment {
    private Button mRecordViedo;
    private int VIDEO_REQUEST_CODE = 1001;
    public RecordVideoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_record_video, container, false);
        mRecordViedo = (Button) v.findViewById( R.id.videoBtn );
        mRecordViedo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordVideo(v);
            }
        } );
        return v;
    }

    public void recordVideo(View view) {
        Intent camera_intent = new Intent( MediaStore.ACTION_VIDEO_CAPTURE);
        File video_file = getFilepath();
        //Uri video_uri = Uri.fromFile(video_file);
        Uri video_uri = FileProvider.getUriForFile(getActivity(), "yourteamnumber.seshealthpatient.provider", getFilepath());
        camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, video_uri);
        camera_intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(camera_intent, VIDEO_REQUEST_CODE);
    }

    public File getFilepath() {
        //create a folder to store the video
        File folder = new File("sdcard/video_app");
        //check fold if exists
        if (folder.exists()) {
            folder.mkdir();
        }
        File video_file = new File(folder, "sample_video.mp4");
        return video_file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VIDEO_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                Toast.makeText(getActivity(),
                        "Video Successfully Recorded",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(),
                        "Video recorded failed",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
