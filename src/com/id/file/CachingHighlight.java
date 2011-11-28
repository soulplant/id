package com.id.file;

import java.util.ArrayList;
import java.util.List;

import com.id.editor.Point;

public class CachingHighlight implements Highlight, File.Listener {
  private static class Match {
    public final int start;
    public final int length;

    public Match(int start, int length) {
      this.start = start;
      this.length = length;
    }

    public boolean contains(int x) {
      return x >= start && x < start + length;
    }

    public boolean startsAt(int x) {
      return start == x;
    }
  }

  private static class LineMatches {
    private final List<Match> matches = new ArrayList<Match>();
    public void addMatch(Match match) {
      matches.add(match);
    }

    public boolean isMatchAt(int x) {
      for (Match match : matches) {
        if (match.contains(x)) {
          return true;
        }
      }
      return false;
    }

    public int getNextMatch(int x) {
      boolean pastCurrentMatch = false;
      for (Match match : matches) {
        if (match.contains(x)) {
          pastCurrentMatch = true;
          continue;
        }
        if (pastCurrentMatch) {
          return match.start;
        }
      }
      return -1;
    }

    public int getPreviousMatch(int x) {
      boolean skippedMatchAtStart = false;
      for (int i = matches.size() - 1; i >= 0; i--) {
        Match match = matches.get(i);
        if (match.contains(x)) {
          if (match.startsAt(x)) {
            if (skippedMatchAtStart) {
              return match.start;
            } else {
              skippedMatchAtStart = true;
            }
          } else {
            return match.start;
          }
        }
      }
      return -1;
    }

    public boolean isEmpty() {
      return matches.isEmpty();
    }

    public int getFirstMatch() {
      return matches.get(0).start;
    }

    public int getLastMatch() {
      return matches.get(matches.size() - 1).start;
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

  @Override
  public Point getNextMatch(int y, int x) {
    int n = lineMatches.get(y).getNextMatch(x);
    if (n != -1) {
      return new Point(y, n);
    }
    // No more matches on the current line so we go looking.
    for (int i = y + 1; i < lineMatches.size(); i++) {
      LineMatches matches = lineMatches.get(i);
      if (!matches.isEmpty()) {
        return new Point(i, matches.getFirstMatch());
      }
    }
    // No more matches.
    return null;
  }

  @Override
  public Point getPreviousMatch(int y, int x) {
    int n = lineMatches.get(y).getPreviousMatch(x);
    if (n != -1) {
      return new Point(y, n);
    }
    for (int i = y - 1; i >= 0; i--) {
      LineMatches matches = lineMatches.get(i);
      if (!matches.isEmpty()) {
        return new Point(i, matches.getLastMatch());
      }
    }
    return null;
  }
}
