package com.nadeem.fadesalon.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
// class manages the business objects .
public class Business {
    private String Mail;
    private String Name;
    private String Uid;

    private List<Map<String,Object>>[] Services;
    private List<Map<String,Object>>[] sundayRoutine;
    private List<Map<String,Object>>[] mondayRoutine;
    private List<Map<String,Object>>[] tuesdayRoutine;
    private List<Map<String,Object>>[] wednesdayRoutine;
    private List<Map<String,Object>>[] thursdayRoutine;
    private List<Map<String,Object>>[] saturdayRoutine;

    public Business(String mail, String name, String uid, List<Map<String, Object>>[] services,
                    List<Map<String, Object>>[] sundayRoutine, List<Map<String, Object>>[] mondayRoutine,
                    List<Map<String, Object>>[] tuesdayRoutine, List<Map<String, Object>>[] wednesdayRoutine,
                    List<Map<String, Object>>[] thursdayRoutine, List<Map<String, Object>>[] saturdayRoutine)
    {
        Mail = mail;
        Name = name;
        Uid = uid;
        Services = services;
        this.sundayRoutine = sundayRoutine;
        this.mondayRoutine = mondayRoutine;
        this.tuesdayRoutine = tuesdayRoutine;
        this.wednesdayRoutine = wednesdayRoutine;
        this.thursdayRoutine = thursdayRoutine;
        this.saturdayRoutine = saturdayRoutine;
    }

    public Business() {
    }

    public String getMail() {
        return Mail;
    }

    public void setMail(String mail) {
        Mail = mail;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public List<Map<String, Object>>[] getServices() {
        return Services;
    }

    public void setServices(ArrayList<Map<String, Object>>[] services) {
        Services = services;
    }

    public List<Map<String, Object>>[] getSundayRoutine() {
        return sundayRoutine;
    }

    public void setSundayRoutine(List<Map<String, Object>>[] sundayRoutine) {
        this.sundayRoutine = sundayRoutine;
    }

    public List<Map<String, Object>>[] getMondayRoutine() {
        return mondayRoutine;
    }

    public void setMondayRoutine(List<Map<String, Object>>[] mondayRoutine) {
        this.mondayRoutine = mondayRoutine;
    }

    public List<Map<String, Object>>[] getTuesdayRoutine() {
        return tuesdayRoutine;
    }

    public void setTuesdayRoutine(List<Map<String, Object>>[] tuesdayRoutine) {
        this.tuesdayRoutine = tuesdayRoutine;
    }

    public List<Map<String, Object>>[] getWednesdayRoutine() {
        return wednesdayRoutine;
    }

    public void setWednesdayRoutine(List<Map<String, Object>>[] wednesdayRoutine) {
        this.wednesdayRoutine = wednesdayRoutine;
    }

    public List<Map<String, Object>>[] getThursdayRoutine() {
        return thursdayRoutine;
    }

    public void setThursdayRoutine(List<Map<String, Object>>[] thursdayRoutine) {
        this.thursdayRoutine = thursdayRoutine;
    }

    public List<Map<String, Object>>[] getSaturdayRoutine() {
        return saturdayRoutine;
    }

    public void setSaturdayRoutine(List<Map<String, Object>>[] saturdayRoutine) {
        this.saturdayRoutine = saturdayRoutine;
    }

}
