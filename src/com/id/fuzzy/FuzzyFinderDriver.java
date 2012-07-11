package com.id.fuzzy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.id.file.File;
import com.id.file.Trie;

/**
 * Implements the fuzzy file finder logic. Uses a {@link Trie} to build an index
 * on the filenames, which are sourced from a file. Changes to the file are
 * reflected in the index.
 */
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
    trie.add(getIndexString(line), line);
  }

  @Override
  public void onLineRemoved(int y, String line) {
    trie.remove(getIndexString(line), line);
  }

  @Override
  public void onLineChanged(int y, String oldLine, String newLine) {
    trie.remove(oldLine, oldLine);
    trie.add(newLine, newLine);
  }

  private String getIndexString(String filename) {
    List<String> words = new ArrayList<String>(Arrays.asList(filename.split("/")));
    String basename = words.remove(words.size() - 1);
    StringBuffer buffer = new StringBuffer();
    for (String word : words) {
      buffer.append(word.substring(0, 1));
      buffer.append("/");
    }
    buffer.append(basename);
    return buffer.toString();
  }
}
