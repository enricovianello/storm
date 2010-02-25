/**
 * 
 */
package it.grid.storm.tape.recalltable.model;

/**
 * @author zappi
 * 
 */
public enum RecallTaskStatus {

    SUCCESS(0, "success"), QUEUED(1, "queued"), IN_PROGRESS(2, "in-progress"), ERROR(3, "error"), ABORTED(4, "aborted"), UNDEFINED(
            5, "undefined");

    private final int taskStatusRepresentation;
    private final String statusName;

    private RecallTaskStatus(int statusId, String statusName) {
        taskStatusRepresentation = statusId;
        this.statusName = statusName;
    }

    public static RecallTaskStatus getRecallTaskStatus(int statusId) {
        switch (statusId) {
        case 0:
            return SUCCESS;
        case 1:
            return QUEUED;
        case 2:
            return IN_PROGRESS;
        case 3:
            return ERROR;
        case 4:
            return ABORTED;
        default:
            return UNDEFINED;
        }
    }

    public static RecallTaskStatus getRecallTaskStatus(String status) {
        if (status.toLowerCase().equals(RecallTaskStatus.ABORTED.toString().toLowerCase())) {
            return RecallTaskStatus.ABORTED;
        }
        if (status.toLowerCase().equals(RecallTaskStatus.SUCCESS.toString().toLowerCase())) {
            return RecallTaskStatus.SUCCESS;
        }
        if (status.toLowerCase().equals(RecallTaskStatus.IN_PROGRESS.toString().toLowerCase())) {
            return RecallTaskStatus.IN_PROGRESS;
        }
        if (status.toLowerCase().equals(RecallTaskStatus.ERROR.toString().toLowerCase())) {
            return RecallTaskStatus.ERROR;
        }
        if (status.toLowerCase().equals(RecallTaskStatus.QUEUED.toString().toLowerCase())) {
            return RecallTaskStatus.QUEUED;
        }
        return UNDEFINED;
    }

    public int getStatusId() {
        return taskStatusRepresentation;
    }

    @Override
    public String toString() {
        return statusName;
    }

}