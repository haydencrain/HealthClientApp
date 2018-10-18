package M5.seshealthpatient.Models;

/*
*   BaseUser class, in which all Users within this application extend
*   Any fields shared between Users are added here
*/
public class BaseUser {
    private boolean IsDoctor;
    private String Name;

    public BaseUser() { }

    public void setIsDoctor(boolean isDoctor) {
        IsDoctor = isDoctor;
    }

    public boolean getIsDoctor() {
        return IsDoctor;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getName() {
        return Name;
    }
}
