package com.id.file;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.id.data.Data;
import com.id.editor.Point;
import com.id.file.Tombstone.Status;
import com.id.git.FileDelta;
import com.id.platform.FileSystem;

public class FileView implements File.Listener, ModifiedListener {
  private final File file;
  private int start;
  private int end;
  private final List<File.Listener> listeners = new ArrayList<File.Listener>();

  public FileView(File file) {
    this(file, 0, file.getLineCount() - 1);
  }

  public FileView(File file, int start, int end) {
    this.file = file;
    this.start = start;
    this.end = end == -1 ? file.getLineCount() - 1 : end;
    // TODO Make this not leak.
    file.addListener(this);
  }

  public int getStart() {
    return start;
  }

  public Range getRange() {
    return new Range(start, end);
  }

  public String getLine(int y) {
    return file.getLine(start + y);
  }

  public int getLineCount() {
    return end - start + 1;
  }

  public void insertLine(int y, String line) {
    file.insertLine(start + y, line);
  }

  public void changeLine(int y, String line) {
    file.changeLine(start + y, line);
  }

  public String removeLine(int y) {
    return file.removeLine(start + y);
  }

  @Override
  public void onLineInserted(int y, String line) {
    if (y < start) {
      start++;
      end++;
    } else if (start <= y && y <= end + 1) {
      end++;
      fireOnLineInserted(y - start, line);
    }
  }

  @Override
  public void onLineRemoved(int y, String line) {
    if (y < start) {
      start--;
      end--;
    } else if (start <= y && y <= end) {
      end--;
      fireOnLineRemoved(y - start, line);
    }
  }

  @Override
  public void onLineChanged(int y, String oldLine, String newLine) {
    if (start <= y && y <= end) {
      fireOnLineChanged(y - start, oldLine, newLine);
    }
  }

  @Override
  public void onModifiedStateChanged() {
    // Do nothing.
  }

  public void growToCover(Range range) {
    if (range.getStart() < start) {
      start = range.getStart();
    }
    if (range.getEnd() > end) {
      end = range.getEnd();
    }
  }

  public boolean isInPatch() {
    return file.isInPatch();
  }

  public void startPatchAt(int y, int x) {
    file.startPatchAt(start + y, x);
  }

  public void breakPatch() {
    file.breakPatch();
  }

  public Point undo() {
    return translatePoint(file.undo());
  }

  public Point redo() {
    return translatePoint(file.redo());
  }

  public String removeText(int y, int x) {
    return removeText(y, x, getLine(y).length());
  }

  public String removeText(int y, int x, int length) {
    String line = getLine(y);
    if (x >= line.length()) {
      return "";
    }
    int substringMax = Math.min(line.length(), x + length);
    String newLine = line.substring(0, x) + line.substring(substringMax);
    changeLine(y, newLine);
    return line.substring(x, substringMax);
  }

  public void splitLine(int y, int x, String paddingText) {
    String removedText = removeText(y, x);
    insertLine(y + 1, paddingText + removedText);
  }

  public void removeText(Point start, Point end) {
    int startLine = start.getY();
    int endLine = end.getY();
    int startX = start.getX();
    int endX = end.getX();
    if (startLine == endLine) {
      removeText(startLine, startX, endX - startX + 1);
      return;
    }
    removeText(startLine, startX);
    removeText(endLine, 0, endX + 1);
    String tail = getLine(endLine);
    removeLineRange(startLine + 1, endLine);
    appendToLine(startLine, tail);
  }

  public boolean isEmpty() {
    return getLineCount() == 0;
  }

  public String getFilename() {
    return file.getFilename();
  }

  public Status getStatus(int y) {
    return file.getStatus(start + y);
  }

  public Grave getGrave(int y) {
    return file.getGrave(start + y);
  }

  public List<String> removeLineRange(int from, int to) {
    List<String> result = new ArrayList<String>();
    for (int i = 0; i < to - from + 1; i++) {
      result.add(removeLine(from));
    }
    return result;
  }

  public void appendToLine(int y, String tail) {
    changeLine(y, getLine(y) + tail);
  }

  public boolean hasUndo() {
    return file.hasUndo();
  }

  public int findNextLetter(int y, int startPos, char c) {
    String line = getLine(y);
    for (int i = startPos; i < line.length() - 1; i++) {
      if (line.charAt(i + 1) == c) {
        return i + 1;
      }
    }
    return -1;
  }

  public int findPreviousLetter(int y, int startPos, char c) {
    String line = getLine(y);
    for (int i = startPos; i > 0; i--) {
      if (line.charAt(i - 1) == c) {
        return i - 1;
      }
    }
    return -1;
  }

