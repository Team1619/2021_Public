package org.uacr.utilities;

import java.util.HashMap;

/**
 * WebDashboardGraphDataset makes it very easy to make a set of points to send to the web webdashboard.
 * Once the dataset is completed, simply put it in shared input values as a vector with the prefix "gr_",
 * and it will be put on the graph page of the web webdashboard.
 *
 * @author Matthew Oates
 */

public class WebDashboardGraphDataset extends HashMap<String, Double> {

    private int numPoints = 0;

    public WebDashboardGraphDataset addPoint(double x, double y) {
        put(numPoints + "x", x);
        put(numPoints + "y", y);
        numPoints++;

        return this;
    }
}
