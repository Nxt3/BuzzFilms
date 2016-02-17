package com.nullpointexecutioners.buzzfilms;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsSpinner;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import butterknife.Bind;
import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.currentName)
    TextView profileName;
    @Bind(R.id.currentEmail)
    TextView profileEmail;
    @Bind(R.id.currentMajor)
    TextView profileMajor;
    @Bind(R.id.currentInterests)
    TextView profileInterests;

    @BindDrawable(R.drawable.ic_arrow_back)
    Drawable backArrow;

    @BindString(R.string.edit_profile_dialog_title)
    String editProfileDialogTitle;
    @BindString(R.string.edit_password_dialog_title)
    String editPasswordDialogTitle;
    @BindString(R.string.save)
    String save;
    @BindString(R.string.cancel)
    String cancel;
    @BindString(R.string.major_not_specified)
    String majorNotSpecified;
    @BindString(R.string.new_password_mismatch)
    String passwordMismatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        //Set the current user's attributes
        profileName.setText(getCurrentUser().getName());
        profileEmail.setText(getCurrentUser().getEmail());
        if (getCurrentUser().getMajor() == null) {
            profileMajor.setText(majorNotSpecified);
        } else {
            profileMajor.setText(getCurrentUser().getMajor().toString());
        }
        if (getCurrentUser().getInterests() != null) {
            profileInterests.setText(getCurrentUser().getInterests());
        }

        initToolbar();
    }

    @OnClick(R.id.profile_fab)
    public void editProfile() {
        final MaterialDialog editProfileDialog = new MaterialDialog.Builder(ProfileActivity.this)
                .title(editProfileDialogTitle)
                .customView(R.layout.edit_profile_dialog, true)
                .theme(Theme.DARK)
                .positiveText(save)
                .negativeText(cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog editProfileDialog, @NonNull DialogAction which) {
                        final EditText editName = ButterKnife.findById(editProfileDialog, R.id.edit_name);
                        final EditText editEmail = ButterKnife.findById(editProfileDialog, R.id.edit_email);
                        final Spinner majorDropdown = ButterKnife.findById(editProfileDialog, R.id.major_dropdown);
                        final EditText editInterests = ButterKnife.findById(editProfileDialog, R.id.edit_interests);

                        getCurrentUser().setName(editName.getText().toString());
                        getCurrentUser().setEmail(editEmail.getText().toString());
                        getCurrentUser().setInterests(editInterests.getText().toString());
                        getCurrentUser().setMajor((User.Major) majorDropdown.getSelectedItem());

                        (ProfileActivity.this).passThrough(editName, editEmail, editInterests);
                        (ProfileActivity.this).profileMajor.setText(getCurrentUser().getMajor().toString());
                    }
                }).build();

        if (editProfileDialog.getCustomView() != null) {
            final EditText editName = ButterKnife.findById(editProfileDialog, R.id.edit_name);
            final EditText editEmail = ButterKnife.findById(editProfileDialog, R.id.edit_email);
            final EditText editInterests = ButterKnife.findById(editProfileDialog, R.id.edit_interests);

            /*Need to override isEnabled so the user can't select the hint text in the spinner*/
            ArrayAdapter<User.Major> adapter = new ArrayAdapter<User.Major>(this, android.R.layout.simple_spinner_dropdown_item, User.Major.values()) {
                @Override
                public boolean isEnabled(int position) {
                    return position != 0; //Disabled "Select a major" item
                }
                @Override
                public boolean areAllItemsEnabled() {
                    return false;
                }
            };
            final AbsSpinner majorDropdown = ButterKnife.findById(editProfileDialog, R.id.major_dropdown);
            majorDropdown.setAdapter(adapter);
            editName.setText(getCurrentUser().getName());
            editEmail.setText(getCurrentUser().getEmail());
            if (getCurrentUser().getMajor() != null) {
                majorDropdown.setSelection(((ArrayAdapter<User.Major>) majorDropdown.getAdapter()).getPosition(getCurrentUser().getMajor()));
            }
            if (getCurrentUser().getInterests() != null) {
                editInterests.setText(getCurrentUser().getInterests());
            }
        }
        editProfileDialog.show();
    }

    private void changePassword() {
        final MaterialDialog editPasswordDialog = new MaterialDialog.Builder(ProfileActivity.this)
                .title(editPasswordDialogTitle)
                .customView(R.layout.edit_password_dialog, true)
                .theme(Theme.DARK)
                .positiveText(save)
                .negativeText(cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog editPasswordDialog, @NonNull DialogAction which) {
                        final EditText editPassword = ButterKnife.findById(editPasswordDialog, R.id.edit_password);
                        final EditText editPasswordConfirm = ButterKnife.findById(editPasswordDialog, R.id.edit_password_confirm);

                        if (passwordMatch(editPassword, editPasswordConfirm)
                                && editPassword.getText().toString().length() != 0
                                && editPasswordConfirm.getText().toString().length() != 0) {
                            getCurrentUser().setPassword(editPasswordConfirm.getText().toString());
                        }
                    }
                }).build();

        final View saveAction = editPasswordDialog.getActionButton(DialogAction.POSITIVE);

        if (editPasswordDialog.getCustomView() != null) {
            final EditText editPassword = ButterKnife.findById(editPasswordDialog, R.id.edit_password);
            final EditText editPasswordConfirm = ButterKnife.findById(editPasswordDialog, R.id.edit_password_confirm);

            final TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (passwordMatch(editPassword, editPasswordConfirm)
                            && editPassword.getText().toString().length() != 0
                            && editPasswordConfirm.getText().toString().length() != 0) {
                        editPasswordConfirm.setError(null); //Clears the error
                        saveAction.setEnabled(true);
                    } else {
                        editPasswordConfirm.setError(passwordMismatch);
                        saveAction.setEnabled(false);
                    }
                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            };
            editPassword.addTextChangedListener(watcher);
            editPasswordConfirm.addTextChangedListener(watcher);
        }
        editPasswordDialog.show();
        saveAction.setEnabled(false); //Disabled by default
    }

    private boolean passwordMatch(EditText newPassword, EditText newPasswordConfirm) {
        return (newPassword.getText().toString().matches(newPasswordConfirm.getText().toString()));
    }

    /*Method to help us keep our code cleaner*/
    private User getCurrentUser() {
        return DataHolder.getCurrentUser();
    }

    /*For some reason, this is needed to update the Profile view after we've edited one views*/
    private void passThrough(EditText editName, EditText editEmail, EditText editInterests) {
        profileName.setText(editName.getText().toString());
        profileEmail.setText(editEmail.getText().toString());
        profileInterests.setText(editInterests.getText().toString());
    }

    /*Helper method for initializing the Toolbar*/
    private void initToolbar() {
        toolbar.setTitle(DataHolder.getCurrentUser().getUsername());
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(backArrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_overflow, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.change_password) {
            changePassword();
        }

        return super.onOptionsItemSelected(item);
    }
}
