package model;


// Its a simple user class with it attributes
// with its uniquesId, userName and password
public class User extends Person {
    private String userID;
    private String password;

    public User(String userID, String name, String password) {
        super(name);
        this.userID = userID;
        this.password = password;
    }

    @Override
    public String getRole() { return "User"; }

    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return "User[" + userID + "] " + name;
    }
}