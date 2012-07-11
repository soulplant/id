package com.id.file;

import java.util.Arrays;
import java.util.List;

public class TokenCounter implements File.Listener {
  private final Trie<String> trie = new Trie<String>();

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
    trie.add(token, token);
  }

  private void removeToken(String token) {
    trie.remove(token, token);
  }
}
