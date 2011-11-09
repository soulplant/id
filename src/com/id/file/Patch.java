package com.id.file;

import java.util.Stack;

import com.id.editor.Point;

public class Patch implements File.Listener {
  private interface Change {
    void apply(File file);
    Change invert();
  }

  private class InsertLine implements Change {
    private final int y;
    private final String line;

    public InsertLine(int y, String line) {
      this.y = y;
      this.line = line;
    }

    @Override
    public void apply(File file) {
      file.insertLine(y, line);
    }

    @Override
    public Change invert() {
      return new RemoveLine(y, line);
    }
  }

  private class RemoveLine implements Change {
    private final int y;
    private final String line;

    public RemoveLine(int y, String line) {
      this.y = y;
      this.line = line;
    }

    @Override
    public void apply(File file) {
      file.removeLine(y);
    }

    @Override
    public Change invert() {
      return new InsertLine(y, line);
    }
  }

  private class ChangeLine implements Change {
    private final int y;
    private final String from;
    private final String to;

    public ChangeLine(int y, String from, String to) {
      this.y = y;
      this.from = from;
      this.to = to;
    }

    @Override
    public void apply(File file) {
      file.changeLine(y, to);
    }

    @Override
    public Change invert() {
      return new ChangeLine(y, to, from);
    }
  }

  private final Stack<Change> changes = new Stack<Change>();
  private final Point position;

  public Patch(Point position) {
    this.position = position;
  }

  @Override
  public void onLineInserted(int y, String line) {
    changes.push(new InsertLine(y, line));
  }

  @Override
  public void onLineRemoved(int y, String line) {
    changes.push(new RemoveLine(y, line));
  }

  @Override
  public void onLineChanged(int y, String oldLine, String newLine) {
    changes.push(new ChangeLine(y, oldLine, newLine));
  }

  public boolean isEmpty() {
    return changes.isEmpty();
  }

  public void applyInverse(File file) {
    invert().apply(file);
  }

  public void apply(File file) {
    for (Change change : changes) {
      change.apply(file);
    }
  }

  private Patch invert() {
    Patch result = new Patch(position);
    for (int i = changes.size() - 1; i >= 0; i--) {
      result.changes.push(changes.get(i).invert());
    }
    return result;
  }

  public Point getPosition() {
    return position;
  }
}
