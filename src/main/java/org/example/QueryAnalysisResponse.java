package org.example;

import java.util.List;

public class QueryAnalysisResponse {

    private QueryLog query;
    private String performanceSeverity;
    private List<QueryIssue> issues;
    private int problemScore;
    private String aiDecision;

    public QueryAnalysisResponse(QueryLog query, String performanceSeverity,
                                 List<QueryIssue> issues, int problemScore) {
        this.query = query;
        this.performanceSeverity = performanceSeverity;
        this.issues = issues;
        this.problemScore = problemScore;
        this.aiDecision = determineAiDecision(problemScore);
    }

    private String determineAiDecision(int score) {
        if (score > 15) return "REQUIRED — sending to Claude";
        if (score > 8)  return "QUEUED — background processing";
        return "NOT NEEDED — score acceptable";
    }

    public QueryLog getQuery()                  { return query; }
    public String getPerformanceSeverity()      { return performanceSeverity; }
    public List<QueryIssue> getIssues()         { return issues; }
    public int getProblemScore()                { return problemScore; }
    public String getAiDecision()               { return aiDecision; }
}