package com.nullpointexecutioners.buzzfilms.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.github.florent37.picassopalette.PicassoPalette;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.nullpointexecutioners.buzzfilms.R;
import com.nullpointexecutioners.buzzfilms.Review;
import com.nullpointexecutioners.buzzfilms.adapters.ReviewAdapter;
import com.nullpointexecutioners.buzzfilms.helpers.SessionManager;
import com.nullpointexecutioners.buzzfilms.helpers.StringHelper;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MovieDetailActivity extends AppCompatActivity {

    @Bind(R.id.movie_critic_score) TextView movieCriticScore;
    @Bind(R.id.movie_detail_toolbar) Toolbar toolbar;
    @Bind(R.id.movie_poster) ImageView moviePoster;
    @Bind(R.id.movie_release_date) TextView movieReleaseDate;
    @Bind(R.id.movie_synopsis) TextView movieSynopsis;
    @Bind(R.id.movie_title) TextView movieTitle;
    @Bind(R.id.review_fab) FloatingActionButton floatingActionButton;
    @Bind(R.id.user_reviews_button) Button userReviewsButton;
    @BindString(R.string.cancel) String cancel;
    @BindString(R.string.leave_review_title) String leaveReviewTitle;
    @BindString(R.string.save) String save;

    final private Firebase mReviewRef = new Firebase("https://buzz-films.firebaseio.com/reviews");
    final private Firebase mUserRef = new Firebase("https://buzz-films.firebaseio.com/users");
    private String mMovieTitle;

    ArrayList<String> usernames = new ArrayList<>();
    ArrayList<String> majors = new ArrayList<>();
    ArrayList<Double> ratings = new ArrayList<>();
    ArrayList<Review> reviews = new ArrayList<>();

    private ReviewAdapter mReviewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            mMovieTitle = (String) bundle.get("title");
            movieTitle.setText(mMovieTitle);
            String releaseDate = (String) bundle.get("release_date");
            try { //try to parse the release dates to be the Locale default (in our case, 'murica)
                SimpleDateFormat fromDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat toDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                movieReleaseDate.setText(toDate.format(fromDate.parse(releaseDate)));
            } catch (ParseException pe) {
                movieReleaseDate.setText(releaseDate);
            }
            movieCriticScore.setText(String.format(Locale.getDefault(), "%1$.2f", (Double) bundle.get("critics_score")));
            movieCriticScore.append(" / 10"); //outta ten
            movieSynopsis.setText((String) bundle.get("synopsis"));

            String posterURL = StringHelper.getPosterUrl((String) bundle.get("poster_path"));
            //used for getting colors from the movie poster
            Picasso.with(this).load(posterURL).into(moviePoster,
                    PicassoPalette.with(posterURL, moviePoster)
                            .intoCallBack(new PicassoPalette.CallBack() {
                                @Override
                                public void onPaletteLoaded(Palette palette) {
                                    int color = palette.getLightVibrantColor(getThemeAccentColor(MovieDetailActivity.this));
                                    //because the support library doesn't allow us to change the background color of the FAB, we just tint it instead
                                    floatingActionButton.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{color}));
                                    userReviewsButton.setTextColor(color);
                                }
                            }));
        }

        Drawable addReviewIcon = new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_add)
                .color(Color.BLACK)
                .sizeDp(24)
                .paddingDp(2);
        floatingActionButton.setImageDrawable(addReviewIcon);

        initToolbar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    /**
     * Handles leaving a review
     */
    @OnClick(R.id.review_fab)
    public void leaveReview() {
        //get current username
        final String currentUser = SessionManager.getInstance(MovieDetailActivity.this).getLoggedInUsername();
        final MaterialDialog reviewDialog = new MaterialDialog.Builder(MovieDetailActivity.this)
                .title(leaveReviewTitle)
                .customView(R.layout.rating_movie_dialog, true)
                .theme(Theme.DARK)
                .positiveText(save)
                .negativeText(cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog reviewDialog, @NonNull DialogAction which) {
                        final RatingBar ratingBar = ButterKnife.findById(reviewDialog, R.id.rating_bar);
                        final double rating = ratingBar.getRating(); //get the rating

                        /*Get Major from Firebase, and also store the review while we're at it*/
                        mUserRef.child(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String major = dataSnapshot.child("major").getValue(String.class);
                                final Firebase reviewRef = mReviewRef.child(StringHelper.reviewHelper(mMovieTitle, currentUser));
                                reviewRef.child("username").setValue(currentUser);
                                reviewRef.child("major").setValue(major);
                                reviewRef.child("rating").setValue(rating);
                            }
                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                            }
                        });
                    }
                }).build();
        //Leave review as {current_username}
        TextView reviewee = ButterKnife.findById(reviewDialog, R.id.reviewee);
        final Spannable revUsername = new SpannableString(currentUser);
        revUsername.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, revUsername.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        reviewee.append(" " + revUsername); //bold the username text

        reviewDialog.show();
    }

    /**
     * This entire method is literally Hitler.
     * *ATTEMPTS* to add and update the reviews list per each movie. It's hacky and I hate it.
     */
    @OnClick(R.id.user_reviews_button)
    public void setupReviews() {
        mReviewRef.child(mMovieTitle).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildKey) {
                //iterate through all of the reviews for the movie
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    //I'm either dumb or tired--but there isn't a way to do this all at once
                    //So, we have an switch block for determining when we're at a particular child,
                    //then we add it to a running list of values to parse later.
                    switch(child.getKey()) {
                        case ("username"):
                            usernames.add(child.getValue(String.class));
                            break;
                        case ("major"):
                            majors.add(child.getValue(String.class));
                            break;
                        case ("rating"):
                            ratings.add(child.getValue(Double.class));
                            break;
                    }
                }

                //Literally the hackiest of workarounds; I'm not even proud of it.
                //However, this is God-tier shit
                if (!usernames.isEmpty()) { //only want to iterate if we're rating a movie that already has reviews
                    for (int i = 0; i < usernames.size(); ++i) {
                        try { //I hate that checking if Usernames != empty isn't enough, and this is
                            // the only way I could get it to work...
                            reviews.add(new Review(usernames.get(i), majors.get(i), ratings.get(i)));
                        } catch (IndexOutOfBoundsException ioobe) {
                        }
                    }
                }

                if (mReviewAdapter == null) {
                    mReviewAdapter = new ReviewAdapter(MovieDetailActivity.this,
                            R.layout.review_list_item, reviews);
//                    mMovieReviewsList.setAdapter(mReviewAdapter);
                } else {
                    try {
                        mReviewAdapter.addAll(reviews);
                        Firebase mUserRevRef = new Firebase("https://buzz-films.firebaseio.com/reviews/" + dataSnapshot.getKey());
                        mUserRevRef.setValue(ratings.get(ratings.size() - 1));
                        mReviewAdapter.notifyDataSetChanged();
                    } catch (NullPointerException npe) {
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildKey) {
                mReviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildKey) {
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    /**
     * Helper method that inits all of the Toolbar stuff
     */
    private void initToolbar() {
        assert getSupportActionBar() != null;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); //Simulate a system's "Back" button functionality.
            }
        });
    }

    /**
     * Helper method for determining which color to use for the toolbar
     * @param palette of generate colors from the movie poster
     * @return selected color
     */
    private int colorSelector(Palette palette) {
        int defaultColor = getThemeAccentColor(MovieDetailActivity.this); //primary color
        int vibrantDark = palette.getDarkVibrantColor(defaultColor);
        int mutedDark = palette.getDarkMutedColor(defaultColor);
        int vibrant = palette.getVibrantColor(defaultColor);

        if (vibrantDark != defaultColor) {
            return vibrantDark;
        } else if (mutedDark != defaultColor) {
            return mutedDark;
        } else if (vibrant != defaultColor) {
            return vibrant;
        } else {
            return defaultColor;
        }
    }

    /**
     * Helper method for getting the current app's accent color
     * @param context from which to get the color
     * @return int value of color
     */
    private int getThemeAccentColor(final Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, value, true);
        return value.data;
    }
}
