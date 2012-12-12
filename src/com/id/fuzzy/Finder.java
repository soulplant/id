package com.id.fuzzy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.id.editor.Editor;
import com.id.editor.Minibuffer;
import com.id.events.KeyStroke;
import com.id.events.KeyStrokeHandler;
import com.id.events.KeyStrokeParser;
import com.id.events.ShortcutTree;
import com.id.file.File;

public class Finder implements KeyStrokeHandler, Minibuffer.Listener {
  public interface Listener {
    void onMatchesChanged(List<String> items);
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
  private int cursorIndex = 0;
  private FinderDriver driver = null;
  private List<String> currentMatches = null;

  public Finder(File file) {
    this.file = file;
    minibuffer.addListener(this);
    shortcuts.setShortcut(Arrays.asList(KeyStroke.escape()), new ShortcutTree.Action() {
      @Override
      public void execute() {
        clearQuery();
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

  public void runFindAction(FinderDriver driver, SelectionListener listener) {
    this.driver = driver;
    this.selectionListener = listener;
    setVisible(true);
    updateMatches();
  }

  private void updateMatches() {
    currentMatches = getMatches();
    fireMatchesChanged();
  }

  public void moveSelectionUp() {
    cursorIndex--;
    if (cursorIndex < 0) {
      cursorIndex = 0;
    }
    fireSelectionChanged();
  }

  public void moveSelectionDown() {
    cursorIndex++;
    int matches = currentMatches.size();
    if (cursorIndex >= matches) {
      cursorIndex = matches - 1;
    }
    fireSelectionChanged();
  }

  public void setSelectionListener(SelectionListener selectionListener) {
    this.selectionListener = selectionListener;
  }

  public void selectCurrentItem() {
    if (currentMatches.isEmpty() || cursorIndex >= currentMatches.size()) {
      setVisible(false);
      return;
    }
    fireItemSelected(currentMatches.get(cursorIndex));
    clearQuery();
    setVisible(false);
  }

  public void setVisible(boolean visible) {
    if (this.visible == visible) {
      return;
    }
    this.visible = visible;
    fireSetVisible();
  }

  public List<String> getMatches() {
    return driver.getMatches(minibuffer.getText());
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

  private void fireMatchesChanged() {
    for (Listener listener : listeners) {
      listener.onMatchesChanged(currentMatches);
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
      listener.onSelectionChanged(cursorIndex);
    }
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
    updateMatches();
  }

  public String getCurrentQuery() {
    return minibuffer.getText();
  }

  public void clearQuery() {
    minibuffer.clear();
  }

  @Override
  public void onTextEntered(boolean controlPressed) {
    setVisible(false);
  }

  @Override
  public void onQuit() {
    setVisible(false);
  }

  @Override
  public void onTextChanged() {
    cursorIndex = 0;
    updateMatches();
    fireSelectionChanged();
  }

  public Editor getQueryEditor() {
    return minibuffer.getEditor();
  }

  public int getCursorIndex() {
    return cursorIndex;
  }

  // TODO(koz): This doesn't really go here, Controller should just have a
  // reference to this file.
  public String findFirstFileMatching(String pattern) {
    int i = file.getFirstLineMatchingPattern(pattern);
    if (i < 0) {
      return null;
    }
    return file.getLine(i);
  }
}
