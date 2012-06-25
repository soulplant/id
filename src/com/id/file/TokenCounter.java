package com.id.file;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class TokenCounter implements File.Listener {
  private static class TrieNode {
    private int wordCount = 0;
    private Map<Character, TrieNode> children =
        new HashMap<Character, TrieNode>();

    public TrieNode() {
    }

    public void addToken(String token) {
      if (token.isEmpty()) {
        wordCount++;
      } else {
        char next = token.charAt(0);
        String substring = token.substring(1);
        if (!children.containsKey(next)) {
          children.put(next, new TrieNode());
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
  }

  private TrieNode trie = new TrieNode();

  public TokenCounter() {
  }

  @Override
  public void onLineInserted(int y, String line) {
    for (String token : getTokens(line)) {
      addToken(token);
    }
  }

  @Override
  public void onLineRemoved(int y, String line) {
    for (String token : getTokens(line)) {
      removeToken(token);
    }
  }

  @Override
  public void onLineChanged(int y, String oldLine, String newLine) {
    onLineRemoved(y, oldLine);
    onLineInserted(y, newLine);
  }

  public List<String> getCompletions(String partialToken) {
    return trie.getCompletions("", partialToken);
  }

  public String getCompletion(String partialToken) {
    return trie.getCompletion("", partialToken);
  }

  private List<String> getTokens(String line) {
    return Arrays.asList(line.split(" "));
  }

  private void addToken(String token) {
    trie.addToken(token);
  }

  private void removeToken(String token) {
    trie.removeToken(token);
  }
}
