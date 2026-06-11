# Smart Database Query Optimizer

A production-grade backend system that automatically detects slow database queries, analyzes SQL structure, scores performance issues, and determines when AI analysis is needed.

## Problem It Solves
Companies run thousands of database queries every second. Slow or poorly written queries cause performance degradation, increased costs, and bad user experience. This system automates the detection and analysis process.

## Features
- Automatic slow query detection with configurable threshold
- SQL structure analysis (SELECT *, missing JOIN conditions, missing indexes, wildcard patterns)
- Severity scoring system (CRITICAL, HIGH, MEDIUM, OK)
- Problem score calculation with performance multiplier
- Three-tier AI decision engine (Required, Queued, Not Needed)
- Input validation with custom exceptions
- Bulletproof error handling — no invalid data enters the system

## Tech Stack
- Java 17
- Maven
- Spring Boot (coming in Week 2)
- PostgreSQL (coming in Week 3)
- Claude AI API (coming in Week 4)

## Project Architecture
QueryLog → QueryAnalyzer → QueryOptimizer → Scoring → AI Decision
## Detection Rules
| Rule | Severity | Weight |
|---|---|---|
| SELECT * usage | HIGH | 7 |
| Missing JOIN condition | HIGH | 7 |
| Missing index suggestion | MEDIUM | 3 |
| Inefficient wildcard LIKE | MEDIUM | 3 |

## Scoring System
- Each issue has a weight based on severity
- Final score = raw score × performance multiplier
- CRITICAL × 2.0, HIGH × 1.5, MEDIUM × 1.0
- Score > 15 → AI analysis required immediately
- Score > 8 → Queued for background AI processing

## Validation Rules
- SQL text cannot be null or empty
- SQL must start with SELECT, INSERT, UPDATE or DELETE
- Execution time cannot be negative
- Table name cannot be empty or contain special characters
- Query ID must be positive

## Sample Output
QUERY ANALYSIS REPORT
Query ID    : 3
Table       : orders
SQL         : SELECT * FROM orders JOIN users ON orders.user_id = users.id
Time        : 3500ms
Performance : CRITICAL
ISSUES FOUND (2):
[HIGH] SELECT_STAR — Weight: 7
[MEDIUM] MISSING_INDEX_SUGGESTION — Weight: 3
PROBLEM SCORE : 20
AI ANALYSIS   : REQUIRED

## Project Structure
src/main/java/org/example/
├── QueryLog.java                 → Data model
├── QueryAnalyzer.java            → Performance detection
├── QueryOptimizer.java           → SQL structure analysis
├── QueryIssue.java               → Structured issue output
├── IssueType.java                → Issue type enum
├── IssueSeverity.java            → Severity level enum
├── QueryValidator.java           → Input validation
├── InvalidQueryException.java    → Custom exception
└── Main.java                     → Entry point


## How To Run
1. Clone the repository
2. Open in IntelliJ IDEA
3. Make sure Java 17 is installed
4. Run `Main.java`

## What's Coming Next
- Week 2: Spring Boot REST API with endpoints
- Week 3: PostgreSQL persistent storage
- Week 4: Claude AI integration for smart suggestions

## Author
Built as part of a 25-day backend engineering challenge.
Goal: Internship-ready full-stack project in 1 month.