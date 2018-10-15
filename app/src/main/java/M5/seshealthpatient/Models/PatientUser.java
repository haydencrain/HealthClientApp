package M5.seshealthpatient.Models;

import java.io.Serializable;

public class PatientUser extends BaseUser implements Serializable {
    private String Sex;
    private String Age;
    private String Phone;
    private String Weight;
    private String Height;
    private String MedicalCondition;
    private String DoctorID;


    public PatientUser() {
        setName("");
        Sex = "";
        Age = "";
        Phone = "";
        Weight = "";
        Height = "";
        DoctorID = "";
        MedicalCondition = "";
        setIsDoctor(false);
    }

    public PatientUser(String name, String sex, String age, String phone, String weight, String height, String medicalCondition, String doctor) {
        super.setName(name);
        Sex = sex;
        Age = age;
        Phone = phone;
        Weight = weight;
        Height = height;
        DoctorID = doctor;
        MedicalCondition = medicalCondition;
        super.setIsDoctor(false);
    }

    public String getSex() {
        return Sex;
    }

    public void setSex(String sex) {
        Sex = sex;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
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

    public String getMedicalCondition() {
        return MedicalCondition;
    }

    public void setMedicalCondition(String medicalCondition) {
        MedicalCondition = medicalCondition;
    }
}
