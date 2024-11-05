package org.solotyan.simulationmodelqmo;

import java.util.Comparator;

public class WaitingTimeComparator implements Comparator<Double> {
    @Override
    public int compare(Double d1, Double d2){
        // Сравниваем время ожидания
        return Double.compare(d1, d2);
    }
}
