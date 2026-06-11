package org.example;

public class QueryLog {

    private int id;
    private String sqlText;
    private long executionTimeMs;
    private String tableName;

    public QueryLog(int id, String sqlText, long executionTimeMs, String tableName) {
        this.id = id;
        this.sqlText = sqlText;
        this.executionTimeMs = executionTimeMs;
        this.tableName = tableName;

        // Validate immediately after setting fields
        QueryValidator.validate(this);
    }

    public int getId()                { return id; }
    public String getSqlText()        { return sqlText; }
    public long getExecutionTimeMs()  { return executionTimeMs; }
    public String getTableName()      { return tableName; }

    public boolean isSlow(long thresholdMs) {
        return executionTimeMs > thresholdMs;
    }

    public String getSeverity(long thresholdMs) {
        if (executionTimeMs > thresholdMs * 5) return "CRITICAL";
        if (executionTimeMs > thresholdMs * 2) return "HIGH";
        if (executionTimeMs > thresholdMs)     return "MEDIUM";
        return "OK";
    }

    public void printDetails(long thresholdMs) {
        System.out.println("Query #" + id + " on table: " + tableName);
        System.out.println("SQL: " + sqlText);
        System.out.println("Time taken: " + executionTimeMs + "ms");
        System.out.println("Severity: " + getSeverity(thresholdMs));
        System.out.println("---");
    }
}