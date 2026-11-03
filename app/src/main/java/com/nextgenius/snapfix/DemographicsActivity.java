package com.nextgenius.snapfix;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nextgenius.snapfix.Model.DemographicData;
import com.nextgenius.snapfix.Model.ReportIdResponse;
import com.nextgenius.snapfix.api.ReportIdRetrofitClient;
import com.nextgenius.snapfix.Model.HotspotData;
import com.nextgenius.snapfix.Model.PopulationDensity;
import com.nextgenius.snapfix.Model.PriorityData;
import com.nextgenius.snapfix.Model.PriorityItem;
import com.nextgenius.snapfix.Model.PriorityResponse;
import com.nextgenius.snapfix.Model.ReportIdResponse;
import com.nextgenius.snapfix.api.DemographicApi;
import com.nextgenius.snapfix.api.DemographicRetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DemographicsActivity extends BaseActivity {

    /*
     * ============================================================
     * CONSTANTS
     * ============================================================
     */

    private static final String CITY = "Bhubaneswar";

    private static final int ACTIVE_COLOR =
            Color.rgb(0, 217, 255);

    private static final int INACTIVE_COLOR =
            Color.rgb(119, 119, 119);

    private static final int WHITE =
            Color.rgb(255, 255, 255);

    private static final int MUTED =
            Color.rgb(135, 146, 162);

    private static final int HIGH_COLOR =
            Color.rgb(255, 90, 102);

    private static final int MEDIUM_COLOR =
            Color.rgb(250, 204, 21);

    private static final int LOW_COLOR =
            Color.rgb(34, 197, 94);


    /*
     * ============================================================
     * NAVIGATION
     * ============================================================
     */

    private LinearLayout navMap;
    private LinearLayout navDemographics;
    private LinearLayout navSolutions;

    private ImageView iconMap;
    private ImageView iconDemographics;
    private ImageView iconSolutions;

    private TextView textMap;
    private TextView textDemographics;
    private TextView textSolutions;
    private TextView tvIssueId;


    /*
     * ============================================================
     * DEMOGRAPHIC UI
     * ============================================================
     */

    private TextView cityText;
    private TextView totalIssuesText;
    private TextView populationText;
    private TextView highRiskText;
    private TextView recordsText;
    private TextView insightText;

    private LinearLayout issueContainer;


    /*
     * ============================================================
     * FILTERS
     * ============================================================
     */

    private TextView filterAll;
    private TextView filterHigh;
    private TextView filterMedium;


    /*
     * ============================================================
     * API
     * ============================================================
     */

    private DemographicApi api;


    /*
     * ============================================================
     * DATA
     * ============================================================
     */

    private final List<PriorityItem> allItems =
            new ArrayList<>();


    /*
     * Current filter.
     *
     * ALL
     * HIGH
     * MEDIUM
     */

    private String currentFilter = "ALL";


    /*
     * ============================================================
     * ON CREATE
     * ============================================================
     */

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);


        /*
         * Load XML.
         */

        setContentLayout(R.layout.activity_demographics);


        /*
         * Initialize views.
         */

        initializeViews();


        /*
         * Setup bottom navigation.
         */

        setupNavigation();


        /*
         * Demographics active.
         */

        setActiveTab();


        /*
         * Setup filters.
         */

        setupFilters();


        /*
         * Retrofit API.
         */

        api =
                DemographicRetrofitClient.getApi();


        /*
         * Load actual data.
         */

        loadPriorities();
    }


    /*
     * ============================================================
     * INITIALIZE VIEWS
     * ============================================================
     */

    private void initializeViews() {

        /*
         * Navigation.
         */

        navMap =
                findViewById(
                        R.id.navMap
                );

        navDemographics =
                findViewById(
                        R.id.navDemographics
                );

        navSolutions =
                findViewById(
                        R.id.navSolutions
                );


        iconMap =
                findViewById(
                        R.id.iconMap
                );

        iconDemographics =
                findViewById(
                        R.id.iconDemographics
                );

        iconSolutions =
                findViewById(
                        R.id.iconSolutions
                );


        textMap =
                findViewById(
                        R.id.textMap
                );

        textDemographics =
                findViewById(
                        R.id.textDemographics
                );

        textSolutions =
                findViewById(
                        R.id.textSolutions
                );


        /*
         * Content.
         */

        cityText =
                findViewById(
                        R.id.cityText
                );

        totalIssuesText =
                findViewById(
                        R.id.totalIssuesText
                );

        populationText =
                findViewById(
                        R.id.populationText
                );

        highRiskText =
                findViewById(
                        R.id.highRiskText
                );

        recordsText =
                findViewById(
                        R.id.recordsText
                );

        insightText =
                findViewById(
                        R.id.insightText
                );

        issueContainer =
                findViewById(
                        R.id.issueContainer
                );


        /*
         * Filters.
         */

        filterAll =
                findViewById(
                        R.id.filterAll
                );

        filterHigh =
                findViewById(
                        R.id.filterHigh
                );

        filterMedium =
                findViewById(
                        R.id.filterMedium
                );
    }


    /*
     * ============================================================
     * API CALL
     * ============================================================
     */

    private void loadPriorities() {

        /*
         * Show loading state.
         */

        totalIssuesText.setText("--");

        populationText.setText("--");

        highRiskText.setText("--");

        recordsText.setText(
                "Loading..."
        );

        insightText.setText(
                "Loading demographic analysis..."
        );


        /*
         * Clear old cards.
         */

        issueContainer.removeAllViews();


        /*
         * ========================================================
         * GET /priorities?city=Bhubaneswar
         * ========================================================
         */

        api.getPriorities(
                CITY
        ).enqueue(
                new Callback<PriorityResponse>() {

                    @Override
                    public void onResponse(
                            Call<PriorityResponse> call,
                            Response<PriorityResponse> response
                    ) {

                        /*
                         * HTTP failure.
                         */

                        if (!response.isSuccessful()
                                || response.body() == null) {

                            showError(
                                    "Failed to load demographic data. HTTP "
                                            + response.code()
                            );

                            return;
                        }


                        PriorityResponse data =
                                response.body();


                        /*
                         * API success flag.
                         */

                        if (!data.isSuccess()) {

                            showError(
                                    "Demographic API returned an error."
                            );

                            return;
                        }


                        /*
                         * Data validation.
                         */

                        if (data.getData() == null
                                || data.getData().isEmpty()) {

                            showError(
                                    "No demographic priority data found."
                            );

                            totalIssuesText.setText("0");

                            recordsText.setText(
                                    "0 detected"
                            );

                            insightText.setText(
                                    "No priority issues are currently available."
                            );

                            return;
                        }


                        /*
                         * Store data.
                         */

                        allItems.clear();

                        allItems.addAll(
                                data.getData()
                        );


                        /*
                         * Update city.
                         */

                        if (data.getCity() != null) {

                            cityText.setText(
                                    data.getCity()
                                            + " • Civic Intelligence"
                            );
                        }


                        /*
                         * Update summary.
                         */

                        updateSummary(
                                data
                        );


                        /*
                         * Create cards.
                         */

                        displayIssues();


                        /*
                         * Generate insight from actual data.
                         */

                        updateInsight();


                        /*
                         * Log.
                         */

                        android.util.Log.d(
                                "DEMOGRAPHICS",
                                "Loaded "
                                        + allItems.size()
                                        + " priority records"
                        );
                    }


                    @Override
                    public void onFailure(
                            Call<PriorityResponse> call,
                            Throwable t
                    ) {

                        android.util.Log.e(
                                "DEMOGRAPHICS",
                                "API ERROR",
                                t
                        );


                        showError(
                                "Network error: "
                                        + t.getMessage()
                        );
                    }
                }
        );
    }


    /*
     * ============================================================
     * SUMMARY
     * ============================================================
     */

    private void updateSummary(
            PriorityResponse response
    ) {

        int totalRecords =
                response.getTotalRecords();


        /*
         * Total issues.
         */

        totalIssuesText.setText(
                String.valueOf(
                        totalRecords
                )
        );


        recordsText.setText(
                totalRecords
                        + " detected"
        );


        /*
         * Count priorities.
         */

        int highCount = 0;

        int mediumCount = 0;

        int lowCount = 0;


        /*
         * Population.
         */

        double totalPopulation = 0;


        for (PriorityItem item :
                allItems) {

            /*
             * Priority.
             */

            String level =
                    item.getPriorityLevel();


            if (level != null) {

                if (level.equalsIgnoreCase("High")) {

                    highCount++;

                } else if (
                        level.equalsIgnoreCase("Medium")
                                || level.equalsIgnoreCase("Moderate")
                ) {

                    mediumCount++;

                } else if (
                        level.equalsIgnoreCase("Low")
                ) {

                    lowCount++;
                }
            }


            /*
             * Population.
             */

            PriorityData priorityData =
                    item.getPriorityData();


            if (priorityData == null) {
                continue;
            }


            DemographicData demographicData =
                    priorityData.getDemographicData();


            if (demographicData == null) {
                continue;
            }


            PopulationDensity density =
                    demographicData.getPopulationDensity();


            if (density == null) {
                continue;
            }


            if (density.getMetadata() == null) {
                continue;
            }


            totalPopulation +=
                    density
                            .getMetadata()
                            .getTotalPopulation();
        }


        /*
         * High-risk count.
         */

        highRiskText.setText(
                String.valueOf(
                        highCount
                )
        );


        /*
         * Population.
         */

        populationText.setText(
                formatPopulation(
                        totalPopulation
                )
        );
    }


    /*
     * ============================================================
     * DISPLAY ISSUES
     * ============================================================
     */

    private void displayIssues() {

        issueContainer.removeAllViews();


        for (PriorityItem item :
                allItems) {

            /*
             * Apply filter.
             */

            if (!matchesCurrentFilter(item)) {

                continue;
            }


            /*
             * Create card.
             */

            View card =
                    createIssueCard(
                            item
                    );


            issueContainer.addView(
                    card
            );
        }


        /*
         * No results for filter.
         */

        if (issueContainer.getChildCount() == 0) {

            TextView empty =
                    new TextView(this);

            empty.setText(
                    "No issues found for this filter."
            );

            empty.setTextColor(
                    MUTED
            );

            empty.setTextSize(
                    11
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


            issueContainer.addView(
                    empty
            );
        }
    }


    /*
     * ============================================================
     * FILTER
     * ============================================================
     */

    private boolean matchesCurrentFilter(
            PriorityItem item
    ) {

        if (currentFilter.equals("ALL")) {

            return true;
        }


        String level =
                item.getPriorityLevel();


        if (level == null) {

            return false;
        }


        if (currentFilter.equals("HIGH")) {

            return level.equalsIgnoreCase(
                    "High"
            );
        }


        if (currentFilter.equals("MEDIUM")) {

            return level.equalsIgnoreCase(
                    "Medium"
            )
                    || level.equalsIgnoreCase(
                    "Moderate"
            );
        }


        return true;
    }


    /*
     * ============================================================
     * ISSUE CARD
     * ============================================================
     */

    private View createIssueCard(
            PriorityItem item
    ) {

        /*
         * Outer card.
         */

        LinearLayout card =
                new LinearLayout(this);


        card.setOrientation(
                LinearLayout.VERTICAL
        );


        card.setPadding(
                dp(12),
                dp(12),
                dp(12),
                dp(12)
        );


        card.setBackgroundResource(
                R.drawable.bg_demo_card
        );


        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );


        cardParams.setMargins(
                0,
                dp(8),
                0,
                0
        );


        card.setLayoutParams(
                cardParams
        );


        /*
         * ========================================================
         * TOP ROW
         * ========================================================
         */

        LinearLayout topRow =
                new LinearLayout(this);


        topRow.setOrientation(
                LinearLayout.HORIZONTAL
        );


        topRow.setGravity(
                Gravity.CENTER_VERTICAL
        );


        /*
         * Priority badge.
         */

        TextView badge =
                new TextView(this);


        String level =
                item.getPriorityLevel();


        if (level == null) {

            level = "Unknown";
        }


        badge.setText(
                level.toUpperCase(
                        Locale.US
                )
        );


        badge.setTextSize(
                8
        );


        badge.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );


        badge.setGravity(
                Gravity.CENTER
        );


        badge.setPadding(
                dp(8),
                0,
                dp(8),
                0
        );


        int priorityColor =
                getPriorityColor(
                        level
                );


        badge.setTextColor(
                priorityColor
        );


        badge.setBackgroundResource(
                getPriorityBackground(
                        level
                )
        );


        LinearLayout.LayoutParams badgeParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        dp(24)
                );


        topRow.addView(
                badge,
                badgeParams
        );


        /*
         * Issue category.
         */

        TextView category =
                new TextView(this);


        String categoryText =
                "Issue";


        PriorityData priorityData =
                item.getPriorityData();


        if (priorityData != null
                && priorityData.getIssueCategory() != null) {

            categoryText =
                    priorityData.getIssueCategory();
        }


        category.setText(
                categoryText
        );


        category.setTextColor(
                WHITE
        );


        category.setTextSize(
                13
        );


        category.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );


        category.setMaxLines(
                2
        );


        LinearLayout.LayoutParams categoryParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );


        categoryParams.setMargins(
                dp(8),
                0,
                dp(8),
                0
        );


        topRow.addView(
                category,
                categoryParams
        );


        /*
         * Priority score.
         */

        TextView score =
                new TextView(this);


        score.setText(
                formatNumber(
                        item.getPriorityScore()
                )
        );


        score.setTextColor(
                priorityColor
        );


        score.setTextSize(
                10
        );


        score.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );


        topRow.addView(
                score
        );


        card.addView(
                topRow
        );


        /*
         * ========================================================
         * REASONING
         * ========================================================
         */

        if (priorityData != null
                && priorityData.getReasoning() != null
                && !priorityData.getReasoning().isEmpty()) {

            TextView reasoning =
                    new TextView(this);


            reasoning.setText(
                    priorityData.getReasoning()
            );


            reasoning.setTextColor(
                    MUTED
            );


            reasoning.setTextSize(
                    10
            );


            reasoning.setMaxLines(
                    4
            );


            reasoning.setEllipsize(
                    android.text.TextUtils.TruncateAt.END
            );


            LinearLayout.LayoutParams reasoningParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );


            reasoningParams.setMargins(
                    0,
                    dp(7),
                    0,
                    0
            );


            card.addView(
                    reasoning,
                    reasoningParams
            );
        }


        /*
         * ========================================================
         * WARD / LOCATION
         * ========================================================
         */

        if (priorityData != null) {

            TextView location =
                    new TextView(this);


            String locationText =
                    "Ward "
                            + priorityData.getWardNumber();


            if (priorityData.getCity() != null) {

                locationText +=
                        " • "
                                + getShortCity(
                                priorityData.getCity()
                        );
            }


            location.setText(
                    "📍 " + locationText
            );


            location.setTextColor(
                    Color.rgb(
                            101,
                            112,
                            128
                    )
            );


            location.setTextSize(
                    9
            );


            LinearLayout.LayoutParams locationParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );


            locationParams.setMargins(
                    0,
                    dp(8),
                    0,
                    0
            );


            card.addView(
                    location,
                    locationParams
            );
        }


        /*
         * ========================================================
         * STATISTICS
         * ========================================================
         */

        LinearLayout stats =
                new LinearLayout(this);


        stats.setOrientation(
                LinearLayout.HORIZONTAL
        );


        LinearLayout.LayoutParams statsParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );


        statsParams.setMargins(
                0,
                dp(10),
                0,
                0
        );


        /*
         * Issue count.
         */

        String issueCount =
                priorityData != null
                        ? String.valueOf(
                        priorityData.getIssueCount()
                )
                        : "N/A";


        /*
         * Population.
         */

        String population =
                getPopulation(
                        priorityData
                );


        /*
         * Density.
         */

        String density =
                getDensity(
                        priorityData
                );


        /*
         * Add stat texts.
         */

        TextView issueStat =
                createStatText(
                        "LOCAL REPORTS  "
                                + issueCount
                );


        TextView populationStat =
                createStatText(
                        "POPULATION  "
                                + population
                );


        TextView densityStat =
                createStatText(
                        "DENSITY  "
                                + density
                );


        stats.addView(
                issueStat,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );


        stats.addView(
                populationStat,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );


        stats.addView(
                densityStat,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );


        card.addView(
                stats,
                statsParams
        );


        /*
         * ========================================================
         * HOTSPOT
         * ========================================================
         */

        if (priorityData != null
                && priorityData.getHotspot() != null) {

            HotspotData hotspot =
                    priorityData.getHotspot();


            TextView hotspotText =
                    new TextView(this);


            hotspotText.setText(
                    "HOTSPOT  "
                            + formatNumber(
                            hotspot.getHotspotScore()
                    )
                            + "  •  "
                            + hotspot.getReportCount()
                            + " reports"
            );


            hotspotText.setTextColor(
                    Color.rgb(
                            151,
                            161,
                            174
                    )
            );


            hotspotText.setTextSize(
                    9
            );


            LinearLayout.LayoutParams hotspotParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );


            hotspotParams.setMargins(
                    0,
                    dp(8),
                    0,
                    0
            );


            card.addView(
                    hotspotText,
                    hotspotParams
            );
        }


        /*
         * ========================================================
         * KEY FACTORS
         * ========================================================
         */

        if (priorityData != null
                && priorityData.getKeyFactors() != null
                && priorityData.getKeyFactors().length > 0) {

            TextView factors =
                    new TextView(this);


            StringBuilder builder =
                    new StringBuilder();


            builder.append(
                    "Factors: "
            );


            String[] keyFactors =
                    priorityData.getKeyFactors();


            for (int i = 0;
                 i < keyFactors.length;
                 i++) {

                builder.append(
                        keyFactors[i]
                );


                if (i < keyFactors.length - 1) {

                    builder.append(
                            " • "
                    );
                }
            }


            factors.setText(
                    builder.toString()
            );


            factors.setTextColor(
                    Color.rgb(
                            103,
                            114,
                            130
                    )
            );


            factors.setTextSize(
                    9
            );


            factors.setMaxLines(
                    2
            );


            factors.setEllipsize(
                    android.text.TextUtils.TruncateAt.END
            );


            LinearLayout.LayoutParams factorParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );


            factorParams.setMargins(
                    0,
                    dp(7),
                    0,
                    0
            );


            card.addView(
                    factors,
                    factorParams
            );
        }


        /*
         * ========================================================
         * CARD CLICK
         * ========================================================
         */

        card.setClickable(true);
        card.setFocusable(true);

        card.setOnClickListener(
                v -> showDemographicPopup(item)
        );

        return card;
    }

    private void showDemographicPopup(PriorityItem item) {

        final android.app.Dialog dialog =
                new android.app.Dialog(this);

        dialog.setContentView(
                R.layout.popup_issue_details
        );

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent
            );
        }

        // =========================================================
        // VIEWS
        // =========================================================

        TextView tvCategory =
                dialog.findViewById(R.id.tvPopupCategory);

        TextView tvPriority =
                dialog.findViewById(R.id.tvPopupPriority);

        TextView tvLocation =
                dialog.findViewById(R.id.tvPopupLocation);

        TextView tvWard =
                dialog.findViewById(R.id.tvPopupWard);

        TextView tvScore =
                dialog.findViewById(R.id.tvPopupScore);

        TextView tvReports =
                dialog.findViewById(R.id.tvPopupReports);

        TextView tvPopulation =
                dialog.findViewById(R.id.tvPopupPopulation);
        TextView tvIssueId =
                dialog.findViewById(
                        R.id.tvPopupIssueId
                );

        TextView tvDensity =
                dialog.findViewById(R.id.tvPopupDensity);

        TextView tvHotspot =
                dialog.findViewById(R.id.tvPopupHotspot);

        TextView tvReasoning =
                dialog.findViewById(R.id.tvPopupReasoning);

        TextView tvFactors =
                dialog.findViewById(R.id.tvPopupFactors);

        TextView btnIssueDetails =
                dialog.findViewById(R.id.btnIssueDetails);


        // =========================================================
        // GET PRIORITY DATA
        // =========================================================

        PriorityData data =
                item.getPriorityData();


        // =========================================================
        // CATEGORY
        // =========================================================

        String category = "Unknown";

        if (data != null
                && data.getIssueCategory() != null
                && !data.getIssueCategory().trim().isEmpty()) {

            category =
                    data.getIssueCategory();
        }

        tvCategory.setText(category);


        // =========================================================
        // PRIORITY
        // =========================================================

        String priority =
                item.getPriorityLevel();

        if (priority == null
                || priority.trim().isEmpty()) {

            priority = "Unknown";
        }

        tvPriority.setText(
                priority.toUpperCase(Locale.US)
        );

        tvPriority.setTextColor(
                getPriorityColor(priority)
        );


        // =========================================================
        // CITY
        // =========================================================

        String city = CITY;

        if (data != null
                && data.getCity() != null
                && !data.getCity().trim().isEmpty()) {

            city = data.getCity();
        }

        tvLocation.setText(
                "📍 " + getShortCity(city)
        );


        // =========================================================
        // WARD
        // =========================================================

        if (data != null) {

            tvWard.setText(
                    "WARD " +
                            data.getWardNumber()
            );

        } else {

            tvWard.setText(
                    "WARD N/A"
            );
        }
        tvIssueId.setText(
                String.valueOf(
                        item.getIssueId()
                )
        );


        // =========================================================
        // PRIORITY SCORE
        // =========================================================

        tvScore.setText(
                "PRIORITY SCORE  " +
                        formatNumber(
                                item.getPriorityScore()
                        )
        );


        // =========================================================
        // REPORT COUNT
        // =========================================================

        if (data != null) {

            tvReports.setText(
                    "LOCAL REPORTS  " +
                            data.getIssueCount()
            );

        } else {

            tvReports.setText(
                    "LOCAL REPORTS  N/A"
            );
        }


        // =========================================================
        // POPULATION
        // =========================================================

        tvPopulation.setText(
                "POPULATION  " +
                        getPopulation(data)
        );


        // =========================================================
        // POPULATION DENSITY
        // =========================================================

        tvDensity.setText(
                "DENSITY  " +
                        getDensity(data)
        );


        // =========================================================
        // HOTSPOT
        // =========================================================

        if (data != null
                && data.getHotspot() != null) {

            HotspotData hotspot =
                    data.getHotspot();

            tvHotspot.setText(
                    "HOTSPOT  " +
                            formatNumber(
                                    hotspot.getHotspotScore()
                            )
                            +
                            " • "
                            +
                            hotspot.getReportCount()
                            +
                            " reports"
            );

        } else {

            tvHotspot.setText(
                    "HOTSPOT  N/A"
            );
        }


        // =========================================================
        // REASONING
        // =========================================================

        if (data != null
                && data.getReasoning() != null
                && !data.getReasoning().trim().isEmpty()) {

            tvReasoning.setText(
                    data.getReasoning()
            );

        } else {

            tvReasoning.setText(
                    "Demographic analysis is available "
                            + "for this priority."
            );
        }


        // =========================================================
        // KEY FACTORS
        // =========================================================

        if (data != null
                && data.getKeyFactors() != null
                && data.getKeyFactors().length > 0) {

            StringBuilder factors =
                    new StringBuilder();

            String[] keyFactors =
                    data.getKeyFactors();

            for (int i = 0;
                 i < keyFactors.length;
                 i++) {

                if (keyFactors[i] == null) {
                    continue;
                }

                if (factors.length() > 0) {
                    factors.append(" • ");
                }

                factors.append(
                        keyFactors[i]
                );
            }

            tvFactors.setText(
                    factors.toString()
            );

        } else {

            tvFactors.setText(
                    "No additional factors available."
            );
        }


        // =========================================================
        // VIEW ISSUE DETAILS BUTTON
        // =========================================================

        btnIssueDetails.setOnClickListener(
                v -> {

                    int issueId =
                            item.getIssueId();

                    if (issueId <= 0) {

                        Toast.makeText(
                                DemographicsActivity.this,
                                "Issue ID not available",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    ReportIdRetrofitClient
                            .getApi()
                            .getReportId(issueId)
                            .enqueue(
                                    new Callback<ReportIdResponse>() {

                                        @Override
                                        public void onResponse(
                                                Call<ReportIdResponse> call,
                                                Response<ReportIdResponse> response) {

                                            if (!response.isSuccessful()
                                                    || response.body() == null) {

                                                Toast.makeText(
                                                        DemographicsActivity.this,
                                                        "Unable to find report ID",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                                return;
                                            }

                                            int reportId =
                                                    response.body()
                                                            .getReportId();

                                            Intent intent =
                                                    new Intent(
                                                            DemographicsActivity.this,
                                                            CategoryIssueDetailsActivity.class
                                                    );

                                            intent.putExtra(
                                                    "report_id",
                                                    reportId
                                            );

                                            startActivity(intent);

                                            dialog.dismiss();
                                        }

                                        @Override
                                        public void onFailure(
                                                Call<ReportIdResponse> call,
                                                Throwable t) {

                                            Toast.makeText(
                                                    DemographicsActivity.this,
                                                    "Network error",
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        }
                                    }
                            );
                }
        );

        // =========================================================
        // SHOW POPUP
        // =========================================================

        dialog.show();


        if (dialog.getWindow() != null) {

            int width =
                    (int) (
                            getResources()
                                    .getDisplayMetrics()
                                    .widthPixels
                                    * 0.92
                    );

            dialog.getWindow().setLayout(
                    width,
                    android.view.WindowManager
                            .LayoutParams
                            .WRAP_CONTENT
            );
        }
    }


    /*
     * ============================================================
     * CREATE STAT TEXT
     * ============================================================
     */

    private TextView createStatText(
            String text
    ) {

        TextView view =
                new TextView(this);


        view.setText(
                text
        );


        view.setTextColor(
                Color.rgb(
                        180,
                        189,
                        201
                )
        );


        view.setTextSize(
                8
        );


        view.setMaxLines(
                2
        );


        return view;
    }


    /*
     * ============================================================
     * PRIORITY COLOR
     * ============================================================
     */

    private int getPriorityColor(
            String level
    ) {

        if (level == null) {

            return MUTED;
        }


        if (level.equalsIgnoreCase("High")) {

            return HIGH_COLOR;
        }


        if (level.equalsIgnoreCase("Medium")
                || level.equalsIgnoreCase("Moderate")) {

            return MEDIUM_COLOR;
        }


        if (level.equalsIgnoreCase("Low")) {

            return LOW_COLOR;
        }


        return MUTED;
    }


    /*
     * ============================================================
     * PRIORITY BACKGROUND
     * ============================================================
     */

    private int getPriorityBackground(
            String level
    ) {

        if (level == null) {

            return R.drawable.bg_demo_card;
        }


        if (level.equalsIgnoreCase("High")) {

            return R.drawable.bg_high_badge;
        }


        if (level.equalsIgnoreCase("Medium")
                || level.equalsIgnoreCase("Moderate")) {

            return R.drawable.bg_moderate_badge;
        }


        if (level.equalsIgnoreCase("Low")) {

            return R.drawable.bg_low_badge;
        }


        return R.drawable.bg_demo_card;
    }


    /*
     * ============================================================
     * POPULATION
     * ============================================================
     */

    private String getPopulation(
            PriorityData data
    ) {

        if (data == null
                || data.getDemographicData() == null
                || data.getDemographicData()
                .getPopulationDensity() == null
                || data.getDemographicData()
                .getPopulationDensity()
                .getMetadata() == null) {

            return "N/A";
        }


        double population =
                data.getDemographicData()
                        .getPopulationDensity()
                        .getMetadata()
                        .getTotalPopulation();


        return formatPopulation(
                population
        );
    }


    /*
     * ============================================================
     * DENSITY
     * ============================================================
     */

    private String getDensity(
            PriorityData data
    ) {

        if (data == null
                || data.getDemographicData() == null
                || data.getDemographicData()
                .getPopulationDensity() == null
                || data.getDemographicData()
                .getPopulationDensity()
                .getValue() == null) {

            return "N/A";
        }


        double density =
                data.getDemographicData()
                        .getPopulationDensity()
                        .getValue();


        return String.format(
                Locale.US,
                "%.0f/km²",
                density
        );
    }


    /*
     * ============================================================
     * POPULATION FORMAT
     * ============================================================
     */

    private String formatPopulation(
            double population
    ) {

        if (population >= 1_000_000) {

            return String.format(
                    Locale.US,
                    "%.1fM",
                    population / 1_000_000.0
            );
        }


        if (population >= 1_000) {

            return String.format(
                    Locale.US,
                    "%.1fK",
                    population / 1_000.0
            );
        }


        return String.format(
                Locale.US,
                "%.0f",
                population
        );
    }


    /*
     * ============================================================
     * NUMBER FORMAT
     * ============================================================
     */

    private String formatNumber(
            double value
    ) {

        if (value == (long) value) {

            return String.valueOf(
                    (long) value
            );
        }


        return String.format(
                Locale.US,
                "%.1f",
                value
        );
    }


    /*
     * ============================================================
     * CITY SHORT NAME
     * ============================================================
     */

    private String getShortCity(
            String city
    ) {

        if (city == null) {

            return "";
        }


        String[] parts =
                city.split(",");


        if (parts.length > 0) {

            return parts[0].trim();
        }


        return city;
    }


    /*
     * ============================================================
     * INSIGHT
     * ============================================================
     */

    private void updateInsight() {

        if (allItems.isEmpty()) {

            insightText.setText(
                    "No demographic insights available."
            );

            return;
        }


        PriorityItem highest =
                allItems.get(0);


        double highestScore =
                highest.getPriorityScore();


        for (PriorityItem item :
                allItems) {

            if (item.getPriorityScore()
                    > highestScore) {

                highest =
                        item;

                highestScore =
                        item.getPriorityScore();
            }
        }


        PriorityData data =
                highest.getPriorityData();


        if (data == null) {

            insightText.setText(
                    "Priority analysis is available, but detailed demographic information is currently limited."
            );

            return;
        }


        StringBuilder insight =
                new StringBuilder();


        insight.append(
                "Highest priority: "
        );


        if (data.getIssueCategory() != null) {

            insight.append(
                    data.getIssueCategory()
            );
        }


        insight.append(
                " in Ward "
        );


        insight.append(
                data.getWardNumber()
        );


        insight.append(
                " with priority score "
        );


        insight.append(
                formatNumber(
                        highestScore
                )
        );


        PopulationDensity density =
                data.getDemographicData() != null
                        ? data.getDemographicData()
                        .getPopulationDensity()
                        : null;


        if (density != null
                && density.getValue() != null) {

            insight.append(
                    ". Population density is "
            );


            insight.append(
                    String.format(
                            Locale.US,
                            "%.0f people/km²",
                            density.getValue()
                    )
            );
        }


        insightText.setText(
                insight.toString()
        );
    }


    /*
     * ============================================================
     * FILTER SETUP
     * ============================================================
     */

    private void setupFilters() {

        filterAll.setOnClickListener(
                v -> {

                    currentFilter = "ALL";

                    updateFilterColors();

                    displayIssues();
                }
        );


        filterHigh.setOnClickListener(
                v -> {

                    currentFilter = "HIGH";

                    updateFilterColors();

                    displayIssues();
                }
        );


        filterMedium.setOnClickListener(
                v -> {

                    currentFilter = "MEDIUM";

                    updateFilterColors();

                    displayIssues();
                }
        );


        updateFilterColors();
    }


    /*
     * ============================================================
     * FILTER COLORS
     * ============================================================
     */

    private void updateFilterColors() {

        filterAll.setTextColor(
                INACTIVE_COLOR
        );

        filterHigh.setTextColor(
                INACTIVE_COLOR
        );

        filterMedium.setTextColor(
                INACTIVE_COLOR
        );


        if (currentFilter.equals("ALL")) {

            filterAll.setTextColor(
                    ACTIVE_COLOR
            );

        } else if (
                currentFilter.equals("HIGH")
        ) {

            filterHigh.setTextColor(
                    ACTIVE_COLOR
            );

        } else if (
                currentFilter.equals("MEDIUM")
        ) {

            filterMedium.setTextColor(
                    ACTIVE_COLOR
            );
        }
    }


    /*
     * ============================================================
     * NAVIGATION
     * ============================================================
     */

    private void setupNavigation() {

        navMap.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    DemographicsActivity.this,
                                    MainActivity.class
                            );

                    intent.addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_SINGLE_TOP
                    );

                    startActivity(
                            intent
                    );

                    finish();
                }
        );


        navDemographics.setOnClickListener(
                v -> {
                    // Already here.
                }
        );


        navSolutions.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    DemographicsActivity.this,
                                    SolutionsActivity.class
                            );

                    intent.addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_SINGLE_TOP
                    );

                    startActivity(
                            intent
                    );

                    finish();
                }
        );
    }


    /*
     * ============================================================
     * ACTIVE TAB
     * ============================================================
     */

    private void setActiveTab() {

        textMap.setTextColor(
                INACTIVE_COLOR
        );

        textDemographics.setTextColor(
                ACTIVE_COLOR
        );

        textSolutions.setTextColor(
                INACTIVE_COLOR
        );


        iconMap.setColorFilter(
                INACTIVE_COLOR
        );

        iconDemographics.setColorFilter(
                ACTIVE_COLOR
        );

        iconSolutions.setColorFilter(
                INACTIVE_COLOR
        );
    }


    /*
     * ============================================================
     * DP CONVERSION
     * ============================================================
     */

    private int dp(
            int value
    ) {

        return (int) (
                value
                        * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }


    /*
     * ============================================================
     * ERROR
     * ============================================================
     */

    private void showError(
            String message
    ) {

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();


        recordsText.setText(
                "Unable to load"
        );


        insightText.setText(
                message
        );
    }
}