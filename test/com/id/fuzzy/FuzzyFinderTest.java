package com.id.fuzzy;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.id.platform.InMemoryFileSystem;

public class FuzzyFinderTest {
  private InMemoryFileSystem fileSystem;
  private FuzzyFinder fuzzyFinder;
  @Before
  public void setup() {
    fileSystem = new InMemoryFileSystem();
    fuzzyFinder = new FuzzyFinder(fileSystem);
  }

  @Test
  public void simpleMatchTest() {
    fileSystem.insertFile("src/browser/ui/chrome.h");
    fileSystem.insertFile("src/browser/ui/chrome.cc");
    fileSystem.insertFile("src/test");
    fileSystem.insertFile("src/blah.gyp");

    fuzzyFinder.addPathToIndex("src");
    assertTrue(fuzzyFinder.contains("src/browser/ui/chrome.h"));
    assertEquals(2, fuzzyFinder.getMatches("chrome").size());
    assertEquals(1, fuzzyFinder.getMatches("gyp").size());
  }
}
