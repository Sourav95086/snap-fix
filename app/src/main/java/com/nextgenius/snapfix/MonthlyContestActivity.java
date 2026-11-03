package com.nextgenius.snapfix;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nextgenius.snapfix.Model.ContestReporter;
import com.nextgenius.snapfix.Model.SendWinnerEmailRequest;
import com.nextgenius.snapfix.Model.SendWinnerEmailResponse;
import com.nextgenius.snapfix.Model.TopReportersResponse;
import com.nextgenius.snapfix.api.ContestApi;
import com.nextgenius.snapfix.api.ContestRetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MonthlyContestActivity extends AppCompatActivity {

    private static final String TAG = "MonthlyContest";

    private static final String ISSUE_LOCATION =
            "Bhubaneswar";

    private RecyclerView winnersRecyclerView;

    private ProgressBar progressBar;

    private TextView totalIssuesText;
    private TextView totalReportersText;
    private TextView topReportsText;
    private TextView winnersText;

    private Button reevaluateButton;

    private ContestApi contestApi;


    // ============================================================
    // ACTIVITY CREATE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_monthly_contest
        );

        initializeViews();

        contestApi =
                ContestRetrofitClient.getApi();

        /*
         * First load:
         *
         * true = send winner emails automatically
         */
        loadTopReporters(true);
    }


    // ============================================================
    // INITIALIZE VIEWS
    // ============================================================

    private void initializeViews() {

        winnersRecyclerView =
                findViewById(
                        R.id.winnersRecyclerView
                );

        progressBar =
                findViewById(
                        R.id.progressBar
                );

        totalIssuesText =
                findViewById(
                        R.id.totalIssuesText
                );

        totalReportersText =
                findViewById(
                        R.id.totalReportersText
                );

        topReportsText =
                findViewById(
                        R.id.topReportsText
                );

        winnersText =
                findViewById(
                        R.id.winnersText
                );

        reevaluateButton =
                findViewById(
                        R.id.reevaluateButton
                );


        winnersRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );


        // ========================================================
        // RE-EVALUATE BUTTON
        // ========================================================

        reevaluateButton.setOnClickListener(
                v -> reevaluateResults()
        );
    }


    // ============================================================
    // RE-EVALUATE RESULTS
    // ============================================================

    private void reevaluateResults() {

        Log.d(
                TAG,
                "================================"
        );

        Log.d(
                TAG,
                "RE-EVALUATING CONTEST RESULTS"
        );

        Log.d(
                TAG,
                "================================"
        );


        reevaluateButton.setEnabled(false);

        reevaluateButton.setText(
                "RE-EVALUATING..."
        );


        /*
         * false means:
         *
         * Fetch latest results
         * DO NOT send winner emails again
         */

        loadTopReporters(false);
    }


    // ============================================================
    // API 2
    //
    // GET /top-reporters
    //
    // ?issue_location=Bhubaneswar
    // ============================================================

    private void loadTopReporters(
            boolean sendEmails
    ) {

        progressBar.setVisibility(
                View.VISIBLE
        );


        Log.d(
                TAG,
                "Fetching top reporters for "
                        + ISSUE_LOCATION
        );


        Call<TopReportersResponse> call =
                contestApi.getTopReporters(
                        ISSUE_LOCATION
                );


        call.enqueue(
                new Callback<TopReportersResponse>() {

                    @Override
                    public void onResponse(
                            Call<TopReportersResponse> call,
                            Response<TopReportersResponse> response
                    ) {

                        progressBar.setVisibility(
                                View.GONE
                        );


                        // =================================================
                        // HTTP ERROR
                        // =================================================

                        if (!response.isSuccessful()) {

                            Log.e(
                                    TAG,
                                    "Top reporters API error: "
                                            + response.code()
                            );


                            Toast.makeText(
                                    MonthlyContestActivity.this,
                                    "Failed to load contest",
                                    Toast.LENGTH_SHORT
                            ).show();


                            resetReevaluateButton();

                            return;
                        }


                        // =================================================
                        // RESPONSE
                        // =================================================

                        TopReportersResponse data =
                                response.body();


                        if (data == null) {

                            Log.e(
                                    TAG,
                                    "Response body is null"
                            );


                            Toast.makeText(
                                    MonthlyContestActivity.this,
                                    "Empty contest response",
                                    Toast.LENGTH_SHORT
                            ).show();


                            resetReevaluateButton();

                            return;
                        }


                        // =================================================
                        // LOG DATA
                        // =================================================

                        Log.d(
                                TAG,
                                "Total issues: "
                                        + data.getTotalIssues()
                        );


                        Log.d(
                                TAG,
                                "Total reporters: "
                                        + data.getTotalUniqueReporters()
                        );


                        // =================================================
                        // SCORE MATRIX
                        // =================================================

                        totalIssuesText.setText(
                                String.valueOf(
                                        data.getTotalIssues()
                                )
                        );


                        totalReportersText.setText(
                                String.valueOf(
                                        data.getTotalUniqueReporters()
                                )
                        );


                        // =================================================
                        // GET TOP REPORTERS
                        // =================================================

                        List<ContestReporter> reporters =
                                data.getTopReporters();


                        if (
                                reporters == null ||
                                        reporters.isEmpty()
                        ) {

                            Toast.makeText(
                                    MonthlyContestActivity.this,
                                    "No winners found",
                                    Toast.LENGTH_SHORT
                            ).show();


                            topReportsText.setText(
                                    "0"
                            );


                            winnersText.setText(
                                    "0"
                            );


                            winnersRecyclerView.setAdapter(
                                    null
                            );


                            resetReevaluateButton();

                            return;
                        }


                        // =================================================
                        // TOP REPORTS
                        //
                        // First reporter has the highest report count
                        // =================================================

                        int topReports =
                                reporters.get(0)
                                        .getReportCount();


                        topReportsText.setText(
                                String.valueOf(
                                        topReports
                                )
                        );


                        // =================================================
                        // NUMBER OF WINNERS
                        // =================================================

                        int numberOfWinners =
                                Math.min(
                                        reporters.size(),
                                        3
                                );


                        winnersText.setText(
                                String.valueOf(
                                        numberOfWinners
                                )
                        );


                        // =================================================
                        // DISPLAY WINNERS
                        // =================================================

                        ContestWinnerAdapter adapter =
                                new ContestWinnerAdapter(
                                        reporters
                                );


                        winnersRecyclerView.setAdapter(
                                adapter
                        );


                        // =================================================
                        // AUTOMATIC EMAILS
                        //
                        // Only happens on initial loading.
                        //
                        // Re-evaluation does NOT send emails again.
                        // =================================================

                        if (sendEmails) {

                            Log.d(
                                    TAG,
                                    "Initial contest load."
                            );

                            Log.d(
                                    TAG,
                                    "Sending winner emails..."
                            );


                            sendEmailsToWinners(
                                    reporters
                            );

                        } else {

                            Log.d(
                                    TAG,
                                    "Re-evaluation complete."
                            );

                            Log.d(
                                    TAG,
                                    "Winner emails will NOT be sent again."
                            );
                        }


                        // =================================================
                        // RESET BUTTON
                        // =================================================

                        resetReevaluateButton();
                    }


                    @Override
                    public void onFailure(
                            Call<TopReportersResponse> call,
                            Throwable t
                    ) {

                        progressBar.setVisibility(
                                View.GONE
                        );


                        Log.e(
                                TAG,
                                "Top reporters request failed",
                                t
                        );


                        Toast.makeText(
                                MonthlyContestActivity.this,
                                "Network error",
                                Toast.LENGTH_SHORT
                        ).show();


                        resetReevaluateButton();
                    }
                }
        );
    }


    // ============================================================
    // RESET RE-EVALUATE BUTTON
    // ============================================================

    private void resetReevaluateButton() {

        if (reevaluateButton == null) {
            return;
        }


        reevaluateButton.setEnabled(
                true
        );


        reevaluateButton.setText(
                "RE-EVALUATE RESULTS"
        );
    }


    // ============================================================
    // AUTOMATIC EMAIL PROCESS
    // ============================================================

    private void sendEmailsToWinners(
            List<ContestReporter> reporters
    ) {

        Log.d(
                TAG,
                "================================"
        );

        Log.d(
                TAG,
                "STARTING WINNER EMAIL PROCESS"
        );

        Log.d(
                TAG,
                "================================"
        );


        for (
                ContestReporter reporter :
                reporters
        ) {

            if (reporter == null) {
                continue;
            }


            int rank =
                    reporter.getRank();


            String email =
                    reporter.getEmail();


            String name =
                    reporter.getName();


            // ========================================================
            // ONLY TOP 3
            // ========================================================

            if (
                    rank < 1 ||
                            rank > 3
            ) {

                continue;
            }


            // ========================================================
            // EMAIL CHECK
            // ========================================================

            if (
                    email == null ||
                            email.trim().isEmpty()
            ) {

                Log.e(
                        TAG,
                        "No email for rank "
                                + rank
                                + " - "
                                + name
                );

                continue;
            }


            Log.d(
                    TAG,
                    "Preparing email for rank "
                            + rank
                            + " | "
                            + email
            );


            sendWinnerEmail(
                    email,
                    rank
            );
        }
    }


    // ============================================================
    // API 1
    //
    // POST /send-winner-email
    //
    // {
    //     "email": "sourav95086@gmail.com",
    //     "rank": 3
    // }
    // ============================================================

    private void sendWinnerEmail(
            String email,
            int rank
    ) {

        SendWinnerEmailRequest request =
                new SendWinnerEmailRequest(
                        email,
                        rank
                );


        Log.d(
                TAG,
                "--------------------------------"
        );


        Log.d(
                TAG,
                "POST /send-winner-email"
        );


        Log.d(
                TAG,
                "Email = " + email
        );


        Log.d(
                TAG,
                "Rank = " + rank
        );


        Log.d(
                TAG,
                "--------------------------------"
        );


        Call<SendWinnerEmailResponse> call =
                contestApi.sendWinnerEmail(
                        request
                );


        call.enqueue(
                new Callback<SendWinnerEmailResponse>() {

                    @Override
                    public void onResponse(
                            Call<SendWinnerEmailResponse> call,
                            Response<SendWinnerEmailResponse> response
                    ) {

                        // =================================================
                        // HTTP ERROR
                        // =================================================

                        if (!response.isSuccessful()) {

                            Log.e(
                                    TAG,
                                    "Email API failed"
                            );


                            Log.e(
                                    TAG,
                                    "HTTP code: "
                                            + response.code()
                            );


                            return;
                        }


                        // =================================================
                        // RESPONSE
                        // =================================================

                        SendWinnerEmailResponse data =
                                response.body();


                        if (data == null) {

                            Log.e(
                                    TAG,
                                    "Email response is null"
                            );


                            return;
                        }


                        // =================================================
                        // SUCCESS
                        // =================================================

                        if (data.isSuccess()) {

                            Log.d(
                                    TAG,
                                    "================================"
                            );


                            Log.d(
                                    TAG,
                                    "WINNER EMAIL SENT SUCCESSFULLY"
                            );


                            Log.d(
                                    TAG,
                                    "Rank: "
                                            + data.getRank()
                            );


                            Log.d(
                                    TAG,
                                    "Email: "
                                            + data.getEmail()
                            );


                            Log.d(
                                    TAG,
                                    "Reward: "
                                            + data.getReward()
                            );


                            Log.d(
                                    TAG,
                                    "Email ID: "
                                            + data.getEmailId()
                            );


                            Log.d(
                                    TAG,
                                    "================================"
                            );


                        } else {

                            Log.e(
                                    TAG,
                                    "Email API returned success=false"
                            );


                            Log.e(
                                    TAG,
                                    "Message: "
                                            + data.getMessage()
                            );
                        }
                    }


                    @Override
                    public void onFailure(
                            Call<SendWinnerEmailResponse> call,
                            Throwable t
                    ) {

                        Log.e(
                                TAG,
                                "Winner email request failed",
                                t
                        );
                    }
                }
        );
    }
}