package com.example.localhackday2017;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.ArrayList;

/**
 * Created by Weiran on 2017-12-02.
 */

public class EventData {
    public int id;
    public String name;
    public String description;
    public LatLng location;
    public Date time;
    public String userEmail;
    public ArrayList<String> eventTags;

    public EventData(int eventId,
                     String eventName,
                     String eventDesc,
                     LatLng eventLocation,
                     Date eventTime,
                     String email,
                     ArrayList<String> tags){
        id = eventId;
        name = eventName;
        description = eventDesc;
        location = eventLocation;
        time = eventTime;
        userEmail = email;
        eventTags = tags;
    }

    public static EventData getData(int id){
        //TEMP
        if (id == 1) return new EventData(1, "Name",
                "description",
                new LatLng(45, 45),
                new Date(2, 3, 4, 1, 2, 5),
                "email@example.com",
                new ArrayList<String>());
        else return new EventData(2, "Some other event!",
                "description",
                new LatLng(45, 45),
                new Date(2, 3, 4, 1, 2, 5),
                "email@example.com",
                new ArrayList<String>());
    }
}
