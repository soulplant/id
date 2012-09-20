package com.id.editor;

import java.util.ArrayList;
import java.util.List;

/**
 * Settings that are shared between all editors.
 */
public class SharedEditorSettings {
  public interface Listener {
    void onSettingsChanged();
  }

  private boolean isInExpandoDiffMode = false;
  private List<Listener> listeners = new ArrayList<Listener>();

  public SharedEditorSettings() {
  }

  public void addListener(Listener listener) {
    listeners.add(listener);
  }

  public void removeListener(Listener listener) {
    listeners.remove(listener);
  }

  public void setExpandoDiffMode(boolean enabled) {
    isInExpandoDiffMode = enabled;
    fireOnChanged();
  }

  public boolean isInExpandoDiffMode() {
    return isInExpandoDiffMode;
  }

  private void fireOnChanged() {
    for (Listener listener : listeners) {
      listener.onSettingsChanged();
    }
  }
}
