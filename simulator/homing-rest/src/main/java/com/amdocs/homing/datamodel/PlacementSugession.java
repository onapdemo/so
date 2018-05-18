package com.amdocs.homing.datamodel;

public class PlacementSugession {

    private String requestStatus;

    private String transactionId;

    private Solutions solutions;

    private String requestId;

    private String statusMessage;

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Solutions getSolutions() {
        return solutions;
    }

    public void setSolutions(Solutions solutions) {
        this.solutions = solutions;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    /*@Override
    public String toString() {
        return PlacementSugession.class.getSimpleName() + " [requestStatus = " + requestStatus
                + ", transactionId = " + transactionId + ", solutions = " + solutions
                + ", requestId = " + requestId + ", statusMessage = " + statusMessage + "]";
    }*/

}
