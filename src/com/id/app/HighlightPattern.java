package com.id.app;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HighlightPattern {
  public enum MatchType {
    WHOLE_WORD,
    PART_WORD
  }

  private final String text;
  private final MatchType matchType;
  private final Pattern pattern;

  public HighlightPattern(String text, MatchType matchType) {
    this.text = text;
    this.matchType = matchType;
    this.pattern = makePattern();
  }

  public String getText() {
    return text;
  }

  public Pattern getPattern() {
    return pattern;
  }

  public boolean isWholeWordPattern() {
    return matchType == MatchType.WHOLE_WORD;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof HighlightPattern)) {
      return false;
    }
    HighlightPattern p = (HighlightPattern) other;
    return text.equals(p.getText()) && matchType == p.matchType;
  }

  public Matcher matcher(String line) {
    return pattern.matcher(line);
  }

  private Pattern makePattern() {
    switch (matchType) {
      case WHOLE_WORD:
        return Pattern.compile("\\b" + Pattern.quote(text) + "\\b");
      case PART_WORD:
        return Pattern.compile(Pattern.quote(text));
      default:
        throw new IllegalStateException();
    }
  }
}
