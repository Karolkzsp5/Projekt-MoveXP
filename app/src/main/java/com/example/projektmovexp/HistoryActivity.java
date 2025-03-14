package com.example.projektmovexp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HistoryActivity {
    private static final String PREFS_NAME = "WalkHistoryPrefs";
    private static final String KEY_WALKS = "walks";

    private SharedPreferences sharedPreferences;

    public HistoryActivity(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveWalk(int steps, float distance, long duration) {
        Set<String> walks = new HashSet<>(sharedPreferences.getStringSet(KEY_WALKS, new HashSet<>()));

        String walkEntry = steps + " kroków, " + String.format("%.2f km", distance) + ", " + formatTime(duration);
        walks.add(walkEntry);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(KEY_WALKS, walks);
        editor.apply();
    }

    public List<String> getWalkHistory() {
        return new ArrayList<>(sharedPreferences.getStringSet(KEY_WALKS, new HashSet<>()));
    }

    private String formatTime(long duration) {
        long seconds = duration / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);

    }
}

