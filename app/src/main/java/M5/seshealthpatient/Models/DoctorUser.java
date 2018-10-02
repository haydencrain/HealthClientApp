package M5.seshealthpatient.Models;

public class DoctorUser extends BaseUser {
    private String Occupation;


    public DoctorUser() {
        setName("");
        Occupation = "";
        setIsDoctor(true);
    }

    public DoctorUser(String name, String occupation) {
        setName(name);
        Occupation = occupation;
        setIsDoctor(true);
    }

    public void setOccupation(String occupation) {
        Occupation = occupation;
    }

    public String getOccupation() {
        return Occupation;
    }
}
