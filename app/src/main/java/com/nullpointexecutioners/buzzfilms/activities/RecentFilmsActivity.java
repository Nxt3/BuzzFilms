package com.nullpointexecutioners.buzzfilms.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.nullpointexecutioners.buzzfilms.R;
import com.nullpointexecutioners.buzzfilms.helpers.SessionManager;

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
import butterknife.BindDrawable;
import butterknife.BindString;

public class RecentFilmsActivity extends Activity {

    /*I love ButterKnife <3*/
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @BindDrawable(R.drawable.rare_pepe_avatar)
    Drawable mProfileDrawerIcon;
    @BindString(R.string.title_activity_main) String dashboard;
    @BindString(R.string.profile) String profile;
    @BindString(R.string.settings) String settings;

    Drawer mNavDrawer;
    private SessionManager mSession;
    private ArrayAdapter<String> filmAdapter;

    final private int PROFILE = 1;
    final private int DASHBOARD = 2;
    final private int RECENT_RELEASES = 3;
    final private int SETTINGS = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_films);

//        ButterKnife.bind(this);
//
//        toolbar.setTitle(recentReleases);


        filmAdapter = new ArrayAdapter<>(
                this,
                R.layout.list_item_film,
                R.id.list_item_film,
                new ArrayList<String>());

        ListView listView = (ListView) findViewById(R.id.listview_film);
        listView.setAdapter(filmAdapter);

        FetchData task = new FetchData();
        task.execute();

    }

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

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Movie: " + s);
            }
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

                Log.v(LOG_TAG, "Forecast JSON String: " + FilmJsonStr);

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
                filmAdapter.clear();
                for (String movie : result) {
                    filmAdapter.add(movie);
                }
                // New data is back from the server.  Hooray!
            }
        }
    }
    /**
     * Helper method to create the nav drawer for the MainActivity
     */
    private void createNavDrawer() {
        //Create the AccountHeader for the nav drawer
        final AccountHeader drawerHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.accent)
                .addProfiles(
                        new ProfileDrawerItem()
                                .withName(mSession.getLoggedInName())
                                .withEmail(mSession.getLoggedInEmail())
                                .withIcon(mProfileDrawerIcon))
                .withSelectionListEnabledForSingleProfile(false)
                .build();
        //Create the nav drawer
        mNavDrawer = new DrawerBuilder()
                .withAccountHeader(drawerHeader)
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(profile).withIcon(GoogleMaterial.Icon.gmd_person).withIdentifier(PROFILE).withSelectable(false),
                        new PrimaryDrawerItem().withName(dashboard).withIcon(GoogleMaterial.Icon.gmd_dashboard).withIdentifier(DASHBOARD).withSetSelected(false),
                        new PrimaryDrawerItem().withName(profile).withIcon(GoogleMaterial.Icon.gmd_local_movies).withIdentifier(RECENT_RELEASES).withSetSelected(true),
                        new SecondaryDrawerItem().withName(settings).withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(SETTINGS).withSelectable(false))
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null && drawerItem instanceof Nameable) {
                            Intent intent;

                            switch(drawerItem.getIdentifier()) {
                                case PROFILE:
                                    mNavDrawer.closeDrawer();
                                    intent = new Intent(RecentFilmsActivity.this, ProfileActivity.class);
                                    startActivity(intent);
                                    return true;
                                case DASHBOARD:
                                    mNavDrawer.closeDrawer();
                                    intent = new Intent(RecentFilmsActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    return false;
                                case RECENT_RELEASES:
                                    return false;
                                case SETTINGS:
                                    //TODO, handle Settings
                                    return false;
                            }
                        }
                        return false;
                    }
                }).build();
    }
}
