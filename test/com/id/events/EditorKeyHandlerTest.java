package com.id.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.event.KeyEvent;

import org.junit.Before;
import org.junit.Test;

public class EditorKeyHandlerTest {

  private EditorKeyHandler handler;
  @Before
  public void setup() {
    handler = new EditorKeyHandler();
  }

  @Test
  public void capitalC() {
    KeyStroke event = handler.makeEventFromChar('C');
    assertTrue(event.isShiftDown());
    assertEquals(KeyEvent.VK_C, event.getKeyCode());
  }

  @Test
  public void lowercaseL() {
    KeyStroke event = handler.makeEventFromChar('l');
    assertFalse(event.isShiftDown());
    assertEquals(KeyEvent.VK_L, event.getKeyCode());
  }
}
