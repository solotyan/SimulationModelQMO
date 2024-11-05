package org.solotyan.simulationmodelqmo;

public class RowXlsx {
    private Request request;
    private Integer queueLength;
    private Double waitingTime;
    private Double downtimeTime;
    private Double nextTime1;
    private Double nextTime2;
    private Double R1;
    private Double R2;
    private Integer income;
    private Integer outcome;
    private Integer nextIncome;
    private Integer nextOutcome;

    public RowXlsx() {
    }

    public Integer getNextIncome() {
        return nextIncome;
    }

    public void setNextIncome(Integer nextIncome) {
        this.nextIncome = nextIncome;
    }

    public Integer getNextOutcome() {
        return nextOutcome;
    }

    public void setNextOutcome(Integer nextOutcome) {
        this.nextOutcome = nextOutcome;
    }

    public Integer getIncome() {
        return income;
    }

    public void setIncome(Integer income) {
        this.income = income;
    }

    public Integer getOutcome() {
        return outcome;
    }

    public void setOutcome(Integer outcome) {
        this.outcome = outcome;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Integer getQueueLength() {
        return queueLength;
    }

    public void setQueueLength(Integer queueLength) {
        this.queueLength = queueLength;
    }

    public Double getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(Double waitingTime) {
        this.waitingTime = waitingTime;
    }

    public Double getDowntimeTime() {
        return downtimeTime;
    }

    public void setDowntimeTime(Double downtimeTime) {
        this.downtimeTime = downtimeTime;
    }

    public Double getNextTime1() {
        return nextTime1;
    }

    public void setNextTime1(Double nextTime1) {
        this.nextTime1 = nextTime1;
    }

    public Double getNextTime2() {
        return nextTime2;
    }

    public void setNextTime2(Double nextTime2) {
        this.nextTime2 = nextTime2;
    }

    public Double getR1() {
        return R1;
    }

    public void setR1(Double r1) {
        R1 = r1;
    }

    public Double getR2() {
        return R2;
    }

    public void setR2(Double r2) {
        R2 = r2;
    }
}
