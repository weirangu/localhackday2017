package com.example.localhackday2017;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item";

    /**
     * The name of this fragment.
     */
    private int mID;
    private EventData data;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mID = getArguments().getInt(ARG_ITEM_ID);

            Activity activity = this.getActivity();
        }
        new GrabOneEventTask().execute(mID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);
        repopulate(rootView);
        return rootView;
    }

    private void repopulate(View rootView) {
        if (data == null)
            return;
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        String closest = null;
        try {
            Address add = geocoder.getFromLocation(data.location.latitude, data.location.longitude, 1).get(0);
            closest = add.getAddressLine(0);

        }
        catch (IOException e)
        {
            closest = data.location.toString();
        }
        String text = data.description + "\n\n" + data.userEmail + "\n\n" + closest + "\n\n" + data.time.toLocaleString() + "\n\n";
        for (int i = 0; i < data.eventTags.size(); i++){
            text += data.eventTags.get(i) + ", ";
        }
        ((TextView) rootView.findViewById(R.id.item_detail)).setText(text);
    }
    private void refreshData() {
        repopulate(getView());
        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null){
            appBarLayout.setTitle(data.name);
        }

    }
    private class GrabOneEventTask extends AsyncTask<Integer, Void, List<EventData>> {
        @Override
        protected List<EventData> doInBackground(Integer... myInt) {
            String postEndpoint = "https://data.chrysalis21.hasura-app.io/v1/query";
            //postEndpoint = "http://localhost:1234";
            String jsonStr = null;
            try {
                JSONObject whereClause = new JSONObject("{'eventid':{'$eq': " + myInt[0] + "}}");
                JSONObject queryObject = new JSONObject(EventData.queryTemplate);
                queryObject.getJSONObject("args").put("where", whereClause);
                jsonStr = queryObject.toString();
            } catch (JSONException jsonE) {
                throw new RuntimeException(jsonE);
            }
            String authToken = SecretToken.database;
            Request request = new Request.Builder()
                    .url(postEndpoint)
                    .post(RequestBody.create(MediaType.parse("application/json"), jsonStr))
                    .addHeader("Authorization", authToken)
                    .build();
            OkHttpClient client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                String body = response.body().string();
                Log.d("ItemListActivity", body);
                return EventData.fromJSONString(body);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(List<EventData> result) {
            if (result != null && result.size() == 1) {
                data = result.get(0);
                refreshData();
            }
        }
    }
}
