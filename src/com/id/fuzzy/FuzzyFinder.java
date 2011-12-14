package com.id.fuzzy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.id.events.KeyStroke;
import com.id.events.KeyStrokeHandler;
import com.id.events.ShortcutTree;
import com.id.platform.FileSystem;

public class FuzzyFinder implements KeyStrokeHandler {
  public interface Listener {
    void onQueryChanged();
    void onItemSelected(String item);
    void onSetVisible(boolean visible);
  }
  private final List<String> paths = new ArrayList<String>();
  private final List<String> filenames = new ArrayList<String>();
  private final FileSystem fileSystem;
  private boolean visible = false;
  private String query = "";
  private final List<Listener> listeners = new ArrayList<Listener>();
  private final ShortcutTree shortcuts = new ShortcutTree();

  public FuzzyFinder(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
    shortcuts.setShortcut(Arrays.asList(KeyStroke.escape()), new ShortcutTree.Action() {
      @Override
      public void execute() {
        setVisible(false);
      }
    });
  }

  public void setVisible(boolean visible) {
    if (this.visible == visible) {
      return;
    }
    this.visible = visible;
    fireSetVisible();
  }

  public void addPathToIndex(String path) {
    paths.add(path);
    addAllFilesUnder(path);
  }

  public List<String> getMatches() {
    List<String> result = new ArrayList<String>();
    Pattern pattern = Pattern.compile(".*" + query + ".*");
    for (String candidate : filenames) {
      Matcher matcher = pattern.matcher(candidate);
      if (matcher.matches()) {
        result.add(candidate);
      }
    }
    return result;
  }

  private void addAllFilesUnder(String path) {
    if (!fileSystem.isExistent(path)) {
      return;
    }
    if (fileSystem.isDirectory(path)) {
      for (String filename : fileSystem.getSubdirectories(path)) {
        String subDirectory = path + "/" + filename;
        addAllFilesUnder(subDirectory);
      }
    } else if (fileSystem.isFile(path)) {
      filenames.add(path);
    }
  }

  public boolean contains(String filename) {
    return filenames.contains(filename);
  }

  @Override
  public boolean handleKeyStroke(KeyStroke keyStroke) {
    if (shortcuts.stepAndExecute(keyStroke)) {
      return true;
    }
    addToQuery(keyStroke.getKeyChar());
    return true;
  }

  private void addToQuery(char keyChar) {
    // TODO Handle backspace, up, down.
    this.query = query + keyChar;
    fireQueryChanged();
  }

  private void fireQueryChanged() {
    for (Listener listener : listeners) {
      listener.onQueryChanged();
    }
  }

  private void fireSetVisible() {
    for (Listener listener : listeners) {
      listener.onSetVisible(visible);
    }
  }

  public void addListener(Listener listener) {
    listeners.add(listener);
  }

  public boolean isVisible() {
    return visible;
  }

  // For testing.
  void setQuery(String query) {
    this.query = query;
    fireQueryChanged();
  }
}
