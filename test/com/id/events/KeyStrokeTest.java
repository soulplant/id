package com.id.events;

import static org.junit.Assert.*;

import org.junit.Test;

public class KeyStrokeTest {

  @Test
  public void equals() {
    assertEquals(KeyStroke.fromChar('c'), KeyStroke.fromChar('c'));
    assertNotSame(KeyStroke.fromChar('C'), KeyStroke.fromChar('c'));
  }

}
