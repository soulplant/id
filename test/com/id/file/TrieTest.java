package com.id.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import com.id.app.HighlightState;
import com.id.editor.Editor;
import com.id.editor.Register;
import com.id.events.EditorKeyHandler;
import com.id.events.KeyStroke;
import com.id.file.File;
import com.id.file.FileView;
import com.id.file.Tombstone;

public class TrieTest {
  private static final String phr_cc =
      "chrome/browser/custom_handlers/protocol_handler_registry.cc";
  private static final String phr_h =
      "chrome/browser/custom_handlers/protocol_handler_registry.h";
  @Test
  public void testNoFuzzyMatches() {
    Trie trie = new Trie();
    trie.addToken(phr_cc);
    List<String> matches = trie.doFuzzyMatch(true, "", "PHA");
    assertTrue(matches.isEmpty());
  }

  @Test
  public void testOneFuzzyMatch() {
    Trie trie = new Trie();
    trie.addToken(phr_cc);
    List<String> matches = trie.doFuzzyMatch(true, "", "PHR");
    assertEquals(1, matches.size());
    assertEquals(phr_cc, matches.get(0));
  }

  @Test
  public void testTwoFuzzyMatches() {
    Trie trie = new Trie();
    trie.addToken(phr_cc);
    trie.addToken(phr_h);
    List<String> matches = trie.doFuzzyMatch(true, "", "PHR");
    assertEquals(2, matches.size());
  }

  @Test
  public void testNormalMatchingWorks() {
    Trie trie = new Trie();
    trie.addToken(phr_cc);
    trie.addToken(phr_h);
    List<String> matches = trie.doFuzzyMatch(true, "", "protocol_handler_registry");
    assertEquals(2, matches.size());
  }
}
