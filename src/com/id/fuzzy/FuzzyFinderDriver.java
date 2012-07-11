package com.id.fuzzy;

import java.util.List;

import com.id.file.File;
import com.id.file.Trie;

public class FuzzyFinderDriver implements FinderDriver, File.Listener {
  private final Trie<String> trie = new Trie<String>();

  public FuzzyFinderDriver(File file) {
    for (int i = 0; i < file.getLineCount(); i++) {
      onLineInserted(i, file.getLine(i));
    }
    // TODO(koz): This is leaked.
    file.addListener(this);
  }

  // FinderDriver.
  @Override
  public List<String> getMatches(String query) {
    return trie.doFuzzyMatch(true, "", query);
  }

  // File.Listener
  @Override
  public void onLineInserted(int y, String line) {
    trie.addToken(line, line);
  }

  @Override
  public void onLineRemoved(int y, String line) {
    trie.removeToken(line, line);
  }

  @Override
  public void onLineChanged(int y, String oldLine, String newLine) {
    trie.removeToken(oldLine, oldLine);
    trie.addToken(newLine, newLine);
  }
}
