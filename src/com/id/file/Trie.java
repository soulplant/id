package com.id.file;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Trie {
  private int wordCount = 0;
  private Map<Character, Trie> children = new HashMap<Character, Trie>();

  public Trie() {
  }

  public void addToken(String token) {
    if (token.isEmpty()) {
      wordCount++;
    } else {
      char next = token.charAt(0);
      String substring = token.substring(1);
      if (!children.containsKey(next)) {
        children.put(next, new Trie());
      }
      children.get(next).addToken(substring);
    }
  }

  public boolean removeToken(String token) {
    if (token.isEmpty()) {
      if (wordCount > 0) {
        wordCount--;
      }
    } else {
      char next = token.charAt(0);
      if (children.containsKey(next)) {
        if (children.get(next).removeToken(token.substring(1))) {
          children.remove(next);
        }
      }
    }
    return wordCount == 0 && children.isEmpty();
  }

  public List<String> getCompletions(String prefix, String suffix) {
    if (suffix.isEmpty()) {
      List<String> result = new ArrayList<String>();
      if (wordCount > 0) {
        result.add(prefix);
      }
      for (char c : children.keySet()) {
        result.addAll(children.get(c).getCompletions(prefix + c, suffix));
      }
      return result;
    }
    char next = suffix.charAt(0);
    String substring = suffix.substring(1);
    if (children.containsKey(next)) {
      return children.get(next).getCompletions(prefix + next, substring);
    }
    return new ArrayList<String>();
  }

  public String getCompletion(String prefix, String suffix) {
    if (suffix.isEmpty()) {
      if (children.size() == 1) {
        for (char c : children.keySet()) {
          return children.get(c).getCompletion(prefix + c, suffix);
        }
      }
      return prefix;
    }
    char next = suffix.charAt(0);
    String substring = suffix.substring(1);
    if (children.containsKey(next)) {
      return children.get(next).getCompletion(prefix + next, substring);
    }
    return prefix;
  }

  public List<String> doFuzzyMatch(boolean onBoundary, String prefix, String suffix) {
    if (suffix.isEmpty()) {
      return getCompletions(prefix, suffix);
    }

    char next = suffix.charAt(0);
    String substring = suffix.substring(1);
    boolean isNextBoundary = isBoundary(next);
    List<String> result = new ArrayList<String>();

    for (char c : children.keySet()) {
      boolean isBoundaryMatch = (onBoundary || isBoundary(c)) && isNextBoundary;
      Trie child = children.get(c);
      if (equal(c, next, isBoundaryMatch)) {
        result.addAll(child.doFuzzyMatch(isBoundaryStart(c), prefix + c, substring));
      } else {
        result.addAll(child.doFuzzyMatch(isBoundaryStart(c), prefix + c, suffix));
      }
    }
    return result;
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
