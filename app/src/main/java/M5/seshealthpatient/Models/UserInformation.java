package M5.seshealthpatient.Models;

public class UserInformation {
    private String Name;
    private String Phone;
    private String Weight;
    private String Height;
    private String DoctorID;


    public UserInformation(){
    }

    public void setName(String name) {
        Name = name;
    }

    public String getName() {
        return Name;
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

    public UserInformation(String name, String phone, String weight, String height, String doctor) {
        Name = name;
        Phone = phone;
        Weight = weight;
        Height = height;
        DoctorID = doctor;

    }

}
