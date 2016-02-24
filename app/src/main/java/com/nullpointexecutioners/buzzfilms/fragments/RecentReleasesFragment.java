package com.nullpointexecutioners.buzzfilms.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nullpointexecutioners.buzzfilms.R;

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

public class RecentReleasesFragment extends Fragment {

    private ArrayAdapter<String> mFilmAdapter;
    private int mPage;
    public static final String ARG_PAGE = "ARG_PAGE";

    public static RecentReleasesFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        RecentReleasesFragment fragment = new RecentReleasesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_releases, container, false);

        FetchData task = new FetchData();
        task.execute();

        switch(mPage) {
            case 1:
                recentMoviesList(view);
                return view;
            case 2:
                recentDVDsList(view);
                return view;
        }

        return view;
    }

    public void recentMoviesList(View v) {
        ListView mRecentsList = (ListView) v.findViewById(R.id.listview_recent_releases);
        mFilmAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.list_item_film,
                R.id.list_item_film,
                new ArrayList<String>());
        mRecentsList.setAdapter(mFilmAdapter);
    }

    public void recentDVDsList(View v) {
        ListView mRecentsList = (ListView) v.findViewById(R.id.listview_recent_releases);
        mFilmAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.list_item_film,
                R.id.list_item_film,
                new ArrayList<String>());
        mRecentsList.setAdapter(mFilmAdapter);
    }

    /**
     * Class for Fetching data (JSON) using RottenTomatoes API asynchronously
     */
    public class FetchData extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchData.class.getSimpleName();

        private String[] getDataFromJson(String FilmJsonStr, int num)
                throws JSONException {

            JSONObject forecastJson = new JSONObject(FilmJsonStr);
            JSONArray FilmArray = forecastJson.getJSONArray("movies");

            String[] resultStrs = new String[FilmArray.length()];
            for (int i = 0; i < FilmArray.length(); i++) {

                // Get the JSON object representing the title
                JSONObject titleObject = FilmArray.getJSONObject(i);
                resultStrs[i] = titleObject.getString("title");
            }

            //Disable logging for debugging TODO re-enable
//            for (String s : resultStrs) {
//                Log.v(LOG_TAG, "Movie: " + s);
//            }
            return resultStrs;

        }

        @Override
        protected String[] doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String FilmJsonStr = null;

            try {

                URL url = new URL("http://api.rottentomatoes.com/api/public/v1.0/lists/movies/in_theaters.json?apikey=vbhetn4chdpudf7mqhckacca");


                // Create the request to OpenWeatherMap, and open the connection
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
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                FilmJsonStr = buffer.toString();

                //Disable logging for debugging, TODO re-enable this
//                Log.v(LOG_TAG, "Forecast JSON String: " + FilmJsonStr);

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
                return getDataFromJson(FilmJsonStr, 10);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mFilmAdapter.clear();
                for (String movie : result) {
                    mFilmAdapter.add(movie);
                }
                // New data is back from the server.  Hooray!
            }
        }
    }
}
