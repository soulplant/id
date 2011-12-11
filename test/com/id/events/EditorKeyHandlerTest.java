package com.id.events;

import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import com.id.test.EditorTestBase;

public class EditorKeyHandlerTest extends EditorTestBase {
  @Before
  public void setup() {
    setFileContents();
  }

  @Test
  public void unhandledKeystrokesReturnFalse() {
    // NOTE This will break if we ever add a key binding for q in the editor.
    assertFalse(handler.handleKeyPress(KeyStroke.fromChar('q'), editor));
  }
}
