package model;

/**
 * Example of a second subclass in the Person hierarchy.
 */
public class Admin extends Person {
    private String email;

    public Admin(String name, String email) {
        super(name);
        this.email = email;
    }

    @Override
    public String getRole() {
        return "Admin";
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Admin: " + name + " <" + email + ">";
    }
}
