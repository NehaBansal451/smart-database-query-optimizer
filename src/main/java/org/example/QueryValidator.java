package org.example;

public class QueryValidator {

    private static final int MAX_SQL_LENGTH = 2000;
    private static final long MAX_EXECUTION_TIME = 300_000; // 5 minutes max

    // Validate entire QueryLog object
    public static void validate(QueryLog query) {
        if (query == null) {
            throw new InvalidQueryException("query", null,
                    "QueryLog object cannot be null");
        }

        validateId(query.getId());
        validateSqlText(query.getSqlText());
        validateExecutionTime(query.getExecutionTimeMs());
        validateTableName(query.getTableName());
    }

    // Validate ID
    private static void validateId(int id) {
        if (id <= 0) {
            throw new InvalidQueryException("id", id,
                    "Query ID must be a positive number. Received: " + id);
        }
    }

    // Validate SQL text
    private static void validateSqlText(String sqlText) {
        if (sqlText == null) {
            throw new InvalidQueryException("sqlText", null,
                    "SQL text cannot be null");
        }
        if (sqlText.trim().isEmpty()) {
            throw new InvalidQueryException("sqlText", sqlText,
                    "SQL text cannot be empty or blank");
        }
        if (sqlText.length() > MAX_SQL_LENGTH) {
            throw new InvalidQueryException("sqlText", sqlText.length(),
                    "SQL text too long. Maximum allowed: " + MAX_SQL_LENGTH +
                            " characters. Received: " + sqlText.length());
        }
        if (!startsWithValidKeyword(sqlText.trim().toUpperCase())) {
            throw new InvalidQueryException("sqlText", sqlText,
                    "SQL must start with SELECT, INSERT, UPDATE, or DELETE. " +
                            "Received: " + sqlText.trim().substring(0, Math.min(20, sqlText.length())));
        }
        if(!sqlText.contains(" ")){
            throw new InvalidQueryException("sqlText",sqlText,
                    "SQL text does not contain space");
        }
    }

    // Validate execution time
    private static void validateExecutionTime(long executionTimeMs) {
        if (executionTimeMs < 0) {
            throw new InvalidQueryException("executionTimeMs", executionTimeMs,
                    "Execution time cannot be negative. Received: " + executionTimeMs + "ms");
        }
        if (executionTimeMs > MAX_EXECUTION_TIME) {
            throw new InvalidQueryException("executionTimeMs", executionTimeMs,
                    "Execution time exceeds maximum limit of " + MAX_EXECUTION_TIME +
                            "ms. Received: " + executionTimeMs + "ms");
        }
    }

    // Validate table name
    private static void validateTableName(String tableName) {
        if (tableName == null) {
            throw new InvalidQueryException("tableName", null,
                    "Table name cannot be null");
        }
        if (tableName.trim().isEmpty()) {
            throw new InvalidQueryException("tableName", tableName,
                    "Table name cannot be empty or blank");
        }
        if (!tableName.matches("[a-zA-Z][a-zA-Z0-9_]*")) {
            throw new InvalidQueryException("tableName", tableName,
                    "Table name contains invalid characters. " +
                            "Only letters, numbers, and underscores allowed. " +
                            "Received: " + tableName);
        }
    }

    // Check SQL starts with valid keyword
    private static boolean startsWithValidKeyword(String sql) {
        return sql.startsWith("SELECT") ||
                sql.startsWith("INSERT") ||
                sql.startsWith("UPDATE") ||
                sql.startsWith("DELETE");
    }
}