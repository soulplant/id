package com.id.app;

import java.util.ArrayList;
import java.util.List;

// Contains a highlight pattern and exposes a listener interface. The main use
// case for this is as the singleton highlight state for the app.
public class HighlightState {
  public interface Listener {
    void onHighlightStateChanged();
  }

  private HighlightPattern highlightPattern = null;
  private final List<HighlightPattern> previousPatterns = new ArrayList<HighlightPattern>();
  private final List<Listener> listeners = new ArrayList<Listener>();

  public HighlightState() {
  }

  public void setHighlightPattern(HighlightPattern pattern) {
    if (pattern == null && this.highlightPattern == null) {
      return;
    }
    if (pattern != null && pattern.equals(this.highlightPattern)) {
      return;
    }
    this.highlightPattern = pattern;
    if (this.highlightPattern != null) {
      addPatternToHistory(this.highlightPattern);
    }
    for (Listener listener : listeners) {
      listener.onHighlightStateChanged();
    }
  }

  public HighlightPattern getHighlightPattern() {
    return highlightPattern;
  }

  public String getHighlightText() {
    return highlightPattern.getText();
  }

  public void addListener(Listener listener) {
    listeners.add(listener);
  }

  public void removeListener(Listener listener) {
    listeners.remove(listener);
  }

  public boolean isEmpty() {
    return highlightPattern == null;
  }

  public List<HighlightPattern> getPreviousHighlights() {
    return previousPatterns;
  }

  private void addPatternToHistory(HighlightPattern highlightPattern) {
    for (HighlightPattern pattern : previousPatterns) {
      if (pattern.equals(highlightPattern)) {
        return;
      }
    }
    previousPatterns.add(0, highlightPattern);
  }
}
