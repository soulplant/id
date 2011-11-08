package com.id.editor;

public class Cursor {
  private Point point;
  public Cursor() {
    this.point = new Point(0, 0);
  }

  public Point getPoint() {
    return point;
  }

  public void moveTo(Point point) {
    this.point = point;
  }
}
