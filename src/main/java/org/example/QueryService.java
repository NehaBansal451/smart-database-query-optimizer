package org.example;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service //@Service tells Spring Boot this class contains business logic. Spring creates exactly ONE instance of this class when the app starts — called a singleton. That one instance is shared everywhere it's needed.
public class QueryService {

    private final QueryAnalyzer analyzer;
    private final QueryOptimizer optimizer;
    private final List<QueryLog> queryStore = new ArrayList<>();

    public QueryService() {
        this.analyzer = new QueryAnalyzer(500);
        this.optimizer = new QueryOptimizer();
        loadSampleData();
    }

    private void loadSampleData() {
        queryStore.add(new QueryLog(1, "SELECT * FROM users", 1200, "users"));
        queryStore.add(new QueryLog(2, "SELECT id FROM products", 80, "products"));
        queryStore.add(new QueryLog(3, "SELECT * FROM orders JOIN users ON orders.user_id = users.id", 3500, "orders"));
        queryStore.add(new QueryLog(4, "SELECT name FROM categories", 45, "categories"));
        queryStore.add(new QueryLog(5, "SELECT * FROM transactions JOIN accounts", 2100, "transactions"));
        queryStore.add(new QueryLog(6, "SELECT * FROM logs WHERE message LIKE '%error%'", 1800, "logs"));
    }

    // Get all queries
    public List<QueryLog> getAllQueries() {
        return queryStore;
    }

    // Get query by ID
    public QueryLog getQueryById(int id) {
        for (QueryLog query : queryStore) {
            if (query.getId() == id) {
                return query;
            }
        }
        return null;
    }

    // Add new query
    public QueryLog addQuery(QueryLog query) {
        queryStore.add(query);
        return query;
    }

    // Delete query by ID
    public boolean deleteQuery(int id) {
        for (int i = 0; i < queryStore.size(); i++) {
            if (queryStore.get(i).getId() == id) {
                queryStore.remove(i);
                return true;
            }
        }
        return false;
    }

    // Get slow queries
    public List<QueryLog> getSlowQueries(long threshold) {
        QueryAnalyzer customAnalyzer = new QueryAnalyzer(threshold);
        return customAnalyzer.findSlowQueries(queryStore);
    }

    // Get fast queries
    public List<QueryLog> getFastQueries() {
        return analyzer.findFastQueries(queryStore);
    }

    // Get system statistics
    public SystemStats getStats() {
        List<QueryLog> slowQueries = analyzer.findSlowQueries(queryStore);

        long totalTime = 0;
        long maxTime = 0;

        for (QueryLog query : queryStore) {
            totalTime += query.getExecutionTimeMs();
            if (query.getExecutionTimeMs() > maxTime) {
                maxTime = query.getExecutionTimeMs();
            }
        }

        long avgTime = queryStore.isEmpty() ? 0 : totalTime / queryStore.size();

        return new SystemStats(
                queryStore.size(),
                slowQueries.size(),
                avgTime,
                maxTime,
                500
        );
    }

    // Analyze one query — run optimizer
    public QueryAnalysisResponse analyzeQuery(int id) {
        QueryLog query = getQueryById(id);
        if (query == null) return null;

        String severity = query.getSeverity(500);
        List<QueryIssue> issues = optimizer.analyze(query);
        int score = optimizer.calculateScore(issues, severity);

        return new QueryAnalysisResponse(query, severity, issues, score);
    }
}

