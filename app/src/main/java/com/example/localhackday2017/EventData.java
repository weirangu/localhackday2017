package com.example.localhackday2017;

import android.util.EventLog;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

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

    public static List<EventData> fromJSONString(String inStr) throws JSONException, ParseException {
        JSONArray jsonArray = new JSONArray(inStr);
        List<EventData> outData = new ArrayList<EventData>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            int eventId = jsonObj.getInt("eventid");
            String eventName = jsonObj.getString("Name");
            String eventDesc = jsonObj.getString("Description");
            double lat = jsonObj.getDouble("Latitude");
            double lon = jsonObj.getDouble("Longitude");
            LatLng eventLocation = new LatLng(lat, lon);

            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+00'");
            df.setTimeZone(tz);
            Date eventTime = df.parse(jsonObj.getString("Date") + "T" + jsonObj.getString("Time"));
            String email = jsonObj.getJSONObject("EventUser").getString("Email");
            ArrayList<String> tags = new ArrayList<String>(Arrays.asList(jsonObj.getString("Tags").split(",")));
            outData.add(new EventData(eventId, eventName, eventDesc, eventLocation, eventTime, email, tags));
        }
        return outData;
    }

    public static final String queryTemplate = "{\"type\":\"select\",\"args\":{\"table\":\"Event\",\"columns\":[" +
            "\"Name\",\"Description\",\"Latitude\",\"Longitude\",\"Date\",\"Time\",\"userid\",\"Tags\",\"eventid\"," +
            "{\"name\":\"EventUser\",\"columns\": [\"Email\"]}]" +
            "}}";
}
