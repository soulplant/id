package com.id.editor;

import java.util.Arrays;

import com.id.events.KeyStroke;
import com.id.events.KeyStrokeHandler;
import com.id.events.ShortcutTree;
import com.id.file.File;

public class Search implements KeyStrokeHandler, Minibuffer.Listener {
  public interface Listener {
    void onDone();
    void onHighlightChanged();
    void onMoveTo(int y, int x);
  }

  private final Minibuffer minibuffer;
  private final File file;
  private Highlight highlight = new EmptyHighlight();
  private final Listener listener;
  private final ShortcutTree shortcuts = new ShortcutTree();
  private final Point startPoint;
  private Point currentPoint;

  public Search(Minibuffer minibuffer, File file, Point startPoint, Listener listener) {
    this.minibuffer = minibuffer;
    this.file = file;
    this.startPoint = startPoint;
    this.currentPoint = startPoint;
    this.listener = listener;
    this.minibuffer.addListener(this);
    file.addListener(highlight);
    shortcuts.setShortcut(Arrays.asList(KeyStroke.up()), new ShortcutTree.Action() {
      @Override
      public void execute() {
        up();
      }
    });
    shortcuts.setShortcut(Arrays.asList(KeyStroke.down()), new ShortcutTree.Action() {
      @Override
      public void execute() {
        down();
      }
    });
  }

  public void down() {
    moveTo(highlight.getNextMatch(currentPoint.getY(), currentPoint.getX()));
  }

  private void moveTo(Point point) {
    if (point == null) {
      return;
    }
    if (point.equals(currentPoint)) {
      return;
    }
    currentPoint = point;
    listener.onMoveTo(currentPoint.getY(), currentPoint.getX());
  }

  public void up() {
    moveTo(highlight.getPreviousMatch(currentPoint.getY(), currentPoint.getX()));
  }

  public int getOccurrences() {
    return highlight.getMatchCount();
  }

  @Override
  public boolean handleKeyStroke(KeyStroke keyStroke) {
    if (shortcuts.stepAndExecute(keyStroke)) {
      return true;
    }
    return minibuffer.handleKeyStroke(keyStroke);
  }

  @Override
  public void onDone() {
    listener.onDone();
  }

  @Override
  public void onTextChanged() {
    file.removeListener(highlight);
    highlight = new CachingHighlight(minibuffer.getText(), file.getLineList());
    moveTo(highlight.getNextMatch(startPoint.getY(), startPoint.getX()));
    file.addListener(highlight);
    listener.onHighlightChanged();
  }

  public boolean isHighlight(int y, int x) {
    return highlight.isHighlighted(y, x);
  }

  @Override
  public void onQuit() {
    listener.onMoveTo(startPoint.getY(), startPoint.getX());
    listener.onDone();
  }

  public String getQuery() {
    return minibuffer.getText();
  }
}
