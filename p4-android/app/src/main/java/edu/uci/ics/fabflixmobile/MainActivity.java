package edu.uci.ics.fabflixmobile;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {

    private EditText editText_title;
    private TextView message;
    private Button searchButton;
    private Button fuzzyButton;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText_title = findViewById(R.id.editTextTitle);
        message = findViewById(R.id.message);
        searchButton = findViewById(R.id.search_button);
        fuzzyButton = findViewById(R.id.fuzzy_button);


        url = "https://ec2-54-204-78-21.compute-1.amazonaws.com:8443/cs122b-spring20-project1/api/";
//        url = "https://10.0.2.2:8443/cs122b-spring20-project1/api/";

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveSearchResults("0");
            }
        });

        fuzzyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveSearchResults("1");
            }
        });
    }

    public void retrieveSearchResults(String leda) {
        message.setText("Retrieving results...");

        String title = editText_title.getText().toString().trim().replace(" ", "+").replace("&", "_");
        Integer offset = 0;

        if (title.length() == 0) {
            message.setText("Please enter a valid title.");
            return;
        }

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final StringRequest searchRequest = new StringRequest(Request.Method.GET, url + "android-movie-list?title=" + title + "&offset=" + offset + "&leda=" + leda, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //TODO should parse the json response to redirect to appropriate functions.
                Log.d("main.FOUND_RESPONSE", response);
                Intent listPage = new Intent(MainActivity.this, ListViewActivity.class);

                ArrayList<Movie> results = new ArrayList<Movie>();
                String key;
                JSONObject val;
                try {
                    JSONObject jresponse = new JSONObject(response);
                    JSONArray keys = jresponse.names();
                    for (int i = 0; i < keys.length(); i++) {
                        key = keys.getString(i);
                        val = jresponse.getJSONObject(key);
                        Movie m = new Movie(val.getString("id"), val.getString("title"), Integer.parseInt(val.getString("year")), val.getString("director"), Float.parseFloat(val.getString("rating")), val.getString("genres"), val.getString("stars"));
                        results.add(m);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    Log.d("main.JSON_ERROR", e.toString());
                }

                listPage.putExtra("results", results);
                listPage.putExtra("offset", offset);
                listPage.putExtra("title", title);
                listPage.putExtra("leda", leda);
                // retrieve in ListViewActivity with ArrayList<Movie> results = (ArrayList<Movie>) getIntent().getSerializableExtra("results");
                startActivity(listPage);
            }
        }, //created response listener, now create response error listener (still inside StringRequest constructor call!)
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("main.error", error.toString());
                message.setText("Retrieval failed.");
            }
        });

        searchRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // !important: queue.add is where the login request is actually sent
        queue.add(searchRequest);
    }
}
