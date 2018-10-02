package M5.seshealthpatient.Models;

public class PatientUser extends BaseUser {
    private String Phone;
    private String Weight;
    private String Height;
    private String DoctorID;


    public PatientUser() {
        setName("");
        Phone = "";
        Weight = "";
        Height = "";
        DoctorID = "";
        setIsDoctor(false);
    }

    public PatientUser(String name, String phone, String weight, String height, String doctor) {
        super.setName(name);
        Phone = phone;
        Weight = weight;
        Height = height;
        DoctorID = doctor;
        super.setIsDoctor(false);
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getWeight() {
        return Weight;
    }

    public void setWeight(String weight) {
        Weight = weight;
    }

    public String getHeight() {
        return Height;
    }

    public void setHeight(String height) {
        Height = height;
    }

    public String getDoctorID() {
        return DoctorID;
    }

    public void setDoctorID(String doctor) {
        DoctorID = doctor;
    }

}
