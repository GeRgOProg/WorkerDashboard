package hu.pannon.workerdashboard;

public class Worker {

    private String name;
    private String address;
    private String birthdate;
    private String idCardNum;
    private String gender;
    private int salary;
    private String territory;
    private String position;
    private static Worker instance = null;

    public Worker(String name, String address, String birthdate, String idCardNum, String gender, int salary, String territory, String position) {
        this.name = name;
        this.address = address;
        this.birthdate = birthdate;
        this.idCardNum = idCardNum;
        this.gender = gender;
        this.salary = salary;
        this.territory = territory;
        this.position = position;
    }

    private Worker()
    {

    }

    public static Worker getInstance()
    {
        if(instance == null)
            instance = new Worker();
        return instance;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getIdCardNum() {
        return idCardNum;
    }

    public void setIdCardNum(String idCardNum) {
        this.idCardNum = idCardNum;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public String getTerritory() {
        return territory;
    }

    public void setTerritory(String territory) {
        this.territory = territory;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
