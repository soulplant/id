package com.id.fuzzy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.id.editor.Editor;
import com.id.editor.Minibuffer;
import com.id.events.KeyStroke;
import com.id.events.KeyStrokeHandler;
import com.id.events.KeyStrokeParser;
import com.id.events.ShortcutTree;
import com.id.file.File;

public class FuzzyFinder implements KeyStrokeHandler, Minibuffer.Listener {
  public interface Listener {
    void onQueryChanged();
    void onSetVisible(boolean visible);
    void onSelectionChanged(int selectedIndex);
  }

  public interface SelectionListener {
    void onItemSelected(String item);
  }

  private final File file;
  private boolean visible = false;
  private final Minibuffer minibuffer = new Minibuffer();
  private final List<Listener> listeners = new ArrayList<Listener>();
  private final ShortcutTree shortcuts = new ShortcutTree();
  private SelectionListener selectionListener;
  private int cursor = 0;

  public FuzzyFinder(File file) {
    this.file = file;
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
    shortcuts.setShortcut(KeyStrokeParser.parseKeyStrokes("<UP>"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        moveSelectionUp();
      }
    });
    shortcuts.setShortcut(KeyStrokeParser.parseKeyStrokes("<DOWN>"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        moveSelectionDown();
      }
    });
  }

  public void moveSelectionUp() {
    cursor--;
    int matches = getMatches().size();
    if (cursor >= matches) {
      cursor = matches - 1;
    }
    fireSelectionChanged();
  }

  public void moveSelectionDown() {
    cursor++;
    if (cursor < 0) {
      cursor = 0;
    }
    fireSelectionChanged();
  }

  public void setSelectionListener(SelectionListener selectionListener) {
    this.selectionListener = selectionListener;
  }

  public void selectCurrentItem() {
    List<String> matches = getMatches();
    if (matches.isEmpty() || cursor >= matches.size()) {
      setVisible(false);
      return;
    }
    fireItemSelected(matches.get(cursor));
  }

  public void setVisible(boolean visible) {
    if (this.visible == visible) {
      return;
    }
    this.visible = visible;
    fireSetVisible();
  }

  public List<String> getMatches() {
    List<String> result = new ArrayList<String>();
    Pattern pattern = Pattern.compile(".*" + minibuffer.getText() + ".*");
    for (int i = 0; i < file.getLineCount(); i++) {
      String candidate = file.getLine(i);
      Matcher matcher = pattern.matcher(candidate);
      if (matcher.matches()) {
        result.add(candidate);
      }
    }
    return result;
  }

  public boolean contains(String filename) {
    for (int i = 0; i < file.getLineCount(); i++) {
      if (file.getLine(i).equals(filename)) {
        return true;
      }
    }
    return false;
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

  private void fireItemSelected(String item) {
    selectionListener.onItemSelected(item);
  }

  private void fireSelectionChanged() {
    for (Listener listener : listeners) {
      listener.onSelectionChanged(cursor);
    }
  }

  private String getSelectedItem() {
    return getMatches().get(cursor);
  }

  public void addListener(Listener listener) {
    listeners.add(listener);
  }

  public void removeListener(Listener listener) {
    listeners.remove(listener);
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
    cursor = 0;
    fireQueryChanged();
    fireSelectionChanged();
  }

  public Editor getQueryEditor() {
    return minibuffer.getEditor();
  }

  public int getCursorIndex() {
    return cursor;
  }
}
