package org.example;

import java.util.ArrayList;
import java.util.List;

public class QueryOptimizer {

    // Entry point — analyze one query and return all issues found
    public List<QueryIssue> analyze(QueryLog query) {
        List<QueryIssue> issues = new ArrayList<>();

        String sql = query.getSqlText().toUpperCase().trim();

        detectWildcardLike(sql, issues);
        detectSelectStar(sql, issues);
        detectMissingJoinCondition(sql, issues);
        suggestIndexForJoin(sql, query.getTableName(), issues);

        return issues;
    }

    // Rule 1 — Detect SELECT *
    private void detectSelectStar(String sql, List<QueryIssue> issues) {
        if (sql.contains("SELECT *")) {
            issues.add(new QueryIssue(
                    IssueType.SELECT_STAR,
                    IssueSeverity.HIGH,
                    "SELECT * fetches all columns including unused ones. " +
                            "This wastes memory, bandwidth, and prevents index-only scans.",
                    "Replace SELECT * with only the columns you need. " +
                            "Example: SELECT id, name, email FROM users"
            ));
        }
    }

    // Rule 2 — Detect JOIN without ON condition
    private void detectMissingJoinCondition(String sql, List<QueryIssue> issues) {
        boolean hasJoin = sql.contains(" JOIN ");
        boolean hasOn   = sql.contains(" ON ");

        if (hasJoin && !hasOn) {
            issues.add(new QueryIssue(
                    IssueType.MISSING_JOIN_CONDITION,
                    IssueSeverity.HIGH,
                    "JOIN detected without an ON condition. " +
                            "This creates a cartesian product — every row joined with every other row. " +
                            "With 1000 rows in each table this produces 1,000,000 result rows.",
                    "Add an ON condition to your JOIN. " +
                            "Example: JOIN users ON orders.user_id = users.id"
            ));
        }
    }

    // Rule 3 — Suggest index for JOIN columns
    private void suggestIndexForJoin(String sql, String tableName,
                                     List<QueryIssue> issues) {
        if (sql.contains(" JOIN ") && sql.contains(" ON ")) {
            issues.add(new QueryIssue(
                    IssueType.MISSING_INDEX_SUGGESTION,
                    IssueSeverity.MEDIUM,
                    "JOIN with ON condition detected. " +
                            "The columns used in the ON clause may lack database indexes. " +
                            "Without indexes, every JOIN requires a full table scan.",
                    "Add an index on the JOIN column. " +
                            "Example: CREATE INDEX idx_orders_user_id ON " +
                            tableName + "(user_id)"
            ));
        }
    }

    //Rule 4 - detectWildcardLike
    private void detectWildcardLike(String sql, List<QueryIssue> issues) {
        if (sql.contains("LIKE '%")) {
            issues.add(new QueryIssue(
                    IssueType.INEFFICIENT_WILDCARD,
                    IssueSeverity.MEDIUM,
                    "Leading wildcard in LIKE '%value' prevents index usage. " +
                            "Database must scan every single row to find matches.",
                    "Avoid leading wildcards. Use full-text search instead, " +
                            "or restructure to LIKE 'value%' if business logic allows."
            ));
        }
    }

    // Calculate total problem score for a list of issues
    public int calculateScore(List<QueryIssue> issues, String performanceSeverity) {
        int rawScore = 0;
        for (QueryIssue issue : issues) {
            rawScore += issue.getWeight();
        }

        double multiplier = getPerformanceMultiplier(performanceSeverity);
        return (int) (rawScore * multiplier);
    }

    private double getPerformanceMultiplier(String performanceSeverity) {
        switch (performanceSeverity) {
            case "CRITICAL": return 2.0;
            case "HIGH":     return 1.5;
            case "MEDIUM":   return 1.0;
            default:         return 0.5;
        }
    }

    // Print full analysis report for one query
    public void printReport(QueryLog query, String performanceSeverity,
                            List<QueryIssue> issues, int score) {
        System.out.println("========================================");
        System.out.println("QUERY ANALYSIS REPORT");
        System.out.println("========================================");
        System.out.println("Query ID    : " + query.getId());
        System.out.println("Table       : " + query.getTableName());
        System.out.println("SQL         : " + query.getSqlText());
        System.out.println("Executed    : " + query.getExecutionTimeMs() + "ms");
        System.out.println("Performance : " + performanceSeverity);
        System.out.println();

        if (issues.isEmpty()) {
            System.out.println("No SQL issues detected.");
        } else {
            System.out.println("ISSUES FOUND (" + issues.size() + "):");
            System.out.println("-----------------");
            for (QueryIssue issue : issues) {
                issue.printIssue();
                System.out.println();
            }
        }

        System.out.println("PROBLEM SCORE : " + score);
        System.out.println("AI ANALYSIS   : " +
                (score > 15 ? "REQUIRED — score above threshold"
                        : "Not needed — score within acceptable range"));
        System.out.println("========================================");
        System.out.println();
    }
}