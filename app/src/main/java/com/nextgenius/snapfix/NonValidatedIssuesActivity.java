package com.nextgenius.snapfix;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

public class NonValidatedIssuesActivity extends AppCompatActivity {

    private LinearLayout issuesContainer;
    private ProgressBar progressBar;

    private TextView tvNonValidatedCount;
    private TextView tvValidatedCount;
    private TextView tvRemainingCount;
    private TextView tvEmpty;

    private static final String BASE_URL =
            "https://smsparakram-production.up.railway.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_non_validated_issues);

        initializeViews();

        fetchIssues();
    }

    private void initializeViews() {

        issuesContainer = findViewById(R.id.issuesContainer);
        progressBar = findViewById(R.id.progressBar);

        tvNonValidatedCount = findViewById(R.id.tvNonValidatedCount);
        tvValidatedCount = findViewById(R.id.tvValidatedCount);
        tvRemainingCount = findViewById(R.id.tvRemainingCount);

        tvEmpty = findViewById(R.id.tvEmpty);
    }

    private void fetchIssues() {

        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IssuesApi api = retrofit.create(IssuesApi.class);

        Call<IssuesResponse> call = api.getIssues();

        call.enqueue(new Callback<IssuesResponse>() {

            @Override
            public void onResponse(
                    Call<IssuesResponse> call,
                    Response<IssuesResponse> response
            ) {

                progressBar.setVisibility(View.GONE);

                if (!response.isSuccessful() || response.body() == null) {

                    showError("Unable to load issues");

                    return;
                }

                IssuesResponse data = response.body();

                if (!data.success || data.issues == null) {

                    showError("No issue data available");

                    return;
                }

                processIssues(data.issues);
            }

            @Override
            public void onFailure(
                    Call<IssuesResponse> call,
                    Throwable t
            ) {

                progressBar.setVisibility(View.GONE);

                showError(
                        "Connection failed"
                );
            }
        });
    }

    private void processIssues(List<Issue> allIssues) {

        int nonValidatedCount = 0;
        int validatedCount = 0;

        List<Issue> nonValidatedIssues = new ArrayList<>();

        for (Issue issue : allIssues) {

            if (issue.validated) {

                validatedCount++;

            } else {

                nonValidatedCount++;

                nonValidatedIssues.add(issue);
            }
        }

        /*
         * Current API has no separate "solved" field.
         *
         * Therefore:
         *
         * validatedCount = issues with validated=true
         * nonValidatedCount = issues with validated=false
         *
         * The "remaining" number represents issues still
         * requiring validation.
         */

        tvNonValidatedCount.setText(
                String.valueOf(nonValidatedCount+validatedCount)
        );

        tvValidatedCount.setText(
                String.valueOf(validatedCount)
        );

        tvRemainingCount.setText(
                String.valueOf(nonValidatedCount)
        );

        displayIssues(nonValidatedIssues);
    }

    private void displayIssues(List<Issue> issues) {

        issuesContainer.removeAllViews();

        if (issues == null || issues.isEmpty()) {

            tvEmpty.setVisibility(View.VISIBLE);

            tvEmpty.setText(
                    "ALL ISSUES ARE VALIDATED"
            );

            return;
        }

        tvEmpty.setVisibility(View.GONE);

        for (Issue issue : issues) {

            View issueCard = createIssueCard(issue);

            issuesContainer.addView(issueCard);
        }
    }

    private View createIssueCard(Issue issue) {

        View card = getLayoutInflater().inflate(
                R.layout.item_non_validated_issue,
                issuesContainer,
                false
        );

        TextView tvIssueId =
                card.findViewById(R.id.tvIssueId);

        TextView tvReporter =
                card.findViewById(R.id.tvReporter);

        TextView tvDescription =
                card.findViewById(R.id.tvDescription);

        TextView tvLocation =
                card.findViewById(R.id.tvLocation);

        TextView tvLandmark =
                card.findViewById(R.id.tvLandmark);

        TextView tvDate =
                card.findViewById(R.id.tvDate);

        TextView tvStatus =
                card.findViewById(R.id.tvStatus);

        tvIssueId.setText(
                "#" + issue.id
        );

        tvReporter.setText(
                issue.reporterName
        );

        tvDescription.setText(
                issue.issueDescription
        );

        tvLocation.setText(
                "Location  •  " + safeText(issue.locationAddress)
        );

        tvLandmark.setText(
                "Landmark  •  " + safeText(issue.landmark)
        );

        tvDate.setText(
                formatDate(issue.createdAt)
        );

        tvStatus.setText(
                "NOT VALIDATED"
        );

        return card;
    }

    private String safeText(String value) {

        if (value == null || value.trim().isEmpty()) {

            return "Not available";
        }

        return value;
    }

    private String formatDate(String input) {

        if (input == null || input.isEmpty()) {

            return "Date unavailable";
        }

        try {

            SimpleDateFormat inputFormat =
                    new SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX",
                            Locale.US
                    );

            Date date = inputFormat.parse(input);

            SimpleDateFormat outputFormat =
                    new SimpleDateFormat(
                            "dd MMM yyyy  •  hh:mm a",
                            Locale.US
                    );

            return outputFormat.format(date);

        } catch (ParseException e) {

            /*
             * Fallback because APIs can sometimes return
             * timestamps with a different number of
             * fractional seconds.
             */

            try {

                String cleaned = input;

                if (cleaned.contains(".")) {

                    int dotIndex = cleaned.indexOf('.');

                    int plusIndex = cleaned.indexOf(
                            '+',
                            dotIndex
                    );

                    if (plusIndex != -1) {

                        cleaned =
                                cleaned.substring(0, dotIndex)
                                        + cleaned.substring(plusIndex);
                    }
                }

                SimpleDateFormat fallbackInput =
                        new SimpleDateFormat(
                                "yyyy-MM-dd'T'HH:mm:ssXXX",
                                Locale.US
                        );

                Date date = fallbackInput.parse(cleaned);

                SimpleDateFormat output =
                        new SimpleDateFormat(
                                "dd MMM yyyy  •  hh:mm a",
                                Locale.US
                        );

                return output.format(date);

            } catch (Exception ignored) {

                return input;
            }
        }
    }

    private void showError(String message) {

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
        ).show();

        tvEmpty.setVisibility(View.VISIBLE);

        tvEmpty.setText(
                "UNABLE TO LOAD ISSUES"
        );
    }

    /*
     * Retrofit API
     */

    public interface IssuesApi {

        @GET("issues")
        Call<IssuesResponse> getIssues();
    }

    /*
     * API response
     */

    public static class IssuesResponse {

        @SerializedName("success")
        boolean success;

        @SerializedName("count")
        int count;

        @SerializedName("issues")
        List<Issue> issues;
    }

    /*
     * Issue model
     */

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