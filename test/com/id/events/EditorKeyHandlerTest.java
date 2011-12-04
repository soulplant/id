package com.id.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.event.KeyEvent;

import org.junit.Before;
import org.junit.Test;

public class EditorKeyHandlerTest {
  @Before
  public void setup() {
    new EditorKeyHandler();
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
