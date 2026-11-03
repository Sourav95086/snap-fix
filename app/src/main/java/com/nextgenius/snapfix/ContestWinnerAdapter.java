package com.nextgenius.snapfix;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nextgenius.snapfix.Model.ContestReport;
import com.nextgenius.snapfix.Model.ContestReporter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContestWinnerAdapter
        extends RecyclerView.Adapter<ContestWinnerAdapter.WinnerViewHolder> {

    private final List<ContestReporter> reporters;

    public ContestWinnerAdapter(
            List<ContestReporter> reporters
    ) {
        this.reporters = reporters;
    }

    @NonNull
    @Override
    public WinnerViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(
                        parent.getContext()
                ).inflate(
                        R.layout.item_contest_winner,
                        parent,
                        false
                );

        return new WinnerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull WinnerViewHolder holder,
            int position
    ) {

        ContestReporter reporter =
                reporters.get(position);

        int rank =
                reporter.getRank();

        // -----------------------------------------
        // Rank
        // -----------------------------------------

        holder.rankText.setText(
                "#" + rank
        );

        // -----------------------------------------
        // Name
        // -----------------------------------------

        String name = reporter.getName();

        if (name == null || name.trim().isEmpty()) {
            name = "Anonymous Reporter";
        }

        holder.nameText.setText(name);

        // -----------------------------------------
        // Phone
        // -----------------------------------------

        String phone = reporter.getPhone();

        if (phone == null || phone.trim().isEmpty()) {
            phone = "Phone unavailable";
        }

        holder.phoneText.setText(phone);

        // -----------------------------------------
        // Email
        // -----------------------------------------

        String email = reporter.getEmail();

        if (email == null || email.trim().isEmpty()) {
            email = "Email unavailable";
        }

        holder.emailText.setText(email);

        // -----------------------------------------
        // Number of reports
        // -----------------------------------------

        int reportCount =
                reporter.getReportCount();

        holder.reportCountText.setText(
                String.valueOf(reportCount)
        );

        // -----------------------------------------
        // Most reported category
        // -----------------------------------------

        String category =
                getMostReportedCategory(
                        reporter
                );

        holder.categoryText.setText(
                category
        );

        // -----------------------------------------
        // Reward
        // -----------------------------------------

        String reward;

        if (rank == 1) {

            reward = "Amazon Gift Card";

        } else if (rank == 2) {

            reward = "Flipkart Gift Card";

        } else {

            reward = "Meesho Gift Card";
        }

        holder.rewardText.setText(
                reward
        );
    }

    /**
     * Calculates the category appearing most often
     * in the reporter's issue descriptions.
     */
    private String getMostReportedCategory(
            ContestReporter reporter
    ) {

        List<ContestReport> reports =
                reporter.getReports();

        if (
                reports == null ||
                        reports.isEmpty()
        ) {

            return "No category data";
        }

        Map<String, Integer> categoryCount =
                new HashMap<>();

        for (ContestReport report : reports) {

            if (report == null) {
                continue;
            }

            String description =
                    report.getDescription();

            String category =
                    classifyCategory(description);

            int current =
                    categoryCount.containsKey(category)
                            ? categoryCount.get(category)
                            : 0;

            categoryCount.put(
                    category,
                    current + 1
            );
        }

        String mostReportedCategory =
                "Other";

        int highestCount = 0;

        for (
                Map.Entry<String, Integer> entry :
                categoryCount.entrySet()
        ) {

            if (
                    entry.getValue() >
                            highestCount
            ) {

                highestCount =
                        entry.getValue();

                mostReportedCategory =
                        entry.getKey();
            }
        }

        return mostReportedCategory;
    }

    /**
     * Classifies the issue description.
     *
     * IMPORTANT:
     * Street Lighting is checked before Road Infrastructure
     * so "streetlight" is never treated as a road issue.
     */
    private String classifyCategory(
            String description
    ) {

        if (
                description == null ||
                        description.trim().isEmpty()
        ) {

            return "Other";
        }

        String text =
                description.toLowerCase();

        // -----------------------------------------
        // STREET LIGHTING
        // -----------------------------------------

        if (
                text.contains("streetlight") ||
                        text.contains("street light") ||
                        text.contains("lamp post") ||
                        text.contains("light pole") ||
                        text.contains("lighting") ||
                        text.contains("bulb")
        ) {

            return "Street Lighting";
        }

        // -----------------------------------------
        // ROAD INFRASTRUCTURE
        // -----------------------------------------

        if (
                text.contains("pothole") ||
                        text.contains("road") ||
                        text.contains("pavement") ||
                        text.contains("asphalt") ||
                        text.contains("road surface") ||
                        text.contains("broken pavement")
        ) {

            return "Road Infrastructure";
        }

        // -----------------------------------------
        // GARBAGE / WASTE
        // -----------------------------------------

        if (
                text.contains("garbage") ||
                        text.contains("waste") ||
                        text.contains("trash") ||
                        text.contains("bin") ||
                        text.contains("dump")
        ) {

            return "Waste Management";
        }

        // -----------------------------------------
        // DRAINAGE
        // -----------------------------------------

        if (
                text.contains("drain") ||
                        text.contains("drainage") ||
                        text.contains("waterlogging") ||
                        text.contains("water logging") ||
                        text.contains("sewer")
        ) {

            return "Drainage";
        }

        // -----------------------------------------
        // OTHER
        // -----------------------------------------

        return "Other";
    }

    @Override
    public int getItemCount() {

        if (reporters == null) {
            return 0;
        }

        return reporters.size();
    }

    static class WinnerViewHolder
            extends RecyclerView.ViewHolder {

        TextView rankText;
        TextView nameText;
        TextView phoneText;
        TextView emailText;
        TextView reportCountText;
        TextView categoryText;
        TextView rewardText;

        public WinnerViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            rankText =
                    itemView.findViewById(
                            R.id.rankText
                    );

            nameText =
                    itemView.findViewById(
                            R.id.nameText
                    );

            phoneText =
                    itemView.findViewById(
                            R.id.phoneText
                    );

            emailText =
                    itemView.findViewById(
                            R.id.emailText
                    );

            reportCountText =
                    itemView.findViewById(
                            R.id.reportCountText
                    );

            categoryText =
                    itemView.findViewById(
                            R.id.categoryText
                    );

            rewardText =
                    itemView.findViewById(
                            R.id.rewardText
                    );
        }
    }
}