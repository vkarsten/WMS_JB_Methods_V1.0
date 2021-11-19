package main.java.data;

import java.util.Date;

/**
 * This details of a Person
 * @author pujanov
 *
 */
public class Person {
    /**
     * Username of the Person
     */
    private String userName;
    /**
     * Password of the Person
     */
    private String password;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public String toString() {
        return "Person{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
