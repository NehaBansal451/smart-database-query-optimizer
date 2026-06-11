package org.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class QueryAnalyzer {

    private final long thresholdMs;

    public QueryAnalyzer(long thresholdMs) {
        this.thresholdMs = thresholdMs;
    }

    public List<QueryLog> findSlowQueries(List<QueryLog> allQueries) {
        List<QueryLog> slowQueries = new ArrayList<>();

        for (QueryLog query : allQueries) {
            if (query.isSlow(thresholdMs)) {
                slowQueries.add(query);
            }
        }

        slowQueries.sort(Comparator.comparingLong(QueryLog::getExecutionTimeMs).reversed());

        return slowQueries;
    }

    public List<QueryLog> findFastQueries(List<QueryLog> allQueries) {
        List<QueryLog> fastQueries = new ArrayList<>();

        for (QueryLog query : allQueries) {
            if (!query.isSlow(thresholdMs)) {
                fastQueries.add(query);
            }
        }

        return fastQueries;
    }

    public void printStats(List<QueryLog> allQueries) {
        if (allQueries.isEmpty()) {
            System.out.println("No queries to analyze.");
            return;
        }

        long total = allQueries.size();
        long slowCount = findSlowQueries(allQueries).size();
        long totalTime = 0;
        long maxTime = 0;

        for (QueryLog query : allQueries) {
            totalTime += query.getExecutionTimeMs();
            if (query.getExecutionTimeMs() > maxTime) {
                maxTime = query.getExecutionTimeMs();
            }
        }

        long avgTime = totalTime / total;

        System.out.println("=== SYSTEM STATS ===");
        System.out.println("Total queries analyzed: " + total);
        System.out.println("Slow queries detected:  " + slowCount);
        System.out.println("Average execution time: " + avgTime + "ms");
        System.out.println("Slowest query time:     " + maxTime + "ms");
        System.out.println("Threshold used:         " + thresholdMs + "ms");
        System.out.println("====================");
    }
}
