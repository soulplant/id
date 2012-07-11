package com.id.file;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Trie<T> {
  private final List<T> endPoints = new ArrayList<T>();

  private final Map<Character, Trie<T>> children = new HashMap<Character, Trie<T>>();

  private static class FuzzyResult implements Comparable<FuzzyResult> {
    private final int score;
    private final String text;

    public FuzzyResult(int score, String text) {
      this.score = score;
      this.text = text;
    }

    public String getText() {
      return text;
    }

    @Override
    public int compareTo(FuzzyResult other) {
      if (score < other.score) {
        return -1;
      }
      return score == other.score ? 0 : 1;
    }
  }

  public Trie() {
  }

  public void add(String token, T t) {
    if (token.isEmpty()) {
      endPoints.add(t);
    } else {
      char next = token.charAt(0);
      String substring = token.substring(1);
      if (!children.containsKey(next)) {
        children.put(next, new Trie<T>());
      }
      children.get(next).add(substring, t);
    }
  }

  public boolean remove(String token, T t) {
    if (token.isEmpty()) {
      endPoints.remove(t);
    } else {
      char next = token.charAt(0);
      if (children.containsKey(next)) {
        if (children.get(next).remove(token.substring(1), t)) {
          children.remove(next);
        }
      }
    }
    return endPoints.isEmpty() && children.isEmpty();
  }

  public List<String> getCompletions(String prefix, String query) {
    if (query.isEmpty()) {
      List<String> result = new ArrayList<String>();
      for (T endPoint : endPoints) {
        result.add(endPoint.toString());
      }
      for (char c : children.keySet()) {
        result.addAll(children.get(c).getCompletions(prefix + c, query));
      }
      return result;
    }
    char queryHead = query.charAt(0);
    String queryTail = query.substring(1);
    if (children.containsKey(queryHead)) {
      return children.get(queryHead).getCompletions(prefix + queryHead, queryTail);
    }
    return new ArrayList<String>();
  }

  public String getCompletion(String prefix, String query) {
    if (query.isEmpty()) {
      if (children.size() == 1) {
        for (char c : children.keySet()) {
          return children.get(c).getCompletion(prefix + c, query);
        }
      }
      return prefix;
    }
    char queryHead = query.charAt(0);
    String queryTail = query.substring(1);
    if (children.containsKey(queryHead)) {
      return children.get(queryHead).getCompletion(prefix + queryHead, queryTail);
    }
    return prefix;
  }

  public List<String> doFuzzyMatch(boolean onBoundary, String prefix, String query) {
    PriorityQueue<FuzzyResult> results = new PriorityQueue<FuzzyResult>();
    doFuzzyMatch(results, 0, onBoundary, prefix, query);
    List<String> stringResults = new ArrayList<String>();
    Set<String> uniqueStrings = new HashSet<String>();
    for (FuzzyResult result : results) {
      if (uniqueStrings.add(result.getText())) {
        stringResults.add(result.getText());
      }
    }
    return stringResults;
  }

  // boundariesPassed - how many word boundaries have been passed
  // onBoundary - are we matching directly after a boundary
  // prefix - characters passed
  public void doFuzzyMatch(PriorityQueue<FuzzyResult> result,
      int boundariesPassed, boolean onBoundary, String prefix, String query) {
    if (query.isEmpty()) {
      for (String completion : getCompletions(prefix, query)) {
        result.add(new FuzzyResult(boundariesPassed * 20, completion));
      }
      return;
    }

    char queryHead = query.charAt(0);
    String queryTail = query.substring(1);
    boolean isNextBoundary = isBoundary(queryHead);

    for (char c : children.keySet()) {
      boolean isBoundaryMatch = (onBoundary || isBoundary(c)) && isNextBoundary;
      int boundaryCount = boundariesPassed + (isBoundary(c) ? 1 : 0);
      Trie<T> child = children.get(c);
      if (equal(c, queryHead, isBoundaryMatch)) {
       child.doFuzzyMatch(result, boundaryCount, isBoundaryStart(c), prefix + c, queryTail);
      }
      child.doFuzzyMatch(result, boundaryCount, isBoundaryStart(c), prefix + c, query);
    }
    return;
  }

  private boolean isBoundaryStart(char c) {
    return c == '/' || c == '_';
  }

  private boolean isBoundary(char c) {
    return Character.isUpperCase(c);
  }

  private boolean equal(char a, char b, boolean caseInsensitive) {
    if (caseInsensitive) {
      return equalI(a, b);
    }
    return a == b;
  }

  private boolean equalI(char a, char b) {
    return Character.toLowerCase(a) == Character.toLowerCase(b);
  }
}
