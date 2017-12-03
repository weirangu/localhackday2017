package com.example.localhackday2017;

import android.content.Intent;
import android.location.Location;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.TimeZone;

public class CreateEventActivity extends AppCompatActivity {
    private LatLng location = new LatLng(0, 0);

    private Date selectedDate;
    private Button dateButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        //Button Event Listener
        final Button button = findViewById(R.id.choose_location);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    startActivityForResult(new PlacePicker.IntentBuilder().build(CreateEventActivity.this), 2);
                }
                catch (GooglePlayServicesNotAvailableException e){}
                catch (GooglePlayServicesRepairableException e){}
            }
        });
        dateButton = findViewById(R.id.create_event_pick_date);
        selectedDate = new Date(new Date().getTime() + 1000 * 60 * 60 /* one hour */);
        updateSelectedDate();
    }

    private void updateSelectedDate() {
        dateButton.setText("Date: " + DateFormat.getInstance().format(selectedDate));
    }

    public void selectDate(View v) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this);
        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, final int year, final int month, final int day) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedDate.setYear(year - 1900);
                        selectedDate.setMonth(month);
                        selectedDate.setDate(day);
                        selectedDate.setHours(hourOfDay);
                        selectedDate.setMinutes(minute);
                        selectedDate.setSeconds(0);
                        updateSelectedDate();
                    }
                }, selectedDate.getHours(), selectedDate.getMinutes(), false);
                timePickerDialog.show();
            }
        });
        datePickerDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_create_event_save:
                saveTheEvent();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != 2 || resultCode != RESULT_OK)
            return;
        Place place = PlacePicker.getPlace(data, this);
        String placeName = String.format("Place: %s", place.getName());
        ((TextView) findViewById(R.id.location_name)).setText(placeName);
        location = place.getLatLng();
    }
    private void saveTheEvent() {
        String name = ((TextView)findViewById(R.id.create_event_name)).getText().toString();
        String description = ((TextView)findViewById(R.id.create_event_description)).getText().toString();
        String tags = join(((TagsEditText)findViewById(R.id.create_event_tags)).getObjects());

        // https://stackoverflow.com/questions/3914404/how-to-get-current-moment-in-iso-8601-format-with-date-hour-and-minute
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat tf = new SimpleDateFormat("HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        tf.setTimeZone(tz);
        String dateISO = df.format(selectedDate);
        String timeISO = tf.format(selectedDate);

        try {
            JSONObject object = new JSONObject();
            object.put("Name", name);
            object.put("Description", description);
            object.put("Latitude", location.latitude);
            object.put("Longitude", location.longitude);
            object.put("Date", dateISO);
            object.put("Time", timeISO);
            object.put("userid", 0);
            object.put("Tags", tags);

            JSONArray objectsArr = new JSONArray();
            objectsArr.put(object);

            JSONObject argsObject = new JSONObject();
            argsObject.put("table", "Event");
            argsObject.put("objects", objectsArr);

            JSONObject insertQuery = new JSONObject();
            insertQuery.put("type", "insert");
            insertQuery.put("args", argsObject);

            new CreateEventAsyncTask().execute(insertQuery.toString());
        } catch (JSONException jsonEx) {
            throw new RuntimeException(jsonEx);
        }
    }
    private class CreateEventAsyncTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... jsonStrs) {
            String postEndpoint = "https://data.chrysalis21.hasura-app.io/v1/query";
            //postEndpoint = "http://localhost:1234";
            String jsonStr = jsonStrs[0];
            String authToken = SecretToken.database;
            Request request = new Request.Builder()
                    .url(postEndpoint)
                    .post(RequestBody.create(MediaType.parse("application/json"), jsonStr))
                    .addHeader("Authorization", authToken)
                    .build();
            OkHttpClient client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                Log.d("CreateEventActivity", response.body().string());
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(CreateEventActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                CreateEventActivity.this.finish();
            } else {
                Toast.makeText(CreateEventActivity.this, "Failed to save event", Toast.LENGTH_LONG).show();
            }
        }
    }
    private static String join(List<String> inStrs) {
        StringBuilder builder = new StringBuilder();
        boolean alreadyFirst = false;
        for (String s : inStrs) {
            if (!alreadyFirst) {
                alreadyFirst = true;
            } else {
                builder.append(',');
            }
            builder.append(s);
        }
        return builder.toString();
    }
}
