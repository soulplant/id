package com.id.editor;

import java.util.regex.Pattern;

import com.id.app.HighlightPattern;

/**
 * Static methods for creating {@link Pattern}s.
 */
public class Patterns {
  public static HighlightPattern wholeWord(String word) {
    Pattern pattern = Pattern.compile("\\b" + Pattern.quote(word) + "\\b");
    return new HighlightPattern(word, pattern);
  }

  public static HighlightPattern partWord(String word) {
    Pattern pattern = Pattern.compile(Pattern.quote(word));
    return new HighlightPattern(word, pattern);
  }
}
