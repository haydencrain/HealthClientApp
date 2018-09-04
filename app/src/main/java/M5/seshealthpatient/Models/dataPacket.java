package M5.seshealthpatient.Models;

public class dataPacket {
    String location;
    String heartRate;
    String query;

    public dataPacket(String location, String heartRate, String query) {
        this.location = location;
        this.heartRate = heartRate;
        this.query = query;
    }
}
