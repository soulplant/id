package com.id.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class TokenCounterTest {
  @Test
  public void singleCompletion() {
    TokenCounter tokenCounter = new TokenCounter();
    tokenCounter.onLineInserted(0, "hello world");
    List<String> completions = tokenCounter.getCompletions("h");
    assertEquals(1, completions.size());
  }

  @Test
  public void multipleCompletions() {
    TokenCounter tokenCounter = new TokenCounter();
    tokenCounter.onLineInserted(0, "hello world hi ha");
    List<String> completions = tokenCounter.getCompletions("h");
    assertEquals(3, completions.size());
    assertTrue(completions.contains("hello"));
    assertTrue(completions.contains("hi"));
    assertTrue(completions.contains("ha"));
  }

  @Test
  public void getToNextCompletion() {
    TokenCounter tokenCounter = new TokenCounter();
    tokenCounter.onLineInserted(0, "baaa baaab baaaa");
    String completion = tokenCounter.getCompletion("b");
    assertEquals("baaa", completion);
  }

  @Test
  public void removeToken() {
    TokenCounter tokenCounter = new TokenCounter();
    tokenCounter.onLineInserted(0, "dog cat");
    tokenCounter.onLineChanged(0, "dog cat", "dog");
    assertTrue(tokenCounter.getCompletions("c").isEmpty());
    assertFalse(tokenCounter.getCompletions("d").isEmpty());
  }
}
