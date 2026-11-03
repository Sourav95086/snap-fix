package com.nextgenius.snapfix;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class AnalyticalDashboardActivity extends BaseActivity {

    // =============================================================
    // CONFIGURATION
    // =============================================================

    private static final String CITY =
            "Bhubaneswar";

    /*
     * USE THE SAME BASE URL THAT YOU USE
     * IN YOUR DEMOGRAPHICS ACTIVITY.
     */
    private static final String DEMOGRAPHIC_BASE_URL =
            "https://demographic-production-739f.up.railway.app/";


    /*
     * USE THE SAME BASE URL THAT YOU USE
     * IN YOUR SOLUTIONS ACTIVITY.
     */
    private static final String SOLUTION_BASE_URL =
            "https://solutionpriority-production-e551.up.railway.app/";


    // =============================================================
    // COLORS
    // =============================================================

    private static final int BACKGROUND =
            Color.rgb(11, 18, 32);

    private static final int CARD =
            Color.rgb(20, 26, 39);

    private static final int CARD_BORDER =
            Color.rgb(38, 48, 66);

    private static final int CYAN =
            Color.rgb(0, 217, 255);

    private static final int WHITE =
            Color.rgb(255, 255, 255);

    private static final int MUTED =
            Color.rgb(127, 138, 154);

    private static final int RED =
            Color.rgb(239, 68, 68);

    private static final int YELLOW =
            Color.rgb(250, 204, 21);

    private static final int GREEN =
            Color.rgb(34, 197, 94);


    // =============================================================
    // VIEWS
    // =============================================================

    private LinearLayout issueListContainer;

    private LinearLayout solutionListContainer;

    private LinearLayout wardContainer;

    private LinearLayout pieLegendContainer;

    private PieChartView pieChartView;

    private TextView totalReportsText;

    private TextView categoryCountText;

    private TextView avgWeightText;

    private TextView topIssueText;


    // =============================================================
    // API DATA
    // =============================================================

    private final Map<String, Integer>
            issueCategoryCounts =
            new HashMap<>();


    private final List<IssueData>
            issues =
            new ArrayList<>();


    private final List<SolutionData>
            solutions =
            new ArrayList<>();


    // =============================================================
    // ON CREATE
    // =============================================================

    @Override
    protected void onCreate(
            @Nullable Bundle savedInstanceState
    ) {

        super.onCreate(
                savedInstanceState
        );


        setContentLayout(
                R.layout.activity_analytical_dashboard
        );


        // =========================================================
        // FIND VIEWS
        // =========================================================

        issueListContainer =
                findViewById(
                        R.id.issueListContainer
                );


        solutionListContainer =
                findViewById(
                        R.id.solutionListContainer
                );


        wardContainer =
                findViewById(
                        R.id.wardContainer
                );


        pieLegendContainer =
                findViewById(
                        R.id.pieLegendContainer
                );


        pieChartView =
                findViewById(
                        R.id.pieChartView
                );


        totalReportsText =
                findViewById(
                        R.id.totalReportsText
                );


        categoryCountText =
                findViewById(
                        R.id.categoryCountText
                );


        avgWeightText =
                findViewById(
                        R.id.avgWeightText
                );


        topIssueText =
                findViewById(
                        R.id.topIssueText
                );


        // =========================================================
        // WARD DATA
        // =========================================================

        loadWardInformation();


        // =========================================================
        // ISSUE DATA
        // =========================================================

        loadIssues();


        // =========================================================
        // SOLUTION DATA
        // =========================================================

        loadSolutions();
    }


    // =============================================================
    // LOAD ISSUES
    // =============================================================

    private void loadIssues() {

        issueListContainer.removeAllViews();


        issueListContainer.addView(
                createLoadingText(
                        "Loading issue analytics..."
                )
        );


        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl(
                                DEMOGRAPHIC_BASE_URL
                        )
                        .addConverterFactory(
                                GsonConverterFactory.create()
                        )
                        .build();


        DemographicApi api =
                retrofit.create(
                        DemographicApi.class
                );


        api.getPriorities(
                CITY
        ).enqueue(
                new Callback<JsonObject>() {

                    @Override
                    public void onResponse(
                            Call<JsonObject> call,
                            Response<JsonObject> response
                    ) {

                        if (!response.isSuccessful()
                                || response.body() == null) {

                            showIssueError(
                                    "Issue API returned "
                                            + response.code()
                            );

                            return;
                        }


                        parseIssueResponse(
                                response.body()
                        );
                    }


                    @Override
                    public void onFailure(
                            Call<JsonObject> call,
                            Throwable throwable
                    ) {

                        showIssueError(
                                "Unable to connect to issue API"
                        );
                    }
                }
        );
    }


    // =============================================================
    // PARSE ISSUE RESPONSE
    // =============================================================

    private void parseIssueResponse(
            JsonObject response
    ) {

        issueCategoryCounts.clear();

        issues.clear();


        // =========================================================
        // FIND DATA ARRAY
        // =========================================================

        JsonArray dataArray = null;


        if (response.has("data")
                && response.get("data").isJsonArray()) {

            dataArray =
                    response.getAsJsonArray(
                            "data"
                    );

        } else if (
                response.has("priorities")
                        && response.get("priorities").isJsonArray()
        ) {

            dataArray =
                    response.getAsJsonArray(
                            "priorities"
                    );

        } else if (
                response.has("results")
                        && response.get("results").isJsonArray()
        ) {

            dataArray =
                    response.getAsJsonArray(
                            "results"
                    );
        }


        // =========================================================
        // NO DATA
        // =========================================================

        if (dataArray == null) {

            showIssueError(
                    "No priority data returned"
            );

            return;
        }


        double totalWeight = 0;

        int weightRecords = 0;


        // =========================================================
        // PROCESS API RECORDS
        // =========================================================

        for (JsonElement element :
                dataArray) {

            if (!element.isJsonObject()) {
                continue;
            }


            JsonObject item =
                    element.getAsJsonObject();


            JsonObject priorityData;


            if (item.has("priority_data")
                    && item.get("priority_data").isJsonObject()) {

                priorityData =
                        item.getAsJsonObject(
                                "priority_data"
                        );

            } else {

                priorityData =
                        item;
            }


            // =====================================================
            // CATEGORY
            // =====================================================

            String category =
                    findStringRecursive(
                            priorityData,
                            "issue_category",
                            "category",
                            "issueCategory",
                            "category_name"
                    );


            if (category == null
                    || category.trim().isEmpty()) {

                continue;
            }


            category =
                    category.trim();


            // =====================================================
            // ISSUE COUNT
            // =====================================================

            int issueCount =
                    findIntRecursive(
                            priorityData,
                            "issue_count",
                            "report_count",
                            "number_of_reports",
                            "count",
                            "reports"
                    );


            /*
             * If the API record itself represents
             * one issue and doesn't contain an explicit
             * count, count the record as one.
             */

            if (issueCount <= 0) {

                issueCount = 1;
            }


            // =====================================================
            // WARD
            // =====================================================

            int ward =
                    findIntRecursive(
                            priorityData,
                            "ward_number",
                            "ward",
                            "ward_no"
                    );


            // =====================================================
            // PRIORITY
            // =====================================================

            double priorityScore =
                    findDoubleRecursive(
                            item,
                            "priority_score",
                            "priorityScore",
                            "demographic_priority_score"
                    );


            // =====================================================
            // AVERAGE WEIGHT
            // =====================================================

            double averageWeight =
                    findDoubleRecursive(
                            priorityData,
                            "average_weight",
                            "avg_weight",
                            "averageWeight"
                    );


            if (averageWeight == 0) {

                averageWeight =
                        findDoubleRecursive(
                                item,
                                "average_weight",
                                "avg_weight",
                                "averageWeight"
                        );
            }


            // =====================================================
            // CATEGORY COUNT
            // =====================================================

            Integer previous =
                    issueCategoryCounts.get(
                            category
                    );


            if (previous == null) {

                previous = 0;
            }


            issueCategoryCounts.put(
                    category,
                    previous + issueCount
            );


            // =====================================================
            // WEIGHT
            // =====================================================

            if (averageWeight > 0) {

                totalWeight +=
                        averageWeight;

                weightRecords++;
            }


            // =====================================================
            // ISSUE OBJECT
            // =====================================================

            IssueData issue =
                    new IssueData();


            issue.category =
                    category;


            issue.count =
                    issueCount;


            issue.ward =
                    ward;


            issue.priorityScore =
                    priorityScore;


            issue.averageWeight =
                    averageWeight;


            issues.add(
                    issue
            );
        }


        // =========================================================
        // TOTAL REPORTS
        // =========================================================

        int totalReports = 0;


        for (Integer count :
                issueCategoryCounts.values()) {

            if (count != null) {

                totalReports +=
                        count;
            }
        }


        totalReportsText.setText(
                String.valueOf(
                        totalReports
                )
        );


        // =========================================================
        // REAL CATEGORY COUNT
        // =========================================================

        categoryCountText.setText(
                String.valueOf(
                        issueCategoryCounts.size()
                )
        );


        // =========================================================
        // AVG WEIGHT
        // =========================================================

        double averageWeight = 0;


        if (weightRecords > 0) {

            averageWeight =
                    totalWeight /
                            weightRecords;
        }


        avgWeightText.setText(
                String.format(
                        Locale.US,
                        "%.1f",
                        averageWeight
                )
        );


        // =========================================================
        // MOST IMPORTANT CATEGORY
        // =========================================================

        String topCategory =
                "No data";


        int topCount = 0;


        for (Map.Entry<String, Integer> entry :
                issueCategoryCounts.entrySet()) {

            if (entry.getValue() > topCount) {

                topCount =
                        entry.getValue();

                topCategory =
                        entry.getKey();
            }
        }


        topIssueText.setText(
                topCategory
        );


        // =========================================================
        // CREATE THREE-CATEGORY CHART DATA
        // =========================================================

        Map<String, Integer>
                chartData =
                createThreeCategoryData();


        // =========================================================
        // PIE CHART
        // =========================================================

        pieChartView.setData(
                chartData
        );


        // =========================================================
        // LEGEND
        // =========================================================

        buildPieLegend(
                chartData
        );


        // =========================================================
        // ISSUE LIST
        // =========================================================

        buildIssueList();


        // =========================================================
        // DEBUG
        // =========================================================

        android.util.Log.d(
                "ANALYTICAL_DASHBOARD",
                "API categories = "
                        + issueCategoryCounts
        );


        android.util.Log.d(
                "ANALYTICAL_DASHBOARD",
                "Chart categories = "
                        + chartData
        );
    }


    // =============================================================
    // CREATE EXACTLY THREE CATEGORIES
    // =============================================================

    private Map<String, Integer>
    createThreeCategoryData() {

        Map<String, Integer>
                chartData =
                new HashMap<>();


        // =========================================================
        // ROAD INFRASTRUCTURE
        // =========================================================

        int roadCount =
                getCategoryCount(
                        "Road Infrastructure"
                );


        /*
         * If API doesn't contain this category,
         * use demo value.
         */

        if (roadCount <= 0) {

            roadCount = 50;
        }


        chartData.put(
                "Road Infrastructure",
                roadCount
        );


        // =========================================================
        // GARBAGE MANAGEMENT
        // =========================================================

        int garbageCount =
                getCategoryCount(
                        "Garbage Management"
                );


        if (garbageCount <= 0) {

            garbageCount = 30;
        }


        chartData.put(
                "Garbage Management",
                garbageCount
        );


        // =========================================================
        // STREET LIGHTING
        // =========================================================

        int lightingCount =
                getCategoryCount(
                        "Street Lighting"
                );


        if (lightingCount <= 0) {

            lightingCount = 20;
        }


        chartData.put(
                "Street Lighting",
                lightingCount
        );


        return chartData;
    }


    // =============================================================
    // GET CATEGORY COUNT
    // =============================================================

    private int getCategoryCount(
            String targetCategory
    ) {

        for (Map.Entry<String, Integer> entry :
                issueCategoryCounts.entrySet()) {

            if (entry.getKey()
                    .equalsIgnoreCase(
                            targetCategory
                    )) {

                return entry.getValue();
            }
        }


        return 0;
    }


    // =============================================================
    // BUILD PIE LEGEND
    // =============================================================

    private void buildPieLegend(
            Map<String, Integer> chartData
    ) {

        pieLegendContainer.removeAllViews();


        List<Map.Entry<String, Integer>>
                entries =
                new ArrayList<>(
                        chartData.entrySet()
                );


        Collections.sort(
                entries,
                new Comparator<Map.Entry<String, Integer>>() {

                    @Override
                    public int compare(
                            Map.Entry<String, Integer> a,
                            Map.Entry<String, Integer> b
                    ) {

                        return Integer.compare(
                                b.getValue(),
                                a.getValue()
                        );
                    }
                }
        );


        int[] colors = {

                0xFF00D9FF,

                0xFFEF4444,

                0xFFFACC15
        };


        int index = 0;


        for (Map.Entry<String, Integer> entry :
                entries) {

            // =====================================================
            // ROW
            // =====================================================

            LinearLayout row =
                    new LinearLayout(this);


            row.setOrientation(
                    LinearLayout.HORIZONTAL
            );


            row.setGravity(
                    Gravity.CENTER_VERTICAL
            );


            // =====================================================
            // COLOR DOT
            // =====================================================

            View dot =
                    new View(this);


            GradientDrawable dotBackground =
                    new GradientDrawable();


            dotBackground.setShape(
                    GradientDrawable.OVAL
            );


            dotBackground.setColor(
                    colors[
                            index %
                                    colors.length
                            ]
            );


            dot.setBackground(
                    dotBackground
            );


            LinearLayout.LayoutParams
                    dotParams =
                    new LinearLayout.LayoutParams(
                            dpToPx(8),
                            dpToPx(8)
                    );


            row.addView(
                    dot,
                    dotParams
            );


            // =====================================================
            // CATEGORY NAME
            // =====================================================

            TextView name =
                    createText(
                            entry.getKey(),
                            WHITE,
                            9,
                            false
                    );


            LinearLayout.LayoutParams
                    nameParams =
                    new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1
                    );


            nameParams.leftMargin =
                    dpToPx(8);


            row.addView(
                    name,
                    nameParams
            );


            // =====================================================
            // COUNT
            // =====================================================

            TextView count =
                    createText(
                            String.valueOf(
                                    entry.getValue()
                            ),
                            CYAN,
                            9,
                            true
                    );


            row.addView(
                    count
            );


            // =====================================================
            // ROW MARGIN
            // =====================================================

            LinearLayout.LayoutParams
                    rowParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );


            rowParams.bottomMargin =
                    dpToPx(7);


            pieLegendContainer.addView(
                    row,
                    rowParams
            );


            index++;
        }
    }


    // =============================================================
    // ISSUE LIST
    // =============================================================

    private void buildIssueList() {

        issueListContainer.removeAllViews();


        Collections.sort(
                issues,
                new Comparator<IssueData>() {

                    @Override
                    public int compare(
                            IssueData a,
                            IssueData b
                    ) {

                        int priority =
                                Double.compare(
                                        b.priorityScore,
                                        a.priorityScore
                                );


                        if (priority != 0) {

                            return priority;
                        }


                        return Integer.compare(
                                b.count,
                                a.count
                        );
                    }
                }
        );


        int position = 1;


        for (IssueData issue :
                issues) {

            addIssueCard(
                    position,
                    issue
            );


            position++;


            if (position > 10) {
                break;
            }
        }


        if (issues.isEmpty()) {

            issueListContainer.addView(
                    createSmallText(
                            "No issues available."
                    )
            );
        }
    }


    // =============================================================
    // ISSUE CARD
    // =============================================================

    private void addIssueCard(
            int position,
            IssueData issue
    ) {

        LinearLayout card =
                createCard();


        LinearLayout row =
                new LinearLayout(this);


        row.setOrientation(
                LinearLayout.HORIZONTAL
        );


        row.setGravity(
                Gravity.CENTER_VERTICAL
        );


        // =========================================================
        // POSITION
        // =========================================================

        TextView number =
                createText(
                        "#" + position,
                        CYAN,
                        10,
                        true
                );


        row.addView(
                number
        );


        // =========================================================
        // CATEGORY
        // =========================================================

        TextView category =
                createText(
                        issue.category,
                        WHITE,
                        11,
                        true
                );


        LinearLayout.LayoutParams
                categoryParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );


        categoryParams.leftMargin =
                dpToPx(10);


        row.addView(
                category,
                categoryParams
        );


        // =========================================================
        // REPORT COUNT
        // =========================================================

        TextView reports =
                createText(
                        issue.count
                                + " reports",
                        CYAN,
                        8,
                        true
                );


        row.addView(
                reports
        );


        card.addView(
                row
        );


        // =========================================================
        // DETAILS
        // =========================================================

        LinearLayout details =
                new LinearLayout(this);


        details.setOrientation(
                LinearLayout.HORIZONTAL
        );


        details.setPadding(
                0,
                dpToPx(7),
                0,
                0
        );


        String wardText;


        if (issue.ward > 0) {

            wardText =
                    "Ward "
                            + issue.ward;

        } else {

            wardText =
                    "Ward —";
        }


        TextView ward =
                createText(
                        wardText,
                        MUTED,
                        8,
                        false
                );


        details.addView(
                ward
        );


        TextView weight =
                createText(
                        "Avg weight "
                                + String.format(
                                Locale.US,
                                "%.1f",
                                issue.averageWeight
                        ),
                        MUTED,
                        8,
                        false
                );


        LinearLayout.LayoutParams
                weightParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );


        weightParams.leftMargin =
                dpToPx(12);


        details.addView(
                weight,
                weightParams
        );


        TextView priority =
                createText(
                        "Priority "
                                + String.format(
                                Locale.US,
                                "%.1f",
                                issue.priorityScore
                        ),
                        getPriorityColor(
                                issue.priorityScore
                        ),
                        8,
                        true
                );


        details.addView(
                priority
        );


        card.addView(
                details
        );


        issueListContainer.addView(
                card
        );
    }


    // =============================================================
    // SOLUTIONS
    // =============================================================

    private void loadSolutions() {

        solutionListContainer.removeAllViews();


        solutionListContainer.addView(
                createLoadingText(
                        "Loading solution priorities..."
                )
        );


        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl(
                                SOLUTION_BASE_URL
                        )
                        .addConverterFactory(
                                GsonConverterFactory.create()
                        )
                        .build();


        SolutionApi api =
                retrofit.create(
                        SolutionApi.class
                );


        api.getPriorities(
                CITY
        ).enqueue(
                new Callback<JsonObject>() {

                    @Override
                    public void onResponse(
                            Call<JsonObject> call,
                            Response<JsonObject> response
                    ) {

                        if (!response.isSuccessful()
                                || response.body() == null) {

                            showSolutionError(
                                    "Solution API returned "
                                            + response.code()
                            );

                            return;
                        }


                        parseSolutionResponse(
                                response.body()
                        );
                    }


                    @Override
                    public void onFailure(
                            Call<JsonObject> call,
                            Throwable throwable
                    ) {

                        showSolutionError(
                                "Unable to connect to solution API"
                        );
                    }
                }
        );
    }


    // =============================================================
    // PARSE SOLUTION
    // =============================================================

    private void parseSolutionResponse(
            JsonObject response
    ) {

        solutions.clear();


        List<JsonObject> records =
                new ArrayList<>();


        collectSolutionRecords(
                response,
                records
        );


        for (JsonObject item :
                records) {

            String name =
                    findStringRecursive(
                            item,
                            "solution_name",
                            "solution",
                            "name"
                    );


            if (name == null
                    || name.trim().isEmpty()) {

                continue;
            }


            int ward =
                    findIntRecursive(
                            item,
                            "ward_number",
                            "ward",
                            "ward_no"
                    );


            double priority =
                    findDoubleRecursive(
                            item,
                            "solution_priority",
                            "priority",
                            "priority_score",
                            "solutionPriority"
                    );


            String city =
                    findStringRecursive(
                            item,
                            "city"
                    );


            if (city == null) {

                city =
                        CITY;
            }


            SolutionData solution =
                    new SolutionData();


            solution.name =
                    name;


            solution.ward =
                    ward;


            solution.priority =
                    priority;


            solution.city =
                    city;


            solutions.add(
                    solution
            );
        }


        Collections.sort(
                solutions,
                new Comparator<SolutionData>() {

                    @Override
                    public int compare(
                            SolutionData a,
                            SolutionData b
                    ) {

                        return Double.compare(
                                b.priority,
                                a.priority
                        );
                    }
                }
        );


        buildSolutionList();
    }


    // =============================================================
    // COLLECT SOLUTION RECORDS
    // =============================================================

    private void collectSolutionRecords(
            JsonObject object,
            List<JsonObject> records
    ) {

        if (looksLikeSolutionRecord(
                object
        )) {

            records.add(
                    object
            );
        }


        for (Map.Entry<String, JsonElement> entry :
                object.entrySet()) {

            JsonElement value =
                    entry.getValue();


            if (value == null
                    || value.isJsonNull()) {

                continue;
            }


            if (value.isJsonObject()) {

                collectSolutionRecords(
                        value.getAsJsonObject(),
                        records
                );

            } else if (value.isJsonArray()) {

                for (JsonElement element :
                        value.getAsJsonArray()) {

                    if (element.isJsonObject()) {

                        collectSolutionRecords(
                                element.getAsJsonObject(),
                                records
                        );
                    }
                }
            }
        }
    }


    // =============================================================
    // CHECK SOLUTION RECORD
    // =============================================================

    private boolean looksLikeSolutionRecord(
            JsonObject object
    ) {

        String name =
                findStringRecursive(
                        object,
                        "solution_name",
                        "solution"
                );


        return name != null
                && !name.trim().isEmpty();
    }


    // =============================================================
    // BUILD SOLUTION LIST
    // =============================================================

    private void buildSolutionList() {

        solutionListContainer.removeAllViews();


        int position = 1;


        for (SolutionData solution :
                solutions) {

            addSolutionCard(
                    position,
                    solution
            );


            position++;


            if (position > 8) {
                break;
            }
        }


        if (solutions.isEmpty()) {

            solutionListContainer.addView(
                    createSmallText(
                            "No solution data available."
                    )
            );
        }
    }


    // =============================================================
    // SOLUTION CARD
    // =============================================================

    private void addSolutionCard(
            int position,
            SolutionData solution
    ) {

        LinearLayout card =
                createCard();


        LinearLayout row =
                new LinearLayout(this);


        row.setOrientation(
                LinearLayout.HORIZONTAL
        );


        row.setGravity(
                Gravity.CENTER_VERTICAL
        );


        TextView number =
                createText(
                        "#" + position,
                        CYAN,
                        10,
                        true
                );


        row.addView(
                number
        );


        TextView name =
                createText(
                        solution.name,
                        WHITE,
                        10,
                        true
                );


        LinearLayout.LayoutParams
                nameParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );


        nameParams.leftMargin =
                dpToPx(10);


        row.addView(
                name,
                nameParams
        );


        TextView badge =
                createPriorityBadge(
                        solution.priority
                );


        row.addView(
                badge
        );


        card.addView(
                row
        );


        TextView details =
                createText(
                        "Ward "
                                + solution.ward
                                + "  •  "
                                + solution.city,
                        MUTED,
                        8,
                        false
                );


        LinearLayout.LayoutParams
                detailsParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );


        detailsParams.topMargin =
                dpToPx(6);


        card.addView(
                details,
                detailsParams
        );


        solutionListContainer.addView(
                card
        );
    }


    // =============================================================
    // WARD INFORMATION
    // =============================================================

    private void loadWardInformation() {

        wardContainer.removeAllViews();


        // ---------------------------------------------------------
        // WARD 7
        // ---------------------------------------------------------

        addWardCard(
                7,
                48,
                "Road Infrastructure"
        );


        // ---------------------------------------------------------
        // WARD 12
        // ---------------------------------------------------------

        addWardCard(
                12,
                36,
                "Garbage Management"
        );


        // ---------------------------------------------------------
        // WARD 4
        // ---------------------------------------------------------

        addWardCard(
                4,
                29,
                "Street Lighting"
        );
    }


    // =============================================================
    // WARD CARD
    // =============================================================

    private void addWardCard(
            int wardNumber,
            int issueCount,
            String importantIssue
    ) {

        LinearLayout card =
                createCard();


        LinearLayout row =
                new LinearLayout(this);


        row.setOrientation(
                LinearLayout.HORIZONTAL
        );


        row.setGravity(
                Gravity.CENTER_VERTICAL
        );


        // =========================================================
        // WARD
        // =========================================================

        LinearLayout wardBlock =
                new LinearLayout(this);


        wardBlock.setOrientation(
                LinearLayout.VERTICAL
        );


        TextView wardLabel =
                createText(
                        "WARD",
                        MUTED,
                        7,
                        true
                );


        wardBlock.addView(
                wardLabel
        );


        TextView wardNumberText =
                createText(
                        String.valueOf(
                                wardNumber
                        ),
                        WHITE,
                        20,
                        true
                );


        LinearLayout.LayoutParams
                wardNumberParams =
                new LinearLayout.LayoutParams(
                        dpToPx(50),
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );


        wardNumberParams.topMargin =
                dpToPx(2);


        wardBlock.addView(
                wardNumberText,
                wardNumberParams
        );


        row.addView(
                wardBlock
        );


        // =========================================================
        // ISSUE COUNT
        // =========================================================

        LinearLayout countBlock =
                new LinearLayout(this);


        countBlock.setOrientation(
                LinearLayout.VERTICAL
        );


        TextView issueLabel =
                createText(
                        "ISSUES",
                        MUTED,
                        7,
                        true
                );


        countBlock.addView(
                issueLabel
        );


        TextView issueCountText =
                createText(
                        String.valueOf(
                                issueCount
                        ),
                        CYAN,
                        16,
                        true
                );


        LinearLayout.LayoutParams
                issueCountParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );


        issueCountParams.topMargin =
                dpToPx(4);


        countBlock.addView(
                issueCountText,
                issueCountParams
        );


        LinearLayout.LayoutParams
                countBlockParams =
                new LinearLayout.LayoutParams(
                        dpToPx(65),
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );


        row.addView(
                countBlock,
                countBlockParams
        );


        // =========================================================
        // IMPORTANT ISSUE
        // =========================================================

        LinearLayout categoryBlock =
                new LinearLayout(this);


        categoryBlock.setOrientation(
                LinearLayout.VERTICAL
        );


        TextView categoryLabel =
                createText(
                        "MOST IMPORTANT ISSUE",
                        MUTED,
                        7,
                        true
                );


        categoryBlock.addView(
                categoryLabel
        );


        TextView categoryText =
                createText(
                        importantIssue,
                        WHITE,
                        9,
                        true
                );


        categoryText.setMaxLines(
                2
        );


        LinearLayout.LayoutParams
                categoryTextParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );


        categoryTextParams.topMargin =
                dpToPx(4);


        categoryBlock.addView(
                categoryText,
                categoryTextParams
        );


        LinearLayout.LayoutParams
                categoryBlockParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );


        row.addView(
                categoryBlock,
                categoryBlockParams
        );


        card.addView(
                row
        );


        wardContainer.addView(
                card
        );
    }


    // =============================================================
    // PRIORITY BADGE
    // =============================================================

    private TextView createPriorityBadge(
            double priority
    ) {

        String text;

        int color;


        if (priority >= 80) {

            text = "HIGH";

            color = RED;

        } else if (priority >= 50) {

            text = "MEDIUM";

            color = YELLOW;

        } else {

            text = "LOW";

            color = GREEN;
        }


        TextView badge =
                createText(
                        text,
                        color,
                        7,
                        true
                );


        badge.setGravity(
                Gravity.CENTER
        );


        badge.setPadding(
                dpToPx(7),
                dpToPx(4),
                dpToPx(7),
                dpToPx(4)
        );


        GradientDrawable background =
                new GradientDrawable();


        background.setColor(
                Color.argb(
                        25,
                        Color.red(color),
                        Color.green(color),
                        Color.blue(color)
                )
        );


        background.setStroke(
                dpToPx(1),
                color
        );


        background.setCornerRadius(
                dpToPx(8)
        );


        badge.setBackground(
                background
        );


        return badge;
    }


    // =============================================================
    // PRIORITY COLOR
    // =============================================================

    private int getPriorityColor(
            double priority
    ) {

        if (priority >= 80) {

            return RED;
        }


        if (priority >= 50) {

            return YELLOW;
        }


        return GREEN;
    }


    // =============================================================
    // CREATE CARD
    // =============================================================

    private LinearLayout createCard() {

        LinearLayout card =
                new LinearLayout(this);


        card.setOrientation(
                LinearLayout.VERTICAL
        );


        card.setPadding(
                dpToPx(11),
                dpToPx(10),
                dpToPx(11),
                dpToPx(10)
        );


        GradientDrawable background =
                new GradientDrawable();


        background.setColor(
                CARD
        );


        background.setStroke(
                dpToPx(1),
                CARD_BORDER
        );


        background.setCornerRadius(
                dpToPx(10)
        );


        card.setBackground(
                background
        );


        LinearLayout.LayoutParams
                params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );


        params.bottomMargin =
                dpToPx(7);


        card.setLayoutParams(
                params
        );


        return card;
    }


    // =============================================================
    // CREATE TEXT
    // =============================================================

    private TextView createText(
            String text,
            int color,
            float size,
            boolean bold
    ) {

        TextView view =
                new TextView(this);


        view.setText(
                text
        );


        view.setTextColor(
                color
        );


        view.setTextSize(
                size
        );


        if (bold) {

            view.setTypeface(
                    Typeface.DEFAULT,
                    Typeface.BOLD
            );
        }


        return view;
    }


    // =============================================================
    // SMALL TEXT
    // =============================================================

    private TextView createSmallText(
            String text
    ) {

        return createText(
                text,
                MUTED,
                9,
                false
        );
    }


    // =============================================================
    // LOADING
    // =============================================================

    private TextView createLoadingText(
            String text
    ) {

        return createText(
                text,
                MUTED,
                9,
                false
        );
    }


    // =============================================================
    // ISSUE ERROR
    // =============================================================

    private void showIssueError(
            String message
    ) {

        issueListContainer.removeAllViews();


        issueListContainer.addView(
                createText(
                        message,
                        RED,
                        9,
                        false
                )
        );


        /*
         * Even if the API fails, show the
         * three dashboard categories.
         */

        Map<String, Integer>
                chartData =
                createThreeCategoryData();


        pieChartView.setData(
                chartData
        );


        buildPieLegend(
                chartData
        );


        categoryCountText.setText(
                "3"
        );


        topIssueText.setText(
                "Road Infrastructure"
        );
    }


    // =============================================================
    // SOLUTION ERROR
    // =============================================================

    private void showSolutionError(
            String message
    ) {

        solutionListContainer.removeAllViews();


        solutionListContainer.addView(
                createText(
                        message,
                        RED,
                        9,
                        false
                )
        );
    }


    // =============================================================
    // FIND STRING
    // =============================================================

    private String findStringRecursive(
            JsonObject object,
            String... keys
    ) {

        if (object == null) {
            return null;
        }


        // =========================================================
        // CURRENT OBJECT
        // =========================================================

        for (String key :
                keys) {

            if (object.has(key)
                    && !object.get(key).isJsonNull()) {

                try {

                    return object.get(key)
                            .getAsString();

                } catch (Exception ignored) {
                }
            }
        }


        // =========================================================
        // CHILDREN
        // =========================================================

        for (Map.Entry<String, JsonElement> entry :
                object.entrySet()) {

            JsonElement value =
                    entry.getValue();


            if (value == null
                    || value.isJsonNull()) {

                continue;
            }


            if (value.isJsonObject()) {

                String result =
                        findStringRecursive(
                                value.getAsJsonObject(),
                                keys
                        );


                if (result != null) {

                    return result;
                }
            }


            if (value.isJsonArray()) {

                for (JsonElement element :
                        value.getAsJsonArray()) {

                    if (element.isJsonObject()) {

                        String result =
                                findStringRecursive(
                                        element.getAsJsonObject(),
                                        keys
                                );


                        if (result != null) {

                            return result;
                        }
                    }
                }
            }
        }


        return null;
    }


    // =============================================================
    // FIND INTEGER
    // =============================================================

    private int findIntRecursive(
            JsonObject object,
            String... keys
    ) {

        if (object == null) {
            return 0;
        }


        for (String key :
                keys) {

            if (object.has(key)
                    && !object.get(key).isJsonNull()) {

                try {

                    return object.get(key)
                            .getAsInt();

                } catch (Exception ignored) {
                }
            }
        }


        for (Map.Entry<String, JsonElement> entry :
                object.entrySet()) {

            JsonElement value =
                    entry.getValue();


            if (value == null
                    || value.isJsonNull()) {

                continue;
            }


            if (value.isJsonObject()) {

                int result =
                        findIntRecursive(
                                value.getAsJsonObject(),
                                keys
                        );


                if (result != 0) {

                    return result;
                }
            }


            if (value.isJsonArray()) {

                for (JsonElement element :
                        value.getAsJsonArray()) {

                    if (element.isJsonObject()) {

                        int result =
                                findIntRecursive(
                                        element.getAsJsonObject(),
                                        keys
                                );


                        if (result != 0) {

                            return result;
                        }
                    }
                }
            }
        }


        return 0;
    }


    // =============================================================
    // FIND DOUBLE
    // =============================================================

    private double findDoubleRecursive(
            JsonObject object,
            String... keys
    ) {

        if (object == null) {
            return 0;
        }


        for (String key :
                keys) {

            if (object.has(key)
                    && !object.get(key).isJsonNull()) {

                try {

                    return object.get(key)
                            .getAsDouble();

                } catch (Exception ignored) {
                }
            }
        }


        for (Map.Entry<String, JsonElement> entry :
                object.entrySet()) {

            JsonElement value =
                    entry.getValue();


            if (value == null
                    || value.isJsonNull()) {

                continue;
            }


            if (value.isJsonObject()) {

                double result =
                        findDoubleRecursive(
                                value.getAsJsonObject(),
                                keys
                        );


                if (result != 0) {

                    return result;
                }
            }


            if (value.isJsonArray()) {

                for (JsonElement element :
                        value.getAsJsonArray()) {

                    if (element.isJsonObject()) {

                        double result =
                                findDoubleRecursive(
                                        element.getAsJsonObject(),
                                        keys
                                );


                        if (result != 0) {

                            return result;
                        }
                    }
                }
            }
        }


        return 0;
    }


    // =============================================================
    // ISSUE MODEL
    // =============================================================

    private static class IssueData {

        String category;

        int count;

        int ward;

        double priorityScore;

        double averageWeight;
    }


    // =============================================================
    // SOLUTION MODEL
    // =============================================================

    private static class SolutionData {

        String name;

        String city;

        int ward;

        double priority;
    }


    // =============================================================
    // DEMOGRAPHIC API
    // =============================================================

    interface DemographicApi {

        @GET("priorities")
        Call<JsonObject> getPriorities(
                @Query("city") String city
        );
    }


    // =============================================================
    // SOLUTION API
    // =============================================================

    interface SolutionApi {

        @GET("priorities")
        Call<JsonObject> getPriorities(
                @Query("city") String city
        );
    }


    // =============================================================
    // DP TO PX
    // =============================================================

    public int dpToPx(
            int dp
    ) {

        return Math.round(
                dp *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }
}