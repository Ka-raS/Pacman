package com.karas.pacman.resources;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ScoreDatabase {

    public static record Entry(int score, LocalDate date) {}

    public ScoreDatabase(File file) {
        _fileLength = 0;
        _entries = new ArrayList<>();
        if (file == null) {
            _file = null;
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(",");
                _entries.add(new Entry(
                    Integer.parseInt(split[0]),
                    LocalDate.parse(split[1])
                ));
            }

        } catch (IOException e) {
            LOG.log(Level.SEVERE, e, () -> "Failed Reading Database File: " + file.getAbsolutePath());
            _entries.clear();
        }

        _file = file;
        _fileLength = _entries.size();
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

    public void save() {
        if (_file == null || _entries.size() == _fileLength)
            return;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(_file, true))) {
            while (_fileLength < _entries.size()) {
                Entry entry = _entries.get(_fileLength++);
                writer.write(String.format("%05d,%s", entry.score(), entry.date()));
                writer.newLine();
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Failed Saving Database On Exit", e);
        }
    }


    private static final Logger LOG = Logger.getLogger(ScoreDatabase.class.getName());

    private final ArrayList<Entry> _entries;
    private final File _file;
    private int _fileLength;

}