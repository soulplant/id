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
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + x;
    result = prime * result + y;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Point other = (Point) obj;
    if (x != other.x)
      return false;
    if (y != other.y)
      return false;
    return true;
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

  public boolean isInRangeY(int min, int max) {
    return min <= y && y <= max;
  }

  public boolean isInRangeX(int min, int max) {
    return min <= x && x <= max;
  }
}
