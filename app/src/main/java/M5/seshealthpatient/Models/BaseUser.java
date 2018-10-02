package M5.seshealthpatient.Models;

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
