package com.id.editor;

public class Visual {
  public enum Mode {
    NONE,
    CHAR,
    LINE,
    BLOCK ;
    public boolean contains(Visual range, Point point) {
      switch (this) {
      case NONE:
        return false;
      case CHAR:
        return range.getStartPoint().beforeOrEqual(point) && !range.getEndPoint().before(point);
      case LINE:
        return range.getStartPoint().getY() <= point.getY() && point.getY() <= range.getEndPoint().getY();
      case BLOCK:
        Point startPoint = range.getStartPoint();
        Point endPoint = range.getEndPoint();
        int xMin = Math.min(startPoint.getX(), endPoint.getX());
        int xMax = Math.max(startPoint.getX(), endPoint.getX());
        int x = point.getX();
        return LINE.contains(range, point) && xMin <= x && x <= xMax;
      default:
        throw new IllegalStateException("Unknown Mode: " + this);
      }
    }
  };

  private final Cursor cursor;
  private Mode mode = Mode.NONE;
  private Point anchor;

  public Visual(Cursor cursor) {
    this.cursor = cursor;
  }

  public void toggleMode(Mode mode) {
    if (this.mode == Mode.NONE) {
      this.mode = mode;
      reset();
    } else if (this.mode == mode) {
      this.mode = Mode.NONE;
    } else {
      this.mode = mode;
    }
  }

  private void reset() {
    this.anchor = cursor.getPoint();
  }

  public boolean isOn() {
    return this.mode != Mode.NONE;
  }

  public Point getStartPoint() {
    return isCursorBeforeAnchor() ? cursor.getPoint() : anchor;
  }

  public Point getEndPoint() {
    return isCursorBeforeAnchor() ? anchor : cursor.getPoint();
  }

  public boolean isCursorBeforeAnchor() {
    return cursor.getPoint().before(anchor);
  }

  public boolean contains(Point point) {
    return mode.contains(this, point);
  }
}
