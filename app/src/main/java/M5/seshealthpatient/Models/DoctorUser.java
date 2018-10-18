package M5.seshealthpatient.Models;

/*
*   DoctorUser class, extends BaseUser.
*   Fields specific to the Doctor user are here.
*/
public class DoctorUser extends BaseUser {
    private String Department;
    private String Occupation;
    private String Introduction;


    public DoctorUser() {
        setName("");
        Department = "";
        Occupation = "";
        Introduction ="";
        setIsDoctor(true);
    }

    public DoctorUser(String name, String department, String occupation, String introduction) {
        setName(name);
        Department = department;
        Occupation = occupation;
        Introduction = introduction;
        setIsDoctor(true);
    }

    public void setDepartment(String department){
        Department = department;
    }

    public String getDepartment(){
        return Department;
    }

    public void setOccupation(String occupation) {
        Occupation = occupation;
    }

    public String getOccupation() {
        return Occupation;
    }

    public void setIntroduction(String introduction){
        Introduction = introduction;
    }

    public String getIntroduction(){
        return Introduction;
    }

    public boolean setIsDoctor(){
        return true;
    }
}
