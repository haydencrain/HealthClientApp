package M5.seshealthpatient.Models;

import android.location.Location;

import java.util.Date;
import java.io.Serializable;
import java.util.LinkedList;

public class DataPacket implements Serializable {
    private String id;
    private String title;
    private String query;
    private LinkedList<Comment> queryComments;
    private String heartRate;
    private double latitude;
    private double longitude;
    private long sentDate;
    private LinkedList<String> files;

    public DataPacket(String id, String title, String query, String heartRate, double latitude, double longitude, long sentDate, LinkedList<String> files) {
        this.id = id;
        this.title = title == null || title.isEmpty() ? "Data Packet" : title;
        this.heartRate = heartRate;
        this.query = query;
        this.latitude = latitude;
        this.longitude = longitude;
        this.sentDate = sentDate;
        this.files = files;
    }

    public DataPacket() {
        this.id = "";
        this.title = "Data Packet";
        this.latitude = 0;
        this.longitude = 0;
        this.query = null;
        this.heartRate = null;
        this.sentDate = 0;
        this.files = new LinkedList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public long getSentDate() {
        return sentDate;
    }

    public void setSentDate(long sentDate) {
        this.sentDate = sentDate;
    }

    public LinkedList<String> getFiles() {
        return files;
    }

    public boolean hasLocation() {
        return latitude != 0 && longitude != 0;
    }

}
