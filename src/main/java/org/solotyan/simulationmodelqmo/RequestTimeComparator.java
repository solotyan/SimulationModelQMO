package org.solotyan.simulationmodelqmo;

import java.util.Comparator;

public class RequestTimeComparator implements Comparator<Request> {
    @Override
    public int compare(Request r1, Request r2) {
        // Сравниваем время двух запросов
        return Double.compare(r1.getTime(), r2.getTime());
    }
}
