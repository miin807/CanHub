package com.canhub.canhub.lanzamientos;


import com.canhub.canhub.R;
import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

public class CustomMarkerView extends MarkerView {

    private final TextView tvContent;

    public CustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        tvContent = findViewById(R.id.tvContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        // Verifica que se esté llamando este método
        tvContent.setText(String.format("Tiempo: %.1f\nValor: %.2f", e.getX(), e.getY()));
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        // Para centrar el marker en el punto
        return new MPPointF(-(getWidth() / 2f), -getHeight());
    }
}
