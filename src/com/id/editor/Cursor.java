package com.id.editor;

public class Cursor {
  private Point point;
  private int defaultX;

  public Cursor() {
    this.point = new Point(0, 0);
  }

  public Point getPoint() {
    return point;
  }

  public void moveTo(int y, int x) {
    this.point = new Point(y, x);
    this.defaultX = x;
  }

  public void moveBy(int dy, int dx) {
    this.point = point.offset(dy, dx);
    if (dx == 0) {
      this.point = new Point(point.getY(), defaultX);
    } else {
      defaultX = point.getX();
    }
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
}
