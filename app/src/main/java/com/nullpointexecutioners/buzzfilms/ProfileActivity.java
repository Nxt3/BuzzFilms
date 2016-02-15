package com.nullpointexecutioners.buzzfilms;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.currentName)
    TextView currentName;
    @Bind(R.id.currentEmail)
    TextView currentEmail;
    @BindDrawable(R.drawable.ic_arrow_back)
    Drawable backArrow;
    @BindString(R.string.register_dialog_title) String registerDialogTitle;
    @BindString(R.string.register) String register;
    @BindString(R.string.cancel) String cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        //Set the current user's attributes
        currentName.setText(DataHolder.getCurrentUser().getName());
        currentEmail.setText(DataHolder.getCurrentUser().getEmail());

        toolbar.setTitle(DataHolder.getCurrentUser().getName());
        toolbar.setNavigationIcon(backArrow);
        toolbar.showOverflowMenu();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @OnClick(R.id.profile_fab)
    public void editProfile() {
        //TODO, handle editing the profile
    }
}
