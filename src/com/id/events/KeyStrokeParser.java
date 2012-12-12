package com.id.events;

import java.util.ArrayList;
import java.util.List;

public class KeyStrokeParser {
  private static final char ESCAPE_CHAR = '\\';
  private final String input;
  private KeyStroke keyStroke;
  private int endIndex;

  public KeyStrokeParser(String input) {
    this.input = input;
    compute();
  }

  public static List<KeyStroke> parseKeyStrokes(String input) {
    List<KeyStroke> result = new ArrayList<KeyStroke>();
    String remaining = input;
    while (!remaining.isEmpty()) {
      KeyStrokeParser parser = new KeyStrokeParser(remaining);
      result.add(parser.getKeyStroke());
      remaining = parser.getRemainingInput();
    }
    return result;
  }

  public static KeyStroke parseKeyStroke(String input) {
    return parseKeyStrokes(input).get(0);
  }

  public KeyStroke getKeyStroke() {
    return keyStroke;
  }

  public String getRemainingInput() {
    return input.substring(endIndex);
  }

  private void compute() {
    char c = input.charAt(0);
    if (c == '<') {
      int i = getIndexOfUnescaped(1, '>');
      if (i == -1) {
        error();
        return;
      }
      String innerText = input.substring(1, i);
      setResult(parseAngledBracketsText(innerText), i + 1);
      return;
    } else if (c == ESCAPE_CHAR) {
      if (input.length() < 2) {
        error();
        return;
      }
      setResult(KeyStroke.fromChar(input.charAt(1)), 2);
      return;
    }
    setResult(KeyStroke.fromChar(c), 1);
  }

  private void error() {
    setResult(null, -1);
  }

  private void setResult(KeyStroke keyStroke, int endIndex) {
    this.keyStroke = keyStroke;
    this.endIndex = endIndex;
  }

  private KeyStroke modify(KeyStroke keyStroke, boolean control,
                           boolean shift) {
    if (control) {
      keyStroke = keyStroke.withControl();
    }
    if (shift) {
      keyStroke = keyStroke.withShift();
    }
    return keyStroke;
  }

  private KeyStroke parseAngledBracketsText(String innerText) {
    boolean control = innerText.startsWith("C-");
    boolean shift = innerText.startsWith("S-");
    if (control || shift) {
      String modifiedKeyStrokes = innerText.substring(2);
      KeyStroke modified = parseAngledBracketsText(modifiedKeyStrokes);
      if (modified != null) {
        return modify(modified, control, shift);
      }
      List<KeyStroke> keys = KeyStroke.fromString(modifiedKeyStrokes);
      if (keys.size() != 1) {
        throw new IllegalStateException();
      }
      return modify(keys.get(0), control, shift);
    } else if (innerText.equals("ESC")) {
      return KeyStroke.escape();
    } else if (innerText.equals("CR")) {
      return KeyStroke.enter();
    } else if (innerText.equals("UP")) {
      return KeyStroke.up();
    } else if (innerText.equals("LEFT")) {
      return KeyStroke.left();
    } else if (innerText.equals("DOWN")) {
      return KeyStroke.down();
    } else if (innerText.equals("RIGHT")) {
      return KeyStroke.right();
    } else if (innerText.equals("BS")) {
      return KeyStroke.backspace();
    } else if (innerText.equals("TAB")) {
      return KeyStroke.tab();
    }
    return null;
  }

  private int getIndexOfUnescaped(int startIndex, char needle) {
    for (int i = startIndex; i < input.length(); i++) {
      char c = input.charAt(i);
      if (c == ESCAPE_CHAR) {
        i++;
        continue;
      } else if (c == needle) {
        return i;
      }
    }
    return -1;
  }
}
