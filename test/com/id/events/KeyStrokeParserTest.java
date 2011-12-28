package com.id.events;

import static org.junit.Assert.*;

import org.junit.Test;

public class KeyStrokeParserTest {
  @Test
  public void escape() {
    check("<ESC>", KeyStroke.escape());
  }

  @Test
  public void ctrlKey() {
    check("<C-x>", KeyStroke.fromControlChar('x'));
    check("<C-X>", KeyStroke.fromControlChar('X'));
    assertTrue(getFrontKeyStroke("<C-X>").isShiftDown());
    assertTrue(getFrontKeyStroke("<C-X>").isControlDown());
  }

  private void check(String string, KeyStroke keyStroke) {
    assertEquals(keyStroke, getFrontKeyStroke(string));
  }

  private KeyStroke getFrontKeyStroke(String string) {
    return new KeyStrokeParser(string).getKeyStroke();
  }
}
