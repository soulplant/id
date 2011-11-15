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

  public boolean beforeOrEqual(Point other) {
    if (before(other)) {
      return true;
    }
    return equals(other);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Point)) {
      return false;
    }
    Point other = (Point) obj;
    return this.y == other.y && this.x == other.x;
  }

  public Point offset(int dy, int dx) {
    return new Point(y + dy, x + dx);
  }

  public Point constrainY(int min, int max) {
    return new Point(Math.max(min, Math.min(max, y)), x);
  }

  public Point constrainX(int min, int max) {
    return new Point(y, Math.max(min, Math.min(max, x)));
  }

  @Override
  public String toString() {
    return "[" + y + ", " + x + "]";
  }
}
