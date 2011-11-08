package com.id.editor;

public class Point {
  private final int y;
  private final int x;

  public Point(int y, int x) {
    this.y = y;
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public int getX() {
    return x;
  }

  public boolean before(Point other) {
    if (y < other.y) {
      return true;
    } else if (y == other.y) {
      return x < other.x;
    } else {
      return false;
    }
  }
}
