package application;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class searchHistory {

    // Inner class to represent a history entry
    static class HistoryEntry {
        private String query;
        private String timestamp;

        public HistoryEntry(String query, String timestamp) {
            this.query = query;
            this.timestamp = timestamp;
        }

        public String getQuery() {
            return query;
        }

        public String getTimestamp() {
            return timestamp;
        }

        @Override
        public String toString() {
            return "Query: " + query + " | Time: " + timestamp;
        }
    }

    // List to hold the search history
    private List<HistoryEntry> historyList = new ArrayList<>();

    // Method to add a new entry to the history
    public void addEntry(String query) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        historyList.add(new HistoryEntry(query, timestamp));
    }

    // Method to get the history list
    public List<HistoryEntry> getHistory() {
        return historyList;
    }
}
