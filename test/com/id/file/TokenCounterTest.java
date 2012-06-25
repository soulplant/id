package com.id.file;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
