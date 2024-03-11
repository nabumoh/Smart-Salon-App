package com.nadeem.fadesalon.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;

/* class manages the objects of appointments from DB  */
@IgnoreExtraProperties
public class Appointments implements Serializable {

    private String clientId;  // id of the client. helps for any func as block ...
    private String clientName;
    private String clientMail;

    private String docId;
    private String barberName; // name of the barber
    private String serviceName;
    private Timestamp startTime;
    private Timestamp endTime;
    private Number serviceLength;
    private String type;

    public Appointments(String clientId, String clientName, String clientMail, String docId,
                        String barberName, String serviceName, Timestamp startTime,
                        Timestamp endTime, Number serviceLength, String type) {

        this.clientMail = clientMail;
        this.docId = docId;
        this.clientName = clientName;
        this.clientId = clientId;
        this.barberName = barberName;
        this.serviceName = serviceName;
        this.endTime = endTime;
        this.startTime = startTime;
        this.serviceLength = serviceLength;
        this.type = type;
    }

    public Appointments() {
    }



    public String getClientMail() {
        return clientMail;
    }

    public void setClientMail(String clientMail) {
        this.clientMail = clientMail;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getBarberName() {
        return barberName;
    }

    public void setBarberName(String barberName) {
        this.barberName = barberName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Number getServiceLength() {
        return serviceLength;
    }

    public void setServiceLength(Number serviceLength) {
        this.serviceLength = serviceLength;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
