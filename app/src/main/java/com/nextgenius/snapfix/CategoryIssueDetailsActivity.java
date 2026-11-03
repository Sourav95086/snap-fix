package com.nextgenius.snapfix;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import androidx.appcompat.app.AppCompatActivity;

import com.nextgenius.snapfix.Model.CategoryIssueDetails;
import com.nextgenius.snapfix.api.CategoryIssueDetailsApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CategoryIssueDetailsActivity extends AppCompatActivity {

    private static final String BASE_URL =
            "https://api1-production-a997.up.railway.app/";

    private ImageView btnBack;

    private ProgressBar progressBar;

    private TextView tvReportTitle;
    private TextView tvCategory;
    private TextView tvSeverity;

    private TextView tvDescription;

    private TextView tvLocation;

    private TextView tvReportId;
    private TextView tvIssueId;
    private TextView tvAddress;

    private TextView tvWeight;
    private TextView tvCost;

    private TextView tvReporter;
    private TextView tvPhone;

    private TextView tvReportedOn;
    private TextView tvIssueCreated;

    private TextView tvCoordinates;



    private ImageView ivEvidence;
    private TextView tvNoEvidence;

    private int reportId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_category_issue_details
        );

        initializeViews();

        reportId =
                getIntent().getIntExtra(
                        "report_id",
                        -1
                );

        btnBack.setOnClickListener(
                v -> finish()
        );

        if (reportId == -1) {

            showError(
                    "Invalid report ID"
            );

            return;
        }

        loadIssueDetails();
    }

    // ============================================================
    // INITIALIZE VIEWS
    // ============================================================

    private void initializeViews() {

        btnBack =
                findViewById(
                        R.id.btnBack
                );
        tvAddress =
                findViewById(
                        R.id.tvAddress
                );

        progressBar =
                findViewById(
                        R.id.progressBar
                );

        tvReportTitle =
                findViewById(
                        R.id.tvReportTitle
                );

        tvCategory =
                findViewById(
                        R.id.tvCategory
                );

        tvSeverity =
                findViewById(
                        R.id.tvSeverity
                );

        tvDescription =
                findViewById(
                        R.id.tvDescription
                );

        tvLocation =
                findViewById(
                        R.id.tvLocation
                );

        tvReportId =
                findViewById(
                        R.id.tvReportId
                );

        tvIssueId =
                findViewById(
                        R.id.tvIssueId
                );

        tvWeight =
                findViewById(
                        R.id.tvWeight
                );

        tvCost =
                findViewById(
                        R.id.tvCost
                );

        tvReporter =
                findViewById(
                        R.id.tvReporter
                );

        tvPhone =
                findViewById(
                        R.id.tvPhone
                );

        tvReportedOn =
                findViewById(
                        R.id.tvReportedOn
                );

        tvIssueCreated =
                findViewById(
                        R.id.tvIssueCreated
                );

        tvCoordinates =
                findViewById(
                        R.id.tvCoordinates
                );


        ivEvidence =
                findViewById(
                        R.id.ivEvidence
                );

        tvNoEvidence =
                findViewById(
                        R.id.tvNoEvidence
                );
    }

    // ============================================================
    // API
    // ============================================================

    private void loadIssueDetails() {

        progressBar.setVisibility(
                View.VISIBLE
        );

        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(
                                GsonConverterFactory.create()
                        )
                        .build();

        CategoryIssueDetailsApi api =
                retrofit.create(
                        CategoryIssueDetailsApi.class
                );

        api.getIssueReport(
                reportId
        ).enqueue(
                new Callback<CategoryIssueDetails>() {

                    @Override
                    public void onResponse(
                            Call<CategoryIssueDetails> call,
                            Response<CategoryIssueDetails> response
                    ) {

                        progressBar.setVisibility(
                                View.GONE
                        );

                        if (!response.isSuccessful()
                                || response.body() == null) {

                            showError(
                                    "Unable to load issue details"
                            );

                            return;
                        }

                        displayIssueDetails(
                                response.body()
                        );
                    }

                    @Override
                    public void onFailure(
                            Call<CategoryIssueDetails> call,
                            Throwable t
                    ) {

                        progressBar.setVisibility(
                                View.GONE
                        );

                        showError(
                                "Network error"
                        );
                    }
                }
        );
    }

    // ============================================================
    // DISPLAY DETAILS
    // ============================================================

    private void displayIssueDetails(
            CategoryIssueDetails issue
    ) {

        tvReportTitle.setText(
                "REPORT #" + issue.report_id
        );

        tvCategory.setText(
                formatCategory(
                        issue.issue_category
                )
        );

        tvSeverity.setText(
                getSeverity(
                        issue.issue_weight
                )
        );

        tvDescription.setText(
                safeText(
                        issue.issue_description
                )
        );

        tvLocation.setText(
                safeText(
                        issue.issue_location
                )
        );

        tvReportId.setText(
                String.valueOf(
                        issue.report_id
                )
        );

        tvIssueId.setText(
                String.valueOf(
                        issue.issue_id
                )
        );
        tvAddress.setText(
                safeText(
                        issue.address
                )
        );

        tvWeight.setText(
                formatNumber(
                        issue.issue_weight
                )
        );

        tvCost.setText(
                safeText(
                        issue.estimated_cost_range
                )
        );

        tvReporter.setText(
                safeText(
                        issue.reported_by_name
                )
        );

        tvPhone.setText(
                safeText(
                        issue.reported_by_phone
                )
        );

        tvReportedOn.setText(
                formatDate(
                        issue.reported_on
                )
        );

        tvIssueCreated.setText(
                formatDate(
                        issue.issue_created_at
                )
        );

        tvCoordinates.setText(
                String.format(
                        Locale.US,
                        "Lat: %.4f    Lng: %.4f",
                        issue.latitude,
                        issue.longitude
                )
        );



        if (issue.evidence == null
                || issue.evidence.trim().isEmpty()) {

            ivEvidence.setVisibility(
                    View.GONE
            );

            tvNoEvidence.setVisibility(
                    View.VISIBLE
            );

        } else {

            ivEvidence.setVisibility(
                    View.VISIBLE
            );

            tvNoEvidence.setVisibility(
                    View.GONE
            );

            Glide.with(this)
                    .load(issue.evidence)
                    .centerCrop()
                    .into(ivEvidence);
        }
    }

    // ============================================================
    // SEVERITY
    // ============================================================

    private String getSeverity(
            double weight
    ) {

        if (weight >= 50) {
            return "HIGH";
        }

        if (weight >= 30) {
            return "MODERATE";
        }

        return "LOW";
    }

    // ============================================================
    // CATEGORY
    // ============================================================

    private String formatCategory(
            String category
    ) {

        if (category == null
                || category.trim().isEmpty()) {

            return "ISSUE";
        }

        return category
                .replace("_", " ")
                .toUpperCase(Locale.US);
    }

    // ============================================================
    // NUMBER
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
    // DATE
    // ============================================================

    private String formatDate(
            String value
    ) {

        if (value == null
                || value.trim().isEmpty()) {

            return "Not available";
        }

        try {

            SimpleDateFormat inputFormat =
                    new SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX",
                            Locale.US
                    );

            Date date =
                    inputFormat.parse(
                            value
                    );

            if (date == null) {
                return value;
            }

            SimpleDateFormat outputFormat =
                    new SimpleDateFormat(
                            "MMM dd, yyyy • hh:mm a",
                            Locale.US
                    );

            return outputFormat.format(
                    date
            );

        } catch (ParseException e) {

            return value;
        }
    }

    // ============================================================
    // SAFE TEXT
    // ============================================================

    private String safeText(
            String value
    ) {

        if (value == null
                || value.trim().isEmpty()) {

            return "Not available";
        }

        return value;
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
}