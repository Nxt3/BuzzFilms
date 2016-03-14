package com.nullpointexecutioners.buzzfilms.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
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
import com.nullpointexecutioners.buzzfilms.Users;
import com.nullpointexecutioners.buzzfilms.adapters.UsersAdapter;
import com.nullpointexecutioners.buzzfilms.helpers.SessionManager;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.ButterKnife;

public class AdminActivity extends AppCompatActivity {

    @Bind(R.id.dashboard_toolbar) Toolbar toolbar;
    @Bind(R.id.users_list) ListView mUsersList;
    @BindDrawable(R.drawable.rare_pepe_avatar) Drawable mProfileDrawerIcon;
    @BindString(R.string.admin) String admin;
    @BindString(R.string.admin_controls) String adminControls;
    @BindString(R.string.dashboard) String dashboard;
    @BindString(R.string.profile) String profile;
    @BindString(R.string.recent_releases) String recentReleases;
    @BindString(R.string.settings) String settings;
    @BindString(R.string.status_active) String active;
    @BindString(R.string.status_banned) String banned;
    @BindString(R.string.status_locked) String locked;

    Drawer mNavDrawer;
    final private Firebase mUsersRef = new Firebase("https://buzz-films.firebaseio.com/users");
    private ArrayList<Users> users = new ArrayList<>();
    private SessionManager mSession;
    private UsersAdapter mUsersAdapter;

    final private int PROFILE = 1;
    final private int DASHBOARD = 2;
    final private int RECENT_RELEASES = 3;
    final private int ADMIN = 4;
    final private int SETTINGS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        ButterKnife.bind(this);

        this.mSession = SessionManager.getInstance(getApplicationContext());

        initToolbar();
        createNavDrawer();
        setupUsersList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    /**
     * Populates list of users
     */
    private void setupUsersList() {
        mUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //iterate through all of the reviews for the movie
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getKey().equals("is_admin")) {
                        continue;
                    }
                    String username = child.child("username").getValue(String.class);
                    String name = child.child("name").getValue(String.class);
                    String email = child.child("email").getValue(String.class);
                    String major = child.child("major").getValue(String.class);
                    String status = child.child("status").getValue() != null
                            ? child.child("status").getValue(String.class) : active;

                    users.add(new Users(username, name, email, major, status));
                }
                if (!users.isEmpty()) {
                    mUsersAdapter = new UsersAdapter(AdminActivity.this,
                            R.layout.user_list_item, new ArrayList<Users>());
                    mUsersList.setAdapter(mUsersAdapter);
                    mUsersAdapter.addAll(users);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        //Handle passing the selected user in the list
        mUsersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
                String username = users.get(position).getUsername();
                String name = users.get(position).getName();
                String email = users.get(position).getEmail();
                String major = users.get(position).getMajor();
                String status = users.get(position).getStatus();

                userView(username, name, email, major, status);
            }
        });
    }

    private void userView(String username, String name, String email, String major, String status) {
        final MaterialDialog userDialog = new MaterialDialog.Builder(this)
                .title(username)
                .customView(R.layout.user_view_dialog, true)
                .positiveText(R.string.okay)
                .negativeText(R.string.cancel)
                .build();

        TextView nameText = ButterKnife.findById(userDialog, R.id.user_view_name);
        nameText.setText(name);
        TextView emailText = ButterKnife.findById(userDialog, R.id.user_view_email);
        emailText.setText(email);
        TextView majorText = ButterKnife.findById(userDialog, R.id.user_view_major);
        majorText.setText(major);

        userDialog.show();
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
                                    intent = new Intent(AdminActivity.this, ProfileActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    return true;
                                case DASHBOARD:
                                    return false;
                                case RECENT_RELEASES:
                                    mNavDrawer.closeDrawer();
                                    intent = new Intent(AdminActivity.this, RecentReleasesActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    return true;
                                case ADMIN:
                                    return false;
                                case SETTINGS:
                                    //TODO, handle Settings
                                    return false;
                            }
                        }
                        return false;
                    }
                }).build();
        mNavDrawer.setSelection(DASHBOARD);
        if (mSession.checkAdmin()) { //if the user is an Admin, we need the Admin drawer item
            mNavDrawer.addItem(new PrimaryDrawerItem().withName(admin).withIcon(GoogleMaterial.Icon.gmd_face).withIdentifier(ADMIN).withSelectable(false));
        }
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
     * Helper method that inits all of the Toolbar stuff.
     * Specifically:
     * sets Toolbar title, enables the visibility of the overflow menu
     */
    private void initToolbar() {
        toolbar.setTitle(adminControls);
        setSupportActionBar(toolbar);
    }
}
