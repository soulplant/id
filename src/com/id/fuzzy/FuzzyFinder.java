package com.id.fuzzy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.id.editor.Minibuffer;
import com.id.events.KeyStroke;
import com.id.events.KeyStrokeHandler;
import com.id.events.ShortcutTree;
import com.id.platform.FileSystem;

public class FuzzyFinder implements KeyStrokeHandler, Minibuffer.Listener {
  public interface Listener {
    void onQueryChanged();
    void onSetVisible(boolean visible);
  }

  public interface SelectionListener {
    void onItemSelected(String item);
  }

  private final List<String> paths = new ArrayList<String>();
  private final List<String> filenames = new ArrayList<String>();
  private final FileSystem fileSystem;
  private boolean visible = false;
  private final Minibuffer minibuffer = new Minibuffer();
  private final List<Listener> listeners = new ArrayList<Listener>();
  private final ShortcutTree shortcuts = new ShortcutTree();
  private SelectionListener selectionListener;

  public FuzzyFinder(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
    minibuffer.addListener(this);
    shortcuts.setShortcut(Arrays.asList(KeyStroke.escape()), new ShortcutTree.Action() {
      @Override
      public void execute() {
        setVisible(false);
      }
    });
    shortcuts.setShortcut(Arrays.asList(KeyStroke.enter()), new ShortcutTree.Action() {
      @Override
      public void execute() {
        selectCurrentItem();
      }
    });
  }

  public void setSelectionListener(SelectionListener selectionListener) {
    this.selectionListener = selectionListener;
  }

  public void selectCurrentItem() {
    if (getMatches().isEmpty()) {
      setVisible(false);
      return;
    }
    fireItemSelected();
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
    Pattern pattern = Pattern.compile(".*" + minibuffer.getText() + ".*");
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
    return minibuffer.handleKeyStroke(keyStroke);
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

  private void fireItemSelected() {
    selectionListener.onItemSelected(getSelectedItem());
  }

  private String getSelectedItem() {
    return getMatches().get(0);
  }

  public void addListener(Listener listener) {
    listeners.add(listener);
  }

  public boolean isVisible() {
    return visible;
  }

  // For testing.
  void setQuery(String query) {
    minibuffer.setText(query);
    fireQueryChanged();
  }

  public String getCurrentQuery() {
    return minibuffer.getText();
  }

  public void clearQuery() {
    minibuffer.clear();
  }

  @Override
  public void onDone() {
    setVisible(false);
  }

  @Override
  public void onQuit() {
    setVisible(false);
  }

  @Override
  public void onTextChanged() {
    fireQueryChanged();
  }
}
