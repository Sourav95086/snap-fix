package com.nextgenius.snapfix;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nextgenius.snapfix.api.PreviousContestApi;
import com.nextgenius.snapfix.api.PreviousContestRetrofitClient;
import com.nextgenius.snapfix.api.UserApi;
import com.nextgenius.snapfix.api.UserRetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserActivity extends AppCompatActivity {

    // ============================================================
    // API
    // ============================================================

    private UserApi userApi;

    private PreviousContestApi previousContestApi;


    // ============================================================
    // FIREBASE
    // ============================================================

    private FirebaseAuth firebaseAuth;


    // ============================================================
    // HAMBURGER / DRAWER
    // ============================================================

    private DrawerLayout userDrawerLayout;

    private View hamburgerButton;
    private View navValidateIssue;
    private View navLogout;


    // ============================================================
    // USER INFORMATION
    // ============================================================

    private TextView userNameText;
    private TextView userEmailText;

    private TextView reportCountText;
    private TextView pointsText;


    // ============================================================
    // CONTAINERS
    // ============================================================

    private LinearLayout contestContainer;
    private LinearLayout issuesContainer;


    // ============================================================
    // CONSTANTS
    // ============================================================

    private static final String CITY = "Bhubaneswar";

    private static final int CYAN =
            Color.rgb(0, 217, 255);

    private static final int WHITE =
            Color.WHITE;

    private static final int GREY =
            Color.rgb(120, 133, 148);

    private static final int CARD_BACKGROUND =
            Color.rgb(12, 18, 26);


    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user);


        // ========================================================
        // FIREBASE
        // ========================================================

        firebaseAuth =
                FirebaseAuth.getInstance();


        // ========================================================
        // USER API
        // ========================================================

        userApi =
                UserRetrofitClient.getApi();


        // ========================================================
        // PREVIOUS CONTEST API
        // ========================================================

        previousContestApi =
                PreviousContestRetrofitClient.getApi();


        // ========================================================
        // INITIALIZE ALL VIEWS
        // ========================================================

        initializeViews();


        // ========================================================
        // SETUP HAMBURGER DRAWER
        // ========================================================

        setupDrawer();


        // ========================================================
        // LOAD USER DATA
        // ========================================================

        loadUserData();


        // ========================================================
        // LOAD PREVIOUS CONTEST
        // ========================================================

        loadContestResults();
    }


    // ============================================================
    // INITIALIZE VIEWS
    // ============================================================

    private void initializeViews() {

        // --------------------------------------------------------
        // USER INFORMATION
        // --------------------------------------------------------

        userNameText =
                findViewById(R.id.userNameText);

        userEmailText =
                findViewById(R.id.userEmailText);

        reportCountText =
                findViewById(R.id.reportCountText);

        pointsText =
                findViewById(R.id.pointsText);


        // --------------------------------------------------------
        // CONTAINERS
        // --------------------------------------------------------

        contestContainer =
                findViewById(R.id.contestContainer);

        issuesContainer =
                findViewById(R.id.issuesContainer);


        // --------------------------------------------------------
        // DRAWER
        // --------------------------------------------------------

        userDrawerLayout =
                findViewById(R.id.userDrawerLayout);

        hamburgerButton =
                findViewById(R.id.hamburgerButton);

        navValidateIssue =
                findViewById(R.id.navValidateIssue);

        navLogout =
                findViewById(R.id.navLogout);
    }


    // ============================================================
    // HAMBURGER DRAWER
    // ============================================================

    private void setupDrawer() {

        // ========================================================
        // HAMBURGER BUTTON
        // ========================================================

        hamburgerButton.setOnClickListener(v -> {

            if (userDrawerLayout.isDrawerOpen(
                    Gravity.LEFT
            )) {

                userDrawerLayout.closeDrawer(
                        Gravity.LEFT
                );

            } else {

                userDrawerLayout.openDrawer(
                        Gravity.LEFT
                );
            }
        });


        // ========================================================
        // VALIDATE ISSUE
        // ========================================================
        //
        // NOT IMPLEMENTED YET.
        // We will connect this to the real activity later.
        //

        navValidateIssue.setOnClickListener(v -> {

            userDrawerLayout.closeDrawer(Gravity.LEFT);

            Intent intent = new Intent(
                    UserActivity.this,
                    Activity6.class
            );

            startActivity(intent);
        });


        // ========================================================
        // LOGOUT
        // ========================================================

        navLogout.setOnClickListener(v -> {

            // Close drawer first
            userDrawerLayout.closeDrawer(
                    Gravity.LEFT
            );


            // Firebase logout
            firebaseAuth.signOut();


            // Go to LoginActivity
            Intent intent =
                    new Intent(
                            UserActivity.this,
                            LoginActivity.class
                    );


            // Remove UserActivity from back stack
            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );


            startActivity(intent);

            finish();
        });
    }


    // ============================================================
    // LOAD CURRENT USER
    // ============================================================

    private void loadUserData() {

        FirebaseUser currentUser =
                firebaseAuth.getCurrentUser();


        // --------------------------------------------------------
        // CHECK LOGIN
        // --------------------------------------------------------

        if (currentUser == null) {

            Toast.makeText(
                    this,
                    "User session expired",
                    Toast.LENGTH_LONG
            ).show();


            Intent intent =
                    new Intent(
                            UserActivity.this,
                            LoginActivity.class
                    );


            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );


            startActivity(intent);

            finish();

            return;
        }


        // --------------------------------------------------------
        // GET EMAIL
        // --------------------------------------------------------

        String email =
                currentUser.getEmail();


        if (email == null
                || email.isEmpty()) {

            Toast.makeText(
                    this,
                    "Unable to identify user",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        // --------------------------------------------------------
        // LOAD ISSUES
        // --------------------------------------------------------

        loadUserIssues(email);


        // --------------------------------------------------------
        // LOAD POINTS
        // --------------------------------------------------------

        loadUserPoints(email);
    }


    // ============================================================
    // USER POINTS API
    // ============================================================

    private void loadUserPoints(String email) {

        userApi
                .getUserPoints(email)
                .enqueue(
                        new Callback<UserPointsResponse>() {

                            @Override
                            public void onResponse(
                                    Call<UserPointsResponse> call,
                                    Response<UserPointsResponse> response
                            ) {

                                if (!response.isSuccessful()
                                        || response.body() == null) {

                                    Toast.makeText(
                                            UserActivity.this,
                                            "Failed to load user points",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }


                                UserPointsResponse data =
                                        response.body();


                                if (!data.isSuccess()
                                        || data.getUser() == null) {

                                    return;
                                }


                                UserPoints user =
                                        data.getUser();


                                // ------------------------------------------------
                                // NAME
                                // ------------------------------------------------

                                if (user.getName() != null) {

                                    userNameText.setText(
                                            user.getName()
                                    );
                                }


                                // ------------------------------------------------
                                // EMAIL
                                // ------------------------------------------------

                                if (user.getEmail() != null) {

                                    userEmailText.setText(
                                            user.getEmail()
                                    );
                                }


                                // ------------------------------------------------
                                // REPORT COUNT
                                // ------------------------------------------------

                                reportCountText.setText(
                                        String.valueOf(
                                                user.getNumberOfReports()
                                        )
                                );


                                // ------------------------------------------------
                                // POINTS
                                // ------------------------------------------------

                                pointsText.setText(
                                        String.valueOf(
                                                user.getPoints()
                                        )
                                );
                            }


                            @Override
                            public void onFailure(
                                    Call<UserPointsResponse> call,
                                    Throwable t
                            ) {

                                Toast.makeText(
                                        UserActivity.this,
                                        "Points API error",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
    }


    // ============================================================
    // USER ISSUES API
    // ============================================================

    private void loadUserIssues(String email) {

        userApi
                .getUserIssues(email)
                .enqueue(
                        new Callback<UserIssuesResponse>() {

                            @Override
                            public void onResponse(
                                    Call<UserIssuesResponse> call,
                                    Response<UserIssuesResponse> response
                            ) {

                                if (!response.isSuccessful()
                                        || response.body() == null) {

                                    Toast.makeText(
                                            UserActivity.this,
                                            "Failed to load reports",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }


                                UserIssuesResponse data =
                                        response.body();


                                if (!data.isSuccess()) {

                                    return;
                                }


                                // ------------------------------------------------
                                // CLEAR PREVIOUS CARDS
                                // ------------------------------------------------

                                issuesContainer.removeAllViews();


                                List<UserIssue> issues =
                                        data.getIssues();


                                // ------------------------------------------------
                                // EMPTY
                                // ------------------------------------------------

                                if (issues == null
                                        || issues.isEmpty()) {

                                    TextView empty =
                                            new TextView(
                                                    UserActivity.this
                                            );


                                    empty.setText(
                                            "NO REPORTS FOUND"
                                    );


                                    empty.setTextColor(
                                            GREY
                                    );


                                    empty.setTextSize(
                                            10
                                    );


                                    empty.setGravity(
                                            Gravity.CENTER
                                    );


                                    empty.setPadding(
                                            0,
                                            30,
                                            0,
                                            30
                                    );


                                    issuesContainer.addView(
                                            empty
                                    );


                                    return;
                                }


                                // ------------------------------------------------
                                // ADD EVERY ISSUE
                                // ------------------------------------------------

                                for (UserIssue issue : issues) {

                                    addIssueCard(issue);
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<UserIssuesResponse> call,
                                    Throwable t
                            ) {

                                Toast.makeText(
                                        UserActivity.this,
                                        "Reports API error",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
    }


    // ============================================================
    // CREATE ISSUE CARD
    // ============================================================

    private void addIssueCard(UserIssue issue) {

        View card =
                getLayoutInflater().inflate(
                        R.layout.item_user_issue,
                        issuesContainer,
                        false
                );


        TextView issueIdText =
                card.findViewById(
                        R.id.issueIdText
                );


        TextView descriptionText =
                card.findViewById(
                        R.id.descriptionText
                );


        TextView locationText =
                card.findViewById(
                        R.id.locationText
                );


        TextView reportIdText =
                card.findViewById(
                        R.id.reportIdText
                );


        TextView dateText =
                card.findViewById(
                        R.id.dateText
                );


        // --------------------------------------------------------
        // ISSUE ID
        // --------------------------------------------------------

        issueIdText.setText(
                "ISSUE #" + issue.getIssueId()
        );


        // --------------------------------------------------------
        // DESCRIPTION
        // --------------------------------------------------------

        descriptionText.setText(
                issue.getIssueDescription()
        );


        // --------------------------------------------------------
        // REPORT ID
        // --------------------------------------------------------

        reportIdText.setText(
                "REPORT ID " + issue.getReportId()
        );


        // --------------------------------------------------------
        // LOCATION
        // --------------------------------------------------------

        if (issue.getIssueLocation() != null) {

            locationText.setText(
                    issue.getIssueLocation()
                            .toUpperCase()
            );

        } else {

            locationText.setText(
                    "LOCATION UNKNOWN"
            );
        }


        // --------------------------------------------------------
        // DATE
        // --------------------------------------------------------

        dateText.setText(
                formatDate(
                        issue.getReportedOn()
                )
        );


        // --------------------------------------------------------
        // ADD
        // --------------------------------------------------------

        issuesContainer.addView(
                card
        );
    }


    // ============================================================
    // PREVIOUS CONTEST API
    // ============================================================

    private void loadContestResults() {

        previousContestApi
                .getTopReporters(CITY)
                .enqueue(
                        new Callback<PreviousContestResponse>() {

                            @Override
                            public void onResponse(
                                    Call<PreviousContestResponse> call,
                                    Response<PreviousContestResponse> response
                            ) {

                                if (!response.isSuccessful()
                                        || response.body() == null) {

                                    Toast.makeText(
                                            UserActivity.this,
                                            "Failed to load contest results",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }


                                PreviousContestResponse data =
                                        response.body();


                                // ------------------------------------------------
                                // CLEAR
                                // ------------------------------------------------

                                contestContainer.removeAllViews();


                                // ------------------------------------------------
                                // EMPTY
                                // ------------------------------------------------

                                if (data.getTopReporters() == null
                                        || data.getTopReporters().isEmpty()) {

                                    TextView empty =
                                            new TextView(
                                                    UserActivity.this
                                            );


                                    empty.setText(
                                            "NO PREVIOUS CONTEST RESULTS"
                                    );


                                    empty.setTextColor(
                                            GREY
                                    );


                                    empty.setTextSize(
                                            9
                                    );


                                    empty.setGravity(
                                            Gravity.CENTER_VERTICAL
                                    );


                                    contestContainer.addView(
                                            empty
                                    );


                                    return;
                                }


                                // ------------------------------------------------
                                // ADD WINNERS
                                // ------------------------------------------------

                                for (
                                        PreviousContestWinner winner :
                                        data.getTopReporters()
                                ) {

                                    addContestWinner(
                                            winner
                                    );
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<PreviousContestResponse> call,
                                    Throwable t
                            ) {

                                Toast.makeText(
                                        UserActivity.this,
                                        "Contest API error",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
    }


    // ============================================================
    // CREATE CONTEST WINNER CARD
    // ============================================================

    private void addContestWinner(
            PreviousContestWinner winner
    ) {

        LinearLayout card =
                new LinearLayout(this);


        card.setOrientation(
                LinearLayout.VERTICAL
        );


        card.setGravity(
                Gravity.CENTER
        );


        // --------------------------------------------------------
        // SIZE
        // --------------------------------------------------------

        int width =
                dp(125);

        int height =
                dp(82);


        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        width,
                        height
                );


        params.setMargins(
                0,
                0,
                dp(8),
                0
        );


        card.setLayoutParams(
                params
        );


        // --------------------------------------------------------
        // PADDING
        // --------------------------------------------------------

        card.setPadding(
                dp(8),
                dp(8),
                dp(8),
                dp(8)
        );


        // --------------------------------------------------------
        // BACKGROUND
        // --------------------------------------------------------

        card.setBackgroundColor(
                CARD_BACKGROUND
        );


        // ========================================================
        // RANK
        // ========================================================

        TextView rank =
                new TextView(this);


        rank.setText(
                "#" + winner.getRank()
        );


        rank.setTextColor(
                CYAN
        );


        rank.setTextSize(
                9
        );


        rank.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );


        rank.setGravity(
                Gravity.CENTER
        );


        // ========================================================
        // NAME
        // ========================================================

        TextView name =
                new TextView(this);


        name.setText(
                winner.getName()
        );


        name.setTextColor(
                WHITE
        );


        name.setTextSize(
                12
        );


        name.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );


        name.setGravity(
                Gravity.CENTER
        );


        name.setMaxLines(
                1
        );


        // ========================================================
        // REPORT COUNT
        // ========================================================

        TextView reports =
                new TextView(this);


        reports.setText(
                winner.getReportCount()
                        + " REPORTS"
        );


        reports.setTextColor(
                GREY
        );


        reports.setTextSize(
                7
        );


        reports.setGravity(
                Gravity.CENTER
        );


        // ========================================================
        // ADD TO CARD
        // ========================================================

        card.addView(
                rank
        );


        card.addView(
                name
        );


        card.addView(
                reports
        );


        // ========================================================
        // ADD TO CONTAINER
        // ========================================================

        contestContainer.addView(
                card
        );
    }


    // ============================================================
    // DATE FORMAT
    // ============================================================

    private String formatDate(
            String value
    ) {

        if (value == null
                || value.isEmpty()) {

            return "DATE UNKNOWN";
        }


        try {

            String datePart =
                    value.substring(
                            0,
                            10
                    );


            String timePart =
                    value.substring(
                            11,
                            16
                    );


            return datePart
                    + " • "
                    + timePart;

        } catch (Exception e) {

            return value;
        }
    }


    // ============================================================
    // DP
    // ============================================================

    private int dp(int value) {

        return (int) (
                value
                        * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }
}