package com.nextgenius.snapfix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nextgenius.snapfix.Model.Issue;

import java.util.List;
import java.util.Locale;

public class IssueAdapter
        extends RecyclerView.Adapter<IssueAdapter.IssueViewHolder> {

    private final Context context;

    private List<Issue> issues;

    public IssueAdapter(
            Context context,
            List<Issue> issues
    ) {

        this.context = context;

        this.issues = issues;
    }

    @NonNull
    @Override
    public IssueViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(context)
                        .inflate(
                                R.layout.item_issue,
                                parent,
                                false
                        );

        return new IssueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull IssueViewHolder holder,
            int position
    ) {

        Issue issue = issues.get(position);

        holder.tvIssueId.setText(
                "ISSUE #" + issue.issue_id
        );

        holder.tvWeight.setText(
                formatWeight(issue.issue_weight)
        );

        holder.tvDescription.setText(
                issue.issue_description
        );

        holder.tvCost.setText(
                issue.estimated_cost_range
        );

        holder.tvReportId.setText(
                "REPORT #" + issue.report_id
        );

        holder.tvLocation.setText(
                "📍 " + issue.issue_location
        );

        holder.tvCoordinates.setText(
                String.format(
                        Locale.US,
                        "%.4f, %.4f",
                        issue.latitude,
                        issue.longitude
                )
        );

        holder.tvReporter.setText(
                "Reported by "
                        + issue.reported_by_name
        );
    }

    private String formatWeight(double weight) {

        if (weight == (int) weight) {

            return String.valueOf(
                    (int) weight
            );
        }

        return String.valueOf(weight);
    }

    @Override
    public int getItemCount() {

        if (issues == null) {
            return 0;
        }

        return issues.size();
    }

    public void updateIssues(
            List<Issue> newIssues
    ) {

        this.issues = newIssues;

        notifyDataSetChanged();
    }

    public static class IssueViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvIssueId;
        TextView tvWeight;
        TextView tvDescription;
        TextView tvCost;
        TextView tvReportId;
        TextView tvLocation;
        TextView tvCoordinates;
        TextView tvReporter;

        public IssueViewHolder(
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