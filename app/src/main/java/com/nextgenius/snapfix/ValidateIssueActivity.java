package com.nextgenius.snapfix;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

// =============================================================
// VALIDATE ISSUE ACTIVITY
// =============================================================

public class ValidateIssueActivity extends AppCompatActivity {

    // =========================================================
    // API BASE URLS
    // =========================================================

    // API 1 + API 2
    private static final String REPORT_API_BASE_URL =
            "https://nonauthissues-production.up.railway.app/";

    // API 3
    private static final String UPLOAD_API_BASE_URL =
            "https://parakramuserreportchatbot-production.up.railway.app/";

    // API 4
    private static final String VALIDATION_API_BASE_URL =
            "https://image-validation-production.up.railway.app/";

    // =========================================================
    // ISSUE ID
    // =========================================================

    private int issueId = -1;

    // =========================================================
    // REPORT FIELDS
    // =========================================================

    private EditText etReporterName;
    private EditText etIssueDescription;
    private EditText etLocationAddress;
    private EditText etLandmark;
    private EditText etCreatedAt;
    private EditText etValidated;

    // =========================================================
    // LOCATION
    // =========================================================

    private EditText etLatitude;
    private EditText etLongitude;

    // =========================================================
    // EVIDENCE
    // =========================================================

    private ImageView ivEvidence;
    private TextView tvEvidenceText;

    // =========================================================
    // BUTTONS
    // =========================================================

    private Button btnFetchLocation;
    private Button btnAddEvidence;
    private Button btnValidateIssue;

    // =========================================================
    // LOADING
    // =========================================================

    private ProgressBar progressBar;

    // =========================================================
    // SELECTED IMAGE
    // =========================================================

    private Uri selectedEvidenceUri;

    // =========================================================
    // LOCATION CLIENT
    // =========================================================

    private FusedLocationProviderClient fusedLocationClient;

