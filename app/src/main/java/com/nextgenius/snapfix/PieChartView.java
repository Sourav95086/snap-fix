package com.nextgenius.snapfix;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class PieChartView extends View {

    // =============================================================
    // PAINTS
    // =============================================================

    private final Paint piePaint =
            new Paint(
                    Paint.ANTI_ALIAS_FLAG
            );


    private final Paint centerPaint =
            new Paint(
                    Paint.ANTI_ALIAS_FLAG
            );


    private final Paint totalPaint =
            new Paint(
                    Paint.ANTI_ALIAS_FLAG
            );


    private final Paint labelPaint =
            new Paint(
                    Paint.ANTI_ALIAS_FLAG
            );


    // =============================================================
    // DATA
    // =============================================================

    private final List<PieData>
            data =
            new ArrayList<>();


    private int totalValue =
            0;


    // =============================================================
    // ANIMATION
    // =============================================================

    private float animationProgress =
            0f;


    private ValueAnimator animator;


    // =============================================================
    // COLORS
    // =============================================================

    private final int[] colors = {

            0xFF00D9FF, // CYAN

            0xFFEF4444, // RED

            0xFFFACC15, // YELLOW

            0xFF22C55E,

            0xFFA855F7,

            0xFFF97316,

            0xFF38BDF8,

            0xFFEC4899
    };


    // =============================================================
    // CONSTRUCTORS
    // =============================================================

    public PieChartView(
            Context context
    ) {

        super(context);

        init();
    }


    public PieChartView(
            Context context,
            AttributeSet attrs
    ) {

        super(
                context,
                attrs
        );

        init();
    }


    public PieChartView(
            Context context,
            AttributeSet attrs,
            int defStyleAttr
    ) {

        super(
                context,
                attrs,
                defStyleAttr
        );

        init();
    }


    // =============================================================
    // INIT
    // =============================================================

    private void init() {

        piePaint.setAntiAlias(
                true
        );


        piePaint.setStyle(
                Paint.Style.FILL
        );


        centerPaint.setAntiAlias(
                true
        );


        centerPaint.setStyle(
                Paint.Style.FILL
        );


        totalPaint.setAntiAlias(
                true
        );


        totalPaint.setColor(
                0xFFFFFFFF
        );


        totalPaint.setTextAlign(
                Paint.Align.CENTER
        );


        totalPaint.setTypeface(
                android.graphics.Typeface.DEFAULT_BOLD
        );


        labelPaint.setAntiAlias(
                true
        );


        labelPaint.setColor(
                0xFF7F8A9A
        );


        labelPaint.setTextAlign(
                Paint.Align.CENTER
        );
    }


    // =============================================================
    // SET DATA
    // =============================================================

    public void setData(
            Map<String, Integer> values
    ) {

        if (animator != null) {

            animator.cancel();
        }


        data.clear();


        totalValue =
                0;


        animationProgress =
                0f;


        if (values == null
                || values.isEmpty()) {

            invalidate();

            return;
        }


        // =========================================================
        // SORT
        // =========================================================

        List<Map.Entry<String, Integer>>
                entries =
                new ArrayList<>(
                        values.entrySet()
                );


        Collections.sort(
                entries,
                new Comparator<Map.Entry<String, Integer>>() {

                    @Override
                    public int compare(
                            Map.Entry<String, Integer> a,
                            Map.Entry<String, Integer> b
                    ) {

                        return Integer.compare(
                                b.getValue(),
                                a.getValue()
                        );
                    }
                }
        );


        // =========================================================
        // CREATE DATA
        // =========================================================

        int colorIndex =
                0;


        for (Map.Entry<String, Integer> entry :
                entries) {

            if (entry.getValue() == null
                    || entry.getValue() <= 0) {

                continue;
            }


            PieData item =
                    new PieData();


            item.name =
                    entry.getKey();


            item.value =
                    entry.getValue();


            item.color =
                    colors[
                            colorIndex %
                                    colors.length
                            ];


            data.add(
                    item
            );


            totalValue +=
                    item.value;


            colorIndex++;
        }


        android.util.Log.d(
                "PIE_CHART",
                "Received data: "
                        + values
        );


        android.util.Log.d(
                "PIE_CHART",
                "Total: "
                        + totalValue
        );


        startAnimation();
    }


    // =============================================================
    // START ANIMATION
    // =============================================================

    private void startAnimation() {

        animationProgress =
                0f;


        animator =
                ValueAnimator.ofFloat(
                        0f,
                        1f
                );


        animator.setDuration(
                1400
        );


        animator.setInterpolator(
                new DecelerateInterpolator(
                        1.5f
                )
        );


        animator.addUpdateListener(
                animation -> {

                    animationProgress =
                            (float)
                                    animation.getAnimatedValue();


                    invalidate();
                }
        );


        animator.start();
    }


    // =============================================================
    // DRAW
    // =============================================================

    @Override
    protected void onDraw(
            Canvas canvas
    ) {

        super.onDraw(
                canvas
        );


        // =========================================================
        // EMPTY
        // =========================================================

        if (data.isEmpty()
                || totalValue <= 0) {

            drawEmpty(
                    canvas
            );

            return;
        }


        // =========================================================
        // DIMENSIONS
        // =========================================================

        float width =
                getWidth();


        float height =
                getHeight();


        float centerX =
                width / 2f;


        float centerY =
                height / 2f;


        float radius =
                Math.min(
                        width,
                        height
                ) * 0.36f;


        // =========================================================
        // RECT
        // =========================================================

        RectF rect =
                new RectF(
                        centerX - radius,
                        centerY - radius,
                        centerX + radius,
                        centerY + radius
                );


        // =========================================================
        // ANIMATED ANGLE
        // =========================================================

        float remainingAngle =
                360f *
                        animationProgress;


        float currentAngle =
                -90f;


        // =========================================================
        // DRAW SLICES
        // =========================================================

        for (PieData item :
                data) {

            if (remainingAngle <= 0) {
                break;
            }


            float sliceAngle =
                    360f *
                            item.value /
                            (float) totalValue;


            float drawAngle =
                    Math.min(
                            sliceAngle,
                            remainingAngle
                    );


            piePaint.setColor(
                    item.color
            );


            canvas.drawArc(
                    rect,
                    currentAngle,
                    drawAngle,
                    true,
                    piePaint
            );


            currentAngle +=
                    drawAngle;


            remainingAngle -=
                    drawAngle;
        }


        // =========================================================
        // CENTER HOLE
        // =========================================================

        centerPaint.setColor(
                0xFF141A27
        );


        canvas.drawCircle(
                centerX,
                centerY,
                radius * 0.57f,
                centerPaint
        );


        // =========================================================
        // TOTAL
        // =========================================================

        totalPaint.setTextSize(
                dpToPx(22)
        );


        canvas.drawText(
                String.valueOf(
                        totalValue
                ),
                centerX,
                centerY + dpToPx(7),
                totalPaint
        );


        // =========================================================
        // LABEL
        // =========================================================

        labelPaint.setTextSize(
                dpToPx(7)
        );


        canvas.drawText(
                "TOTAL ISSUES",
                centerX,
                centerY + dpToPx(21),
                labelPaint
        );
    }


    // =============================================================
    // EMPTY
    // =============================================================

    private void drawEmpty(
            Canvas canvas
    ) {

        float centerX =
                getWidth() / 2f;


        float centerY =
                getHeight() / 2f;


        Paint paint =
                new Paint(
                        Paint.ANTI_ALIAS_FLAG
                );


        paint.setColor(
                0xFF697585
        );


        paint.setTextSize(
                dpToPx(9)
        );


        paint.setTextAlign(
                Paint.Align.CENTER
        );


        canvas.drawText(
                "No issue data",
                centerX,
                centerY,
                paint
        );
    }


    // =============================================================
    // DP
    // =============================================================

    private float dpToPx(
            float dp
    ) {

        return dp *
                getResources()
                        .getDisplayMetrics()
                        .density;
    }


    // =============================================================
    // DATA CLASS
    // =============================================================

    private static class PieData {

        String name;

        int value;

        int color;
    }
}