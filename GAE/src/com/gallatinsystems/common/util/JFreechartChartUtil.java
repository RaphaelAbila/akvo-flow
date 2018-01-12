/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package com.gallatinsystems.common.util;

import java.awt.Color;
import java.awt.RenderingHints;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 * wrapper class for interaction with the JFreechart library.
 * 
 * @author cfagiani
 */
public class JFreechartChartUtil {

    /**
     * generates a pie chart with the set of labels and values passed in (the labels and values
     * arrays must both be non-null and contain the same number of elements). The chart is returned
     * as a byte array representing the image.
     * 
     * @param labels
     * @param values
     * @param title
     * @param width
     * @param height
     * @return - byte array containing the image, null if there is an error.
     */
    public static byte[] getPieChart(List<String> labels, List<String> values,
            String title, int width, int height) {
        DefaultPieDataset pieDataset = new DefaultPieDataset();

        for (int i = 0; i < labels.size(); i++) {
            pieDataset.setValue(labels.get(i) + " (" + values.get(i) + ")",
                    Double.parseDouble(values.get(i)));
        }
        JFreeChart chart = ChartFactory.createPieChart(title, pieDataset,
                false, false, false);
        try {
            return ChartUtilities.encodeAsPNG(chart.createBufferedImage(width, height));
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }

    }
    /**
     * generates a bar chart with the set of labels and values passed in (the labels and values
     * arrays must both be non-null and contain the same number of elements). The chart is returned
     * as a byte array representing the image.
     * 
     * @param labels
     * @param values
     * @param title
     * @param width
     * @param height
     * @return - byte array containing the image, null if there is an error.
     */
    public static byte[] getBarChart(List<String> labels, List<String> values,
            String title, int width, int height) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < labels.size(); i++) {
            dataset.setValue(
                    Double.parseDouble(values.get(i)),
                    labels.get(i),//Integer.valueOf(i), //Row
                    "" //Integer.valueOf(0) //Column
                    );
        }
        //TODO: legend or not? It can take up lots of space.
        JFreeChart chart = ChartFactory.createBarChart(title, "", "", dataset, PlotOrientation.HORIZONTAL, true, false, false);
        // Colors
        chart.setBackgroundPaint(new Color(255,255,255)); //White
        chart.setBorderVisible(false);
        BarRenderer r = (BarRenderer) chart.getCategoryPlot().getRenderer();
        r.setShadowVisible(false); //Flatten chart
        
        //Pixel and font stuff:
        //RenderingHints renderingHints = new RenderingHints(key, value);
        //chart.setRenderingHints(renderingHints);
        try {
            return ChartUtilities.encodeAsPNG(chart.createBufferedImage(width, height));
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }

    }
}
