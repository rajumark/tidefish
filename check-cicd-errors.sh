#!/bin/bash

# GitHub CI/CD Error Checker Script
# Usage: ./check-cicd-errors.sh

REPO="rajumark/tidefish"
API_BASE="https://api.github.com/repos/$REPO"

echo "🔍 Checking latest CI/CD run for errors..."

# Step 1: Get Latest Completed Run
echo "📋 Step 1: Getting latest completed run..."
LATEST_RUN=$(curl -s "$API_BASE/actions/runs?per_page=1&status=completed" -H "Accept: application/vnd.github+json")

# Check if we have any runs
TOTAL_COUNT=$(echo "$LATEST_RUN" | grep -o '"total_count":[0-9]*' | cut -d':' -f2)
if [ "$TOTAL_COUNT" -eq 0 ]; then
    echo "❌ No completed workflow runs found."
    exit 0
fi

# Get conclusion and run ID
CONCLUSION=$(echo "$LATEST_RUN" | grep -o '"conclusion":"[^"]*"' | head -1 | cut -d':' -f2 | tr -d '"')
RUN_ID=$(echo "$LATEST_RUN" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)

echo "📊 Latest run #$RUN_ID - Conclusion: $CONCLUSION"

if [ "$CONCLUSION" != "failure" ]; then
    echo "✅ Latest run completed successfully (or was cancelled/skipped)"
    exit 0
fi

echo "❌ FAILURE DETECTED! Analyzing..."

# Step 2: Get Failed Jobs
echo "🔍 Step 2: Finding failed jobs..."
JOBS=$(curl -s "$API_BASE/actions/runs/$RUN_ID/jobs" -H "Accept: application/vnd.github+json")

# Extract failed job names
FAILED_JOBS=$(echo "$JOBS" | grep -B5 '"conclusion":"failure"' | grep '"name":"' | cut -d'"' -f4)

echo "🚨 Failed jobs:"
echo "$FAILED_JOBS"

# Step 3: Download and analyze logs
echo "📥 Step 3: Downloading error logs..."
curl -sL "$API_BASE/actions/runs/$RUN_ID/logs" -o logs.zip

if [ -f "logs.zip" ]; then
    echo "📂 Extracting logs..."
    unzip -o logs.zip >/dev/null 2>&1
    
    echo "🔥 ERROR SUMMARY:"
    echo "=================="
    
    # Find all error lines
    for log_file in *.txt; do
        if [ -f "$log_file" ]; then
            echo "📄 $log_file:"
            grep -i "error\|exception\|failed\|failure" "$log_file" | head -10
            echo "---"
        fi
    done
    
    # Cleanup
    rm -f logs.zip *.txt
    
    echo "✅ Error analysis complete!"
else
    echo "❌ Failed to download logs"
fi
