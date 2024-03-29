package com.id.editor;

import java.util.ArrayList;
import java.util.List;

import com.id.file.FileView;

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
  private Point lastCursorPoint;
  private Point lastAnchor;
  private Mode lastMode = Mode.NONE;

  public Visual(Cursor cursor) {
    this.cursor = cursor;
  }

  public void toggleMode(Mode mode) {
    if (this.mode == Mode.NONE) {
      startVisual(mode);
    } else if (this.mode == mode || mode == Mode.NONE) {
      endVisual();
    } else {
      this.mode = mode;
    }
  }

  private void startVisual(Mode mode) {
    this.mode = mode;
    this.anchor = getCursorPoint();
  }

  private void endVisual() {
    lastMode = this.mode;
    lastCursorPoint = getCursorPoint();
    lastAnchor = this.anchor;
    this.mode = Mode.NONE;
  }

  public boolean isOn() {
    return this.mode != Mode.NONE;
  }

  public Point getStartPoint() {
    return isCursorBeforeAnchor() ? getCursorPoint() : anchor;
  }

  private Point getCursorPoint() {
    return cursor.getPoint();
  }

  public int getStartY() {
    return getStartPoint().getY();
  }

  public Point getEndPoint() {
    return isCursorBeforeAnchor() ? anchor : getCursorPoint();
  }

  public int getEndY() {
    return getEndPoint().getY();
  }

  public boolean isCursorBeforeAnchor() {
    return getCursorPoint().before(anchor);
  }

  public boolean contains(Point point) {
    return mode.contains(this, point);
  }

  public TextFragment getRegister(FileView fileView) {
    List<String> lines = new ArrayList<String>();
    int startLine = getStartPoint().getY();
    int endLine = getEndPoint().getY();
    int startX = getStartPoint().getX();
    int endX = getEndPoint().getX();

    boolean coversTrailingNewline = false;
    if (endX == fileView.getLine(endLine).length()) {
      coversTrailingNewline = true;
      endX--;
    }

    switch (mode) {
    case CHAR:
      if (startLine == endLine) {
        lines.add(fileView.getLine(startLine).substring(startX, endX + 1));
        return makeRegister(lines, coversTrailingNewline);
      }
      for (int y = startLine; y <= endLine; y++) {
        String line = fileView.getLine(y);
        if (y == startLine) {
          lines.add(line.substring(startX));
        } else if (y == endLine) {
          lines.add(line.substring(0, endX + 1));
        } else {
          lines.add(line);
        }
      }
      return makeRegister(lines, coversTrailingNewline);
    case LINE:
      return makeRegister(fileView.getLineRange(startLine, endLine), coversTrailingNewline);
    default:
      throw new IllegalStateException();
    }
  }

  private TextFragment makeRegister(List<String> lines, boolean trailingNewline) {
    return new TextFragment(mode, trailingNewline, lines);
  }

  public void removeFrom(FileView file) {
    switch (mode) {
    case BLOCK:
      int left = Math.min(getStartPoint().getX(), getEndPoint().getX());
      int right = Math.max(getStartPoint().getX(), getEndPoint().getX());

      for (int i = getStartPoint().getY(); i < getEndPoint().getY(); i++) {
        file.removeText(i, left, right - left);
      }
      break;
    case CHAR:
      file.removeText(getStartPoint(), getEndPoint());
      break;
    case LINE:
      file.removeLineRange(getStartPoint().getY(), getEndPoint().getY());
      break;
    default:
      throw new UnsupportedOperationException();
    }
  }

  public Point getLastCursorPoint() {
    return lastCursorPoint;
  }

  public Mode getLastMode() {
    return lastMode;
  }

  public void reselectLast() {
    this.mode = lastMode;
    this.anchor = lastAnchor;
    cursor.jumpTo(lastCursorPoint.getY(), lastCursorPoint.getX());
  }
}
