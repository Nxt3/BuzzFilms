package com.nullpointexecutioners.buzzfilms;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

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

import butterknife.Bind;
import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    /*I love ButterKnife <3*/
    @Bind(R.id.toolbar) Toolbar toolbar;
    private Drawer navDrawer;
    @BindDrawable(R.drawable.rare_pepe_avatar)
    Drawable profileDrawerIcon;
    @BindString(R.string.profile) String profile;
    @BindString(R.string.settings) String settings;
    @BindString(R.string.title_activity_main) String dashboard;

    final private int DASHBOARD = 1;
    final private int PROFILE = 2;
    final private int SETTINGS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        toolbar.setTitle(dashboard);

        // For the sake of debugging...
//        WelcomeActivity.registerUser("Nate Gantt", "nateg@gatech.edu", "nxt3", "pass");
//        User tempUser = WelcomeActivity.accounts.get("nxt3");
//        DataHolder.setCurrentUser(tempUser);

        // Create the AccountHeader
        final AccountHeader drawerHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.accent)
                .addProfiles(
                        new ProfileDrawerItem()
                                .withName(DataHolder.getCurrentUser().getUsername())
                                .withEmail(DataHolder.getCurrentUser().getEmail())
                                .withIcon(profileDrawerIcon)
                ).withSelectionListEnabledForSingleProfile(false)
                .build();

        //create the drawer and remember the `Drawer` result object
        navDrawer = new DrawerBuilder()
                .withAccountHeader(drawerHeader)
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(dashboard).withIcon(GoogleMaterial.Icon.gmd_dashboard).withIdentifier(DASHBOARD).withSetSelected(true),
                        new PrimaryDrawerItem().withName(profile).withIcon(GoogleMaterial.Icon.gmd_person).withIdentifier(PROFILE).withSelectable(false),
                        new SecondaryDrawerItem().withName(settings).withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(SETTINGS).withSelectable(false)
                ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null && drawerItem instanceof Nameable) {
                            Intent intent = null;

                            switch(drawerItem.getIdentifier()) {
                                case DASHBOARD:
                                    return false;
                                case PROFILE:
                                    intent = new Intent(MainActivity.this, ProfileActivity.class);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
//        navDrawer.setSelection(navDrawer.getDrawerItem(DASHBOARD));
    }

    @Override
    protected void onPause() {
        super.onPause();
        navDrawer.setSelection(navDrawer.getDrawerItem(DASHBOARD));
        navDrawer.closeDrawer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public void onLogoutClick(View v) {
        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
        //make sure we can't press the back button to get back to the MaiActivity!
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
