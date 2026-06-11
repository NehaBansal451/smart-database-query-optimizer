package org.example;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        long thresholdMs = 500;

        System.out.println("=== TESTING VALIDATION ===");
        System.out.println();

        // Test 1 — null SQL
        testInvalidQuery("Test 1 - null SQL", () ->
                new QueryLog(1, null, 1200, "users")
        );

        // Test 2 — empty SQL
        testInvalidQuery("Test 2 - empty SQL", () ->
                new QueryLog(2, "", 1200, "users")
        );

        // Test 3 — negative execution time
        testInvalidQuery("Test 3 - negative time", () ->
                new QueryLog(3, "SELECT * FROM users", -500, "users")
        );

        // Test 4 — empty table name
        testInvalidQuery("Test 4 - empty table name", () ->
                new QueryLog(4, "SELECT * FROM users", 1200, "")
        );

        // Test 5 — invalid ID
        testInvalidQuery("Test 5 - invalid ID", () ->
                new QueryLog(-1, "SELECT * FROM users", 1200, "users")
        );

        // Test 6 — invalid SQL keyword
        testInvalidQuery("Test 6 - invalid SQL keyword", () ->
                new QueryLog(6, "DROP TABLE users", 1200, "users")
        );
        //Test 7 — contain space or not
        testInvalidQuery("Test 7 - SQL with no spaces", () ->
                new QueryLog(7, "SELECTFROMUSERS", 1200, "users")
        );

        System.out.println("=== VALIDATION TESTS COMPLETE ===");
        System.out.println();

        // Now run normal valid queries
        System.out.println("=== RUNNING VALID QUERIES ===");
        System.out.println();

        List<QueryLog> allQueries = new ArrayList<>();
        allQueries.add(new QueryLog(1, "SELECT * FROM users", 1200, "users"));
        allQueries.add(new QueryLog(2, "SELECT id FROM products", 80, "products"));
        allQueries.add(new QueryLog(3, "SELECT * FROM orders JOIN users ON orders.user_id = users.id", 3500, "orders"));
        allQueries.add(new QueryLog(4, "SELECT name FROM categories", 45, "categories"));
        allQueries.add(new QueryLog(5, "SELECT * FROM transactions JOIN accounts", 2100, "transactions"));
        allQueries.add(new QueryLog(6, "SELECT * FROM logs WHERE message LIKE '%error%'", 1800, "logs"));

        QueryAnalyzer analyzer = new QueryAnalyzer(thresholdMs);
        QueryOptimizer optimizer = new QueryOptimizer();

        analyzer.printStats(allQueries);
        System.out.println();

        List<QueryLog> slowQueries = analyzer.findSlowQueries(allQueries);

        System.out.println(">>> Running deep analysis on "
                + slowQueries.size() + " slow queries...");
        System.out.println();

        for (QueryLog query : slowQueries) {
            String severity = query.getSeverity(thresholdMs);
            List<QueryIssue> issues = optimizer.analyze(query);
            int score = optimizer.calculateScore(issues, severity);
            optimizer.printReport(query, severity, issues, score);
        }
    }

    // Helper method to test invalid queries cleanly
    private static void testInvalidQuery(String testName, Runnable action) {
        try {
            action.run();
            System.out.println("❌ " + testName + " — FAILED: No exception thrown");
        } catch (InvalidQueryException e) {
            System.out.println("✅ " + testName + " — PASSED");
            System.out.println("   Field   : " + e.getField());
            System.out.println("   Value   : " + e.getInvalidValue());
            System.out.println("   Message : " + e.getMessage());
            System.out.println();
        }
    }
}