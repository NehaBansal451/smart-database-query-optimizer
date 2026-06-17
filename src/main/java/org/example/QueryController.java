package org.example;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/queries")
public class QueryController {

    private final QueryAnalyzer analyzer;
    private final QueryOptimizer optimizer;
    private final List<QueryLog> queryStore = new ArrayList<>();

    public QueryController() {
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

    // GET /queries — return all queries
    @GetMapping
    public ResponseEntity<List<QueryLog>> getAllQueries() {
        return ResponseEntity.ok(queryStore);
    }

    // GET /queries/{id} — return one query by ID
    @GetMapping("/{id}")
    public ResponseEntity<QueryLog> getQueryById(@PathVariable int id) {
        for (QueryLog query : queryStore) {
            if (query.getId() == id) {
                return ResponseEntity.ok(query);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // GET /queries/slow — return only slow queries
    @GetMapping("/slow")
    public ResponseEntity<List<QueryLog>> getSlowQueries(
            @RequestParam(defaultValue = "500") long threshold) {
        QueryAnalyzer customAnalyzer = new QueryAnalyzer(threshold);
        List<QueryLog> slowQueries = customAnalyzer.findSlowQueries(queryStore);
        return ResponseEntity.ok(slowQueries);
    }

    // GET /queries/fast — return only fast queries
    @GetMapping("/fast")
    public ResponseEntity<List<QueryLog>> getFastQueries() {
        List<QueryLog> fastQueries = analyzer.findFastQueries(queryStore);
        return ResponseEntity.ok(fastQueries);
    }

    // GET /queries/stats — return system statistics
    @GetMapping("/stats")
    public ResponseEntity<SystemStats> getStats() {
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

        SystemStats stats = new SystemStats(
                queryStore.size(),
                slowQueries.size(),
                avgTime,
                maxTime,
                500
        );

        return ResponseEntity.ok(stats);
    }

    // POST /queries — add a new query
    @PostMapping
    public ResponseEntity<QueryLog> addQuery(@RequestBody QueryLog query) {
        try {
            queryStore.add(query);
            return ResponseEntity.status(HttpStatus.CREATED).body(query);
        } catch (InvalidQueryException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // DELETE /queries/{id} — delete a query by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuery(@PathVariable int id) {
        for (int i = 0; i < queryStore.size(); i++) {
            if (queryStore.get(i).getId() == id) {
                queryStore.remove(i);
                return ResponseEntity.noContent().build();
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}

