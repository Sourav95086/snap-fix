package com.nextgenius.snapfix;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nextgenius.snapfix.Model.HotspotCategory;
import com.nextgenius.snapfix.Model.HotspotResponse;
import com.nextgenius.snapfix.Model.Issue;
import com.nextgenius.snapfix.Model.IssueResponse;
import com.nextgenius.snapfix.Model.MapIssue;
import com.nextgenius.snapfix.api.HotspotApi;
import com.nextgenius.snapfix.api.RetrofitClient;

import org.maplibre.android.MapLibre;
import org.maplibre.android.WellKnownTileServer;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.android.maps.MapLibreMap;
import org.maplibre.android.maps.MapView;
import org.maplibre.android.maps.Style;
import org.maplibre.android.style.layers.CircleLayer;
import org.maplibre.android.style.layers.PropertyFactory;
import org.maplibre.android.style.sources.GeoJsonSource;

import org.maplibre.geojson.Feature;
import org.maplibre.geojson.FeatureCollection;
import org.maplibre.geojson.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends BaseActivity {


    // ============================================================
    // CONSTANTS
    // ============================================================

    private static final String TAG = "MAP_DEBUG";

    private static final String CITY = "Bhubaneswar";

    private static final int ACTIVE_COLOR =
            Color.rgb(0, 217, 255);

    private static final int INACTIVE_COLOR =
            Color.rgb(119, 119, 119);

    private static final int HIGH_COLOR =
            Color.rgb(239, 68, 68);

    private static final int MODERATE_COLOR =
            Color.rgb(250, 204, 21);

    private static final int LOW_COLOR =
            Color.rgb(34, 197, 94);


    // ============================================================
    // MAP
    // ============================================================

    private MapView mapView;

    private MapLibreMap map;


    // ============================================================
    // EXISTING API
    // ============================================================

    private HotspotApi api;


    // ============================================================
    // EXISTING MAP DATA
    // ============================================================

    private final List<MapIssue> mapIssues =
            new ArrayList<>();


    private int pendingRequests = 0;

    private boolean mapStyleLoaded = false;


    // ============================================================
    // CATEGORIES
    // ============================================================

    private final List<HotspotCategory> categories =
            new ArrayList<>();


    // ============================================================
    // UI
    // ============================================================

    private LinearLayout categoryContainer;

    private TextView categoryInfo;

    private TextView categoryCountText;

    private TextView reportCountText;

    private TextView hotspotCountText;

    private TextView activeCountText;

    private TextView cityText;


    // ============================================================
    // FILTERS
    // ============================================================

    private TextView filterAll;

    private TextView filterHotspot;

    private TextView filterModerate;

    private TextView filterLow;


    private String currentFilter = "ALL";


    // ============================================================
    // BOTTOM NAVIGATION
    // ============================================================

    private LinearLayout navMap;

    private LinearLayout navDemographics;

    private LinearLayout navSolutions;


    private ImageView iconMap;

    private ImageView iconDemographics;

    private ImageView iconSolutions;


    private TextView textMap;

    private TextView textDemographics;

    private TextView textSolutions;


    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(
                savedInstanceState
        );


        // --------------------------------------------------------
        // MapLibre initialization
        // --------------------------------------------------------

        MapLibre.getInstance(
                getApplicationContext(),
                null,
                WellKnownTileServer.MapLibre
        );


        // --------------------------------------------------------
        // Layout
        // --------------------------------------------------------

        setContentLayout(R.layout.activity_main);


        // --------------------------------------------------------
        // Find views
        // --------------------------------------------------------

        initializeViews();


        // --------------------------------------------------------
        // Bottom navigation
        // --------------------------------------------------------

        setupNavigation();

        setActiveMapTab();


        // --------------------------------------------------------
        // Filters
        // --------------------------------------------------------

        setupFilters();


        // --------------------------------------------------------
        // Retrofit
        // --------------------------------------------------------

        api =
                RetrofitClient.getApi();


        // --------------------------------------------------------
        // MapView lifecycle
        // --------------------------------------------------------

        mapView.onCreate(
                savedInstanceState
        );


        // --------------------------------------------------------
        // Map
        // --------------------------------------------------------

        mapView.getMapAsync(
                mapLibreMap -> {

                    map = mapLibreMap;


                    Log.d(
                            TAG,
                            "MAP INITIALIZED"
                    );


                    map.setStyle(
                            "https://tiles.openfreemap.org/styles/dark",
                            style -> {

                                mapStyleLoaded = true;


                                Log.d(
                                        TAG,
                                        "MAP STYLE LOADED"
                                );


                                map.setCameraPosition(
                                        new org.maplibre.android.camera
                                                .CameraPosition.Builder()
                                                .target(
                                                        new LatLng(
                                                                20.2961,
                                                                85.8245
                                                        )
                                                )
                                                .zoom(11.0)
                                                .build()
                                );


                                /*
                                 * API requests may finish
                                 * before map style.
                                 */

                                if (pendingRequests == 0
                                        && !mapIssues.isEmpty()) {

                                    displayAllIssuesOnMap();
                                }
                            }
                    );
                }
        );


        // --------------------------------------------------------
        // Load hotspot categories
        // --------------------------------------------------------

        loadCategories();
    }


    // ============================================================
    // INITIALIZE VIEWS
    // ============================================================

    private void initializeViews() {

        mapView =
                findViewById(
                        R.id.mapView
                );


        categoryContainer =
                findViewById(
                        R.id.categoryContainer
                );


        categoryInfo =
                findViewById(
                        R.id.categoryInfo
                );


        cityText =
                findViewById(
                        R.id.cityText
                );


        categoryCountText =
                findViewById(
                        R.id.categoryCountText
                );


        reportCountText =
                findViewById(
                        R.id.reportCountText
                );


        hotspotCountText =
                findViewById(
                        R.id.hotspotCountText
                );


        activeCountText =
                findViewById(
                        R.id.activeCountText
                );


        // --------------------------------------------------------
        // Filters
        // --------------------------------------------------------

        filterAll =
                findViewById(
                        R.id.filterAll
                );


        filterHotspot =
                findViewById(
                        R.id.filterHotspot
                );


        filterModerate =
                findViewById(
                        R.id.filterModerate
                );


        filterLow =
                findViewById(
                        R.id.filterLow
                );


        // --------------------------------------------------------
        // Navigation
        // --------------------------------------------------------

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
    }


    // ============================================================
    // API #1
    // ============================================================

    private void loadCategories() {

        Log.d(
                TAG,
                "GET /hotspots/" + CITY
        );


        api.getHotspots(
                CITY
        ).enqueue(
                new Callback<HotspotResponse>() {

                    @Override
                    public void onResponse(
                            Call<HotspotResponse> call,
                            Response<HotspotResponse> response
                    ) {

                        if (!response.isSuccessful()
                                || response.body() == null) {

                            showError(
                                    "Failed to load hotspot data"
                            );

                            return;
                        }


                        HotspotResponse data =
                                response.body();


                        if (!data.isSuccess()) {

                            showError(
                                    "Hotspot API returned an error"
                            );

                            return;
                        }


                        List<HotspotCategory>
                                responseCategories =
                                data.getCategories();


                        if (responseCategories == null
                                || responseCategories.isEmpty()) {

                            showError(
                                    "No hotspot categories found"
                            );

                            return;
                        }


                        // ------------------------------------------------
                        // Store categories
                        // ------------------------------------------------

                        categories.clear();

                        categories.addAll(
                                responseCategories
                        );


                        // ------------------------------------------------
                        // Update header
                        // ------------------------------------------------

                        cityText.setText(
                                CITY
                                        + " • Civic Hotspots"
                        );


                        // ------------------------------------------------
                        // Update summary
                        // ------------------------------------------------

                        updateSummary();


                        // ------------------------------------------------
                        // Display cards
                        // ------------------------------------------------

                        displayCategories();


                        // ------------------------------------------------
                        // API #2
                        // ------------------------------------------------

                        loadAllCategoryIssues(
                                categories
                        );
                    }


                    @Override
                    public void onFailure(
                            Call<HotspotResponse> call,
                            Throwable t
                    ) {

                        Log.e(
                                TAG,
                                "API #1 NETWORK ERROR",
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


    // ============================================================
    // UPDATE SUMMARY
    // ============================================================

    private void updateSummary() {

        int totalCategories =
                categories.size();


        int totalReports = 0;

        int totalHotspots = 0;


        for (HotspotCategory category :
                categories) {

            totalReports +=
                    category.getReportCount();


            String severity =
                    category.getHotspotClass();


            if (severity != null
                    && severity.equalsIgnoreCase(
                    "hotspot"
            )) {

                totalHotspots++;
            }
        }


        categoryCountText.setText(
                String.valueOf(
                        totalCategories
                )
        );


        reportCountText.setText(
                String.valueOf(
                        totalReports
                )
        );


        hotspotCountText.setText(
                String.valueOf(
                        totalHotspots
                )
        );


        activeCountText.setText(
                totalCategories
                        + " categories"
        );
    }


    // ============================================================
    // API #2 CONTROLLER
    // ============================================================

    private void loadAllCategoryIssues(
            List<HotspotCategory> categories
    ) {

        mapIssues.clear();


        pendingRequests =
                categories.size();


        for (HotspotCategory category :
                categories) {

            loadIssuesForCategory(
                    category
            );
        }
    }


    // ============================================================
    // API #2
    // ============================================================

    private void loadIssuesForCategory(
            HotspotCategory category
    ) {

        Log.d(
                TAG,
                "GET /hotspots/"
                        + CITY
                        + "/"
                        + category.getCategoryId()
                        + "/issues"
        );


        api.getIssues(
                CITY,
                category.getCategoryId()
        ).enqueue(
                new Callback<IssueResponse>() {

                    @Override
                    public void onResponse(
                            Call<IssueResponse> call,
                            Response<IssueResponse> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            List<Issue> issues =
                                    response.body()
                                            .getIssues();


                            if (issues != null) {

                                for (Issue issue :
                                        issues) {

                                    mapIssues.add(
                                            new MapIssue(
                                                    issue,
                                                    category.getHotspotClass()
                                            )
                                    );
                                }
                            }
                        }


                        categoryRequestFinished();
                    }


                    @Override
                    public void onFailure(
                            Call<IssueResponse> call,
                            Throwable t
                    ) {

                        Log.e(
                                TAG,
                                "API #2 ERROR: "
                                        + category.getCategoryName(),
                                t
                        );


                        categoryRequestFinished();
                    }
                }
        );
    }


    // ============================================================
    // REQUEST FINISHED
    // ============================================================

    private void categoryRequestFinished() {

        pendingRequests--;


        if (pendingRequests <= 0) {

            pendingRequests = 0;


            Log.d(
                    TAG,
                    "ALL ISSUE REQUESTS FINISHED"
            );


            if (mapStyleLoaded) {

                displayAllIssuesOnMap();
            }
        }
    }


    // ============================================================
    // DISPLAY MAP ISSUES
    // ============================================================

    private void displayAllIssuesOnMap() {

        if (map == null
                || !mapStyleLoaded) {

            return;
        }


        Style style =
                map.getStyle();


        if (style == null) {

            return;
        }


        removeOldMapLayers(
                style
        );


        List<Feature> hotspotFeatures =
                new ArrayList<>();


        List<Feature> moderateFeatures =
                new ArrayList<>();


        List<Feature> lowFeatures =
                new ArrayList<>();


        for (MapIssue mapIssue :
                mapIssues) {

            Issue issue =
                    mapIssue.getIssue();


            double latitude =
                    issue.getLatitude();


            double longitude =
                    issue.getLongitude();


            if (latitude == 0
                    && longitude == 0) {

                continue;
            }


            Feature feature =
                    Feature.fromGeometry(
                            Point.fromLngLat(
                                    longitude,
                                    latitude
                            )
                    );


            feature.addNumberProperty(
                    "issue_id",
                    issue.getIssueId()
            );


            feature.addNumberProperty(
                    "weight",
                    issue.getIssueWeight()
            );


            feature.addStringProperty(
                    "description",
                    issue.getIssueDescription()
            );


            String severity =
                    mapIssue.getSeverity();


            if (severity == null) {

                continue;
            }


            severity =
                    severity
                            .trim()
                            .toLowerCase();


            switch (severity) {

                case "hotspot":

                    hotspotFeatures.add(
                            feature
                    );

                    break;


                case "moderate":

                    moderateFeatures.add(
                            feature
                    );

                    break;


                case "low":

                    lowFeatures.add(
                            feature
                    );

                    break;
            }
        }


        addIssuePointLayer(
                style,
                "hotspot-source",
                "hotspot-points",
                hotspotFeatures,
                "#EF4444"
        );


        addIssuePointLayer(
                style,
                "moderate-source",
                "moderate-points",
                moderateFeatures,
                "#FACC15"
        );


        addIssuePointLayer(
                style,
                "low-source",
                "low-points",
                lowFeatures,
                "#22C55E"
        );
    }


    // ============================================================
    // ADD MAP LAYER
    // ============================================================

    private void addIssuePointLayer(
            Style style,
            String sourceId,
            String layerId,
            List<Feature> features,
            String color
    ) {

        GeoJsonSource source =
                new GeoJsonSource(
                        sourceId,
                        FeatureCollection.fromFeatures(
                                features
                        )
                );


        style.addSource(
                source
        );


        CircleLayer layer =
                new CircleLayer(
                        layerId,
                        sourceId
                );


        layer.setProperties(

                PropertyFactory.circleColor(
                        Color.parseColor(
                                color
                        )
                ),

                PropertyFactory.circleRadius(
                        5.0f
                ),

                PropertyFactory.circleOpacity(
                        0.95f
                ),

                PropertyFactory.circleStrokeColor(
                        Color.WHITE
                ),

                PropertyFactory.circleStrokeWidth(
                        1.0f
                )
        );


        style.addLayer(
                layer
        );
    }


    // ============================================================
    // REMOVE MAP LAYERS
    // ============================================================

    private void removeOldMapLayers(
            Style style
    ) {

        String[] layerIds = {

                "hotspot-points",

                "moderate-points",

                "low-points"
        };


        for (String layerId :
                layerIds) {

            if (style.getLayer(
                    layerId
            ) != null) {

                style.removeLayer(
                        layerId
                );
            }
        }


        String[] sourceIds = {

                "hotspot-source",

                "moderate-source",

                "low-source"
        };


        for (String sourceId :
                sourceIds) {

            if (style.getSource(
                    sourceId
            ) != null) {

                style.removeSource(
                        sourceId
                );
            }
        }
    }


    // ============================================================
    // CATEGORY CARDS
    // ============================================================

    private void displayCategories() {

        categoryContainer.removeAllViews();


        for (HotspotCategory category :
                categories) {

            if (!matchesFilter(
                    category
            )) {

                continue;
            }


            View card =
                    createCategoryCard(
                            category
                    );


            categoryContainer.addView(
                    card
            );
        }


        if (categoryContainer.getChildCount() == 0) {

            TextView empty =
                    new TextView(this);


            empty.setText(
                    "No categories found for this filter."
            );


            empty.setTextColor(
                    Color.rgb(
                            120,
                            132,
                            146
                    )
            );


            empty.setTextSize(
                    10
            );


            empty.setGravity(
                    Gravity.CENTER
            );


            empty.setPadding(
                    0,
                    dp(20),
                    0,
                    dp(20)
            );


            categoryContainer.addView(
                    empty
            );
        }
    }


    // ============================================================
    // CATEGORY FILTER
    // ============================================================

    private boolean matchesFilter(
            HotspotCategory category
    ) {

        if (currentFilter.equals("ALL")) {

            return true;
        }


        String severity =
                category.getHotspotClass();


        if (severity == null) {

            return false;
        }


        return severity.equalsIgnoreCase(
                currentFilter
        );
    }


    // ============================================================
    // CREATE CATEGORY CARD
    // ============================================================

    private View createCategoryCard(
            HotspotCategory category
    ) {

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


        // --------------------------------------------------------
        // TOP ROW
        // --------------------------------------------------------

        LinearLayout topRow =
                new LinearLayout(this);


        topRow.setOrientation(
                LinearLayout.HORIZONTAL
        );


        topRow.setGravity(
                Gravity.CENTER_VERTICAL
        );


        // --------------------------------------------------------
        // Severity badge
        // --------------------------------------------------------

        TextView badge =
                new TextView(this);


        String severity =
                category.getHotspotClass();


        if (severity == null) {

            severity = "unknown";
        }


        badge.setText(
                severity.toUpperCase(
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


        int severityColor =
                getSeverityColor(
                        severity
                );


        badge.setTextColor(
                severityColor
        );


        badge.setBackgroundResource(
                getSeverityBackground(
                        severity
                )
        );


        topRow.addView(
                badge,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        dp(24)
                )
        );


        // --------------------------------------------------------
        // Category name
        // --------------------------------------------------------

        TextView name =
                new TextView(this);


        name.setText(
                category.getCategoryName()
        );


        name.setTextColor(
                Color.WHITE
        );


        name.setTextSize(
                13
        );


        name.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );


        name.setMaxLines(
                2
        );


        LinearLayout.LayoutParams nameParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );


        nameParams.setMargins(
                dp(8),
                0,
                dp(8),
                0
        );


        topRow.addView(
                name,
                nameParams
        );


        // --------------------------------------------------------
        // Score
        // --------------------------------------------------------

        TextView score =
                new TextView(this);


        score.setText(
                formatNumber(
                        category.getHotspotScore()
                )
        );


        score.setTextColor(
                severityColor
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


        // --------------------------------------------------------
        // Stats
        // --------------------------------------------------------

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
                dp(9),
                0,
                0
        );


        TextView reports =
                createStat(
                        "REPORTS  "
                                + category.getReportCount()
                );


        TextView weight =
                createStat(
                        "AVG WEIGHT  "
                                + formatNumber(
                                category.getAverageWeight()
                        )
                );


        TextView status =
                createStat(
                        "STATUS  "
                                + severity
                );


        stats.addView(
                reports,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );


        stats.addView(
                weight,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );


        stats.addView(
                status,
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


        // --------------------------------------------------------
        // Hotspot score
        // --------------------------------------------------------

        TextView hotspotScore =
                new TextView(this);


        hotspotScore.setText(
                "HOTSPOT SCORE  "
                        + formatNumber(
                        category.getHotspotScore()
                )
        );


        hotspotScore.setTextColor(
                Color.rgb(
                        155,
                        166,
                        180
                )
        );


        hotspotScore.setTextSize(
                9
        );


        LinearLayout.LayoutParams scoreParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );


        scoreParams.setMargins(
                0,
                dp(8),
                0,
                0
        );


        card.addView(
                hotspotScore,
                scoreParams
        );


        // --------------------------------------------------------
        // Click
        // --------------------------------------------------------

        card.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    IssueListActivity.class
            );

            String categoryName =
                    category.getCategoryName()
                            .replace(" ", "_");

            intent.putExtra(
                    "issue_category",
                    categoryName
            );

            intent.putExtra(
                    "issue_location",
                    CITY
            );

            startActivity(intent);
        });

        return card;
    }


    // ============================================================
    // STAT TEXT
    // ============================================================

    private TextView createStat(
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


    // ============================================================
    // CATEGORY DETAILS
    // ============================================================

    private void showCategoryInfo(
            HotspotCategory category
    ) {

        String text =
                category.getCategoryName()
                        + "\n\n"
                        + "Status: "
                        + category.getHotspotClass()
                        + "\n"
                        + "Reports: "
                        + category.getReportCount()
                        + "\n"
                        + "Average Weight: "
                        + formatNumber(
                        category.getAverageWeight()
                )
                        + "\n"
                        + "Hotspot Score: "
                        + formatNumber(
                        category.getHotspotScore()
                );


        categoryInfo.setText(
                text
        );
    }


    // ============================================================
    // FILTER SETUP
    // ============================================================

    private void setupFilters() {

        filterAll.setOnClickListener(
                v -> {

                    currentFilter = "ALL";

                    updateFilterColors();

                    displayCategories();
                }
        );


        filterHotspot.setOnClickListener(
                v -> {

                    currentFilter = "hotspot";

                    updateFilterColors();

                    displayCategories();
                }
        );


        filterModerate.setOnClickListener(
                v -> {

                    currentFilter = "moderate";

                    updateFilterColors();

                    displayCategories();
                }
        );


        filterLow.setOnClickListener(
                v -> {

                    currentFilter = "low";

                    updateFilterColors();

                    displayCategories();
                }
        );


        updateFilterColors();
    }


    // ============================================================
    // FILTER COLORS
    // ============================================================

    private void updateFilterColors() {

        filterAll.setTextColor(
                INACTIVE_COLOR
        );


        filterHotspot.setTextColor(
                INACTIVE_COLOR
        );


        filterModerate.setTextColor(
                INACTIVE_COLOR
        );


        filterLow.setTextColor(
                INACTIVE_COLOR
        );


        if (currentFilter.equals("ALL")) {

            filterAll.setTextColor(
                    ACTIVE_COLOR
            );

        } else if (
                currentFilter.equals("hotspot")
        ) {

            filterHotspot.setTextColor(
                    ACTIVE_COLOR
            );

        } else if (
                currentFilter.equals("moderate")
        ) {

            filterModerate.setTextColor(
                    ACTIVE_COLOR
            );

        } else if (
                currentFilter.equals("low")
        ) {

            filterLow.setTextColor(
                    ACTIVE_COLOR
            );
        }
    }


    // ============================================================
    // SEVERITY COLOR
    // ============================================================

    private int getSeverityColor(
            String severity
    ) {

        if (severity == null) {

            return Color.GRAY;
        }


        if (severity.equalsIgnoreCase(
                "hotspot"
        )) {

            return HIGH_COLOR;
        }


        if (severity.equalsIgnoreCase(
                "moderate"
        )) {

            return MODERATE_COLOR;
        }


        if (severity.equalsIgnoreCase(
                "low"
        )) {

            return LOW_COLOR;
        }


        return Color.GRAY;
    }


    // ============================================================
    // SEVERITY BACKGROUND
    // ============================================================

    private int getSeverityBackground(
            String severity
    ) {

        if (severity == null) {

            return R.drawable.bg_demo_card;
        }


        if (severity.equalsIgnoreCase(
                "hotspot"
        )) {

            return R.drawable.bg_high_badge;
        }


        if (severity.equalsIgnoreCase(
                "moderate"
        )) {

            return R.drawable.bg_moderate_badge;
        }


        if (severity.equalsIgnoreCase(
                "low"
        )) {

            return R.drawable.bg_low_badge;
        }


        return R.drawable.bg_demo_card;
    }


    // ============================================================
    // NUMBER FORMAT
    // ============================================================

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


    // ============================================================
    // BOTTOM NAVIGATION
    // ============================================================

    private void setupNavigation() {

        navMap.setOnClickListener(
                v -> {
                    // Already on Map.
                }
        );


        navDemographics.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    MainActivity.this,
                                    DemographicsActivity.class
                            );


                    intent.addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_SINGLE_TOP
                    );


                    startActivity(
                            intent
                    );
                }
        );


        navSolutions.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    MainActivity.this,
                                    SolutionsActivity.class
                            );


                    intent.addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_SINGLE_TOP
                    );


                    startActivity(
                            intent
                    );
                }
        );
    }


    // ============================================================
    // ACTIVE MAP TAB
    // ============================================================

    private void setActiveMapTab() {

        textMap.setTextColor(
                ACTIVE_COLOR
        );


        textDemographics.setTextColor(
                INACTIVE_COLOR
        );


        textSolutions.setTextColor(
                INACTIVE_COLOR
        );


        iconMap.setColorFilter(
                ACTIVE_COLOR
        );


        iconDemographics.setColorFilter(
                INACTIVE_COLOR
        );


        iconSolutions.setColorFilter(
                INACTIVE_COLOR
        );
    }


    // ============================================================
    // ERROR
    // ============================================================

    private void showError(
            String message
    ) {

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }


    // ============================================================
    // DP
    // ============================================================

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


    // ============================================================
    // MAPVIEW LIFECYCLE
    // ============================================================

    @Override
    protected void onStart() {

        super.onStart();

        if (mapView != null) {

            mapView.onStart();
        }
    }


    @Override
    protected void onResume() {

        super.onResume();

        if (mapView != null) {

            mapView.onResume();
        }
    }


    @Override
    protected void onPause() {

        if (mapView != null) {

            mapView.onPause();
        }

        super.onPause();
    }


    @Override
    protected void onStop() {

        if (mapView != null) {

            mapView.onStop();
        }

        super.onStop();
    }


    @Override
    protected void onDestroy() {

        if (mapView != null) {

            mapView.onDestroy();
        }

        super.onDestroy();
    }


    @Override
    public void onLowMemory() {

        super.onLowMemory();

        if (mapView != null) {

            mapView.onLowMemory();
        }
    }
}