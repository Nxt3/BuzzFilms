package com.nullpointexecutioners.buzzfilms.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.nullpointexecutioners.buzzfilms.Movie;
import com.nullpointexecutioners.buzzfilms.R;
import com.nullpointexecutioners.buzzfilms.helpers.StringHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchMovieResultsActivity extends AppCompatActivity {

    @Bind(R.id.listview_movie_search) ListView mSearchList;
    @Bind(R.id.search_toolbar) Toolbar toolbar;

    private ArrayAdapter<String> mSearchAdapter;
    private ArrayList<Movie> searchResults;
    private SearchView mSearchView;
    private String mSearchTerm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        ButterKnife.bind(this);

        initToolbar();

        mSearchAdapter = new ArrayAdapter<>(this,
                R.layout.list_item_film,
                R.id.list_item_film,
                new ArrayList<String>());
        mSearchList.setAdapter(mSearchAdapter);

        //Handle passing the details of the selected movie over to the Movie Details Activity
        mSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
                Bundle movieDetailBundle = new Bundle();
                String value = (String) adapter.getItemAtPosition(position);
                String movieId = searchResults.get(position).getId();
                String posterPath = searchResults.get(position).getPosterUrl();
                String synopsis = searchResults.get(position).getSynopsis();
                String releaseDate = searchResults.get(position).getReleaseDate();
                Double criticsScore = searchResults.get(position).getCriticsScore();
                movieDetailBundle.putString("id", movieId);
                movieDetailBundle.putString("title", value);
                movieDetailBundle.putString("poster_path", posterPath);
                movieDetailBundle.putString("synopsis", synopsis);
                movieDetailBundle.putString("release_date", releaseDate);
                movieDetailBundle.putDouble("critics_score", criticsScore);
                startActivity(new Intent(SearchMovieResultsActivity.this, MovieDetailActivity.class).putExtras(movieDetailBundle));
            }
        });

        handleIntent(getIntent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        toolbar.getMenu().findItem(R.id.action_search).collapseActionView();
        setIntent(intent);
        handleIntent(intent);
    }

    /**
     * Handles search from the MainActivity
     * Gets query from the intent and passes it in to search
     * @param intent to handle search from
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mSearchTerm = query;
            assert getSupportActionBar() != null;
            getSupportActionBar().setTitle(query);

            FetchSearch search = new FetchSearch();
            search.execute();
        }
    }

    /**
     * Helper method that inits all of the Toolbar stuff
     */
    private void initToolbar() {
        assert getSupportActionBar() != null;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); //Simulate a system's "Back" button functionality.
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_overflow, menu);

        //Set Search icon
        menu.findItem(R.id.action_search).setIcon(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_search)
                .color(Color.WHITE)
                .sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP)
                .paddingDp(4));

        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        //Removes the line under the search text
        View searchPlate = mSearchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        searchPlate.setBackgroundColor(getColor(R.color.primary));

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    /**
     * Class for Fetching data (JSON) using RottenTomatoes API asynchronously
     */
    public class FetchSearch extends AsyncTask<String, Void, ArrayList<Movie>> {

        private final String LOG_TAG = FetchSearch.class.getSimpleName();

        private ArrayList<Movie> getDataFromJson(String FilmJsonStr, int num)
                throws JSONException {

            JSONObject filmJson = new JSONObject(FilmJsonStr);
            JSONArray FilmArray = filmJson.getJSONArray("results");

            ArrayList<Movie> movies = new ArrayList<>();
            for (int i = 0; i < FilmArray.length(); i++) {
                // Get the JSON object representing the movie
                JSONObject movieObject = FilmArray.getJSONObject(i);

                Movie movie = new Movie(
                        movieObject.getString("id"),
                        movieObject.getString("title"),
                        movieObject.getString("release_date"),
                        movieObject.getString("overview"),
                        movieObject.getString("poster_path"),
                        movieObject.getDouble("vote_average"));
                movies.add(movie);
            }

//            for (Movie x : movies) {
//                Log.v("Movies ", x.toString());
//            }
            return movies;
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String filmJsonStr = null;

            try {
                URL url = new URL(StringHelper.searchURL(mSearchTerm));

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                filmJsonStr = buffer.toString();


            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            try {
                return getDataFromJson(filmJsonStr, 10);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> result) {
            if (result != null) {
                mSearchAdapter.clear();
                // Access the results outside this inner class
                searchResults = result;
                for (Movie movie : result) {
                    mSearchAdapter.add(movie.getTitle());
                }
            }
        }
    }
}
