package com.id.editor;

import java.util.ArrayList;
import java.util.List;

public class Cursor {
  public interface Listener {
    void onMoved(int y, int x);
    void onJumped(int y, int x);
  }

  private Point point;
  private int defaultX;
  private final List<Listener> listeners = new ArrayList<Listener>();
  private Editor editor;

  public Cursor() {
    this.point = new Point(0, 0);
  }

  public void setEditor(Editor editor) {
    this.editor = editor;
  }

  public Point getPoint() {
    return point;
  }

  public void moveTo(int y, int x) {
    setPosition(y, x);
    fireOnMoved(y, x);
  }

  public void moveXTo(int x) {
    moveTo(point.getY(), x);
  }

  private void fireOnMoved(int y, int x) {
    fireOnJumped(y, x);
  }

  private void fireOnJumped(int y, int x) {
    for (Listener listener : listeners) {
      listener.onMoved(y, x);
    }
  }

  public void jumpTo(int y, int x) {
    setPosition(y, x);
    applyCursorConstraints();
    for (Listener listener : listeners) {
      listener.onJumped(point.getY(), point.getX());
    }
  }

  private void setPosition(int y, int x) {
    checkBounds(y, x);
    this.point = new Point(y, x);
    this.defaultX = x;
    applyCursorConstraints();
  }

  private void checkBounds(int y, int x) {
    if (x < 0) {
      throw new IllegalArgumentException();
    }
  }

  public void moveBy(int dy, int dx) {
    this.point = point.offset(dy, dx);
    if (dx == 0) {
      this.point = new Point(point.getY(), defaultX);
    } else {
      defaultX = point.getX();
    }
    applyCursorConstraints();
    fireOnMoved(point.getY(), point.getX());
  }

  public void applyCursorConstraints() {
    if (editor == null) {
      return;
    }
    constrainY(0, editor.getLineCount() - 1);
    constrainX(0, editor.getEffectiveLineLength(point.getY()));
  }

  public int getY() {
    return point.getY();
  }

  public int getX() {
    return point.getX();
  }

  public void constrainY(int min, int max) {
    this.point = point.constrainY(min, max);
  }

  public void constrainX(int min, int max) {
    this.point = point.constrainX(min, max);
  }

  public void setDefaultX(int x) {
    this.defaultX = x;
  }

  public void moveTo(Point point) {
    moveTo(point.getY(), point.getX());
  }

  public void addListner(Listener listener) {
    listeners.add(listener);
  }

  public void removeListener(Listener listener) {
    listeners.remove(listener);
  }

  public boolean isInRangeY(int min, int max) {
    return point.isInRangeY(min, max);
  }

  public boolean isInRangeX(int min, int max) {
    return point.isInRangeX(min, max);
  }
}
