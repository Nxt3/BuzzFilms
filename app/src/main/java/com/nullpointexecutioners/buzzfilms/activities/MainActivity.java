package com.nullpointexecutioners.buzzfilms.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
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
import com.nullpointexecutioners.buzzfilms.Movie;
import com.nullpointexecutioners.buzzfilms.R;
import com.nullpointexecutioners.buzzfilms.helpers.RecentSuggestionsProvider;
import com.nullpointexecutioners.buzzfilms.helpers.SessionManager;
import com.nullpointexecutioners.buzzfilms.helpers.StringHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;

import butterknife.Bind;
import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.ButterKnife;

/**
 * Dashboard of the app--Main view
 */
public class MainActivity extends AppCompatActivity {

    @Bind(R.id.dashboard_toolbar) Toolbar toolbar;
    @Bind(R.id.major_recommendations) LinearLayout majorRecommendations;
    @Bind(R.id.rating_recommendations) LinearLayout ratingRecommendations;
    @BindDrawable(R.drawable.rare_pepe_avatar) Drawable mProfileDrawerIcon;
    @BindString(R.string.dashboard) String dashboard;
    @BindString(R.string.profile) String profile;
    @BindString(R.string.recent_releases) String recentReleases;
    @BindString(R.string.settings) String settings;

    Drawer mNavDrawer;
    final private Firebase mReviewRef = new Firebase("https://buzz-films.firebaseio.com/reviews");
    private Hashtable<String, String> majorPosters = new Hashtable<>();
    private Hashtable<String, String> ratingPosters = new Hashtable<>();
    private SessionManager mSession;
    private String mMajor;
    private String mMovieID;

    final private int PROFILE = 1;
    final private int DASHBOARD = 2;
    final private int RECENT_RELEASES = 3;
    final private int SETTINGS = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        this.mSession = SessionManager.getInstance(getApplicationContext());

