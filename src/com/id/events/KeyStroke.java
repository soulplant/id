package com.id.events;

import java.awt.event.KeyEvent;

public class KeyStroke {
  private final char letter;
  private final int modifiers;

  public KeyStroke(char letter, int modifiers) {
    this.modifiers = modifiers;
    this.letter = isShiftDown() ? Character.toUpperCase(letter) : Character
        .toLowerCase(letter);
  }

  public KeyStroke(KeyEvent event) {
    this(event.getKeyChar(), event.getModifiers());
  }

  public boolean isShiftDown() {
    return (modifiers & KeyEvent.SHIFT_MASK) != 0;
  }

  public boolean isControlDown() {
    return (modifiers & KeyEvent.CTRL_MASK) != 0;
  }

  public boolean isAltDown() {
    return (modifiers & KeyEvent.ALT_MASK) != 0;
  }

  public boolean isMetaDown() {
    return (modifiers & KeyEvent.META_MASK) != 0;
  }

  public char getLetter() {
    return letter;
  }

  public int getKeyCode() {
    return Character.toUpperCase(letter);
  }

  public char getKeyChar() {
    return getLetter();
  }

  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("KeyStroke[");
    buffer.append(getLetter()).append(", ");
    maybeAppend(buffer, "SHIFT", isShiftDown());
    maybeAppend(buffer, "CTRL", isControlDown());
    maybeAppend(buffer, "ALT", isAltDown());
    maybeAppend(buffer, "META", isMetaDown());
    buffer.append("]");
    return buffer.toString();
  }

  private void maybeAppend(StringBuffer buffer, String string, boolean append) {
    if (append) {
      buffer.append(string).append(", ");
    }
  }
}
