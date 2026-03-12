package model;

public class Lead {

    private String email;
    private String firstName;
    private String lastName;
    private String companyName;

    public Lead(String email, String firstName, String lastName, String companyName) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.companyName = companyName;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCompanyName() {
        return companyName;
    }

    @Override
    public String toString() {
        return "Lead{email='" + email + "', firstName='" + firstName
                + "', lastName='" + lastName + "', companyName='" + companyName + "'}";
    }

}
