package com.id.editor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import com.id.events.KeyStroke;

public class MinibufferTest {

  private Minibuffer minibuffer;
  private Minibuffer.Listener listener;

  @Before
  public void setup() {
    minibuffer = new Minibuffer();
    listener = mock(Minibuffer.Listener.class);
  }

  @Test
  public void escapeFiresListener() {
    minibuffer.addListener(listener);
    typeString("testing");
    type(KeyStroke.escape());
    verify(listener).onQuit();
  }

  @Test
  public void typingShouldChangeTheContentsOfTheBuffer() {
    minibuffer.addListener(listener);
    typeString("h");
    verify(listener).onTextChanged();
    assertEquals("h", minibuffer.getText());
  }

  @Test
  public void itBecomesEmptyWhenCleared() {
    typeString("hello, there");
    minibuffer.clear();
    assertEquals("", minibuffer.getText());
  }

  @Test
  public void itCanBeClearedAndTypedInFromScratch() {
    typeString("a");
    minibuffer.clear();
    typeString("b");
    assertEquals("b", minibuffer.getText());
  }

  private void type(KeyStroke keyStroke) {
    minibuffer.handleKeyStroke(keyStroke);
  }

  private void typeString(String string) {
    for (int i = 0; i < string.length(); i++) {
      char c = string.charAt(i);
      minibuffer.handleKeyStroke(KeyStroke.fromChar(c));
    }
  }
}
