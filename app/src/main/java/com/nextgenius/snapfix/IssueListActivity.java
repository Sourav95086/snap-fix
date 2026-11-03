package com.nextgenius.snapfix;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nextgenius.snapfix.Model.CategoryResponse;
import com.nextgenius.snapfix.api.CategoryIssueApi;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class IssueListActivity extends AppCompatActivity {

    private RecyclerView recyclerIssues;
    private ProgressBar progressBar;
    private TextView tvCategory;
    private TextView tvLocation;
    private TextView tvIssueCount;
    private TextView tvEmpty;
    private ImageView btnBack;

    private CategoryIssueAdapter issueAdapter;

    private String issueCategory;
    private String issueLocation;

    private static final String BASE_URL =
            "https://api1-production-a997.up.railway.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_issue_list);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        initializeViews();

        // Get category and location from Hotspot Mapping screen
        issueCategory =
                getIntent().getStringExtra("issue_category");

        issueLocation =
                getIntent().getStringExtra("issue_location");

        if (issueCategory == null) {
            issueCategory = "";
        }

        if (issueLocation == null) {
            issueLocation = "";
        }

        tvCategory.setText(
                formatCategory(issueCategory)
        );

        tvLocation.setText(
                issueLocation.toUpperCase()
        );

        setupRecyclerView();

        btnBack.setOnClickListener(v -> finish());

        loadIssues();
    }

    private void initializeViews() {

        recyclerIssues =
                findViewById(R.id.recyclerIssues);

        progressBar =
                findViewById(R.id.progressBar);

        tvCategory =
                findViewById(R.id.tvCategory);

        tvLocation =
                findViewById(R.id.tvLocation);

        tvIssueCount =
                findViewById(R.id.tvIssueCount);

        tvEmpty =
                findViewById(R.id.tvEmpty);

        btnBack =
                findViewById(R.id.btnBack);
    }

    private void setupRecyclerView() {

        issueAdapter =
                new CategoryIssueAdapter(
                        this,
                        new ArrayList<>()
                );

        recyclerIssues.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerIssues.setAdapter(issueAdapter);

        recyclerIssues.setHasFixedSize(true);
    }

    private void loadIssues() {

        progressBar.setVisibility(View.VISIBLE);

        recyclerIssues.setVisibility(View.GONE);

        tvEmpty.setVisibility(View.GONE);

        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(
                                GsonConverterFactory.create()
                        )
                        .build();

        CategoryIssueApi categoryIssueApi =
                retrofit.create(
                        CategoryIssueApi.class
                );

        Call<CategoryResponse> call =
                categoryIssueApi.getCategoryIssues(
                        issueCategory,
                        issueLocation
                );

        call.enqueue(
                new Callback<CategoryResponse>() {

                    @Override
                    public void onResponse(
                            Call<CategoryResponse> call,
                            Response<CategoryResponse> response
                    ) {

                        progressBar.setVisibility(
                                View.GONE
                        );

                        if (!response.isSuccessful()
                                || response.body() == null) {

                            showError(
                                    "Unable to load issues"
                            );

                            return;
                        }

                        CategoryResponse result =
                                response.body();

                        tvIssueCount.setText(
                                result.total_issues
                                        + " ISSUES FOUND"
                        );

                        if (result.issues == null
                                || result.issues.isEmpty()) {

                            tvEmpty.setText(
                                    "No issues found for this category"
                            );

                            tvEmpty.setVisibility(
                                    View.VISIBLE
                            );

                            recyclerIssues.setVisibility(
                                    View.GONE
                            );

                            return;
                        }

                        recyclerIssues.setVisibility(
                                View.VISIBLE
                        );

                        issueAdapter.updateIssues(
                                result.issues
                        );
                    }

                    @Override
                    public void onFailure(
                            Call<CategoryResponse> call,
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

    private void showError(String message) {

        recyclerIssues.setVisibility(
                View.GONE
        );

        tvEmpty.setVisibility(
                View.VISIBLE
        );

        tvEmpty.setText(message);

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }

    private String formatCategory(
            String category
    ) {

        if (category == null
                || category.trim().isEmpty()) {

            return "ISSUES";
        }

        return category
                .replace("_", " ")
                .toUpperCase();
    }
}