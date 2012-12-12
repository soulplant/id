package com.id.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.awt.event.KeyEvent;

import org.junit.Test;

public class KeyStrokeTest {

  @Test
  public void equals() {
    assertEquals(KeyStroke.fromChar('c'), KeyStroke.fromChar('c'));
    assertNotSame(KeyStroke.fromChar('C'), KeyStroke.fromChar('c'));
  }

  @Test
  public void capitalC() {
    KeyStroke event = KeyStroke.fromChar('C');
    assertTrue(event.isShiftDown());
    assertEquals('C', event.getKeyChar());
  }

  @Test
  public void lowercaseL() {
    KeyStroke event = KeyStroke.fromChar('l');
    assertFalse(event.isShiftDown());
    assertEquals('l', event.getKeyChar());
  }

  @Test
  public void testHashCode() {
    KeyStroke event = KeyStroke.fromChar('l');
    KeyStroke event2 = KeyStroke.fromChar('m');
    assertNotSame(event.hashCode(), event2.hashCode());
  }

  @Test
  public void controlCRDoesntEqualCR() {
    KeyStroke ccr = KeyStroke.fromString("<C-CR>").get(0);
    KeyStroke cr = KeyStroke.fromString("<CR>").get(0);

    assertNotSame(ccr, cr);
  }
}
