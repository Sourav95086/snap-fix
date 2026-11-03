package com.nextgenius.snapfix;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nextgenius.snapfix.Model.CategoryIssue;

import java.util.List;
import java.util.Locale;

public class CategoryIssueAdapter
        extends RecyclerView.Adapter<CategoryIssueAdapter.ViewHolder> {

    private final Context context;
    private List<CategoryIssue> issues;

    public CategoryIssueAdapter(
            Context context,
            List<CategoryIssue> issues
    ) {
        this.context = context;
        this.issues = issues;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(context).inflate(
                        R.layout.item_issue,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        CategoryIssue issue =
                issues.get(position);

        // ========================================================
        // ISSUE ID
        // ========================================================

        holder.tvIssueId.setText(
                "ISSUE #" + issue.issue_id
        );


        // ========================================================
        // ISSUE WEIGHT
        // ========================================================

        holder.tvWeight.setText(
                formatWeight(
                        issue.issue_weight
                )
        );


        // ========================================================
        // DESCRIPTION
        // ========================================================

        holder.tvDescription.setText(
                issue.issue_description
        );


        // ========================================================
        // ESTIMATED COST
        // ========================================================

        holder.tvCost.setText(
                issue.estimated_cost_range
        );


        // ========================================================
        // REPORT ID
        // ========================================================

        holder.tvReportId.setText(
                "REPORT #" + issue.report_id
        );


        // ========================================================
        // LOCATION
        // ========================================================

        holder.tvLocation.setText(
                "📍 " + issue.issue_location
        );


        // ========================================================
        // COORDINATES
        // ========================================================

        holder.tvCoordinates.setText(
                String.format(
                        Locale.US,
                        "%.4f, %.4f",
                        issue.latitude,
                        issue.longitude
                )
        );


        // ========================================================
        // REPORTER
        // ========================================================

        holder.tvReporter.setText(
                "Reported by "
                        + issue.reported_by_name
        );


        // ========================================================
        // CLICK ISSUE
        // ========================================================

        holder.itemView.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    context,
                                    CategoryIssueDetailsActivity.class
                            );

                    /*
                     * IMPORTANT:
                     *
                     * The details API requires REPORT ID.
                     *
                     * Example:
                     *
                     * report_id = 408
                     *
                     * becomes:
                     *
                     * GET /issue-report/408
                     */

                    intent.putExtra(
                            "report_id",
                            issue.report_id
                    );

                    context.startActivity(
                            intent
                    );
                }
        );
    }


    // ============================================================
    // FORMAT WEIGHT
    // ============================================================

    private String formatWeight(
            double weight
    ) {

        if (weight == (int) weight) {

            return String.valueOf(
                    (int) weight
            );
        }

        return String.valueOf(
                weight
        );
    }


    // ============================================================
    // ITEM COUNT
    // ============================================================

    @Override
    public int getItemCount() {

        return issues == null
                ? 0
                : issues.size();
    }


    // ============================================================
    // UPDATE ISSUES
    // ============================================================

    public void updateIssues(
            List<CategoryIssue> newIssues
    ) {

        this.issues = newIssues;

        notifyDataSetChanged();
    }


    // ============================================================
    // VIEW HOLDER
    // ============================================================

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvIssueId;
        TextView tvWeight;
        TextView tvDescription;
        TextView tvCost;
        TextView tvReportId;
        TextView tvLocation;
        TextView tvCoordinates;
        TextView tvReporter;


        public ViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);


            tvIssueId =
                    itemView.findViewById(
                            R.id.tvIssueId
                    );


            tvWeight =
                    itemView.findViewById(
                            R.id.tvWeight
                    );


            tvDescription =
                    itemView.findViewById(
                            R.id.tvDescription
                    );


            tvCost =
                    itemView.findViewById(
                            R.id.tvCost
                    );


            tvReportId =
                    itemView.findViewById(
                            R.id.tvReportId
                    );


            tvLocation =
                    itemView.findViewById(
                            R.id.tvLocation
                    );


            tvCoordinates =
                    itemView.findViewById(
                            R.id.tvCoordinates
                    );


            tvReporter =
                    itemView.findViewById(
                            R.id.tvReporter
                    );
        }
    }
}