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

import butterknife.Bind;
import butterknife.BindDrawable;
import butterknife.BindInt;
import butterknife.BindString;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    /*I love ButterKnife <3*/
    @Bind(R.id.toolbar) Toolbar toolbar;
    @BindDrawable(R.drawable.rare_pepe_avatar)
    Drawable profileDrawerIcon;
    @BindString(R.string.profile) String profile;
    @BindString(R.string.settings) String settings;
    @BindString(R.string.title_activity_main) String dashboard;
    @BindInt(R.color.accent) int accent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        toolbar.setTitle(dashboard);

        // Create the AccountHeader
        final AccountHeader drawerHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(accent)
                .addProfiles(
                        new ProfileDrawerItem()
                                .withName("Mike Penz")
                                .withEmail("mikepenz@gmail.com")
                                .withIcon(profileDrawerIcon)
                ).build();


        //create the drawer and remember the `Drawer` result object
        final Drawer navdrawer = new DrawerBuilder()
                .withAccountHeader(drawerHeader)
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(dashboard).withIcon(GoogleMaterial.Icon.gmd_dashboard),
                        new PrimaryDrawerItem().withName(profile).withIcon(GoogleMaterial.Icon.gmd_person),
                        new SecondaryDrawerItem().withName(settings).withIcon(GoogleMaterial.Icon.gmd_settings)
                ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            Intent intent = null;
                            final int dashboardID = 1;
                            final int profileID = 2;
                            final int settingsID = 3;

                            if (drawerItem.getIdentifier() == profileID) {
                                intent = new Intent(MainActivity.this, ProfileActivity.class);
                            } else if (drawerItem.getIdentifier() == settingsID) {
                                intent = null;
                            }

                            if (intent == null) {
                                MainActivity.this.startActivity(intent);
                            }
                        }
                        return true;
                    }
                }).build();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public void onLogoutClick(View v) {
        Intent myIntent = new Intent(MainActivity.this, WelcomeActivity.class);
        startActivity(myIntent);
    }
}
