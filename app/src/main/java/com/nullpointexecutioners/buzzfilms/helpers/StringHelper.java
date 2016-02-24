package com.nullpointexecutioners.buzzfilms.helpers;

import android.app.Activity;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class StringHelper extends Activity {

    /**
     * Appends a dummy domain to the username when adding it to Firebase
     * This then follows Firebase's auth methods
     * @param  username to append dummy domain to
     * @return username appended with the dummy domain
     */
    public static String setUserWithDummyDomain(String username) {
        return username + "@buzz-films.edu";
    }

    /**
     * Helper method for checking the validity of an email
     * This will check and make sure the format of an input email is in email form
     * @param email to check
     * @return true or false depending if the email was valid
     */
    public static boolean isValidEmail(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Creates a TextWatcher for monitoring the validity of an email
     * @param emailInput email EditText field
     * @param view action item to enable/disable
     * @param error to set on the EditText field
     * @return emailWatcher TextWatcher
     */
    public static TextWatcher emailWatcher(final EditText emailInput, final View view, final String error) {
        final TextWatcher emailWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!StringHelper.isValidEmail(emailInput.getText().toString())) {
                    emailInput.setError(error); //not valid email
                    view.setEnabled(false);
                } else {
                    emailInput.setError(null); //clears error
                    view.setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        return emailWatcher;
    }

    /**
     * Helper method for making sure the new password and the confirm new password fields hold the same password.
     * @param newPassword is the new password EditText field
     * @param newPasswordConfirm is the EditText field that confirms the new password
     * @return true or false depending on the equality of the password fields
     */
    public static boolean passwordMatch(EditText newPassword, EditText newPasswordConfirm) {
        return (newPassword.getText().toString().matches(newPasswordConfirm.getText().toString()));
    }

    /**
     * Creates a TextWatcher for monitoring password matches in EditText fields
     * @param oldPasswordInput old password
     * @param passwordInput new password
     * @param passwordInputConfirm new password confirm field
     * @param view action item to enable/disable
     * @param error to set on the EditText field
     * @return passwordWatcher TextWatcher
     */
    public static TextWatcher passwordWatcher(final EditText oldPasswordInput, final EditText passwordInput,
                                              final EditText passwordInputConfirm, final View view,
                                              final String error) {
        final TextWatcher passwordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (StringHelper.passwordMatch(passwordInput, passwordInputConfirm)
                        && oldPasswordInput.getText().toString().length() != 0
                        && passwordInput.getText().toString().length() != 0
                        && passwordInputConfirm.getText().toString().length() != 0) {
                    passwordInputConfirm.setError(null); //Clears the error
                    view.setEnabled(true);
                } else {
                    passwordInputConfirm.setError(error);
                    view.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        return passwordWatcher;
    }

    /**
     * URI builder for creating a URL to access certain methods of the Rotten Tomato API
     * @param args an ArrayList of arguments to add to the
     * @return a String to use as the URL
     */
    public static String tomatoURI(List<String> args) {
        Uri.Builder builder = new Uri.Builder();
        final String API_KEY = "vbhetn4chdpudf7mqhckacca";

        builder.scheme("http")
                .authority("api.rottentomatoes.com")
                .appendPath("api")
                .appendPath("public")
                .appendPath("v1.0");
        for (String p: args) {
            builder.appendPath(p);
        }
        builder.appendQueryParameter("apikey", API_KEY);
        return builder.build().toString();
    }
}
