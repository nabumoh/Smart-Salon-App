package com.nadeem.fadesalon.models;
/* Class manages the services that Barber offers ,helps to get service Data from DB*/

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class CalendarServices {
    private String serviceName;
    private Number serviceLength;

    public CalendarServices() {
    }

    public CalendarServices(String serviceName, Number serviceLength) {
        this.serviceName = serviceName;
        this.serviceLength = serviceLength;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Number getServiceLength() {
        return serviceLength;
    }

    public void setServiceLength(Number serviceLength) {
        this.serviceLength = serviceLength;
    }
}
