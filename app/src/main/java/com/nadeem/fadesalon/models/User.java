package com.nadeem.fadesalon.models;


import com.google.firebase.firestore.IgnoreExtraProperties;
// class handles user object.
@IgnoreExtraProperties
public class User {
    private String User_id;
    private String fullname;
    private String email;
    private String userType;
    private String status;


    public User(String User_id, String fullname, String email,
                      String userType, String status) {

        this.User_id = User_id;
        this.fullname = fullname;
        this.email = email;
        this.userType = userType;
        this.status = status;
    }

    public User() {
    }


    public String getUser_id() {
        return User_id;
    }

    public void setUser_id(String BarberUser_id) {
        User_id = BarberUser_id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return  " Full name='" + getFullname()+"\n" +
                " Email='" + getEmail() + "\n" +
                " Status='" + getStatus() ;
    }
}
