package com.id.events;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyStroke {
  private static Map<Character, Character> shiftSymbols = new HashMap<Character, Character>();
  static {
    shiftSymbols.put('!', '1');
    shiftSymbols.put('@', '2');
    shiftSymbols.put('#', '3');
    shiftSymbols.put('$', '4');
    shiftSymbols.put('%', '5');
    shiftSymbols.put('^', '6');
    shiftSymbols.put('&', '7');
    shiftSymbols.put('*', '8');
    shiftSymbols.put('(', '9');
    shiftSymbols.put(')', '0');
  }

  public static KeyStroke fromChar(char c) {
    if (shiftSymbols.containsKey(c)) {
      return new KeyStroke(c, 0, KeyEvent.SHIFT_MASK);
    }
    int mask = 0;
    if (Character.isUpperCase(c)) {
      mask = KeyEvent.SHIFT_MASK;
    }
    return new KeyStroke(c, 0, mask);
  }

  public static KeyStroke fromVKey(int keyCode) {
    return fromVKey(keyCode, 0);
  }

  public static KeyStroke fromVKey(int keyCode, int modifiers) {
    return new KeyStroke((char) 0, keyCode, modifiers);
  }

  public static KeyStroke escape() {
    return KeyStroke.fromVKey(KeyEvent.VK_ESCAPE);
  }

  public static KeyStroke fromControlChar(char c) {
    return new KeyStroke(c, 0, KeyEvent.CTRL_MASK);
  }

  public static KeyStroke fromKeyEvent(KeyEvent event) {
    if (event.isControlDown() && isKeyCodeForLetter(event.getKeyCode())) {
      // TODO Support control + other modifiers.
      char c = Character.toLowerCase((char) event.getKeyCode());
      return fromControlChar(c);
    }
    if (isKeyCodeForLetter(event.getKeyChar())) {
      return fromChar(event.getKeyChar());
    }
    return fromVKey(event.getKeyCode(), event.getModifiers());
  }

  private final char letter;
  private final int modifiers;
  private final int code;

  public KeyStroke(char letter, int code, int modifiers) {
    this.code = code;
    this.modifiers = modifiers;
    this.letter = isShiftDown() ? Character.toUpperCase(letter) : Character
        .toLowerCase(letter);
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
    return code;
  }

  public char getKeyChar() {
    return getLetter();
  }

  public boolean isLetter() {
    return code ==  0;
  }

  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("KeyStroke[");
    if (isLetter()) {
      buffer.append(getLetter());
    } else {
      buffer.append(getKeyCode());
    }
    buffer.append(", ");
    maybeAppend(buffer, "SHIFT", isShiftDown());
    maybeAppend(buffer, "CTRL", isControlDown());
    maybeAppend(buffer, "ALT", isAltDown());
    maybeAppend(buffer, "META", isMetaDown());
    buffer.append("]");
    return buffer.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof KeyStroke) {
      KeyStroke other = (KeyStroke) obj;
      return letter == other.letter && modifiers == other.modifiers && code == other.code;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return letter * (modifiers + 1);
  }

  private void maybeAppend(StringBuffer buffer, String string, boolean append) {
    if (append) {
      buffer.append(string).append(", ");
    }
  }

  private static boolean isKeyCodeForLetter(int keyCode) {
    return ('a' <= keyCode && keyCode <= 'z')
        || ('A' <= keyCode && keyCode <= 'Z')
        || ('0' <= keyCode && keyCode <= '9')
        || (" `~!@#$%^&*()-_=+[{]}\\|;:,<.>/?\"'".indexOf(keyCode) != -1);
  }

  public static List<KeyStroke> fromString(String string) {
    List<KeyStroke> result = new ArrayList<KeyStroke>();
    for (int i = 0; i < string.length(); i++) {
      result.add(KeyStroke.fromChar(string.charAt(i)));
    }
    return result;
  }

  public static KeyStroke backspace() {
    return KeyStroke.fromVKey(KeyEvent.VK_BACK_SPACE);
  }

  public static KeyStroke enter() {
    return KeyStroke.fromVKey(KeyEvent.VK_ENTER);
  }

  public static KeyStroke tab() {
    return KeyStroke.fromVKey(KeyEvent.VK_TAB);
  }

  public static KeyStroke down() {
    return KeyStroke.fromVKey(KeyEvent.VK_DOWN);
  }

  public static KeyStroke right() {
    return KeyStroke.fromVKey(KeyEvent.VK_RIGHT);
  }

  public static KeyStroke up() {
    return KeyStroke.fromVKey(KeyEvent.VK_UP);
  }

  public static KeyStroke left() {
    return KeyStroke.fromVKey(KeyEvent.VK_LEFT);
  }
}