  public int findNextLetter(int y, int startPos) {
    String line = getLine(y);
    for (int i = startPos; i < line.length() - 1; i++) {
      if (line.charAt(i + 1) != ' ') {
        return i + 1;
      }
    }
    return -1;
  }

  public int findNextSpace(int y, int x) {
    String line = getLine(y);
    return line.indexOf(" ", x);
  }

  public void addListener(File.Listener listener) {
    if (listeners.contains(listener)) {
      throw new IllegalStateException();
    }
    listeners.add(listener);
  }

  public void removeListener(File.Listener listener) {
    listeners.remove(listener);
  }

  public void addModifiedListener(ModifiedListener listener) {
    file.addModifiedListener(listener);
  }

  public void joinRange(int start, int end) {
    // Do nothing when at the end of the file.
    if (start >= getLineCount() - 1) {
      return;
    }
    int rangeSize = end - start + 1;
    // When joining ranges bigger than one, we want n - 1 joins.
    int timesToJoin = rangeSize == 1 ? 1 : rangeSize - 1;
    for (int i = 0; i < timesToJoin; i++) {
      join(start);
    }
  }

  private void join(int y) {
    String nextLine = removeLine(y + 1);
    changeLine(y, removeTrailingWhitespace(getLine(y)) + " " + removeLeadingWhitespace(nextLine));
  }

  private String removeLeadingWhitespace(String line) {
    for (int i = 0; i < line.length(); i++) {
      if (!isWhitespace(line.charAt(i))) {
        return line.substring(i);
      }
    }
    return "";
  }

  private String removeTrailingWhitespace(String line) {
    for (int i = line.length() - 1; i >= 0; i--) {
      if (!isWhitespace(line.charAt(i))) {
        return line.substring(0, i + 1);
      }
    }
    return "";
  }

  private boolean isWhitespace(char c) {
    // TODO(koz): Make more robust.
    return c == ' ';
  }

  public String getWordAt(int y, int x) {
    int start = findWordStart(y, x);
    int end = findWordEnd(y, x);
    return getLine(y).substring(start, end + 1);
  }

  private int findWordEnd(int y, int x) {
    String line = getLine(y);
    int i = x;
    while (i < line.length() - 1 && isWordCharacter(line.charAt(i + 1))) {
      i++;
    }
    return i;
  }

  public int findWordStart(int y, int x) {
    String line = getLine(y);
    int i = x;
    while (i > 0 && isWordCharacter(line.charAt(i - 1))) {
      i--;
    }
    return i;
  }

  public String getFilenameAt(int y, int x) {
    int start = findFilenameStart(y, x);
    int end = findFilenameEnd(y, x);
    return getLine(y).substring(start, end + 1);
  }

  private int findFilenameEnd(int y, int x) {
    String line = getLine(y);
    int i = x;
    while (i < line.length() - 1 && isFilenameCharacter(line.charAt(i + 1))) {
      i++;
    }
    return i;
  }

  private int findFilenameStart(int y, int x) {
    String line = getLine(y);
    int i = x;
    while (i > 0 && isFilenameCharacter(line.charAt(i - 1))) {
      i--;
    }
    return i;
  }

  private boolean isWordCharacter(char c) {
    return Character.isLetterOrDigit(c) || c == '_';
  }

  private boolean isFilenameCharacter(char c) {
    return isWordCharacter(c) || c == '/' || c == '.';
  }

  public void appendText(int y, String text) {
    changeLine(y, getLine(y) + text);
  }

  public void insertText(int y, int x, String... lines) {
    insertText(y, x, Arrays.asList(lines));
  }

  public void insertText(int y, int x, List<String> lines) {
    insertText(y, x, false, lines);
  }

  public void insertTextWithLineBreak(int y, int x, String... lines) {
    insertTextWithLineBreak(y, x, Arrays.asList(lines));
  }

  public void insertTextWithLineBreak(int y, int x, List<String> lines) {
    insertText(y, x, true, lines);
  }

  public void insertText(int y, int x, boolean lineBreakOnLast, List<String> lines) {
    if (lines.isEmpty()) {
      return;
    }
    if (file.isEmpty()) {
      insertLines(0, lines);
      return;
    }
    String topLine = getLine(y);
    if (x > topLine.length()) {
      System.out.println("topLine.length = " + topLine.length() + ", x = " + x);
      System.out.println("insertText(" + y + ", " + x + ", " + lineBreakOnLast + ", " + lines + ")");
    }
    String head = topLine.substring(0, x);
    String tail = topLine.substring(x);
    if (!lineBreakOnLast && lines.size() == 1) {
      changeLine(y, head + lines.get(0) + tail);
      return;
    }
    changeLine(y, head + lines.get(0));
    int lastInsertedLine = y;
    for (int i = 1; i < lines.size(); i++) {
      insertLine(y + i, lines.get(i));
      lastInsertedLine = y + i;
    }
    if (lineBreakOnLast) {
      insertLine(lastInsertedLine + 1, tail);
    } else {
      appendText(lastInsertedLine, tail);
    }
  }

