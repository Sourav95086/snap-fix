package com.nextgenius.snapfix;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class Activity6 extends AppCompatActivity {

    private LinearLayout issuesContainer;

    private ProgressBar progressBar;

    private TextView tvPendingCount;
    private TextView tvValidatedCount;
    private TextView tvRemainingCount;

    private static final String BASE_URL =
            "https://smsparakram-production.up.railway.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_6);

        initializeViews();

        fetchIssues();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
         * Refresh the list whenever we return from
         * ValidateIssueActivity.
         *
         * This makes a successfully validated issue
         * disappear from the pending list.
         */

        if (issuesContainer != null) {
            fetchIssues();
        }
    }

    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        issuesContainer = findViewById(
                R.id.issuesContainer
        );

        progressBar = findViewById(
                R.id.progressBar
        );

        tvPendingCount = findViewById(
                R.id.tvPendingCount
        );

        tvValidatedCount = findViewById(
                R.id.tvValidatedCount
        );

        tvRemainingCount = findViewById(
                R.id.tvRemainingCount
        );
    }

    // =========================================================
    // FETCH ISSUES
    // =========================================================

    private void fetchIssues() {

        progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(
                        GsonConverterFactory.create()
                )
                .build();

        IssuesApi api =
                retrofit.create(IssuesApi.class);

        Call<IssuesResponse> call =
                api.getIssues();

        call.enqueue(new Callback<IssuesResponse>() {

            @Override
            public void onResponse(
                    Call<IssuesResponse> call,
                    Response<IssuesResponse> response
            ) {

                progressBar.setVisibility(View.GONE);

                if (!response.isSuccessful()
                        || response.body() == null) {

                    Toast.makeText(
                            Activity6.this,
                            "Failed to fetch issues",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                IssuesResponse result =
                        response.body();

                if (!result.success
                        || result.issues == null) {

                    Toast.makeText(
                            Activity6.this,
                            "No issue data available",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                processIssues(result.issues);
            }

            @Override
            public void onFailure(
                    Call<IssuesResponse> call,
                    Throwable t
            ) {

                progressBar.setVisibility(View.GONE);

                Toast.makeText(
                        Activity6.this,
                        "Connection failed: "
                                + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    // =========================================================
    // PROCESS ISSUES
    // =========================================================

    private void processIssues(
            List<Issue> allIssues
    ) {

        int validatedCount = 0;

        List<Issue> nonValidatedIssues =
                new ArrayList<>();

        for (Issue issue : allIssues) {

            if (issue.validated) {

                validatedCount++;

            } else {

                nonValidatedIssues.add(issue);
            }
        }

        /*
         * Pending = only validated == false
         */

        int pendingCount =
                nonValidatedIssues.size()+validatedCount;

        /*
         * Remaining = only issues still pending.
         */

        int remainingCount =
                pendingCount - validatedCount;

        tvPendingCount.setText(
                String.valueOf(pendingCount)
        );

        tvValidatedCount.setText(
                String.valueOf(validatedCount)
        );

        tvRemainingCount.setText(
                String.valueOf(remainingCount)
        );

        displayIssues(
                nonValidatedIssues
        );
    }

    // =========================================================
    // DISPLAY ISSUES
    // =========================================================

    private void displayIssues(
            List<Issue> issues
    ) {

        issuesContainer.removeAllViews();

        if (issues == null || issues.isEmpty()) {

            TextView emptyText =
                    new TextView(this);

            emptyText.setText(
                    "NO PENDING ISSUES"
            );

            emptyText.setTextColor(
                    android.graphics.Color.parseColor(
                            "#7C8A9A"
                    )
            );

            emptyText.setTextSize(13);

            emptyText.setGravity(
                    android.view.Gravity.CENTER
            );

            emptyText.setPadding(
                    0,
                    50,
                    0,
                    50
            );

            issuesContainer.addView(
                    emptyText
            );

            return;
        }

        for (Issue issue : issues) {

            addIssueCard(issue);
        }
    }

    // =========================================================
    // CREATE ISSUE CARD
    // =========================================================

    private void addIssueCard(
            Issue issue
    ) {

        View card =
                LayoutInflater
                        .from(this)
                        .inflate(
                                R.layout.item_validate_issue,
                                issuesContainer,
                                false
                        );

        TextView tvIssueId =
                card.findViewById(
                        R.id.tvIssueId
                );

        TextView tvReporter =
                card.findViewById(
                        R.id.tvReporter
                );

        TextView tvDescription =
                card.findViewById(
                        R.id.tvDescription
                );

        TextView tvLocation =
                card.findViewById(
                        R.id.tvLocation
                );

        TextView tvLandmark =
                card.findViewById(
                        R.id.tvLandmark
                );

        TextView tvDate =
                card.findViewById(
                        R.id.tvDate
                );

        TextView tvStatus =
                card.findViewById(
                        R.id.tvStatus
                );

        TextView btnValidate =
                card.findViewById(
                        R.id.btnValidate
                );

        // -----------------------------------------------------
        // DISPLAY DATA
        // -----------------------------------------------------

        tvIssueId.setText(
                "#" + issue.id
        );

        tvReporter.setText(
                safeText(issue.reporterName)
        );

        tvDescription.setText(
                safeText(issue.issueDescription)
        );

        tvLocation.setText(
                "Location  •  "
                        + safeText(issue.locationAddress)
        );

        tvLandmark.setText(
                "Landmark  •  "
                        + safeText(issue.landmark)
        );

        tvDate.setText(
                formatDate(issue.createdAt)
        );

        tvStatus.setText(
                "NOT VALIDATED"
        );

        // -----------------------------------------------------
        // VALIDATE BUTTON
        // -----------------------------------------------------

        btnValidate.setOnClickListener(v -> {

            Intent intent = new Intent(
                    Activity6.this,
                    ValidateIssueActivity.class
            );

            /*
             * We only pass the ID.
             *
             * ValidateIssueActivity will fetch the
             * complete report from:
             *
             * GET /report/{id}
             */

            intent.putExtra(
                    "issue_id",
                    issue.id
            );

            startActivity(intent);
        });

        issuesContainer.addView(card);
    }

    // =========================================================
    // SAFE TEXT
    // =========================================================

    private String safeText(
            String value
    ) {

        if (value == null
                || value.trim().isEmpty()) {

            return "Not available";
        }

        return value;
    }

    // =========================================================
    // FORMAT DATE
    // =========================================================

    private String formatDate(
            String input
    ) {

        if (input == null
                || input.isEmpty()) {

            return "Date unavailable";
        }

        try {

            SimpleDateFormat inputFormat =
                    new SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX",
                            Locale.US
                    );

            Date date =
                    inputFormat.parse(input);

            SimpleDateFormat outputFormat =
                    new SimpleDateFormat(
                            "dd MMM yyyy  •  hh:mm a",
                            Locale.US
                    );

            return outputFormat.format(date);

        } catch (ParseException e) {

            return input;
        }
    }

    // =========================================================
    // RETROFIT API
    // =========================================================

    public interface IssuesApi {

        @GET("issues")
        Call<IssuesResponse> getIssues();
    }

    // =========================================================
    // RESPONSE MODEL
    // =========================================================

    public static class IssuesResponse {

        @SerializedName("success")
        boolean success;

        @SerializedName("count")
        int count;

        @SerializedName("issues")
        List<Issue> issues;
    }

    // =========================================================
    // ISSUE MODEL
    // =========================================================

    public static class Issue {

        @SerializedName("id")
        int id;

        @SerializedName("reporter_name")
        String reporterName;

        @SerializedName("issue_description")
        String issueDescription;

        @SerializedName("location_address")
        String locationAddress;

        @SerializedName("landmark")
        String landmark;

        @SerializedName("created_at")
        String createdAt;

        @SerializedName("validated")
        boolean validated;
    }
}