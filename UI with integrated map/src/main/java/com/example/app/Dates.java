package com.example.app;

import java.util.Objects;
public class Dates {

    private String startDate;

    private String finishDate;

    public Dates(String startDate, String finishDate) {
        this.startDate = startDate;
        this.finishDate = finishDate;
    }

    @Override
    public String toString() {
        return "Start date = " + startDate + ", Finish date = " + finishDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Dates)) {
            return false;
        }
        Dates otherDates = (Dates) obj;
        return (Objects.equals(this.startDate, otherDates.startDate)) && (Objects.equals(this.finishDate, otherDates.finishDate));
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, finishDate);
    }

    public String getStartDate() {
        return startDate;
    }

    public String getFinishDate() {
        return finishDate;
    }
}
