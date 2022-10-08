package hu.pannon.workerdashboard;

public class User {

    private static User instance = null;
    private String username;
    private String password;

    private User()
    {
        this.username = "";
        this.password = "";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static User getInstance()
    {
        if(instance == null)
            instance = new User();
        return instance;
    }

}
