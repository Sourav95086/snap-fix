package com.nextgenius.snapfix;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserIssueAdapter
        extends RecyclerView.Adapter<UserIssueAdapter.ViewHolder> {

    private final Context context;
    private final List<UserIssue> issues;

    public UserIssueAdapter(
            Context context,
            List<UserIssue> issues
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
                LayoutInflater.from(context)
                        .inflate(
                                R.layout.item_user_issue,
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

        UserIssue issue =
                issues.get(position);

        holder.issueIdText.setText(
                "ISSUE #" + issue.getIssueId()
        );

        holder.descriptionText.setText(
                issue.getIssueDescription()
        );

        holder.locationText.setText(
                issue.getIssueLocation() != null
                        ? issue.getIssueLocation().toUpperCase()
                        : "LOCATION UNKNOWN"
        );

        holder.reportIdText.setText(
                "REPORT ID  " + issue.getReportId()
        );

        holder.dateText.setText(
                formatDate(issue.getReportedOn())
        );
    }

    @Override
    public int getItemCount() {
        return issues.size();
    }

    private String formatDate(String value) {

        if (value == null || value.isEmpty()) {
            return "DATE UNKNOWN";
        }

        try {

            SimpleDateFormat input =
                    new SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX",
                            Locale.US
                    );

            Date date = input.parse(value);

            SimpleDateFormat output =
                    new SimpleDateFormat(
                            "dd MMM yyyy • HH:mm",
                            Locale.US
                    );

            return output.format(date);

        } catch (ParseException e) {

            return value.length() >= 10
                    ? value.substring(0, 10)
                    : value;
        }
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView issueIdText;
        TextView descriptionText;
        TextView locationText;
        TextView reportIdText;
        TextView dateText;

        ViewHolder(@NonNull View itemView) {

            super(itemView);

            issueIdText =
                    itemView.findViewById(
                            R.id.issueIdText
                    );

            descriptionText =
                    itemView.findViewById(
                            R.id.descriptionText
                    );

            locationText =
                    itemView.findViewById(
                            R.id.locationText
                    );

            reportIdText =
                    itemView.findViewById(
                            R.id.reportIdText
                    );

            dateText =
                    itemView.findViewById(
                            R.id.dateText
                    );
        }
    }
}