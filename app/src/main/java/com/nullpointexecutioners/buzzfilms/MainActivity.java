package com.nullpointexecutioners.buzzfilms;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.firebase.client.Firebase;
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
import butterknife.OnClick;

/**
 * Dashboard of the app--Main view
 */
public class MainActivity extends AppCompatActivity {

    /*I love ButterKnife <3*/
    @Bind(R.id.toolbar) Toolbar toolbar;
    @BindDrawable(R.drawable.rare_pepe_avatar)
    Drawable mProfileDrawerIcon;
    @BindString(R.string.title_activity_main) String dashboard;
    @BindString(R.string.profile) String profile;
    @BindString(R.string.settings) String settings;

    Drawer mNavDrawer;
    Firebase mRef;

    final private int DASHBOARD = 1;
    final private int PROFILE = 2;
    final private int SETTINGS = 3;

    /**
     * Creates this activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        toolbar.setTitle(dashboard);

        // Create the AccountHeader
        final AccountHeader drawerHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.accent)
                .addProfiles(
                        new ProfileDrawerItem()
                                .withName("temp")
                                .withEmail("temp@gmail")
                                .withIcon(mProfileDrawerIcon)
                ).withSelectionListEnabledForSingleProfile(false)
                .build();

        //Create the drawer and remember the `Drawer` result object
        mNavDrawer = new DrawerBuilder()
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

    /**
     * Handles the activity once it is started
     */
    @Override
    protected void onStart() {
        super.onStart();

        mRef = new Firebase("https://buzz-films.firebaseio.com/Users");
    }

    /**
     * Handles the activity once it is resume
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Handles the activity once it is paused (i.e. in the background)
     */
    @Override
    protected void onPause() {
        super.onPause();
        mNavDrawer.setSelection(mNavDrawer.getDrawerItem(DASHBOARD));
        mNavDrawer.closeDrawer();
    }

    /**
     * Handles the activity once it is destroyed
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.logout_button)
    /**
     * When the user clicks on the logout button, they will be deauthorized from the application.
     * In addition to kicking them back to the login screen, the activity stack is also cleared as to prevent a user from being able to get back into the app with a "Back" button press.
     */
    public void onLogoutClick() {
        mRef.unauth();
        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
        //make sure we can't press the back button to get back to the MaiActivity!
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
