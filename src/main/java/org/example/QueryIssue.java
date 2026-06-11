package org.example;

public class QueryIssue {

    private final IssueType issueType;
    private final IssueSeverity severity;
    private final String message;
    private final String suggestion;
    private final int weight;

    public QueryIssue(IssueType issueType, IssueSeverity severity,
                      String message, String suggestion) {
        this.issueType = issueType;
        this.severity = severity;
        this.message = message;
        this.suggestion = suggestion;
        this.weight = calculateWeight(severity);
    }

    private int calculateWeight(IssueSeverity severity) {
        switch (severity) {
            case HIGH:   return 7;
            case MEDIUM: return 3;
            case LOW:    return 1;
            default:     return 0;
        }
    }

    // Getters
    public IssueType getIssueType()     { return issueType; }
    public IssueSeverity getSeverity()  { return severity; }
    public String getMessage()          { return message; }
    public String getSuggestion()       { return suggestion; }
    public int getWeight()              { return weight; }

    public void printIssue() {
        System.out.println("[" + severity + "] " + issueType);
        System.out.println("  Problem:    " + message);
        System.out.println("  Fix:        " + suggestion);
        System.out.println("  Weight:     " + weight);
    }
}