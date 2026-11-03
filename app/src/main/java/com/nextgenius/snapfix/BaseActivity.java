package com.nextgenius.snapfix;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public abstract class BaseActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;
    protected FrameLayout contentContainer;
    protected NavigationView navigationView;

    // =============================================================
    // COLORS
    // =============================================================

    private static final int BACKGROUND_COLOR =
            Color.rgb(11, 18, 32);

    private static final int CYAN_COLOR =
            Color.rgb(0, 217, 255);

    private static final int WHITE_COLOR =
            Color.rgb(255, 255, 255);

    private static final int SECONDARY_TEXT_COLOR =
            Color.rgb(127, 138, 154);

    private static final int NAV_TEXT_COLOR =
            Color.rgb(215, 222, 232);


    // =============================================================
    // ON CREATE
    // =============================================================

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    // =============================================================
    // SET ACTIVITY CONTENT
    //
    // Child Activities should use:
    //
    // setContentLayout(R.layout.activity_main);
    //
    // instead of:
    //
    // setContentView(R.layout.activity_main);
    // =============================================================

    protected void setContentLayout(@LayoutRes int layoutResID) {

        // =========================================================
        // DRAWER LAYOUT
        // =========================================================

        drawerLayout = new DrawerLayout(this);

        DrawerLayout.LayoutParams rootParams =
                new DrawerLayout.LayoutParams(
                        DrawerLayout.LayoutParams.MATCH_PARENT,
                        DrawerLayout.LayoutParams.MATCH_PARENT
                );

        drawerLayout.setLayoutParams(rootParams);

        drawerLayout.setBackgroundColor(
                BACKGROUND_COLOR
        );


        // =========================================================
        // MAIN CONTENT CONTAINER
        // =========================================================

        contentContainer = new FrameLayout(this);

        DrawerLayout.LayoutParams contentParams =
                new DrawerLayout.LayoutParams(
                        DrawerLayout.LayoutParams.MATCH_PARENT,
                        DrawerLayout.LayoutParams.MATCH_PARENT
                );

        contentContainer.setLayoutParams(
                contentParams
        );


        // =========================================================
        // INFLATE EXISTING ACTIVITY XML
        // =========================================================

        getLayoutInflater().inflate(
                layoutResID,
                contentContainer,
                true
        );


        // Add main content to DrawerLayout
        drawerLayout.addView(
                contentContainer
        );


        // =========================================================
        // HAMBURGER BUTTON
        //
        // IMPORTANT:
        //
        // The hamburger is added to contentContainer.
        //
        // It is NOT added directly to DrawerLayout.
        //
        // This prevents:
        //
        // "Child drawer has absolute gravity LEFT but this
        // DrawerLayout already has a drawer view along that edge"
        // =========================================================

        ImageButton hamburgerButton =
                new ImageButton(this);

        FrameLayout.LayoutParams hamburgerParams =
                new FrameLayout.LayoutParams(
                        dpToPx(44),
                        dpToPx(44)
                );

        hamburgerParams.gravity =
                Gravity.START | Gravity.TOP;

        hamburgerParams.leftMargin =
                dpToPx(2);

        hamburgerParams.topMargin =
                dpToPx(4);

        hamburgerButton.setLayoutParams(
                hamburgerParams
        );

        hamburgerButton.setBackgroundColor(
                Color.TRANSPARENT
        );

        hamburgerButton.setPadding(
                dpToPx(9),
                dpToPx(9),
                dpToPx(9),
                dpToPx(9)
        );

        hamburgerButton.setContentDescription(
                "Open navigation menu"
        );


        // =========================================================
        // HAMBURGER ICON
        // =========================================================

        hamburgerButton.setImageResource(
                android.R.drawable.ic_menu_sort_by_size
        );

        hamburgerButton.setColorFilter(
                CYAN_COLOR
        );


        // =========================================================
        // HAMBURGER CLICK
        // =========================================================

        hamburgerButton.setOnClickListener(view -> {

            if (drawerLayout.isDrawerOpen(
                    GravityCompat.START
            )) {

                drawerLayout.closeDrawer(
                        GravityCompat.START
                );

            } else {

                drawerLayout.openDrawer(
                        GravityCompat.START
                );
            }
        });


        // =========================================================
        // ADD HAMBURGER TO MAIN CONTENT
        // =========================================================

        contentContainer.addView(
                hamburgerButton
        );


        // =========================================================
        // NAVIGATION VIEW
        // =========================================================

        navigationView =
                new NavigationView(this);

        navigationView.setBackgroundColor(
                BACKGROUND_COLOR
        );


        // =========================================================
        // DRAWER WIDTH
        // =========================================================

        DrawerLayout.LayoutParams navigationParams =
                new DrawerLayout.LayoutParams(
                        dpToPx(285),
                        DrawerLayout.LayoutParams.MATCH_PARENT
                );


        // IMPORTANT:
        // Use GravityCompat.START for DrawerLayout.
        navigationParams.gravity =
                GravityCompat.START;


        navigationView.setLayoutParams(
                navigationParams
        );


        // =========================================================
        // DRAWER HEADER
        // =========================================================

        android.view.View headerView =
                getLayoutInflater().inflate(
                        R.layout.drawer_header,
                        navigationView,
                        false
                );

        navigationView.addHeaderView(
                headerView
        );


        // =========================================================
        // MENU
        // =========================================================

        navigationView.inflateMenu(
                R.menu.navigation_menu
        );


        // =========================================================
        // MENU TEXT COLOR
        // =========================================================

        navigationView.setItemTextColor(
                createNavigationTextColor()
        );


        // =========================================================
        // MENU ICON COLOR
        // =========================================================

        navigationView.setItemIconTintList(
                createNavigationIconColor()
        );


        // =========================================================
        // MENU ITEM BACKGROUND
        // =========================================================

        navigationView.setItemBackground(
                getDrawable(
                        R.drawable.bg_navigation_item
                )
        );


        // =========================================================
        // DRAWER PADDING
        // =========================================================

        navigationView.setPadding(
                0,
                dpToPx(10),
                0,
                dpToPx(10)
        );


        // =========================================================
        // MENU CLICK LISTENER
        // =========================================================

        navigationView.setNavigationItemSelectedListener(
                item -> {

                    int itemId =
                            item.getItemId();


                    // =================================================
                    // ANALYTICAL DASHBOARD
                    // =================================================

                    if (itemId ==
                            R.id.navAnalyticalDashboard) {

                        openActivity(
                                AnalyticalDashboardActivity.class
                        );

                    }


                    // =================================================
                    // MONTHLY CONTEST
                    // =================================================

                    else if (itemId ==
                            R.id.navMonthlyContest) {

                        openActivity(
                                MonthlyContestActivity.class
                        );

                    }
                    else if (item.getItemId() == R.id.navLogout) {

                        FirebaseAuth.getInstance().signOut();

                        Intent intent = new Intent(
                                this,
                                LoginActivity.class
                        );

                        intent.setFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK |
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                        );

                        startActivity(intent);

                        finish();

                        return true;
                    }


                    // =================================================
                    // NON-VALIDATED ISSUES
                    // =================================================

                    else if (itemId ==
                            R.id.navNonValidatedIssues) {

                        openActivity(
                                NonValidatedIssuesActivity.class
                        );
                    }


                    // =================================================
                    // CLOSE DRAWER
                    // =================================================

                    drawerLayout.closeDrawer(
                            GravityCompat.START
                    );

                    return true;
                }
        );


        // =========================================================
        // ADD NAVIGATION VIEW
        //
        // This is the ONLY drawer child using START gravity.
        // =========================================================

        drawerLayout.addView(
                navigationView
        );


        // =========================================================
        // SET FINAL CONTENT VIEW
        // =========================================================

        super.setContentView(
                drawerLayout
        );
    }


    // =============================================================
    // OPEN ACTIVITY
    // =============================================================

    private void openActivity(
            Class<?> activityClass
    ) {

        Intent intent =
                new Intent(
                        BaseActivity.this,
                        activityClass
                );

        startActivity(intent);
    }


    // =============================================================
    // NAVIGATION TEXT COLORS
    // =============================================================

    private ColorStateList createNavigationTextColor() {

        int[][] states = new int[][]{

                // Selected
                new int[]{
                        android.R.attr.state_checked
                },

                // Normal
                new int[]{}
        };


        int[] colors = new int[]{

                // Selected
                CYAN_COLOR,

                // Normal
                NAV_TEXT_COLOR
        };


        return new ColorStateList(
                states,
                colors
        );
    }


    // =============================================================
    // NAVIGATION ICON COLORS
    // =============================================================

    private ColorStateList createNavigationIconColor() {

        int[][] states = new int[][]{

                // Selected
                new int[]{
                        android.R.attr.state_checked
                },

                // Normal
                new int[]{}
        };


        int[] colors = new int[]{

                // Selected
                CYAN_COLOR,

                // Normal
                SECONDARY_TEXT_COLOR
        };


        return new ColorStateList(
                states,
                colors
        );
    }


    // =============================================================
    // DP TO PX
    // =============================================================

    protected int dpToPx(int dp) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;

        return Math.round(
                dp * density
        );
    }
}