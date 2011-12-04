package com.id.events;

import static org.junit.Assert.*;

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
    assertEquals(KeyEvent.VK_C, event.getKeyCode());
  }

  @Test
  public void lowercaseL() {
    KeyStroke event = KeyStroke.fromChar('l');
    assertFalse(event.isShiftDown());
    assertEquals(KeyEvent.VK_L, event.getKeyCode());
  }
}
