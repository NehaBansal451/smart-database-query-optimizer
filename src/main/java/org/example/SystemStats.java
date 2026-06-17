package org.example;

public class SystemStats {

    private int totalQueries;
    private int slowQueries;
    private long averageExecutionTimeMs;
    private long slowestQueryTimeMs;
    private long thresholdMs;

    public SystemStats(int totalQueries, int slowQueries,
                       long averageExecutionTimeMs, long slowestQueryTimeMs,
                       long thresholdMs) {
        this.totalQueries = totalQueries;
        this.slowQueries = slowQueries;
        this.averageExecutionTimeMs = averageExecutionTimeMs;
        this.slowestQueryTimeMs = slowestQueryTimeMs;
        this.thresholdMs = thresholdMs;
    }

    public int getTotalQueries()             { return totalQueries; }
    public int getSlowQueries()              { return slowQueries; }
    public long getAverageExecutionTimeMs()  { return averageExecutionTimeMs; }
    public long getSlowestQueryTimeMs()      { return slowestQueryTimeMs; }
    public long getThresholdMs()             { return thresholdMs; }
}