  public void insertLines(int y, String... lines) {
    insertLines(y, Arrays.asList(lines));
  }

  public void insertLines(int y, List<String> lines) {
    // NOTE(koz): We insert lines in reverse order so the markers get restored
    // to their original state.
    for (int i = lines.size() - 1; i >= 0; i--) {
      String line = lines.get(i);
      insertLine(y, line);
    }
  }

  public void setDiffMarkers(FileDelta delta) {
    file.setDiffMarkers(delta);
  }

  public void save(FileSystem fileSystem) {
    file.save(fileSystem);
  }

  public boolean isModified() {
    return file.isModified();
  }

  public boolean isDogEared() {
    return file.isDogEared();
  }

  public boolean isMarkersClear() {
    return file.isMarkersClear();
  }

  public File getFile() {
    return file;
  }

  public List<String> getLineList() {
    return file.getLineRange(start, end);
  }

  public List<String> getLineRange(int start, int end) {
    return file.getLineRange(this.start + start, this.start + end);
  }

  private void fireOnLineInserted(int y, String line) {
    for (File.Listener listener : listeners) {
      listener.onLineInserted(y, line);
    }
  }

  private void fireOnLineRemoved(int y, String line) {
    for (File.Listener listener : listeners) {
      listener.onLineRemoved(y, line);
    }
  }

  private void fireOnLineChanged(int y, String oldLine, String newLine) {
    for (File.Listener listener : listeners) {
      listener.onLineChanged(y, oldLine, newLine);
    }
  }

  public Point getNextModifiedPoint(int y, int x) {
    boolean acceptNextModified = false;
    for (int i = y; i < getLineCount(); i++) {
      if (hasModifiedMarkers(i)) {
        if (acceptNextModified) {
          return new Point(i, 0);
        }
      } else {
        acceptNextModified = true;
      }
    }
    return null;
  }

  public boolean hasModifiedMarkers(int y) {
    return getStatus(y) != Tombstone.Status.NORMAL || !getGrave(y).isEmpty();
  }

  public Point getPreviousModifiedPoint(int y, int x) {
    boolean acceptPreviousModified = false;
    for (int i = y; i >= 0; i--) {
      if (hasModifiedMarkers(i)) {
        if (acceptPreviousModified) {
          return new Point(i, 0);
        }
      } else {
        acceptPreviousModified = true;
      }
    }
    return null;
  }

  public void undoLine(int y) {
    file.undoLine(start + y);
  }

  public void undoLineRange(int startY, int endY) {
    for (int i = endY; i >= startY; i--) {
      undoLine(i);
    }
  }

  public void wipeRange(int startY, int endY) {
    for (int i = endY; i >= startY; i--) {
      wipe(i);
    }
  }

  public void wipe(int y) {
    file.wipe(start + y);
  }

  public int findNextWordBreak(int y, int x) {
    String line = getLine(y);
    boolean foundLetter = isIdentifierLetter(line.charAt(x));
    for (int i = x; i < line.length(); i++) {
      char c = line.charAt(i);
      if (isIdentifierLetter(c)) {
        foundLetter = true;
      } else if (foundLetter) {
        return i;
      }
    }
    return -1;
  }

  public int findPreviousWordBreak(int y, int x) {
    String line = getLine(y);
    boolean foundLetter = isIdentifierLetter(line.charAt(x));
    for (int i = x; i >= 0; i--) {
      char c = line.charAt(i);
      if (isIdentifierLetter(c)) {
        foundLetter = true;
      } else if (foundLetter) {
        return i + 1;
      }
    }
    return -1;
  }

  private boolean isIdentifierLetter(char c) {
    return Character.isLetterOrDigit(c) || c == '_';
  }

  public Point findMatchingLetter(int y, int x) {
    String line = getLine(y);
    char c = line.charAt(x);
    switch (c) {
    case '(': return findNext(y, x, '(', ')');
    case '[': return findNext(y, x, '[', ']');
    case '{': return findNext(y, x, '{', '}');
    case ')': return findPrevious(y, x, ')', '(');
    case ']': return findPrevious(y, x, ']', '[');
    case '}': return findPrevious(y, x, '}', '{');
    }
    return null;
  }

