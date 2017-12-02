package com.example.localhackday2017;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.ArrayList;

/**
 * Created by Weiran on 2017-12-02.
 */

public class EventData {
    public String name;
    public String description;
    public LatLng location;
    public Date time;
    public String userEmail;
    public ArrayList<String> tags;

    public EventData(String eventName,
                     String eventDesc,
                     LatLng eventLocation,
                     Date eventTime,
                     String email,
                     ArrayList<String> tags){
        name = eventName;
        description = eventDesc;
        location = eventLocation;
        time = eventTime;
        userEmail = email;
    }
}
