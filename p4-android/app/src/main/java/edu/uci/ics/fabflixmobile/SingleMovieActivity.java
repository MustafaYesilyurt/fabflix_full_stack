package edu.uci.ics.fabflixmobile;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SingleMovieActivity extends ActionBarActivity {

    private TextView textView_title;
    private TextView textView_director;
    private TextView textView_year;
    private TextView textView_rating;
    private Button backButton;
    private String url;
    private Integer offset;
    private String query;
    private String leda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_movie);

        textView_title = findViewById(R.id.textView_title);
        textView_director = findViewById(R.id.textView_director);
        textView_year = findViewById(R.id.textView_year);
        textView_rating = findViewById(R.id.textView_rating);
        backButton = findViewById(R.id.back_button);

        url = "https://ec2-54-204-78-21.compute-1.amazonaws.com:8443/cs122b-spring20-project1/api/";
//        url = "https://10.0.2.2:8443/cs122b-spring20-project1/api/";

        JSONObject movie_data;

        ArrayList<String> genres = new ArrayList<String>();
        ArrayList<String> stars = new ArrayList<String>();

        try {
            movie_data = new JSONObject((String) getIntent().getSerializableExtra("movie_data"));
            query = (String)getIntent().getSerializableExtra("title");
            offset = (Integer)getIntent().getSerializableExtra("offset");
            leda = (String)getIntent().getSerializableExtra("leda");

            String title = movie_data.getString("title");
            String director = "Director: " + movie_data.getString("director");
            String year = "Release Year: " + movie_data.getString("year");
            String rating = "Rating: " + movie_data.getString("rating");

            textView_title.setText(title);
            textView_director.setText(director);
            textView_year.setText(year);
            textView_rating.setText(rating);

            JSONArray jgenres = movie_data.getJSONObject("genres").names();
            JSONArray jstars = movie_data.getJSONObject("stars").names();
            String key, val;
            for (int i = 0; i < jgenres.length(); i++) {
                key = jgenres.getString(i);
                val = movie_data.getJSONObject("genres").getString(key);
                genres.add(val);
            }
            for (int i = 0; i < jstars.length(); i++) {
                key = jstars.getString(i);
                val = movie_data.getJSONObject("stars").getString(key);
                stars.add(val);
            }
        } catch(Exception e) {
            e.printStackTrace();
            Log.d("SMA.JSON_ERROR", e.toString());
        }

        GenreListViewAdapter genre_adapter = new GenreListViewAdapter(genres, this);
        Log.d("LVA.ON_CREATE", "created GenreListViewAdapter");
        StarListViewAdapter star_adapter = new StarListViewAdapter(stars, this);
        Log.d("LVA.ON_CREATE", "created StarListViewAdapter");

        ListView genreListView = findViewById(R.id.genres);
        genreListView.setAdapter(genre_adapter);
        ListView starListView = findViewById(R.id.stars);
        starListView.setAdapter(star_adapter);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnToSearchResults();
            }
        });
    }

    public void returnToSearchResults() {
        String query_title = query.trim().replace(" ", "+").replace("&", "_");
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final StringRequest searchRequest = new StringRequest(Request.Method.GET, url + "android-movie-list?title=" + query_title + "&offset=" + offset + "&leda=" + leda, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //TODO should parse the json response to redirect to appropriate functions.
                Log.d("SMA.FOUND_RESPONSE", response);
                Intent listPage = new Intent(SingleMovieActivity.this, ListViewActivity.class);

                ArrayList<Movie> results = new ArrayList<Movie>();
                String key;
                JSONObject val;
                try {
                    JSONObject jresponse = new JSONObject(response);
                    JSONArray keys = jresponse.names();
                    for (int i = 0; i < keys.length(); ++i) {
                        key = keys.getString(i);
                        val = jresponse.getJSONObject(key);
                        Movie m = new Movie(val.getString("id"), val.getString("title"), Integer.parseInt(val.getString("year")), val.getString("director"), Float.parseFloat(val.getString("rating")), val.getString("genres"), val.getString("stars"));
                        results.add(m);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    Log.d("SMA.JSON_ERROR", e.toString());
                }

                listPage.putExtra("results", results);
                listPage.putExtra("offset", offset);
                listPage.putExtra("title", query);
                listPage.putExtra("leda", leda);
                // retrieve in ListViewActivity with ArrayList<Movie> results = (ArrayList<Movie>) getIntent().getSerializableExtra("results");
                startActivity(listPage);
            }
        }, //created response listener, now create response error listener (still inside StringRequest constructor call!)
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("SMA.error", error.toString());
                    }
                });

        // !important: queue.add is where the login request is actually sent

        searchRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(searchRequest);
    }
}