        setupMajorRecommendations();
        setupRatingRecommendations();
        initToolbar();
        createNavDrawer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        createNavDrawer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Close the searchview when we leave the MainActivity
        MenuItem searchItem = toolbar.getMenu().findItem(R.id.action_search);
        if (searchItem != null) {
            if (searchItem.isActionViewExpanded()) {
                searchItem.collapseActionView();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    /**
     * Handles what to do when a movie poster is clicked
     * @param imageView to set Listener for
     */
    private void setupOnImageClick(ImageView imageView) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMovieID = (String) v.getTag();
                FetchMovieData fetchMovieData = new FetchMovieData();
                fetchMovieData.execute();
            }
        });
    }

    /**
     * Gets movies that have been rated by other users of the same major
     */
    private void setupMajorRecommendations() {
        majorPosters.clear();
        majorRecommendations.removeAllViews();

        mReviewRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMajor = mSession.getLoggedInMajor();

                //iterate through all of the reviews for the movie
                for (DataSnapshot childA : dataSnapshot.getChildren()) {
                    for (DataSnapshot childB : childA.getChildren()) {
                        if (childB.getKey().equals("posterURL") || childB.getKey().equals("movieId")) {
                            continue;
                        }
                        if (childB.child("major").getValue(String.class).equals(mMajor)) {
                            String posterURL = childA.child("posterURL").getValue(String.class);
                            String movieId = childA.child("movieId").getValue(String.class);
                            majorPosters.put(movieId, posterURL);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        //We have to delay this or else posters will be empty
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                populateImages(majorPosters, majorRecommendations);
            }
        }, 2500);
    }

    /**
     * Gets movies that have been rated highly by other users
     */
    private void setupRatingRecommendations() {
        ratingPosters.clear();
        ratingRecommendations.removeAllViews();

        mReviewRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //iterate through all of the reviews for the movie
                for (DataSnapshot childA : dataSnapshot.getChildren()) {
                    double ratingAverage = 0;
                    for (DataSnapshot childB : childA.getChildren()) {
                        if (childB.getKey().equals("posterURL") || childB.getKey().equals("movieId")) {
                            continue;
                        }
                        ratingAverage += childB.child("rating").getValue(Double.class);
                    }
                    ratingAverage /= childA.getChildrenCount() - 2;
                    //If the average rating is >= 4, we'll add it to the recommendations
                    if (ratingAverage >= 4.0) {
                        String posterURL = childA.child("posterURL").getValue(String.class);
                        String movieId = childA.child("movieId").getValue(String.class);
                        ratingPosters.put(movieId, posterURL);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        //We have to delay this or else posters will be empty
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                populateImages(ratingPosters, ratingRecommendations);
            }
        }, 2500);
    }

    /**
     * Iterates through the given list of URLs and adds the images to the view
     * @param posters URLs to iterate through
     * @param layout to insert images into
     */
    private void populateImages(Hashtable<String, String> posters, LinearLayout layout) {
        for (Map.Entry<String, String> entry : posters.entrySet()) {
            String id = entry.getKey();
            String url = entry.getValue();

            insertImage(layout, id, url);
        }
    }

    /**
     * Puts images into the layout
     * @param layout to insert images into
     * @param id of movie
     * @param url of poster image
     * @return newly added ImageView
     */
    public View insertImage(LinearLayout layout, String id, String url) {
        layout.setGravity(Gravity.CENTER);
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setTag(id); //store the movieId per ImageView
        setupOnImageClick(imageView);
        imageView.setPadding(8, 8, 8, 8);
        Picasso.with(this).load(url).resize(900, 800).centerInside().into(imageView);
        layout.addView(imageView);
        return layout;
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
     * sets Toolbar title, enables the visibility of the overflow menu
     */
    private void initToolbar() {
        toolbar.setTitle(dashboard);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_overflow, menu);

        //Set Search icon
        menu.findItem(R.id.action_search).setIcon(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_search)
                .color(Color.WHITE)
                .sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP)
                .paddingDp(4));

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        //Removes the line under the search text
        View searchPlate = mSearchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        searchPlate.setBackgroundColor(getColor(R.color.primary));

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case (R.id.refresh):
                setupMajorRecommendations();
                setupRatingRecommendations();
                break;
            case (R.id.logout):
                onLogoutClick();
                break;
            case (R.id.clearSearch):
                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                        RecentSuggestionsProvider.AUTHORITY, RecentSuggestionsProvider.MODE);
                suggestions.clearHistory();
                break;
        }
        return super.onOptionsItemSelected(item);
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
        if (mNavDrawer != null && mNavDrawer.isDrawerOpen()) {
            mNavDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Class for Fetching data (JSON) using TheMovieDB Asynchronously
     */
    public class FetchMovieData extends AsyncTask<String, Void, Movie> {

        private final String LOG_TAG = FetchMovieData.class.getSimpleName();

        private Movie getDataFromJson(String FilmJsonStr, int num)
                throws JSONException {

            // Get the JSON object representing the movie
            JSONObject movieObject = new JSONObject(FilmJsonStr);

            return new Movie(
                    movieObject.getString("id"),
                    movieObject.getString("title"),
                    movieObject.getString("release_date"),
                    movieObject.getString("overview"),
                    movieObject.getString("poster_path"),
                    movieObject.getDouble("vote_average"));
        }

        @Override
        protected Movie doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String filmJsonStr = null;

            try {
                URL url = new URL(StringHelper.uniqueMovie(mMovieID));

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
                return getDataFromJson(filmJsonStr, 1);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Movie result) {
            if (result != null) {
                Bundle movieDetailBundle = new Bundle();
                String title = result.getTitle();
                String posterPath = result.getPosterUrl();
                String synopsis = result.getSynopsis();
                String releaseDate = result.getReleaseDate();
                Double criticsScore = result.getCriticsScore();
                movieDetailBundle.putString("title", title);
                movieDetailBundle.putString("poster_path", posterPath);
                movieDetailBundle.putString("synopsis", synopsis);
                movieDetailBundle.putString("release_date", releaseDate);
                movieDetailBundle.putDouble("critics_score", criticsScore);
                startActivity(new Intent(MainActivity.this, MovieDetailActivity.class).putExtras(movieDetailBundle));
            }
        }
    }
}
