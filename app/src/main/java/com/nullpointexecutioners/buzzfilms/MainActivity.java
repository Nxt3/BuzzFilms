package com.nullpointexecutioners.buzzfilms;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import java.util.HashMap;

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
    private SessionManager mSession;

    final private int DASHBOARD = 1;
    final private int PROFILE = 2;
    final private int SETTINGS = 3;

    /**
     * Creates this activity
     * @param savedInstanceState no idea what this is
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        this.mSession = SessionManager.getInstance(getApplicationContext());

        HashMap<String, String> user = mSession.getUserDetails();
        String namey = user.get(SessionManager.KEY_NAME);
        String emaily = user.get(SessionManager.KEY_EMAIL);
        String usernamey = user.get(SessionManager.KEY_USERNAME);
        Log.v("Logged in: ", "<" + namey + ", " + emaily + ", " + usernamey + ">");

        toolbar.setTitle(dashboard);

        createNavDrawer();
    }

    /**
     * Handles this activity once it is destroyed
     */
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
        final AccountHeader drawerHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.accent)
                .addProfiles(
                        new ProfileDrawerItem()
                                .withName(mSession.getUserDetails().get(SessionManager.KEY_NAME))
                                .withEmail(mSession.getUserDetails().get(SessionManager.KEY_EMAIL))
                                .withIcon(mProfileDrawerIcon))
                .withSelectionListEnabledForSingleProfile(false)
                .build();
        //Create the nav drawer
        mNavDrawer = new DrawerBuilder()
                .withAccountHeader(drawerHeader)
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(dashboard).withIcon(GoogleMaterial.Icon.gmd_dashboard).withIdentifier(DASHBOARD).withSetSelected(true),
                        new PrimaryDrawerItem().withName(profile).withIcon(GoogleMaterial.Icon.gmd_person).withIdentifier(PROFILE).withSelectable(false),
                        new SecondaryDrawerItem().withName(settings).withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(SETTINGS).withSelectable(false))
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null && drawerItem instanceof Nameable) {
                            Intent intent;

                            switch(drawerItem.getIdentifier()) {
                                case DASHBOARD:
                                    return false;
                                case PROFILE:
                                    mNavDrawer.closeDrawer();
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
     * When the user clicks on the logout button, they will be deauthorize from the application.
     * In addition to kicking them back to the login screen, the activity stack is also cleared as to prevent a user from being able to get back into the app with a "Back" button press.
     */
    @OnClick(R.id.logout_button)
    public void onLogoutClick() {
        //Removes user from Session, unAuth via Firebase, and clears Activity stack
        mSession.logoutUser();

        //After logout redirect user to WelcomeActivity to login or register
        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);

        //Closing all the Activities
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //Add new Flag to start new Activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }
}
