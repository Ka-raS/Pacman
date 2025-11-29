package com.karas.pacman.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class ScoreDatabase {

    public static record Entry(int score, LocalDate date) {}

    public ScoreDatabase() {
        _databaseFile = null;
        _fileLength = 0;
        _entries = new ArrayList<>();
    }

    public ScoreDatabase(File databaseFile) throws FileNotFoundException, IOException {
        _databaseFile = databaseFile;
        _fileLength = 0;
        _entries = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(databaseFile)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(",");
                _entries.add(new Entry(
                    Integer.parseInt(split[0]),
                    LocalDate.parse(split[1])
                ));
                ++_fileLength;
            }
        }
    }

    public void addEntry(int score) {
        _entries.add(new Entry(score, LocalDate.now()));
    }

    public Iterable<Entry> getTopEntries(int count) {
        PriorityQueue<Entry> maxScores = new PriorityQueue<>(Comparator.comparingInt(Entry::score).reversed());
        for (Entry entry : _entries) {
            maxScores.offer(entry);
            if (maxScores.size() > count)
                maxScores.poll();
        }
        return maxScores;
    }

    public void save() throws IOException {
        if (_databaseFile == null || _entries.size() == _fileLength)
            return;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(_databaseFile, true))) {
            for (int i = _fileLength; i < _entries.size(); ++i) {
                Entry entry = _entries.get(i);
                writer.write(String.format("%05d,%s", entry.score(), entry.date()));
                writer.newLine();
                ++_fileLength;
            }
        }
    }


    private int _fileLength;
    private final ArrayList<Entry> _entries;
    private final File _databaseFile;

}