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

import java.util.List;

@RestController
@RequestMapping("/queries")
public class QueryController {

    private final QueryService queryService;

    public QueryController(QueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    public ResponseEntity<List<QueryLog>> getAllQueries() {
        return ResponseEntity.ok(queryService.getAllQueries());
    }

    @GetMapping("/{id}")
    public ResponseEntity<QueryLog> getQueryById(@PathVariable int id) {
        QueryLog query = queryService.getQueryById(id);
        if (query == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(query);
    }

    @GetMapping("/slow")
    public ResponseEntity<List<QueryLog>> getSlowQueries(
            @RequestParam(defaultValue = "500") long threshold) {
        return ResponseEntity.ok(queryService.getSlowQueries(threshold));
    }

    @GetMapping("/fast")
    public ResponseEntity<List<QueryLog>> getFastQueries() {
        return ResponseEntity.ok(queryService.getFastQueries());
    }

    @GetMapping("/stats")
    public ResponseEntity<SystemStats> getStats() {
        return ResponseEntity.ok(queryService.getStats());
    }

    @PostMapping
    public ResponseEntity<QueryLog> addQuery(@RequestBody QueryLog query) {
        try {
            QueryLog saved = queryService.addQuery(query);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (InvalidQueryException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuery(@PathVariable int id) {
        boolean deleted = queryService.deleteQuery(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/analyze")
    public ResponseEntity<QueryAnalysisResponse> analyzeQuery(@PathVariable int id) {
        QueryAnalysisResponse response = queryService.analyzeQuery(id);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(response);
    }
}

