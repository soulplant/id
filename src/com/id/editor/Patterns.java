package com.id.editor;

import java.util.regex.Pattern;

/**
 * Static methods for creating {@link Pattern}s.
 */
public class Patterns {
  public static Pattern wholeWord(String word) {
    return Pattern.compile("\\b" + Pattern.quote(word) + "\\b");
  }

  public static Pattern partWord(String word) {
    return Pattern.compile(Pattern.quote(word));
  }
}
