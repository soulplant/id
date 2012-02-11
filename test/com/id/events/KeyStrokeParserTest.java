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

  @Test
  public void remaining() {
    assertEquals("", new KeyStrokeParser("<C-x>").getRemainingInput());
    assertEquals("", new KeyStrokeParser("<CR>").getRemainingInput());
  }

  @Test
  public void escapedLetter() {
    KeyStrokeParser parser = new KeyStrokeParser("\\<");
    assertEquals("", parser.getRemainingInput());
    assertEquals(KeyStroke.fromChar('<'), parser.getKeyStroke());
  }

  @Test
  public void parseKeyStrokesOnEscapedLetters() {
    KeyStrokeParser.parseKeyStrokes("\\<");
  }

  private void check(String string, KeyStroke keyStroke) {
    assertEquals(keyStroke, getFrontKeyStroke(string));
  }

  private KeyStroke getFrontKeyStroke(String string) {
    return new KeyStrokeParser(string).getKeyStroke();
  }
}