    // =========================================================
    // IMAGE PICKER
    // =========================================================

    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {

                        if (uri == null) {
                            return;
                        }

                        selectedEvidenceUri = uri;

                        ivEvidence.setImageURI(uri);

                        ivEvidence.setVisibility(
                                View.VISIBLE
                        );

                        tvEvidenceText.setText(
                                "Evidence image selected"
                        );
                    }
            );

    // =========================================================
    // LOCATION PERMISSION
    // =========================================================

    private final ActivityResultLauncher<String>
            locationPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    granted -> {

                        if (granted) {

                            fetchCurrentLocation();

                        } else {

                            Toast.makeText(
                                    this,
                                    "Location permission is required",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );

    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_validate_issue
        );

        initializeViews();

        fusedLocationClient =
                LocationServices
                        .getFusedLocationProviderClient(
                                this
                        );

        issueId = getIntent().getIntExtra(
                "issue_id",
                -1
        );

        if (issueId == -1) {

            Toast.makeText(
                    this,
                    "Invalid issue ID",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
        }

        setupButtons();

        // API 1
        fetchReport();
    }

    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        etReporterName =
                findViewById(R.id.etReporterName);

        etIssueDescription =
                findViewById(R.id.etIssueDescription);

        etLocationAddress =
                findViewById(R.id.etLocationAddress);

        etLandmark =
                findViewById(R.id.etLandmark);

        etCreatedAt =
                findViewById(R.id.etCreatedAt);

        etValidated =
                findViewById(R.id.etValidated);

        etLatitude =
                findViewById(R.id.etLatitude);

        etLongitude =
                findViewById(R.id.etLongitude);

        ivEvidence =
                findViewById(R.id.ivEvidence);

        tvEvidenceText =
                findViewById(R.id.tvEvidenceText);

        btnFetchLocation =
                findViewById(R.id.btnFetchLocation);

        btnAddEvidence =
                findViewById(R.id.btnAddEvidence);

        btnValidateIssue =
                findViewById(R.id.btnValidateIssue);

        progressBar =
                findViewById(R.id.progressBar);
    }

    // =========================================================
    // BUTTONS
    // =========================================================

    private void setupButtons() {

        btnFetchLocation.setOnClickListener(
                v -> checkLocationPermission()
        );

        btnAddEvidence.setOnClickListener(
                v -> imagePicker.launch("image/*")
        );

        btnValidateIssue.setOnClickListener(
                v -> validateBeforeSubmit()
        );
    }

    // =========================================================
    // API 1
    // FETCH REPORT
    // =========================================================

    private void fetchReport() {

        setLoading(true);

        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl(
                                REPORT_API_BASE_URL
                        )
                        .addConverterFactory(
                                GsonConverterFactory.create()
                        )
                        .build();

        ReportsApi api =
                retrofit.create(
                        ReportsApi.class
                );

        Call<ReportResponse> call =
                api.getReport(issueId);

        call.enqueue(
                new Callback<ReportResponse>() {

                    @Override
                    public void onResponse(
                            Call<ReportResponse> call,
                            Response<ReportResponse> response
                    ) {

                        setLoading(false);

                        if (!response.isSuccessful()
                                || response.body() == null) {

                            Toast.makeText(
                                    ValidateIssueActivity.this,
                                    "Failed to fetch report",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        ReportResponse result =
                                response.body();

                        if (!result.success
                                || result.report == null) {

                            Toast.makeText(
                                    ValidateIssueActivity.this,
                                    "Report not found",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        fillReportData(
                                result.report
                        );
                    }

                    @Override
                    public void onFailure(
                            Call<ReportResponse> call,
                            Throwable t
                    ) {

                        setLoading(false);

                        Toast.makeText(
                                ValidateIssueActivity.this,
                                "Connection failed: "
                                        + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    // =========================================================
    // FILL REPORT DATA
    // =========================================================

    private void fillReportData(
            Report report
    ) {

        etReporterName.setText(
                safeText(
                        report.reporterName
                )
        );

        etIssueDescription.setText(
                safeText(
                        report.issueDescription
                )
        );

        etLocationAddress.setText(
                safeText(
                        report.locationAddress
                )
        );

        etLandmark.setText(
                safeText(
                        report.landmark
                )
        );

        etCreatedAt.setText(
                formatDate(
                        report.createdAt
                )
        );

        etValidated.setText(
                report.validated
                        ? "TRUE"
                        : "FALSE"
        );

        etLatitude.setText("");
        etLongitude.setText("");
    }

    // =========================================================
    // LOCATION PERMISSION
    // =========================================================

    private void checkLocationPermission() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED) {

            fetchCurrentLocation();

        } else {

            locationPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
            );
        }
    }

    // =========================================================
    // FETCH CURRENT LOCATION
    // =========================================================

    private void fetchCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        setLoading(true);

        fusedLocationClient
                .getCurrentLocation(
                        com.google.android.gms.location.Priority
                                .PRIORITY_HIGH_ACCURACY,
                        null
                )
                .addOnSuccessListener(
                        location -> {

                            setLoading(false);

                            if (location == null) {

                                Toast.makeText(
                                        this,
                                        "Unable to get current location",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            etLatitude.setText(
                                    String.valueOf(
                                            location.getLatitude()
                                    )
                            );

                            etLongitude.setText(
                                    String.valueOf(
                                            location.getLongitude()
                                    )
                            );

                            Toast.makeText(
                                    this,
                                    "Location fetched successfully",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                )
                .addOnFailureListener(
                        e -> {

                            setLoading(false);

                            Toast.makeText(
                                    this,
                                    "Unable to fetch location: "
                                            + e.getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                );
    }

    // =========================================================
    // VALIDATE BEFORE SUBMIT
    // =========================================================

    private void validateBeforeSubmit() {

        // -----------------------------------------------------
        // Reporter
        // -----------------------------------------------------

        if (isEmpty(etReporterName)) {

            showRequired(
                    etReporterName,
                    "Reporter name is required"
            );

            return;
        }

        // -----------------------------------------------------
        // Description
        // -----------------------------------------------------

        if (isEmpty(etIssueDescription)) {

            showRequired(
                    etIssueDescription,
                    "Issue description is required"
            );

            return;
        }

        // -----------------------------------------------------
        // Address
        // -----------------------------------------------------

        if (isEmpty(etLocationAddress)) {

            showRequired(
                    etLocationAddress,
                    "Location address is required"
            );

            return;
        }

        // -----------------------------------------------------
        // Landmark
        // -----------------------------------------------------

        if (isEmpty(etLandmark)) {

            showRequired(
                    etLandmark,
                    "Landmark is required"
            );

            return;
        }

        // -----------------------------------------------------
        // Created date
        // -----------------------------------------------------

        if (isEmpty(etCreatedAt)) {

            showRequired(
                    etCreatedAt,
                    "Created date is required"
            );

            return;
        }

        // -----------------------------------------------------
        // Latitude
        // -----------------------------------------------------

        if (isEmpty(etLatitude)) {

            Toast.makeText(
                    this,
                    "Please fetch location first",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        // -----------------------------------------------------
        // Longitude
        // -----------------------------------------------------

        if (isEmpty(etLongitude)) {

            Toast.makeText(
                    this,
                    "Please fetch location first",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        // -----------------------------------------------------
        // Evidence
        // -----------------------------------------------------

        if (selectedEvidenceUri == null) {

            Toast.makeText(
                    this,
                    "Please add an evidence image",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        // -----------------------------------------------------
        // Start validation pipeline
        // -----------------------------------------------------

        submitValidationPipeline();
    }

    // =========================================================
    // MAIN VALIDATION PIPELINE
    // =========================================================

    private void submitValidationPipeline() {

        btnValidateIssue.setEnabled(false);

        setLoading(true);

        tvEvidenceText.setText(
                "Uploading evidence..."
        );

        // -----------------------------------------------------
        // STEP 1
        // API 3
        // Upload image to Cloudinary
        // -----------------------------------------------------

        uploadEvidenceImage();
    }

    // =========================================================
    // API 3
    // UPLOAD IMAGE
    // =========================================================

    private void uploadEvidenceImage() {

        try {

            InputStream inputStream =
                    getContentResolver()
                            .openInputStream(
                                    selectedEvidenceUri
                            );

            if (inputStream == null) {

                uploadFailed(
                        "Unable to read selected image"
                );

                return;
            }

            byte[] imageBytes =
                    readBytes(inputStream);

            inputStream.close();

            String mimeType =
                    getContentResolver()
                            .getType(
                                    selectedEvidenceUri
                            );

            if (mimeType == null) {

                mimeType = "image/jpeg";
            }

            RequestBody requestBody =
                    RequestBody.create(
                            MediaType.parse(mimeType),
                            imageBytes
                    );

            String fileName =
                    getFileName(
                            selectedEvidenceUri
                    );

            MultipartBody.Part imagePart =
                    MultipartBody.Part.createFormData(
                            "image",
                            fileName,
                            requestBody
                    );

            Retrofit retrofit =
                    new Retrofit.Builder()
                            .baseUrl(
                                    UPLOAD_API_BASE_URL
                            )
                            .addConverterFactory(
                                    GsonConverterFactory.create()
                            )
                            .build();

            UploadApi api =
                    retrofit.create(
                            UploadApi.class
                    );

            Call<UploadResponse> call =
                    api.uploadImage(
                            imagePart
                    );

            call.enqueue(
                    new Callback<UploadResponse>() {

                        @Override
                        public void onResponse(
                                Call<UploadResponse> call,
                                Response<UploadResponse> response
                        ) {

                            if (!response.isSuccessful()
                                    || response.body() == null) {

                                uploadFailed(
                                        "Image upload failed"
                                );

                                return;
                            }

                            UploadResponse result =
                                    response.body();

                            if (!result.success
                                    || result.url == null
                                    || result.url.isEmpty()) {

                                uploadFailed(
                                        "Cloudinary upload failed"
                                );

                                return;
                            }

                            // -------------------------------------------------
                            // API 3 SUCCESS
                            // We now have Cloudinary URL
                            // -------------------------------------------------

                            tvEvidenceText.setText(
                                    "Evidence uploaded. Checking image and location..."
                            );

                            // -------------------------------------------------
                            // STEP 2
                            // API 4
                            // -------------------------------------------------

                            validateImageAndLocation(
                                    result.url
                            );
                        }

                        @Override
                        public void onFailure(
                                Call<UploadResponse> call,
                                Throwable t
                        ) {

                            uploadFailed(
                                    "Image upload error: "
                                            + t.getMessage()
                            );
                        }
                    }
            );

        } catch (Exception e) {

            uploadFailed(
                    "Unable to prepare image: "
                            + e.getMessage()
            );
        }
    }

    // =========================================================
    // API 4
    // VALIDATE IMAGE + LOCATION
    // =========================================================

    private void validateImageAndLocation(
            String imageUrl
    ) {

        ValidationApiRequest request =
                new ValidationApiRequest();

        // Description from API 1
        request.description =
                etIssueDescription
                        .getText()
                        .toString()
                        .trim();

        // Cloudinary URL from API 3
        request.imageUrl =
                imageUrl;

        // GPS
        try {

            request.latitude =
                    Double.parseDouble(
                            etLatitude
                                    .getText()
                                    .toString()
                                    .trim()
                    );

            request.longitude =
                    Double.parseDouble(
                            etLongitude
                                    .getText()
                                    .toString()
                                    .trim()
                    );

        } catch (NumberFormatException e) {

            validationFailed(
                    "Invalid latitude or longitude"
            );

            return;
        }

        // Address from API 1
        request.providedAddress =
                etLocationAddress
                        .getText()
                        .toString()
                        .trim();

        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl(
                                VALIDATION_API_BASE_URL
                        )
                        .addConverterFactory(
                                GsonConverterFactory.create()
                        )
                        .build();

        ValidationApi api =
                retrofit.create(
                        ValidationApi.class
                );

        Call<ImageValidationResponse> call =
                api.validateImageAndLocation(
                        request
                );

        call.enqueue(
                new Callback<ImageValidationResponse>() {

                    @Override
                    public void onResponse(
                            Call<ImageValidationResponse> call,
                            Response<ImageValidationResponse> response
                    ) {

                        if (!response.isSuccessful()
                                || response.body() == null) {

                            validationFailed(
                                    "Validation API failed"
                            );

                            return;
                        }

                        ImageValidationResponse result =
                                response.body();

                        // =================================================
                        // API 4 RESULT
                        // =================================================

                        if (result.validated) {

                            // ---------------------------------------------
                            // API 4 PASSED
                            // ---------------------------------------------

                            tvEvidenceText.setText(
                                    "Image and location verified ✓"
                            );

                            Toast.makeText(
                                    ValidateIssueActivity.this,
                                    "Evidence validation passed",
                                    Toast.LENGTH_SHORT
                            ).show();

                            // ---------------------------------------------
                            // STEP 3
                            // API 2
                            // ---------------------------------------------

                            updateDatabaseValidation();

                        } else {

                            // ---------------------------------------------
                            // API 4 FAILED
                            // DO NOT CALL API 2
                            // ---------------------------------------------

                            String reason =
                                    safeText(
                                            result.reason
                                    );

                            if (reason.isEmpty()) {

                                reason =
                                        "Image or location validation failed";
                            }

                            validationFailed(
                                    reason
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ImageValidationResponse> call,
                            Throwable t
                    ) {

                        validationFailed(
                                "Validation connection error: "
                                        + t.getMessage()
                        );
                    }
                }
        );
    }

    // =========================================================
    // API 2
    // UPDATE DATABASE VALIDATED = TRUE
    // =========================================================

    private void updateDatabaseValidation() {

        tvEvidenceText.setText(
                "Validation passed. Updating issue..."
        );

        ValidationRequest request =
                new ValidationRequest(true);

        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl(
                                REPORT_API_BASE_URL
                        )
                        .addConverterFactory(
                                GsonConverterFactory.create()
                        )
                        .build();

        ReportsApi api =
                retrofit.create(
                        ReportsApi.class
                );

        Call<ValidationResponse> call =
                api.validateReport(
                        issueId,
                        request
                );

        call.enqueue(
                new Callback<ValidationResponse>() {

                    @Override
                    public void onResponse(
                            Call<ValidationResponse> call,
                            Response<ValidationResponse> response
                    ) {

                        setLoading(false);

                        btnValidateIssue.setEnabled(true);

                        if (!response.isSuccessful()
                                || response.body() == null) {

                            Toast.makeText(
                                    ValidateIssueActivity.this,
                                    "Failed to update issue",
                                    Toast.LENGTH_LONG
                            ).show();

                            tvEvidenceText.setText(
                                    "Evidence validated, but database update failed"
                            );

                            return;
                        }

                        ValidationResponse result =
                                response.body();

                        if (result.success
                                && result.validated) {

                            etValidated.setText(
                                    "TRUE"
                            );

                            tvEvidenceText.setText(
                                    "Issue validated successfully ✓"
                            );

                            Toast.makeText(
                                    ValidateIssueActivity.this,
                                    "Issue validated successfully",
                                    Toast.LENGTH_LONG
                            ).show();

                            // Activity6 will refresh its issue list
                            finish();

                        } else {

                            Toast.makeText(
                                    ValidateIssueActivity.this,
                                    safeText(
                                            result.message
                                    ).isEmpty()
                                            ? "Database validation failed"
                                            : result.message,
                                    Toast.LENGTH_LONG
                            ).show();

                            tvEvidenceText.setText(
                                    "Database update failed"
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ValidationResponse> call,
                            Throwable t
                    ) {

                        setLoading(false);

                        btnValidateIssue.setEnabled(true);

                        tvEvidenceText.setText(
                                "Database update failed"
                        );

                        Toast.makeText(
                                ValidateIssueActivity.this,
                                "Database update error: "
                                        + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    // =========================================================
    // UPLOAD FAILED
    // =========================================================

    private void uploadFailed(
            String message
    ) {

        setLoading(false);

        btnValidateIssue.setEnabled(true);

        tvEvidenceText.setText(
                "Evidence upload failed"
        );

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }

    // =========================================================
    // VALIDATION FAILED
    // =========================================================

    private void validationFailed(
            String reason
    ) {

        setLoading(false);

        btnValidateIssue.setEnabled(true);

        tvEvidenceText.setText(
                "Validation failed ✕"
        );

        Toast.makeText(
                this,
                "Issue validation failed:\n"
                        + reason,
                Toast.LENGTH_LONG
        ).show();
    }

    // =========================================================
    // READ IMAGE BYTES
    // =========================================================

    private byte[] readBytes(
            InputStream inputStream
    ) throws IOException {

        java.io.ByteArrayOutputStream buffer =
                new java.io.ByteArrayOutputStream();

        byte[] data =
                new byte[4096];

        int n;

        while (
                (n = inputStream.read(data))
                        != -1
        ) {

            buffer.write(
                    data,
                    0,
                    n
            );
        }

        return buffer.toByteArray();
    }

    // =========================================================
    // GET FILE NAME
    // =========================================================

    private String getFileName(
            Uri uri
    ) {

        String result = null;

        if ("content".equals(
                uri.getScheme()
        )) {

            try (
                    Cursor cursor =
                            getContentResolver()
                                    .query(
                                            uri,
                                            null,
                                            null,
                                            null,
                                            null
                                    )
            ) {

                if (cursor != null
                        && cursor.moveToFirst()) {

                    int index =
                            cursor.getColumnIndex(
                                    OpenableColumns.DISPLAY_NAME
                            );

                    if (index >= 0) {

                        result =
                                cursor.getString(index);
                    }
                }
            }
        }

        if (result == null) {

            result =
                    uri.getLastPathSegment();
        }

        if (result == null
                || result.isEmpty()) {

            result = "evidence.jpg";
        }

        return result;
    }

    // =========================================================
    // EMPTY CHECK
    // =========================================================

    private boolean isEmpty(
            EditText field
    ) {

        return field.getText()
                .toString()
                .trim()
                .isEmpty();
    }

    // =========================================================
    // REQUIRED ERROR
    // =========================================================

    private void showRequired(
            EditText field,
            String message
    ) {

        field.requestFocus();

        field.setError(
                message
        );

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }

    // =========================================================
    // LOADING
    // =========================================================

    private void setLoading(
            boolean loading
    ) {

        if (loading) {

            progressBar.setVisibility(
                    View.VISIBLE
            );

        } else {

            progressBar.setVisibility(
                    View.GONE
            );
        }
    }

    // =========================================================
    // SAFE TEXT
    // =========================================================

    private String safeText(
            String value
    ) {

        return value == null
                ? ""
                : value;
    }

    // =========================================================
    // DATE FORMAT
    // =========================================================

    private String formatDate(
            String input
    ) {

        if (input == null
                || input.isEmpty()) {

            return "";
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
    // =========================================================
    // API 1 + API 2
    // =========================================================
    // =========================================================

    public interface ReportsApi {

        // API 1
        @GET("report/{id}")
        Call<ReportResponse> getReport(
                @Path("id") int id
        );

        // API 2
        @POST("validate/{id}")
        Call<ValidationResponse> validateReport(
                @Path("id") int id,
                @Body ValidationRequest request
        );
    }

    // =========================================================
    // API 3
    // =========================================================

    public interface UploadApi {

        @Multipart
        @POST("upload-image")
        Call<UploadResponse> uploadImage(
                @Part MultipartBody.Part image
        );
    }

    // =========================================================
    // API 4
    // =========================================================

    public interface ValidationApi {

        @POST("validate")
        Call<ImageValidationResponse> validateImageAndLocation(
                @Body ValidationApiRequest request
        );
    }

    // =========================================================
    // API 1 RESPONSE
    // =========================================================

    public static class ReportResponse {

        @SerializedName("success")
        boolean success;

        @SerializedName("report")
        Report report;
    }

    // =========================================================
    // REPORT
    // =========================================================

    public static class Report {

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

    // =========================================================
    // API 2 REQUEST
    // =========================================================

    public static class ValidationRequest {

        @SerializedName("validated")
        boolean validated;

        public ValidationRequest(
                boolean validated
        ) {

            this.validated =
                    validated;
        }
    }

    // =========================================================
    // API 2 RESPONSE
    // =========================================================

    public static class ValidationResponse {

        @SerializedName("success")
        boolean success;

        @SerializedName("message")
        String message;

        @SerializedName("id")
        int id;

        @SerializedName("validated")
        boolean validated;
    }

    // =========================================================
    // API 3 RESPONSE
    // =========================================================

    public static class UploadResponse {

        @SerializedName("success")
        boolean success;

        @SerializedName("message")
        String message;

        @SerializedName("url")
        String url;

        @SerializedName("public_id")
        String publicId;
    }

    // =========================================================
    // API 4 REQUEST
    // =========================================================

    public static class ValidationApiRequest {

        @SerializedName("description")
        String description;

        @SerializedName("image_url")
        String imageUrl;

        @SerializedName("latitude")
        double latitude;

        @SerializedName("longitude")
        double longitude;

        @SerializedName("provided_address")
        String providedAddress;
    }

    // =========================================================
    // API 4 RESPONSE
    // =========================================================

    public static class ImageValidationResponse {

        @SerializedName("validated")
        boolean validated;

        @SerializedName("image_validation")
        ImageValidation imageValidation;

        @SerializedName("reverse_image_validation")
        ReverseImageValidation reverseImageValidation;

        @SerializedName("location_validation")
        LocationValidation locationValidation;

        @SerializedName("reason")
        String reason;
    }

    // =========================================================
    // API 4 IMAGE VALIDATION
    // =========================================================

    public static class ImageValidation {

        @SerializedName("issue_match")
        boolean issueMatch;

        @SerializedName("confidence")
        double confidence;

        @SerializedName("detected_content")
        String detectedContent;

        @SerializedName("reason")
        String reason;
    }

    // =========================================================
    // API 4 REVERSE IMAGE VALIDATION
    // =========================================================

    public static class ReverseImageValidation {

        @SerializedName("internet_match")
        boolean internetMatch;

        @SerializedName("confidence")
        double confidence;

        @SerializedName("source_url")
        String sourceUrl;

        @SerializedName("reason")
        String reason;
    }

    // =========================================================
    // API 4 LOCATION VALIDATION
    // =========================================================

    public static class LocationValidation {

        @SerializedName("reverse_address")
        String reverseAddress;

        @SerializedName("distance_meters")
        double distanceMeters;

        @SerializedName("semantic_match")
        boolean semanticMatch;

        @SerializedName("confidence")
        double confidence;

        @SerializedName("reason")
        String reason;
    }
}