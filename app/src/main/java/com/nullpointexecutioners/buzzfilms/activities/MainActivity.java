package com.nullpointexecutioners.buzzfilms.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
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
import com.nullpointexecutioners.buzzfilms.TomatoVolley;
import com.nullpointexecutioners.buzzfilms.helpers.SessionManager;
import com.nullpointexecutioners.buzzfilms.helpers.StringHelper;
import com.quinny898.library.persistentsearch.SearchBox;
import com.quinny898.library.persistentsearch.SearchResult;

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
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.ButterKnife;

/**
 * Dashboard of the app--Main view
 */
public class MainActivity extends AppCompatActivity {

    @Bind(R.id.searchbox) SearchBox mSearchBox;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @BindDrawable(R.drawable.rare_pepe_avatar) Drawable mProfileDrawerIcon;
    @BindString(R.string.dashboard) String dashboard;
    @BindString(R.string.profile) String profile;
    @BindString(R.string.recent_releases) String recentReleases;
    @BindString(R.string.settings) String settings;

    Drawer mNavDrawer;
    private SessionManager mSession;
    private TomatoVolley tomato;

    final private int PROFILE = 1;
    final private int DASHBOARD = 2;
    final private int RECENT_RELEASES = 3;
    final private int SETTINGS = 4;

    private ArrayAdapter<String> mFilmAdapter;
    private String search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        this.mSession = SessionManager.getInstance(getApplicationContext());

