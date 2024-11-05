package org.solotyan.simulationmodelqmo;

import java.util.LinkedList;

public class Data {
    private double K;
    private double W;
    private double Q;
    private LinkedList<RowXlsx> rowsXlsx;

    public Data(){

    }

    public LinkedList<RowXlsx> getRowsXlsx() {
        return rowsXlsx;
    }

    public void setRowsXlsx(LinkedList<RowXlsx> rowsXlsx) {
        this.rowsXlsx = rowsXlsx;
    }

    public double getK() {
        return K;
    }

    public void setK(double k) {
        K = k;
    }

    public double getW() {
        return W;
    }

    public void setW(double w) {
        W = w;
    }

    public double getQ() {
        return Q;
    }

    public void setQ(double q) {
        Q = q;
    }
}
