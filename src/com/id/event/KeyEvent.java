package com.id.event;

import java.util.HashMap;
import java.util.Map;

public class KeyEvent {
  private final int keyCode;
  private final boolean isShiftDown;
  private final boolean isAltDown;
  private final boolean isControlDown;

  public KeyEvent(int keyCode, boolean isShiftDown, boolean isAltDown, boolean isControlDown) {
    this.keyCode = keyCode;
    this.isShiftDown = isShiftDown;
    this.isAltDown = isAltDown;
    this.isControlDown = isControlDown;
  }

  public boolean isChar() {
    return isAlphaNum() || isSymbol();
  }

  public boolean isShiftDown() {
    return isShiftDown;
  }

  public boolean isAltDown() {
    return isAltDown;
  }

  public boolean isControlDown() {
    return isControlDown;
  }

  public char getChar() {
    if (isAlpha()) {
      return (isShiftDown ? Character.toUpperCase((char) keyCode) : Character.toLowerCase((char) keyCode));
    }
    if (isNum()) {
      int offset = keyCode - '0';
      return isShiftDown ? ")!@#$%^&*(".charAt(offset) : (char) keyCode;
    }
    if (isSymbol()) {
      return isShiftDown ? symbolHighCharMap.get(keyCode) : symbolLowCharMap.get(keyCode);
    }
    return (char) keyCode;
  }

  private boolean isSymbol() {
    return symbolLowCharMap.containsKey(keyCode);
  }

  private boolean isNum() {
    return '0' <= keyCode && keyCode <= '9';
  }

  public boolean isAlpha() {
    return ('a' <= keyCode && keyCode <= 'z') ||
        ('A' <= keyCode && keyCode <= 'Z');
  }

  public boolean isAlphaNum() {
    return isNum() || isAlpha();
  }

  public int getKeyCode() {
    return keyCode;
  }

  public boolean is(int keyCode) {
    return this.keyCode == keyCode;
  }

  public static int ESCAPE = 27;
  public static int TAB = 9;

  private static final Map<Integer, Character> symbolLowCharMap = new HashMap<Integer, Character>();
  private static final Map<Integer, Character> symbolHighCharMap = new HashMap<Integer, Character>();

  static {
    symbolLowCharMap.put(186, ';');
    symbolLowCharMap.put(187, '=');
    symbolLowCharMap.put(188, ',');
    symbolLowCharMap.put(189, '-');
    symbolLowCharMap.put(190, '.');
    symbolLowCharMap.put(191, '/');
    symbolLowCharMap.put(192, '`');
    symbolLowCharMap.put(219, '[');
    symbolLowCharMap.put(220, '\\');
    symbolLowCharMap.put(221, ']');
    symbolLowCharMap.put(222, '\'');

    symbolHighCharMap.put(186, ':');
    symbolHighCharMap.put(187, '+');
    symbolHighCharMap.put(188, '<');
    symbolHighCharMap.put(189, '_');
    symbolHighCharMap.put(190, '>');
    symbolHighCharMap.put(191, '?');
    symbolHighCharMap.put(192, '~');
    symbolHighCharMap.put(219, '{');
    symbolHighCharMap.put(220, '|');
    symbolHighCharMap.put(221, '}');
    symbolHighCharMap.put(222, '"');
  }
}