package com.id.app;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HighlightPattern {
  private final String text;
  private final Pattern pattern;

  public HighlightPattern(String text, Pattern pattern) {
    this.text = text;
    this.pattern = pattern;
  }

  public String getText() {
    return text;
  }

  public Pattern getPattern() {
    return pattern;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof HighlightPattern)) {
      return false;
    }
    HighlightPattern p = (HighlightPattern) other;
    return text.equals(p.getText()) && pattern.equals(p.getPattern());
  }

  public Matcher matcher(String line) {
    return pattern.matcher(line);
  }
}
