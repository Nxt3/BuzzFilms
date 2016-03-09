package com.nullpointexecutioners.buzzfilms.helpers;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by Matthew on 3/9/2016.
 */
public class RecentSuggestionsProvider
        extends SearchRecentSuggestionsProvider {

    public static final String AUTHORITY =
            RecentSuggestionsProvider.class.getName();

    public static final int MODE = DATABASE_MODE_QUERIES;

    public RecentSuggestionsProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}