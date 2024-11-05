package org.solotyan.simulationmodelqmo;

public class Request {
    private final double time;
    private final TypeRequest type;

    public Request(double time, TypeRequest type) {
        this.time = time;
        this.type = type;
    }

    public double getTime() {
        return time;
    }

    public TypeRequest getType() {
        return type;
    }
}
