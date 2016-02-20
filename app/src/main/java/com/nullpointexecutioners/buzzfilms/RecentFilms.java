package com.nullpointexecutioners.buzzfilms;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.ButterKnife;

public class RecentFilms extends Activity {

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
        String[] data = {
                "Deadpool",
                "Kung Fu Panda 3",
                "How To Be Single",
                "Zoolander2",
                "The Revenant",
                "Hail, Caesar!",
                "Star Wars",
                "The Choice",
                "Ride Along2",
                "The Boy"
        };
        List<String> films = new ArrayList<String>(Arrays.asList(data));

        filmAdapter = new ArrayAdapter<>(
                this,
                R.layout.list_item_film,
                R.id.list_item_film,
                films);

        ListView listView = (ListView) findViewById(R.id.listview_film);
        listView.setAdapter(filmAdapter);

        Log.d("debug", "why list view not shown");

    }


//
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Create some dummy data for the ListView.  Here's a sample weekly forecast
//        String[] data = {
//                "Mon 6/23 - Sunny - 31/17",
//                "Tue 6/24 - Foggy - 21/8",
//                "Wed 6/25 - Cloudy - 22/17",
//                "Thurs 6/26 - Rainy - 18/11",
//                "Fri 6/27 - Foggy - 21/10",
//                "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
//                "Sun 6/29 - Sunny - 20/7"
//        };
//        List<String> weekForecast = new ArrayList<String>(Arrays.asList(data));
//
//        filmAdapter = new ArrayAdapter<>(
//                getActivity(),
//                R.layout.list_item_forecast,
//                R.id.list_item_forecast_textview,
//                new ArrayList<String>());
//
//        View rootView = inflater.inflate(R.layout.content_recent_films, container, false);
//        return rootView;
//    }

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
                                    intent = new Intent(RecentFilms.this, ProfileActivity.class);
                                    startActivity(intent);
                                    return true;
                                case DASHBOARD:
                                    mNavDrawer.closeDrawer();
                                    intent = new Intent(RecentFilms.this, MainActivity.class);
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
