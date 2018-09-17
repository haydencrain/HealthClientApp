package M5.seshealthpatient.Models;

import android.location.Location;
import android.net.Uri;

import java.util.Date;
import java.io.Serializable;
import java.util.LinkedList;

public class DataPacket implements Serializable {
    private String query;
    private String heartRate;
    private Location location;
    private LinkedList<String> files;
    private Date sentDate;
    private String note;
    private Uri photoFilePath;

    public DataPacket(String query, String heartRate, Location location, LinkedList<String> files, String note, Uri photoFilePath) {
        this.heartRate = heartRate;
        this.query = query;
        this.location = location;
        this.files = files;
        this.note = note;
        this.photoFilePath = photoFilePath;
    }

    public DataPacket() {
        this.location = null;
        this.query = null;
        this.heartRate = null;
        this.files = new LinkedList<>();
        this.note = null;
        this.photoFilePath = null;
    }

    public void addFile(String file) {
        files.add(file);
    }

    public String getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }



    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public LinkedList<String> getFiles() {
        return files;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public String getNote() { return note; }

    public void setNote(String note) { this.note = note; }

    public Uri getPhotoFilePath() { return photoFilePath; }

    public void setPhotoFilePath(Uri photoFilePath) { this.photoFilePath = photoFilePath; }
}
