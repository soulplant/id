package com.id.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class TrieTest {
  private static final String phr_cc =
      "chrome/browser/custom_handlers/protocol_handler_registry.cc";
  private static final String phr_h =
      "chrome/browser/custom_handlers/protocol_handler_registry.h";
  @Test
  public void testNoFuzzyMatches() {
    Trie<String> trie = new Trie<String>();
    trie.addToken(phr_cc, phr_cc);
    List<String> matches = trie.doFuzzyMatch(true, "", "PHA");
    assertTrue(matches.isEmpty());
  }

  @Test
  public void testOneFuzzyMatch() {
    Trie<String> trie = new Trie<String>();
    trie.addToken(phr_cc, phr_cc);
    List<String> matches = trie.doFuzzyMatch(true, "", "PHR");
    assertEquals(1, matches.size());
    assertEquals(phr_cc, matches.get(0));
  }

  @Test
  public void testTwoFuzzyMatches() {
    Trie<String> trie = new Trie<String>();
    trie.addToken(phr_cc, phr_cc);
    trie.addToken(phr_h, phr_h);
    List<String> matches = trie.doFuzzyMatch(true, "", "PHR");
    assertEquals(2, matches.size());
  }

  @Test
  public void testNormalMatchingWorks() {
    Trie<String> trie = new Trie<String>();
    trie.addToken(phr_cc, phr_cc);
    trie.addToken(phr_h, phr_h);
    List<String> matches = trie.doFuzzyMatch(true, "", "protocol_handler_registry");
    assertEquals(2, matches.size());
  }

  @Test
  public void testDifferingKeysValues() {
    Trie<String> trie = new Trie<String>();
    trie.addToken("AAA", phr_cc);
    trie.addToken("BBB", phr_h);
    List<String> matches = trie.doFuzzyMatch(true, "", "A");
    assertEquals(1, matches.size());
    assertEquals(phr_cc, matches.get(0));
  }
}
