package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListViewActivity extends Activity {

    private Button searchButton;
    private Button prevButton;
    private Button nextButton;
    private String url;
    private Integer offset;
    private String title;
    private String leda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview2);
        searchButton = findViewById(R.id.search_button);
        prevButton = findViewById(R.id.prev_button);
        nextButton = findViewById(R.id.next_button);
        url = "https://ec2-54-204-78-21.compute-1.amazonaws.com:8443/cs122b-spring20-project1/api/";
//        url = "https://10.0.2.2:8443/cs122b-spring20-project1/api/";

        Log.d("LVA.ON_CREATE", "getting results");
        //this should be retrieved from the database and the backend server
        //final ArrayList<Movie> movies = new ArrayList<>();
        final ArrayList<Movie> movies = (ArrayList<Movie>)getIntent().getSerializableExtra("results");
        offset = (Integer)getIntent().getSerializableExtra("offset");
        title = (String)getIntent().getSerializableExtra("title");
        leda = (String)getIntent().getSerializableExtra("leda");
        Log.d("LVA.ON_CREATE", "retrieved results");
        MovieListViewAdapter adapter = new MovieListViewAdapter(movies, this);
        Log.d("LVA.ON_CREATE", "created MovieListViewAdapter");
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movies.get(position);
//                String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getTitle(), movie.getYear());
//                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                goToMoviePage(movie);
            }
        });

        if (offset == 0) {
            prevButton.setVisibility(View.GONE);
        }
        else {
            prevButton.setVisibility(View.VISIBLE);
        }

        if (movies.size() < 20) {
            nextButton.setVisibility(View.GONE);
        }
        else {
            nextButton.setVisibility(View.VISIBLE);
        }

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToSearch();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { goToPrevPage(); }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToNextPage();
            }
        });
    }

    public void backToSearch() {
        Intent mainPage = new Intent(ListViewActivity.this, MainActivity.class);
        startActivity(mainPage);
    }

    public void goToPrevPage() {
        retrieveSearchResults(offset-20);
    }

    public void goToNextPage() {
        retrieveSearchResults(offset+20);
    }

    public void goToMoviePage(Movie m) {
        Log.d("LVA.goToMoviePage", "going to send request");
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final StringRequest searchRequest = new StringRequest(Request.Method.GET, url + "android-single-movie?id=" + m.getId(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //TODO should parse the json response to redirect to appropriate functions.
                Log.d("LVA.FOUND_RESPONSE", response);
                Intent moviePage = new Intent(ListViewActivity.this, SingleMovieActivity.class);
                moviePage.putExtra("movie_data", response);
                moviePage.putExtra("title", title);
                moviePage.putExtra("offset", offset);
                moviePage.putExtra("leda", leda);
                // retrieve in ListViewActivity with ArrayList<Movie> results = (ArrayList<Movie>) getIntent().getSerializableExtra("results");
                startActivity(moviePage);
            }
        }, //created response listener, now create response error listener (still inside StringRequest constructor call!)
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("LVA.error", error.toString());
                    }
                });

        // !important: queue.add is where the login request is actually sent
        queue.add(searchRequest);
    }

    public void retrieveSearchResults(Integer offset_p) {
        Log.d("LVA.page", "going to send request");
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final StringRequest searchRequest = new StringRequest(Request.Method.GET, url + "android-movie-list?title=" + title + "&offset=" + offset_p + "&leda=" + leda, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //TODO should parse the json response to redirect to appropriate functions.
                Log.d("LVA.FOUND_RESPONSE", response);
                Intent listPage = new Intent(ListViewActivity.this, ListViewActivity.class);

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
                    Log.d("LVA.JSON_ERROR", e.toString());
                }

                listPage.putExtra("results", results);
                listPage.putExtra("offset", offset_p);
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
                        Log.d("LVA.error", error.toString());
                    }
                });

        searchRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // !important: queue.add is where the login request is actually sent
        queue.add(searchRequest);
    }
}