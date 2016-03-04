package com.nullpointexecutioners.buzzfilms.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.nullpointexecutioners.buzzfilms.R;
import com.nullpointexecutioners.buzzfilms.Review;
import com.nullpointexecutioners.buzzfilms.adapters.ReviewAdapter;
import com.nullpointexecutioners.buzzfilms.helpers.SessionManager;
import com.nullpointexecutioners.buzzfilms.helpers.StringHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MovieDetailActivity extends AppCompatActivity {

    @Bind(R.id.movie_detail_toolbar) Toolbar toolbar;
    @Bind(R.id.movie_reviews_list) ListView mMovieReviewsList;
    @Bind(R.id.movie_title) TextView mTempMovieTitle;
    @Bind(R.id.review_fab) FloatingActionButton floatingActionButton;
    @Bind(R.id.movie_poster) ImageView moviePoster;
    @BindString(R.string.cancel) String cancel;
    @BindString(R.string.leave_review_title) String leaveReviewTitle;
    @BindString(R.string.save) String save;

    final private Firebase mReviewRef = new Firebase("https://buzz-films.firebaseio.com/reviews");
    final private Firebase mUserRef = new Firebase("https://buzz-films.firebaseio.com/users");
    private String mMovieTitle;

    private ReviewAdapter mReviewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        initToolbar();

        Drawable addReviewIcon = new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_add)
                .color(Color.BLACK)
                .sizeDp(24)
                .paddingDp(2);
        floatingActionButton.setImageDrawable(addReviewIcon);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            mMovieTitle = (String) bundle.get("title");
            mTempMovieTitle.setText(mMovieTitle);
        }

        moviePoster.bringToFront();
        String posterURL = StringHelper.getPosterUrl((String) bundle.get("poster_path"));
        Picasso.with(moviePoster.getContext()).load(posterURL).into(moviePoster);

        setupReviews();
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
                                setupReviews();
                            }
                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                            }
                        });
                    }
                }).build();
        //Leave review as {current_username}
        TextView reviewee = ButterKnife.findById(reviewDialog, R.id.reviewee);
        reviewee.append(" " + (Html.fromHtml("<b>" + currentUser + "</b>"))); //bold the username text
        reviewDialog.show();
    }

    /**
     * This entire method is literally Hitler.
     * *ATTEMPTS* to add and update the reviews list per each movie. It's hacky and I hate it.
     */
    private void setupReviews() {
        mReviewRef.child(mMovieTitle).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildKey) {
                ArrayList<String> usernames = new ArrayList<>();
                ArrayList<String> majors = new ArrayList<>();
                ArrayList<Double> ratings = new ArrayList<>();
                ArrayList<Review> reviews = new ArrayList<>();

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
                    mMovieReviewsList.setAdapter(mReviewAdapter);
                    mReviewAdapter.addAll(reviews);
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); //Simulate a system's "Back" button functionality.
            }
        });
    }
}
