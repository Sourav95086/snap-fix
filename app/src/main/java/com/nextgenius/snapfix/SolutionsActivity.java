package com.nextgenius.snapfix;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.nextgenius.snapfix.Model.SolutionPriority;
import com.nextgenius.snapfix.Model.SolutionPriorityResponse;
import com.nextgenius.snapfix.api.SolutionPriorityApi;
import com.nextgenius.snapfix.api.SolutionPriorityRetrofitClient;
import com.nextgenius.snapfix.api.SolutionSourceIssueApi;
import com.nextgenius.snapfix.api.SolutionSourceIssueRetrofitClient;
import com.nextgenius.snapfix.api.ImpactApi;
import com.nextgenius.snapfix.api.ImpactRetrofitClient;
import com.nextgenius.snapfix.Model.SolutionSourceIssue;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SolutionsActivity extends BaseActivity {


    // ============================================================
    // CONSTANT
    // ============================================================

    private static final String CITY = "Bhubaneswar";


    // ============================================================
    // UI
    // ============================================================

    private LinearLayout solutionsContainer;

    private TextView tvTotalSolutions;

    private TextView tvHighPriority;

    private TextView tvAverageScore;

    private TextView tvSolutionsError;

    private ProgressBar progressSolutions;


    // ============================================================
    // APIS
    // ============================================================

    private SolutionPriorityApi api;

    private SolutionSourceIssueApi sourceIssueApi;

    private ImpactApi impactApi;


    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentLayout(R.layout.activity_solutions);


        // ========================================================
        // INITIALIZE APIS
        // ========================================================

        api =
                SolutionPriorityRetrofitClient.getApi();

        sourceIssueApi =
                SolutionSourceIssueRetrofitClient.getApi();

        impactApi =
                ImpactRetrofitClient.getApi();


        initializeViews();

        setupBottomNavigation();

        loadPriorities();
    }


    // ============================================================
    // INITIALIZE VIEWS
    // ============================================================

    private void initializeViews() {

        solutionsContainer =
                findViewById(
                        R.id.solutionsContainer
                );


        tvTotalSolutions =
                findViewById(
                        R.id.tvTotalSolutions
                );


        tvHighPriority =
                findViewById(
                        R.id.tvHighPriority
                );


        tvAverageScore =
                findViewById(
                        R.id.tvAverageScore
                );


        tvSolutionsError =
                findViewById(
                        R.id.tvSolutionsError
                );


        progressSolutions =
                findViewById(
                        R.id.progressSolutions
                );
    }


    // ============================================================
    // LOAD SOLUTION PRIORITIES
    // ============================================================

    private void loadPriorities() {

        progressSolutions.setVisibility(
                View.VISIBLE
        );


        tvSolutionsError.setVisibility(
                View.GONE
        );


        solutionsContainer.removeAllViews();


        api.getPriorities(CITY).enqueue(
                new Callback<SolutionPriorityResponse>() {

                    @Override
                    public void onResponse(
                            Call<SolutionPriorityResponse> call,
                            Response<SolutionPriorityResponse> response) {


                        progressSolutions.setVisibility(
                                View.GONE
                        );


                        // =================================================
                        // HTTP ERROR
                        // =================================================

                        if (!response.isSuccessful()) {

                            showError(
                                    "Unable to load solutions. HTTP "
                                            + response.code()
                            );

                            return;
                        }


                        // =================================================
                        // RESPONSE BODY
                        // =================================================

                        SolutionPriorityResponse body =
                                response.body();


                        if (body == null) {

                            showError(
                                    "Empty response received."
                            );

                            return;
                        }


                        // =================================================
                        // API SUCCESS
                        // =================================================

                        if (!body.isSuccess()) {

                            showError(
                                    "Priority API returned success=false."
                            );

                            return;
                        }


                        // =================================================
                        // SOLUTIONS
                        // =================================================

                        List<SolutionPriority> solutions =
                                body.getSolutions();


                        if (solutions == null ||
                                solutions.isEmpty()) {

                            showEmptyState();

                            return;
                        }


                        // =================================================
                        // UPDATE SUMMARY
                        // =================================================

                        updateSummary(
                                solutions
                        );


                        // =================================================
                        // DISPLAY CARDS
                        // =================================================

                        displaySolutions(
                                solutions
                        );
                    }


                    @Override
                    public void onFailure(
                            Call<SolutionPriorityResponse> call,
                            Throwable t) {

                        progressSolutions.setVisibility(
                                View.GONE
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

    private void updateSummary(
            List<SolutionPriority> solutions) {


        int total =
                solutions.size();


        int highPriority =
                0;


        double totalScore =
                0;


        for (SolutionPriority solution :
                solutions) {


            double score =
                    solution.getSolutionPriority();


            totalScore += score;


            if (score >= 80) {

                highPriority++;
            }
        }


        double averageScore =
                totalScore / total;


        tvTotalSolutions.setText(
                String.valueOf(total)
        );


        tvHighPriority.setText(
                String.valueOf(highPriority)
        );


        tvAverageScore.setText(
                String.format(
                        Locale.US,
                        "%.1f",
                        averageScore
                )
        );
    }


    // ============================================================
    // DISPLAY SOLUTIONS
    // ============================================================

    private void displaySolutions(
            List<SolutionPriority> solutions) {


        solutionsContainer.removeAllViews();


        for (int i = 0;
             i < solutions.size();
             i++) {


            SolutionPriority solution =
                    solutions.get(i);


            View card =
                    createSolutionCard(
                            solution,
                            i + 1
                    );


            // ====================================================
            // CARD CLICK
            // ====================================================

            card.setOnClickListener(v -> {

                loadSolutionImpact(
                        solution
                );

            });


            solutionsContainer.addView(
                    card
            );
        }
    }


    // ============================================================
    // CREATE SOLUTION CARD
    // ============================================================

    private View createSolutionCard(
            SolutionPriority solution,
            int rank) {


        // ========================================================
        // CARD
        // ========================================================

        LinearLayout card =
                new LinearLayout(this);


        card.setOrientation(
                LinearLayout.VERTICAL
        );


        card.setPadding(
                dp(16),
                dp(16),
                dp(16),
                dp(16)
        );


        card.setBackgroundResource(
                R.drawable.bg_solution_card
        );


        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );


        cardParams.setMargins(
                0,
                dp(6),
                0,
                dp(8)
        );


        card.setLayoutParams(
                cardParams
        );


        // ========================================================
        // TOP ROW
        // ========================================================

        LinearLayout topRow =
                new LinearLayout(this);


        topRow.setOrientation(
                LinearLayout.HORIZONTAL
        );


        topRow.setGravity(
                Gravity.CENTER_VERTICAL
        );


        // ========================================================
        // RANK
        // ========================================================

        TextView rankText =
                new TextView(this);


        rankText.setText(
                "#" + rank
        );


        rankText.setTextColor(
                Color.WHITE
        );


        rankText.setTextSize(
                13
        );


        rankText.setTypeface(
                null,
                Typeface.BOLD
        );


        rankText.setGravity(
                Gravity.CENTER
        );


        rankText.setPadding(
                dp(10),
                dp(7),
                dp(10),
                dp(7)
        );


        rankText.setBackgroundResource(
                R.drawable.bg_priority_badge
        );


        topRow.addView(
                rankText
        );


        // ========================================================
        // SOLUTION NAME
        // ========================================================

        TextView solutionName =
                new TextView(this);


        solutionName.setText(
                solution.getSolutionName()
        );


        solutionName.setTextColor(
                Color.WHITE
        );


        solutionName.setTextSize(
                17
        );


        solutionName.setTypeface(
                null,
                Typeface.BOLD
        );


        solutionName.setPadding(
                dp(12),
                0,
                dp(8),
                0
        );


        LinearLayout.LayoutParams nameParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );


        topRow.addView(
                solutionName,
                nameParams
        );


        // ========================================================
        // PRIORITY BADGE
        // ========================================================

        TextView priorityBadge =
                new TextView(this);


        String priorityLevel =
                getPriorityLevel(
                        solution.getSolutionPriority()
                );


        priorityBadge.setText(
                priorityLevel
        );


        priorityBadge.setTextSize(
                11
        );


        priorityBadge.setTypeface(
                null,
                Typeface.BOLD
        );


        priorityBadge.setGravity(
                Gravity.CENTER
        );


        priorityBadge.setPadding(
                dp(10),
                dp(6),
                dp(10),
                dp(6)
        );


        priorityBadge.setBackgroundResource(
                R.drawable.bg_priority_badge
        );


        setPriorityColor(
                priorityBadge,
                priorityLevel
        );


        topRow.addView(
                priorityBadge
        );


        card.addView(
                topRow
        );


        // ========================================================
        // PRIORITY SCORE
        // ========================================================

        TextView score =
                new TextView(this);


        score.setText(
                String.format(
                        Locale.US,
                        "Priority Score  %.1f",
                        solution.getSolutionPriority()
                )
        );


        score.setTextColor(
                Color.rgb(
                        0,
                        217,
                        255
                )
        );


        score.setTextSize(
                15
        );


        score.setTypeface(
                null,
                Typeface.BOLD
        );


        LinearLayout.LayoutParams scoreParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );


        scoreParams.setMargins(
                0,
                dp(14),
                0,
                dp(8)
        );


        card.addView(
                score,
                scoreParams
        );


        // ========================================================
        // LOCATION
        // ========================================================

        TextView location =
                new TextView(this);


        location.setText(
                "Ward "
                        + solution.getWardNumber()
                        + "   •   "
                        + solution.getCity()
        );


        location.setTextColor(
                Color.rgb(
                        139,
                        149,
                        167
                )
        );


        location.setTextSize(
                13
        );


        card.addView(
                location
        );


        return card;
    }


    // ============================================================
    // SOLUTION → SOURCE ISSUE → IMPACT
    // ============================================================

    private void loadSolutionImpact(
            SolutionPriority solution) {


        /*
         * IMPORTANT:
         *
         * The solution ID is a UUID.
         *
         * Example:
         *
         * 31c64535-73a2-4adb-80a4-669130e3abce
         *
         * Therefore it must be passed as String.
         */

        String solutionId =
                solution.getId();


        if (solutionId == null ||
                solutionId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Invalid solution ID.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        // ========================================================
        // SHOW LOADING
        // ========================================================

        Dialog loadingDialog =
                createLoadingDialog(
                        "Loading solution impact..."
                );


        loadingDialog.show();


        // ========================================================
        // API 2
        //
        // solution_id
        //       ↓
        // source_issue_id
        // ========================================================

        sourceIssueApi
                .getSourceIssueId(
                        solutionId
                )
                .enqueue(
                        new Callback<SolutionSourceIssue>() {

                            @Override
                            public void onResponse(
                                    Call<SolutionSourceIssue> call,
                                    Response<SolutionSourceIssue> response) {


                                if (!response.isSuccessful()) {

                                    loadingDialog.dismiss();

                                    showErrorToast(
                                            "Source issue API failed. HTTP "
                                                    + response.code()
                                    );

                                    return;
                                }


                                SolutionSourceIssue sourceIssue =
                                        response.body();


                                if (sourceIssue == null) {

                                    loadingDialog.dismiss();

                                    showErrorToast(
                                            "Source issue response is empty."
                                    );

                                    return;
                                }


                                int issueId =
                                        sourceIssue.getSourceIssueId();


                                if (issueId <= 0) {

                                    loadingDialog.dismiss();

                                    showErrorToast(
                                            "Invalid source issue ID."
                                    );

                                    return;
                                }


                                // =================================================
                                // API 1
                                //
                                // source_issue_id
                                //       ↓
                                // impact data
                                // =================================================

                                loadImpactData(
                                        solution,
                                        issueId,
                                        loadingDialog
                                );
                            }


                            @Override
                            public void onFailure(
                                    Call<SolutionSourceIssue> call,
                                    Throwable t) {


                                loadingDialog.dismiss();


                                showErrorToast(
                                        "Source issue API error: "
                                                + t.getMessage()
                                );
                            }
                        }
                );
    }


    // ============================================================
    // API 1
    // ============================================================

    private void loadImpactData(
            SolutionPriority solution,
            int issueId,
            Dialog loadingDialog) {


        impactApi
                .predictImpact(
                        issueId
                )
                .enqueue(
                        new Callback<JsonObject>() {

                            @Override
                            public void onResponse(
                                    Call<JsonObject> call,
                                    Response<JsonObject> response) {


                                loadingDialog.dismiss();


                                if (!response.isSuccessful()) {

                                    showErrorToast(
                                            "Impact API failed. HTTP "
                                                    + response.code()
                                    );

                                    return;
                                }


                                JsonObject impact =
                                        response.body();


                                if (impact == null) {

                                    showErrorToast(
                                            "Impact response is empty."
                                    );

                                    return;
                                }


                                // =================================================
                                // SHOW POPUP
                                // =================================================

                                showImpactPopup(
                                        solution,
                                        issueId,
                                        impact
                                );
                            }


                            @Override
                            public void onFailure(
                                    Call<JsonObject> call,
                                    Throwable t) {


                                loadingDialog.dismiss();


                                showErrorToast(
                                        "Impact API error: "
                                                + t.getMessage()
                                );
                            }
                        }
                );
    }


    // ============================================================
    // CREATE LOADING DIALOG
    // ============================================================

    private Dialog createLoadingDialog(
            String message) {


        Dialog dialog =
                new Dialog(this);


        LinearLayout layout =
                new LinearLayout(this);


        layout.setOrientation(
                LinearLayout.HORIZONTAL
        );


        layout.setGravity(
                Gravity.CENTER_VERTICAL
        );


        layout.setPadding(
                dp(24),
                dp(20),
                dp(24),
                dp(20)
        );


        layout.setBackgroundColor(
                Color.rgb(
                        11,
                        18,
                        32
                )
        );


        ProgressBar progress =
                new ProgressBar(this);


        layout.addView(
                progress
        );


        TextView text =
                new TextView(this);


        text.setText(
                message
        );


        text.setTextColor(
                Color.WHITE
        );


        text.setTextSize(
                14
        );


        text.setPadding(
                dp(16),
                0,
                0,
                0
        );


        layout.addView(
                text
        );


        dialog.setContentView(
                layout
        );


        dialog.setCancelable(
                false
        );


        if (dialog.getWindow() != null) {

            dialog.getWindow()
                    .setBackgroundDrawable(
                            new ColorDrawable(
                                    Color.TRANSPARENT
                            )
                    );
        }


        return dialog;
    }


    // ============================================================
    // IMPACT POPUP
    // ============================================================

    private void showImpactPopup(
            SolutionPriority solution,
            int sourceIssueId,
            JsonObject impact) {


        Dialog dialog =
                new Dialog(this);


        dialog.setContentView(
                R.layout.popup_solution_impact
        );


        if (dialog.getWindow() != null) {

            dialog.getWindow()
                    .setBackgroundDrawable(
                            new ColorDrawable(
                                    Color.TRANSPARENT
                            )
                    );
        }


        // ========================================================
        // FIND VIEWS
        // ========================================================

        TextView tvPopupCode =
                dialog.findViewById(
                        R.id.tvPopupCode
                );


        TextView tvPopupStatus =
                dialog.findViewById(
                        R.id.tvPopupStatus
                );


        TextView tvPopupTitle =
                dialog.findViewById(
                        R.id.tvPopupTitle
                );


        TextView tvPopupDescription =
                dialog.findViewById(
                        R.id.tvPopupDescription
                );


        TextView tvPopupTarget =
                dialog.findViewById(
                        R.id.tvPopupTarget
                );


        TextView tvPopupCost =
                dialog.findViewById(
                        R.id.tvPopupCost
                );


        TextView tvPopupCostRange =
                dialog.findViewById(
                        R.id.tvPopupCostRange
                );


        TextView tvPopupROI =
                dialog.findViewById(
                        R.id.tvPopupROI
                );


        TextView tvPopupBenefitRatio =
                dialog.findViewById(
                        R.id.tvPopupBenefitRatio
                );


        TextView tvPopupPopulation =
                dialog.findViewById(
                        R.id.tvPopupPopulation
                );


        TextView tvPopupBeneficiaries =
                dialog.findViewById(
                        R.id.tvPopupBeneficiaries
                );


        TextView tvPopupEstimatedCost =
                dialog.findViewById(
                        R.id.tvPopupEstimatedCost
                );


        TextView tvPopupBenefit =
                dialog.findViewById(
                        R.id.tvPopupBenefit
                );


        TextView tvPopupNetBenefit =
                dialog.findViewById(
                        R.id.tvPopupNetBenefit
                );


        TextView tvPopupRatio =
                dialog.findViewById(
                        R.id.tvPopupRatio
                );


        TextView tvPopupScenario =
                dialog.findViewById(
                        R.id.tvPopupScenario
                );


        TextView tvPopupSocialImpact =
                dialog.findViewById(
                        R.id.tvPopupSocialImpact
                );


        TextView tvPopupConfidence =
                dialog.findViewById(
                        R.id.tvPopupConfidence
                );





        TextView btnClose =
                dialog.findViewById(
                        R.id.btnClose
                );


        // ========================================================
        // HEADER
        // ========================================================

        double priority =
                solution.getSolutionPriority();


        tvPopupCode.setText(
                "SOLUTION • "
                        + getPriorityLevel(priority)
                        + " PRIORITY"
        );


        tvPopupStatus.setText(
                "STATUS: IMPACT ANALYSIS READY"
        );


        String intervention =
                getString(
                        impact,
                        "proposed_intervention"
                );


        if (intervention.isEmpty()) {

            intervention =
                    solution.getSolutionName();
        }


        tvPopupTitle.setText(
                intervention
        );


        tvPopupDescription.setText(
                getString(
                        impact,
                        "solution_description"
                )
        );


        // ========================================================
        // LOCATION
        // ========================================================

        JsonObject location =
                getObject(
                        impact,
                        "location"
                );


        if (location != null) {

            String city =
                    getString(
                            location,
                            "city"
                    );


            String state =
                    getString(
                            location,
                            "state"
                    );


            int ward =
                    getInt(
                            location,
                            "ward_number"
                    );


            tvPopupTarget.setText(
                    "Target: "
                            + city
                            + " • "
                            + state
                            + " • Ward "
                            + ward
            );
        }


        // ========================================================
        // SOCIAL IMPACT
        // ========================================================

        JsonObject social =
                getObject(
                        impact,
                        "social_impact"
                );


        if (social != null) {

            double socialScore =
                    getDouble(
                            social,
                            "score"
                    );


            int affectedPopulation =
                    getInt(
                            social,
                            "affected_population"
                    );


            int beneficiaries =
                    getInt(
                            social,
                            "estimated_beneficiaries"
                    );


            tvPopupPopulation.setText(
                    formatNumber(
                            affectedPopulation
                    )
            );


            tvPopupBeneficiaries.setText(
                    formatNumber(
                            beneficiaries
                    )
            );


            tvPopupSocialImpact.setText(
                    "SOCIAL IMPACT SCORE     "
                            + String.format(
                            Locale.US,
                            "%.2f",
                            socialScore
                    )
            );
        }


        // ========================================================
        // ECONOMIC IMPACT
        // ========================================================

        JsonObject economic =
                getObject(
                        impact,
                        "economic_impact"
                );


        if (economic != null) {


            JsonObject cost =
                    getObject(
                            economic,
                            "estimated_cost"
                    );


            JsonObject benefit =
                    getObject(
                            economic,
                            "estimated_benefit"
                    );


            JsonObject netBenefit =
                    getObject(
                            economic,
                            "net_benefit"
                    );


            JsonObject ratio =
                    getObject(
                            economic,
                            "benefit_cost_ratio"
                    );


            // ====================================================
            // COST
            // ====================================================

            if (cost != null) {

                double costMin =
                        getDouble(
                                cost,
                                "min"
                        );


                double costMax =
                        getDouble(
                                cost,
                                "max"
                        );


                tvPopupCost.setText(
                        formatMoney(
                                costMin
                        )
                );


                tvPopupCostRange.setText(
                        "Range  "
                                + formatMoney(costMin)
                                + " – "
                                + formatMoney(costMax)
                );


                tvPopupEstimatedCost.setText(
                        "Initial Infrastructure Cost\n"
                                + formatMoney(costMin)
                                + " – "
                                + formatMoney(costMax)
                );
            }


            // ====================================================
            // BENEFIT
            // ====================================================

            if (benefit != null) {

                double benefitMin =
                        getDouble(
                                benefit,
                                "min"
                        );


                double benefitMax =
                        getDouble(
                                benefit,
                                "max"
                        );


                tvPopupBenefit.setText(
                        "Estimated Economic Benefit\n"
                                + formatMoney(benefitMin)
                                + " – "
                                + formatMoney(benefitMax)
                );


                // Projected ROI card uses maximum
                // possible net benefit below.
            }


            // ====================================================
            // NET BENEFIT
            // ====================================================

            if (netBenefit != null) {

                double netMin =
                        getDouble(
                                netBenefit,
                                "min"
                        );


                double netMax =
                        getDouble(
                                netBenefit,
                                "max"
                        );


                tvPopupROI.setText(
                        formatSignedMoney(
                                netMax
                        )
                );


                tvPopupNetBenefit.setText(
                        "Net Economic Benefit\n"
                                + formatSignedMoney(netMin)
                                + " – "
                                + formatSignedMoney(netMax)
                );
            }


            // ====================================================
            // BENEFIT / COST RATIO
            // ====================================================

            if (ratio != null) {

                double ratioMin =
                        getDouble(
                                ratio,
                                "min"
                        );


                double ratioMax =
                        getDouble(
                                ratio,
                                "max"
                        );


                String ratioText =
                        String.format(
                                Locale.US,
                                "%.2fx – %.2fx",
                                ratioMin,
                                ratioMax
                        );


                tvPopupBenefitRatio.setText(
                        ratioText
                );


                tvPopupRatio.setText(
                        "Benefit / Cost Ratio\n"
                                + ratioText
                );
            }
        }


        // ========================================================
        // EXPECTED CASE
        // ========================================================

        JsonObject scenarios =
                getObject(
                        impact,
                        "scenarios"
                );


        if (scenarios != null) {

            JsonObject expected =
                    getObject(
                            scenarios,
                            "expected_case"
                    );


            if (expected != null) {

                double socialScore =
                        getDouble(
                                expected,
                                "social_impact_score"
                        );


                double cost =
                        getDouble(
                                expected,
                                "cost"
                        );


                double benefit =
                        getDouble(
                                expected,
                                "benefit"
                        );


                double netBenefit =
                        getDouble(
                                expected,
                                "net_benefit"
                        );


                tvPopupScenario.setText(
                        "EXPECTED CASE\n\n"
                                + "Social Impact Score     "
                                + String.format(
                                Locale.US,
                                "%.2f",
                                socialScore
                        )
                                + "\n\n"
                                + "Cost     "
                                + formatMoney(cost)
                                + "\n\n"
                                + "Benefit     "
                                + formatMoney(benefit)
                                + "\n\n"
                                + "Net Benefit     "
                                + formatSignedMoney(netBenefit)
                );
            }
        }


        // ========================================================
        // CONFIDENCE
        // ========================================================

        JsonObject confidence =
                getObject(
                        impact,
                        "confidence"
                );


        if (confidence != null) {

            double socialConfidence =
                    getDouble(
                            confidence,
                            "social"
                    );


            double economicConfidence =
                    getDouble(
                            confidence,
                            "economic"
                    );


            tvPopupConfidence.setText(
                    "CONFIDENCE\n\n"
                            + "Social Confidence       "
                            + String.format(
                            Locale.US,
                            "%.2f%%",
                            socialConfidence
                    )
                            + "\n"
                            + "Economic Confidence     "
                            + String.format(
                            Locale.US,
                            "%.2f%%",
                            economicConfidence
                    )
            );
        }


        // ========================================================
        // AUTHORIZE
        // ========================================================




        // ========================================================
        // DOWNLOAD
        // ========================================================

           


        // ========================================================
        // CLOSE
        // ========================================================

        btnClose.setOnClickListener(v -> {

            dialog.dismiss();

        });


        // ========================================================
        // SHOW DIALOG
        // ========================================================

        dialog.show();


        if (dialog.getWindow() != null) {

            dialog.getWindow()
                    .setLayout(
                            (int) (
                                    getResources()
                                            .getDisplayMetrics()
                                            .widthPixels
                                            * 0.94
                            ),
                            (int) (
                                    getResources()
                                            .getDisplayMetrics()
                                            .heightPixels
                                            * 0.88
                            )
                    );
        }
    }


    // ============================================================
    // JSON OBJECT
    // ============================================================

    private JsonObject getObject(
            JsonObject parent,
            String key) {


        if (parent == null ||
                !parent.has(key) ||
                parent.get(key).isJsonNull()) {

            return null;
        }


        try {

            return parent.getAsJsonObject(
                    key
            );

        } catch (Exception e) {

            return null;
        }
    }


    // ============================================================
    // JSON STRING
    // ============================================================

    private String getString(
            JsonObject object,
            String key) {


        if (object == null ||
                !object.has(key) ||
                object.get(key).isJsonNull()) {

            return "";
        }


        try {

            return object
                    .get(key)
                    .getAsString();

        } catch (Exception e) {

            return "";
        }
    }


    // ============================================================
    // JSON DOUBLE
    // ============================================================

    private double getDouble(
            JsonObject object,
            String key) {


        if (object == null ||
                !object.has(key) ||
                object.get(key).isJsonNull()) {

            return 0;
        }


        try {

            return object
                    .get(key)
                    .getAsDouble();

        } catch (Exception e) {

            return 0;
        }
    }


    // ============================================================
    // JSON INT
    // ============================================================

    private int getInt(
            JsonObject object,
            String key) {


        if (object == null ||
                !object.has(key) ||
                object.get(key).isJsonNull()) {

            return 0;
        }


        try {

            return object
                    .get(key)
                    .getAsInt();

        } catch (Exception e) {

            return 0;
        }
    }


    // ============================================================
    // PRIORITY LEVEL
    // ============================================================

    private String getPriorityLevel(
            double score) {


        if (score >= 80) {

            return "HIGH";

        }


        if (score >= 60) {

            return "MEDIUM";

        }


        return "LOW";
    }


    // ============================================================
    // PRIORITY COLOR
    // ============================================================

    private void setPriorityColor(
            TextView textView,
            String level) {


        if (level.equalsIgnoreCase("HIGH")) {

            textView.setTextColor(
                    Color.rgb(
                            255,
                            77,
                            90
                    )
            );


        } else if (
                level.equalsIgnoreCase("MEDIUM")) {

            textView.setTextColor(
                    Color.rgb(
                            250,
                            204,
                            21
                    )
            );


        } else {

            textView.setTextColor(
                    Color.rgb(
                            34,
                            197,
                            94
                    )
            );
        }
    }


    // ============================================================
    // EMPTY STATE
    // ============================================================

    private void showEmptyState() {


        tvTotalSolutions.setText(
                "0"
        );


        tvHighPriority.setText(
                "0"
        );


        tvAverageScore.setText(
                "0"
        );


        TextView empty =
                new TextView(this);


        empty.setText(
                "No prioritized solutions available."
        );


        empty.setTextColor(
                Color.rgb(
                        139,
                        149,
                        167
                )
        );


        empty.setTextSize(
                15
        );


        empty.setGravity(
                Gravity.CENTER
        );


        empty.setPadding(
                dp(20),
                dp(40),
                dp(20),
                dp(40)
        );


        solutionsContainer.addView(
                empty
        );
    }


    // ============================================================
    // ERROR
    // ============================================================

    private void showError(
            String message) {


        tvSolutionsError.setVisibility(
                View.VISIBLE
        );


        tvSolutionsError.setText(
                message
        );


        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }


    // ============================================================
    // ERROR TOAST
    // ============================================================

    private void showErrorToast(
            String message) {


        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }


    // ============================================================
    // BOTTOM NAVIGATION
    // ============================================================

    private void setupBottomNavigation() {


        View mapTab =
                findViewById(
                        R.id.navMap
                );


        View demographicsTab =
                findViewById(
                        R.id.navDemographics
                );


        View solutionsTab =
                findViewById(
                        R.id.navSolutions
                );


        // ========================================================
        // ACTIVE STATE
        // ========================================================

        setNavigationState(
                mapTab,
                false
        );


        setNavigationState(
                demographicsTab,
                false
        );


        setNavigationState(
                solutionsTab,
                true
        );


        // ========================================================
        // MAP
        // ========================================================

        mapTab.setOnClickListener(v -> {


            Intent intent =
                    new Intent(
                            SolutionsActivity.this,
                            MainActivity.class
                    );


            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            );


            startActivity(
                    intent
            );
        });


        // ========================================================
        // DEMOGRAPHICS
        // ========================================================

        demographicsTab.setOnClickListener(v -> {


            Intent intent =
                    new Intent(
                            SolutionsActivity.this,
                            DemographicsActivity.class
                    );


            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            );


            startActivity(
                    intent
            );
        });


        // ========================================================
        // SOLUTIONS
        // ========================================================

        solutionsTab.setOnClickListener(v -> {

            // Already on Solutions screen.

        });
    }


    // ============================================================
    // NAVIGATION STATE
    // ============================================================

    private void setNavigationState(
            View tab,
            boolean active) {


        ImageView icon =
                tab.findViewById(
                        R.id.navIcon
                );


        TextView text =
                tab.findViewById(
                        R.id.navText
                );


        int color;


        if (active) {

            color =
                    Color.rgb(
                            0,
                            217,
                            255
                    );

        } else {

            color =
                    Color.rgb(
                            119,
                            119,
                            119
                    );
        }


        icon.setColorFilter(
                color
        );


        text.setTextColor(
                color
        );
    }


    // ============================================================
    // FORMAT NUMBER
    // ============================================================

    private String formatNumber(
            int value) {


        if (value >= 1000000) {

            return String.format(
                    Locale.US,
                    "%.1fM",
                    value / 1000000.0
            );
        }


        if (value >= 1000) {

            return String.format(
                    Locale.US,
                    "%.1fK",
                    value / 1000.0
            );
        }


        return String.valueOf(
                value
        );
    }


    // ============================================================
    // FORMAT MONEY
    // ============================================================

    private String formatMoney(
            double value) {


        if (value >= 1000000) {

            return String.format(
                    Locale.US,
                    "$%.2fM",
                    value / 1000000.0
            );
        }


        if (value >= 1000) {

            return String.format(
                    Locale.US,
                    "$%.1fK",
                    value / 1000.0
            );
        }


        return String.format(
                Locale.US,
                "$%.0f",
                value
        );
    }


    // ============================================================
    // FORMAT SIGNED MONEY
    // ============================================================

    private String formatSignedMoney(
            double value) {


        if (value < 0) {

            return String.format(
                    Locale.US,
                    "-$%.1fK",
                    Math.abs(value) / 1000.0
            );
        }


        return String.format(
                Locale.US,
                "+$%.1fK",
                value / 1000.0
        );
    }


    // ============================================================
    // DP
    // ============================================================

    private int dp(
            int value) {


        return (int) (
                value *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }
}