  public Point findNext(int y, int x, char push, char pop) {
    return findChar(new ForwardFileCharIterator(y, x), push, pop);
  }

  public Point findChar(FileCharIterator it, char push, char pop) {
    int depth = 0;
    while (it.hasNext()) {
      char c = it.next();
      if (c == push) {
        depth++;
      } else if (c == pop) {
        depth--;
        if (depth == 0) {
          return it.getPoint();
        }
      }
    }
    return null;
  }

  public Point findPrevious(int y, int x, char push, char pop) {
    return findChar(new ReverseFileCharIterator(y, x), push, pop);
  }

  public interface FileCharIterator {
    char next();
    boolean hasNext();
    Point getPoint();
  }

  public class ForwardFileCharIterator implements FileCharIterator {
    protected int y;
    protected int x;
    protected boolean isValid;
    protected Point lastPoint = null;

    public ForwardFileCharIterator(int y, int x) {
      this.y = y;
      this.x = x;
      this.isValid = true;
    }

    @Override
    public char next() {
      char result = getLine(y).charAt(x);
      lastPoint = new Point(y, x);
      advance();
      return result;
    }

    protected void advance() {
      if (!isValid) {
        throw new IllegalStateException();
      }
      int nextX = x + 1;
      int nextY = y;
      if (nextX >= getLine(y).length()) {
        nextY = getNextNonEmptyLine();
        if (nextY == -1) {
          isValid = false;
          return;
        }
        nextX = 0;
      }
      x = nextX;
      y = nextY;
    }

    private int getNextNonEmptyLine() {
      for (int i = y + 1; i < getLineCount(); i++) {
        if (!getLine(i).isEmpty()) {
          return i;
        }
      }
      return -1;
    }

    @Override
    public boolean hasNext() {
      return isValid;
    }

    @Override
    public Point getPoint() {
      return lastPoint;
    }
  }

  public class ReverseFileCharIterator extends ForwardFileCharIterator {
    public ReverseFileCharIterator(int y, int x) {
      super(y, x);
    }

    @Override
    protected void advance() {
      if (!isValid) {
        throw new IllegalStateException();
      }
      int nextX = x - 1;
      int nextY = y;
      if (nextX < 0) {
        nextY = getPreviousNonEmptyLine();
        if (nextY == -1) {
          isValid = false;
          return;
        }
        nextX = getLine(nextY).length() - 1;
      }
      if (nextY < 0) {
        throw new IllegalStateException();
      }
      y = nextY;
      x = nextX;
    }

    private int getPreviousNonEmptyLine() {
      for (int i = y - 1; i >= 0; i--) {
        if (!getLine(i).isEmpty()) {
          return i;
        }
      }
      return -1;
    }
  }


  public FileView makeView(int startY, int endY) {
    return file.makeView(start + startY, start + endY);
  }

  public int getModifiedLinesCount() {
    int result = 0;
    for (int y = 0; y < getLineCount(); y++) {
      if (hasModifiedMarkers(y)) {
        result++;
      }
    }
    return result;
  }

  public List<Range> getDeltas(int padding) {
    List<Range> result = new ArrayList<Range>();
    int deltaStart = -1;
    int deltaEnd = -1;

    for (int y = 0; y < getLineCount(); y++) {
      if (hasModifiedMarkers(y)) {
        if (deltaStart == -1) {
          deltaStart = deltaEnd = y;
        } else {
          deltaEnd = y;
        }
      } else {
        if (deltaStart != -1) {
          Range candidateDelta = makePaddedDelta(deltaStart, deltaEnd, padding);
          if (result.isEmpty()) {
            result.add(candidateDelta);
          } else {
            Range lastAddedDelta = result.remove(result.size() - 1);
            if (lastAddedDelta.isOverlapping(candidateDelta)) {
              result.add(Range.union(lastAddedDelta, candidateDelta));
            } else {
              result.add(lastAddedDelta);
              result.add(candidateDelta);
            }
          }
        }
        deltaStart = deltaEnd = -1;
      }
    }
    if (deltaStart != -1) {
      result.add(makePaddedDelta(deltaStart, deltaEnd, padding));
    }
    return result;
  }

  private Range makePaddedDelta(int start, int end, int padding) {
    start = Math.max(0, start - padding);
    end = Math.min(getLineCount() - 1, end + padding);
    return new Range(start, end);
  }

  public String getBaseFilename() {
    return file.getBaseFilename();
  }

  private Point translatePoint(Point point) {
    if (point == null) {
      return null;
    }
    return point.offset(-start, 0);
  }

  public Data.Editor getSerialized() {
    return Data.Editor.newBuilder().setFilename(getFilename()).setStart(start).setEnd(end).buildPartial();
  }
}
