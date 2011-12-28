package com.id.app;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

// Contains a highlight pattern and exposes a listener interface. The main use
// case for this is as the singleton highlight state for the app.
public class HighlightState {
  public interface Listener {
    void onHighlightStateChanged();
  }

  private Pattern highlightPattern = null;
  private final List<Listener> listeners = new ArrayList<Listener>();

  public HighlightState() {
  }

  public void setHighlightPattern(Pattern pattern) {
    if (pattern == null && this.highlightPattern == null) {
      return;
    }
    if (pattern != null && pattern.equals(this.highlightPattern)) {
      return;
    }
    this.highlightPattern = pattern;
    for (Listener listener : listeners) {
      listener.onHighlightStateChanged();
    }
  }

  public Pattern getHighlightPattern() {
    return highlightPattern;
  }

  public void addListener(Listener listener) {
    listeners.add(listener);
  }

  public void removeListener(Listener listener) {
    listeners.remove(listener);
  }
}