        initToolbar();
        createNavDrawer();

//        // Testing Volley
//        this.tomato = TomatoVolley.getInstance(this);
//        RequestQueue queue = this.tomato.getRequestQueue();
//
//        List<String> search = Arrays.asList("lists", "movies", "in_theaters.json");
//        String url = StringHelper.tomatoURI(search);
//        // Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        // Display the first 500 characters of the response string.
//                        Log.v("Response", response);
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("Volley Error", error.toString());
//            }
//        });
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest);

        mSearchBox.enableVoiceRecognition(this);
        this.setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()) {
                    case (R.id.action_search):
                        //TODO, add checks for icon set to perform two different actions
                        openSearch();
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        createNavDrawer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    /**
     * Helper method to create the nav drawer for the MainActivity
     */
    private void createNavDrawer() {
        //Create the AccountHeader for the nav drawer
        final AccountHeader accountHeader = new AccountHeaderBuilder()
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
                .withAccountHeader(accountHeader)
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(profile).withIcon(GoogleMaterial.Icon.gmd_person).withIdentifier(PROFILE).withSelectable(false),
                        new PrimaryDrawerItem().withName(dashboard).withIcon(GoogleMaterial.Icon.gmd_dashboard).withIdentifier(DASHBOARD),
                        new PrimaryDrawerItem().withName(recentReleases).withIcon(GoogleMaterial.Icon.gmd_local_movies).withIdentifier(RECENT_RELEASES).withSelectable(false),
                        new SecondaryDrawerItem().withName(settings).withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(SETTINGS).withSelectable(false))
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null && drawerItem instanceof Nameable) {
                            Intent intent;

                            switch(drawerItem.getIdentifier()) {
                                case PROFILE:
                                    mNavDrawer.closeDrawer();
                                    intent = new Intent(MainActivity.this, ProfileActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    return true;
                                case DASHBOARD:
                                    return false;
                                case RECENT_RELEASES:
                                    mNavDrawer.closeDrawer();
                                    intent = new Intent(MainActivity.this, RecentReleasesActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    return true;
                                case SETTINGS:
                                    //TODO, handle Settings
                                    return false;
                            }
                        }
                        return false;
                    }
                }).build();
        mNavDrawer.setSelection(DASHBOARD);
    }

    /**
     * Helper method that inits all of the Toolbar stuff.
     * Specifically:
     * sets Toolbar title, enables the visibility of the overflow menu, shows a back arrow for navigation, and handles what to do if a user presses the back button in the Toolbar.
     */
    private void initToolbar() {
        toolbar.setTitle(dashboard);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_overflow, menu);
        menu.findItem(R.id.action_search).setIcon(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_search)
                .color(Color.WHITE)
                .sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP)
                .paddingDp(4));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch(item.getItemId()) {
            case (R.id.logout):
                onLogoutClick();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Opens and shows the searchbar view
     */
    public void openSearch() {
        toolbar.setTitle("");
        mSearchBox.revealFromMenuItem(R.id.action_search, this);
        for (int x = 0; x < 10; x++) {
            SearchResult option = new SearchResult("Result "
                    + Integer.toString(x),
                    new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_history)
                            .sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP)
                            .paddingDp(4));
            mSearchBox.addSearchable(option);
        }

        mSearchBox.setSearchListener(new SearchBox.SearchListener() {

            @Override
            public void onSearchOpened() {
                // Use this to tint the screen
            }

            @Override
            public void onSearchClosed() {
                // Use this to un-tint the screen
                closeSearch();
            }

            @Override
            public void onSearchTermChanged(String term) {
                // React to the search term changing
                // Called after it has updated results
            }

            @Override
            public void onSearch(String searchTerm) {
                Toast.makeText(MainActivity.this, searchTerm + " Searched",
                        Toast.LENGTH_LONG).show();
                toolbar.setTitle(searchTerm);
                toolbar.getMenu().findItem(R.id.action_search).setIcon(new IconicsDrawable(MainActivity.this)
                        .icon(GoogleMaterial.Icon.gmd_cancel)
                        .color(Color.WHITE)
                        .sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP)
                        .paddingDp(4));

                searchTerm = formatSearchString(searchTerm);
                search = searchTerm;

                // TODO: Search and populate the search recommendations in the search box in real time

                // TODO: Show a list of all matching movies
                FetchSearch taskMovie = new FetchSearch();
                taskMovie.execute();
                //searchMovieList();
            }

            @Override
            public void onResultClick(SearchResult result) {
                //React to result being clicked
                toolbar.setTitle(result.toString());
            }

            @Override
            public void onSearchCleared() {
                /**
                 * TODO figure out how to set the Search menu item to clear the toolbar title (like exiting search)
                 * Need to first set the icon, to a "cancel" icon
                 * If the icon is a "cancel" icon--then we need to clear the Toolbar title and reset the menu item's icon
                 * back to the "search" icon
                 */
//                toolbar.getMenu().findItem(R.id.action_search).setIcon(new IconicsDrawable(MainActivity.this)
//                        .icon(GoogleMaterial.Icon.gmd_search)
//                        .color(Color.WHITE)
//                        .sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP)
//                        .paddingDp(4));
            }
        });
    }

    /**
     * Helper method to format the search term
     */
    private String formatSearchString(String search){
        // Replace all spaces with plus signs
        return search.replaceAll(" ", "+");
    }
    /**
     * Helper method to close the searchbar view
     */
    private void closeSearch() {
        mSearchBox.hideCircularly(this);
        if (mSearchBox.getSearchText().isEmpty()) {
            toolbar.setTitle(dashboard);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1234 && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            mSearchBox.populateEditText(matches.get(0));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * When the user clicks on the logout button, they will be deauthorize from the application.
     * In addition to kicking them back to the login screen, the activity stack is also cleared as to prevent a user from being able to get back into the app with a "Back" button press.
     */
    public void onLogoutClick() {
        //Removes user from Session and unAuth via Firebase
        mSession.logoutUser();

        //After logout redirect user to WelcomeActivity to login or register
        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);

        //Closing all the Activities
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (mSearchBox.getSearchOpen()) {
            closeSearch();
        } else if (mNavDrawer != null && mNavDrawer.isDrawerOpen()) {
            mNavDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Class for Fetching data (JSON) using RottenTomatoes API asynchronously
     */
    public class FetchSearch extends AsyncTask<String, Void, String[]>  {

        private final String LOG_TAG = FetchSearch.class.getSimpleName();

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
                URL url = StringHelper.searchURL(search);

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

    public void searchMovieList(View v) {
        ListView mSearchList = (ListView) v.findViewById(R.id.listview_movie_search);
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this,
                        R.layout.list_item_film,
                        R.id.list_item_film,
                        new ArrayList<String>());
        mSearchList.setAdapter(mFilmAdapter);
    }

}
