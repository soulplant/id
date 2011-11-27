package com.id.file;

import java.util.ArrayList;
import java.util.List;

public class CachingHighlight implements Highlight, File.Listener {
  private static class Match {
    public final int start;
    public final int length;

    public Match(int start, int length) {
      this.start = start;
      this.length = length;
    }

    public boolean doesContain(int x) {
      return x >= start && x < start + length;
    }
  }

  private static class LineMatches {
    private final List<Match> matches = new ArrayList<Match>();
    public void addMatch(Match match) {
      matches.add(match);
    }

    public boolean isMatchAt(int x) {
      for (Match match : matches) {
        if (match.doesContain(x)) {
          return true;
        }
      }
      return false;
    }
  }

  private final String word;
  private final List<LineMatches> lineMatches = new ArrayList<LineMatches>();

  public CachingHighlight(String word, List<String> lines) {
    this.word = word;

    for (String line : lines) {
      lineMatches.add(makeMatchFor(line));
    }
  }

  private LineMatches makeMatchFor(String line) {
    LineMatches matches = new LineMatches();
    int lastMatch = line.indexOf(word);
    while (lastMatch != -1) {
      matches.addMatch(new Match(lastMatch, word.length()));
      lastMatch = line.indexOf(word, lastMatch + 1);
    }
    return matches;
  }

  @Override
  public boolean isHighlighted(int y, int x) {
    return lineMatches.get(y).isMatchAt(x);
  }

  @Override
  public void onLineInserted(int y, String line) {
    lineMatches.add(y, makeMatchFor(line));
  }

  @Override
  public void onLineRemoved(int y, String line) {
    lineMatches.remove(y);
  }

  @Override
  public void onLineChanged(int y, String oldLine, String newLine) {
    lineMatches.set(y, makeMatchFor(newLine));
  }
}
