package com.id.editor;

import java.util.ArrayList;
import java.util.List;

import com.id.app.HighlightState;
import com.id.editor.Visual.Mode;
import com.id.events.EditorKeyHandler;
import com.id.events.KeyStroke;
import com.id.events.KeyStrokeHandler;
import com.id.file.File;
import com.id.file.File.Listener;
import com.id.file.FileView;
import com.id.file.Grave;
import com.id.file.ModifiedListener;
import com.id.file.Tombstone.Status;
import com.id.git.FileDelta;
import com.id.platform.FileSystem;

public class Editor implements KeyStrokeHandler, HighlightState.Listener {
  public interface Context {
    void moveViewportToIncludePoint(Point point);
    void recenterScreenOnPoint(Point point);
    int getViewportHeight();
    boolean isVisible(Point point);
  }

  public enum FindMode {
    NONE,
    FIND_FORWARDS,
    FIND_BACKWARDS,
    TIL_FORWARDS,
    TIL_BACKWARDS,;

    public FindMode opposite() {
      switch (this) {
      case FIND_BACKWARDS: return FIND_FORWARDS;
      case FIND_FORWARDS: return FIND_BACKWARDS;
      case TIL_FORWARDS: return TIL_BACKWARDS;
      case TIL_BACKWARDS: return TIL_FORWARDS;
      case NONE: return NONE;
      }
      return null;
    }

    private int findIn(Cursor cursor, FileView file, char letter) {
      switch (this) {
      case FIND_FORWARDS: return file.findNextLetter(cursor.getY(), cursor.getX(), letter);
      case FIND_BACKWARDS: return file.findPreviousLetter(cursor.getY(), cursor.getX(), letter);
      default: return -1;
      }
    }
  }

  class EmptyContext implements Context {
    @Override
    public void moveViewportToIncludePoint(Point point) {
      // Do nothing.
    }

    @Override
    public void recenterScreenOnPoint(Point point) {
      // Do nothing.
    }

    @Override
    public int getViewportHeight() {
      return 10;
    }

    @Override
    public boolean isVisible(Point point) {
      return true;
    }
  }

  private final FileView file;
  private final Cursor cursor;
  private final Visual visual;
  private boolean inInsertMode = false;
  private Context context = new EmptyContext();
  private Register register = null;

  private final EditorKeyHandler keyHandler;
  private final List<File.Listener> fileListeners = new ArrayList<File.Listener>();
  private final List<ModifiedListener> fileModifiedListeners = new ArrayList<ModifiedListener>();
  private FindMode findMode = FindMode.NONE;
  private char lastFindLetter = 0;
  private FindMode lastFindMode = FindMode.NONE;
  private Search currentSearch = null;
  private Highlight highlight = new EmptyHighlight();
  private final HighlightState highlightState;

  public Editor(FileView fileView, HighlightState highlightState) {
    this.file = fileView;
    this.highlightState = highlightState;
    this.cursor = new Cursor();
    this.visual = new Visual(this.cursor);
    this.highlightState.addListener(this);
    addFileListener(highlight);
    onHighlightStateChanged();
    cursor.addListner(new Cursor.Listener() {
      @Override
      public void onMoved(int y, int x) {
        context.moveViewportToIncludePoint(new Point(y, x));
      }

      @Override
      public void onJumped(int y, int x) {
        Point point = new Point(y, x);
        if (!context.isVisible(point)) {
          context.recenterScreenOnPoint(point);
        }
      }
    });
    keyHandler = new EditorKeyHandler();
  }

  public String getLine(int y) {
    return file.getLine(y);
  }

  public int getLineCount() {
    return file.getLineCount();
  }

  public Point getCursorPosition() {
    return cursor.getPoint();
  }

  // Keyboard commands.
  public void down() {
    cursor.moveBy(1, 0);
    applyCursorConstraints();
  }

  public void up() {
    cursor.moveBy(-1, 0);
    applyCursorConstraints();
  }

  public void left() {
    cursor.moveBy(0, -1);
    applyCursorConstraints();
  }

  public void right() {
    cursor.moveBy(0, 1);
    applyCursorConstraints();
  }

  public void insert() {
    inInsertMode = true;
    cursor.setDefaultX(cursor.getX());
  }

  private void applyCursorConstraints() {
    int isInNormalMode = isInInsertMode() ? 0 : 1;
    cursor.constrainY(0, file.getLineCount() - 1);
    cursor.constrainX(0, getCurrentLineLength() - isInNormalMode);
  }

  private String getCurrentLine() {
    return file.getLine(cursor.getY());
  }

  public boolean isInInsertMode() {
    return inInsertMode;
  }

  public void escape() {
    if (visual.isOn()) {
      visual.toggleMode(Mode.NONE);
      return;
    }
    if (isInInsertMode()) {
      inInsertMode = false;
      file.breakPatch();
      cursor.moveBy(0, -1);
      applyCursorConstraints();
    }
  }

  public void onLetterTyped(char keyChar) {
    if (!isInInsertMode()) {
      throw new IllegalStateException("Can't type letters unless in insert mode.");
    }
    startPatchIfNecessary();

    String text = "" + keyChar;
    file.insertText(cursor.getY(), cursor.getX(), text);
    cursor.moveBy(0, text.length());
  }

  private void startPatch() {
    if (isInVisual()) {
      file.startPatchAt(visual.getStartPoint().getY(), visual.getStartPoint().getX());
    } else {
      file.startPatchAt(cursor.getY(), cursor.getX());
    }
  }

  private void startPatchIfNecessary() {
    if (!file.isInPatch()) {
      startPatch();
    }
  }

  public void undo() {
    Point position = file.undo();
    if (position == null) {
      return;
    }
    cursor.moveTo(position.getY(), position.getX());
    applyCursorConstraints();
  }

  public void redo() {
    Point position = file.redo();
    if (position == null) {
      return;
    }
    cursor.moveTo(position.getY(), position.getX());
    applyCursorConstraints();
  }

  public void addEmptyLine() {
    addLineAt(cursor.getY() + 1, getIndentForLine(cursor.getY()));
  }

  public void addEmptyLinePrevious() {
    addLineAt(cursor.getY(), getIndentForLine(cursor.getY()));
  }

  private void addLineAt(int y, String text) {
    startPatchIfNecessary();
    if (file.isEmpty()) {
      file.insertLine(0, "");
    }
    file.insertLine(Math.min(file.getLineCount(), y), text);
    cursor.moveTo(y, 0);
    appendEnd();
  }

  private String getIndentForLine(int y) {
    if (file.isEmpty()) {
      return "";
    }
    String line = getLine(y);
    StringBuffer indent = new StringBuffer();
    for (int i = 0; i < line.length(); i++) {
      if (!isWhitespace(line.charAt(i))) {
        break;
      }
      indent.append(line.charAt(i));
    }
    return indent.toString();
  }

  private static boolean isWhitespace(char c) {
    return c == ' ';
  }

  public void backspace() {
    startPatchIfNecessary();
    if (cursor.getX() == 0) {
      if (cursor.getY() == 0) {
        return;
      }
      String line = getCurrentLine();
      file.removeLine(cursor.getY());
      cursor.moveBy(-1, 0);
      int targetX = getCurrentLine().length();
      file.insertText(cursor.getY(), targetX, line);
      cursor.moveTo(cursor.getY(), targetX);
    } else {
      int charsToRemove = getSpacesBackIncludingSoftTabs(getCurrentLine(), cursor.getX(), 2);
      file.removeText(cursor.getY(), cursor.getX() - charsToRemove, charsToRemove);
      cursor.moveBy(0, -charsToRemove);
    }
  }

  public static int getSpacesBackIncludingSoftTabs(String line, int x, int tabSize) {
    if (x == 0) {
      return 0;
    }
    for (int i = 0; i < x; i++) {
      if (!isWhitespace(line.charAt(i))) {
        return 1;
      }
    }
    return x % tabSize == 0 ? tabSize : x % tabSize;
  }

  public void append() {
    insert();
    cursor.moveBy(0, 1);
    applyCursorConstraints();
  }

  public void appendEnd() {
    cursor.moveTo(cursor.getY(), getCurrentLineLength());
    insert();
  }

  public void insertStart() {
    cursor.moveTo(cursor.getY(), getFirstNonWhitespace(cursor.getY()));
    insert();
  }

  private int getFirstNonWhitespace(int y) {
    String line = getLine(y);
    for (int i = 0; i < line.length(); i++) {
      if (!isWhitespace(line.charAt(i))) {
        return i;
      }
    }
    return line.length();
  }

  private int getCurrentLineLength() {
    if (file.isEmpty()) {
      return 0;
    }
    return getCurrentLine().length();
  }

  public void enter() {
    assert isInInsertMode();
    startPatchIfNecessary();
    if (file.isEmpty()) {
      addEmptyLine();
      return;
    }
    String indentText = getIndentForLine(cursor.getY());
    file.splitLine(cursor.getY(), cursor.getX(), indentText);
    cursor.moveBy(1, 0);
    cursor.moveTo(cursor.getY(), indentText.length());
  }

  public String getFilename() {
    return file.getFilename();
  }

  public void toggleVisual(Mode mode) {
    visual.toggleMode(mode);
  }

  public boolean isInVisual(int y, int x) {
    return visual.contains(new Point(y, x));
  }

  public void changeLine() {
    startPatch();
    file.removeText(cursor.getY(), cursor.getX());
    append();
  }

  public void deleteToEndOfLine() {
    startPatch();
    file.removeText(cursor.getY(), cursor.getX());
    file.breakPatch();
    applyCursorConstraints();
  }

  public void deleteLine() {
    register = new Register(Visual.Mode.LINE, false, getCurrentLine());
    startPatch();
    file.removeLine(cursor.getY());
    file.breakPatch();
    applyCursorConstraints();
  }

  public void moveCursorToEndOfLine() {
    int x = getCurrentLineLength() - (isInInsertMode() ? 0 : 1);
    cursor.moveTo(cursor.getY(), x);
  }

  public void moveCursorToStartOfLine() {
    cursor.moveTo(cursor.getY(), 0);
  }

  public void moveCursorToNextWord() {
    int nextSpace = file.findNextSpace(cursor.getY(), cursor.getX());
    if (nextSpace == -1) {
      moveCursorToEndOfLine();
      return;
    }

    int nextLetter = file.findNextLetter(cursor.getY(), nextSpace);
    if (nextLetter == -1) {
      moveCursorToEndOfLine();
      return;
    }

    cursor.moveTo(cursor.getY(), nextLetter);
  }

  public Grave getGrave(int y) {
    return file.getGrave(y);
  }

  public Status getStatus(int y) {
    return file.getStatus(y);
  }

  public void delete() {
    delete(true);
  }

  public void delete(boolean breakPatch) {
    startPatch();
    if (isInVisual()) {
      register = visual.getRegister(file);
      visual.removeFrom(file);
      cursor.moveTo(visual.getStartPoint().getY(), visual.getStartPoint().getX());
      visual.toggleMode(Visual.Mode.NONE);
    } else {
      register = new Register(Visual.Mode.CHAR, false, "" + getCharUnderCursor());
      file.removeText(cursor.getY(), cursor.getX(), 1);
    }
    if (breakPatch) {
      file.breakPatch();
    }
    applyCursorConstraints();
  }

  private char getCharUnderCursor() {
    return file.getLine(cursor.getY()).charAt(cursor.getX());
  }

  public boolean isInVisual() {
    return visual.isOn();
  }

  public void substitute() {
    delete(false);
    insert();
  }

  public boolean hasUndo() {
    return file.hasUndo();
  }

  public void substituteLine() {
    if (isInVisual()) {
      substitute();
      return;
    }
    moveCursorToStartOfLine();
    startPatch();
    file.removeText(cursor.getY(), 0);
    insert();
  }

  public void addFileListener(Listener listener) {
    fileListeners.add(listener);
    file.addListener(listener);
  }

  private void removeFileListener(Listener listener) {
    fileListeners.remove(listener);
    file.removeListener(listener);
  }

  public void addFileModifiedListener(ModifiedListener listener) {
    fileModifiedListeners.add(listener);
    file.addModifiedListener(listener);
  }

  public void setContext(Context context) {
    this.context = context;
  }

  public void downPage() {
    cursor.moveBy(context.getViewportHeight() - 1, 0);
    applyCursorConstraints();
  }

  public void upPage() {
    cursor.moveBy(-(context.getViewportHeight() - 1), 0);
    applyCursorConstraints();
  }

  public void join() {
    if (!isInVisual()) {
      throw new IllegalStateException();
    }
    startPatch();
    file.joinRange(visual.getStartPoint().getY(), visual.getEndPoint().getY());
    visual.toggleMode(Visual.Mode.NONE);
    file.breakPatch();
    applyCursorConstraints();
  }

  public void setHighlightPattern(String pattern) {
    highlightState.setHighlightPattern(pattern);
  }

  private void setHighlight(Highlight highlight) {
    removeFileListener(this.highlight);
    this.highlight = highlight;
    addFileListener(highlight);
  }

  public boolean isHighlight(int y, int x) {
    return highlight.isHighlighted(y, x);
  }

  public void highlightWordUnderCursor() {
    setHighlightPattern(file.getWordUnder(cursor.getY(), cursor.getX()));
  }

  public void clearHighlight() {
    setHighlightPattern("");
  }

  public void recenter() {
    context.recenterScreenOnPoint(cursor.getPoint());
  }

  public void next() {
    // TODO(koz): We should distinguish between having a search with no matches
    // and not having a search.
    if (highlight.getMatchCount() > 0) {
      Point point = highlight.getNextMatch(cursor.getY(), cursor.getX());
      if (point != null) {
        cursor.moveTo(point);
      }
      return;
    }
    Point point = file.getNextModifiedPoint(cursor.getY(), cursor.getX());
    if (point != null) {
      cursor.moveTo(point);
    }
  }

  public void previous() {
    // TODO(koz): We should distinguish between having a search with no matches
    // and not having a search.
    if (highlight.getMatchCount() > 0) {
      Point point = highlight.getPreviousMatch(cursor.getY(), cursor.getX());
      if (point != null) {
        cursor.moveTo(point);
      }
      return;
    }
    Point point = file.getPreviousModifiedPoint(cursor.getY(), cursor.getX());
    if (point != null) {
      cursor.moveTo(point);
    }
  }

  public void moveCursorToStartOfFile() {
    cursor.moveTo(0, 0);
  }

  public void moveCursorToEndOfFile() {
    cursor.moveTo(getLineCount() - 1, 0);
  }

  public void put() {
    if (register == null) {
      return;
    }
    startPatch();
    register.put(cursor.getY(), cursor.getX(), file);
    file.breakPatch();
  }

  public void putBefore() {
    if (register == null) {
      return;
    }
    startPatch();
    register.putBefore(cursor.getY(), cursor.getX(), file);
    file.breakPatch();
  }

  public void yank() {
    if (!isInVisual()) {
      throw new IllegalStateException();
    }
    register = visual.getRegister(file);
    toggleVisual(Visual.Mode.NONE);
  }

  public void tab() {
    int spacesToAdd = cursor.getX() % 2 == 0 ? 2 : 1;
    for (int i = 0; i < spacesToAdd; i++) {
      onLetterTyped(' ');
    }
  }

  @Override
  public boolean handleKeyStroke(KeyStroke keyStroke) {
    return keyHandler.handleKeyPress(keyStroke, this);
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

  public boolean isMarkersClear() {
    return file.isMarkersClear();
  }

  public void enterFindMode(FindMode findMode) {
    this.findMode = findMode;
  }

  public boolean isInFindMode() {
    return findMode != FindMode.NONE;
  }

  public void exitFindMode() {
    this.findMode = FindMode.NONE;
  }

  public void onFindLetter(char letter) {
    findLetter(letter, this.findMode, true);
  }

  private void findLetter(char letter, FindMode findModeToFind, boolean remember) {
    this.findMode = FindMode.NONE;
    int x = findModeToFind.findIn(cursor, file, letter);
    if (remember) {
      this.lastFindMode = findModeToFind;
      this.lastFindLetter = letter;
    }
    if (x == -1) {
      return;
    }
    cursor.moveTo(cursor.getY(), x);
  }

  public void repeatLastFindForwards() {
    if (lastFindLetter == 0) {
      return;
    }
    findLetter(this.lastFindLetter, this.lastFindMode, false);
  }

  public void repeatLastFindBackwards() {
    if (lastFindLetter == 0) {
      return;
    }
    findLetter(this.lastFindLetter, this.lastFindMode.opposite(), false);
  }

  public boolean isSearchHighlight(int y, int x) {
    return currentSearch != null && currentSearch.isHighlight(y, x);
  }

  public void enterSearch() {
    this.currentSearch = new Search(new Minibuffer(), file.getFile(), cursor.getPoint(), new Search.Listener() {
      @Override
      public void onSearchCompleted() {
        setHighlightPattern(currentSearch.getQuery());
        exitSearch();
      }

      @Override
      public void onMoveTo(int y, int x) {
        cursor.jumpTo(y, x);
      }

      @Override
      public void onSearchCancelled() {
        exitSearch();
      }
    });
  }

  public void exitSearch() {
    this.currentSearch = null;
  }

  public boolean isInSearchMode() {
    return this.currentSearch != null;
  }

  public boolean handleSearchKeyStroke(KeyStroke keyStroke) {
    if (!isInSearchMode()) {
      throw new IllegalStateException();
    }
    return this.currentSearch.handleKeyStroke(keyStroke);
  }

  public int getHighlightMatchCount() {
    return highlight.getMatchCount();
  }

  @Override
  public void onHighlightStateChanged() {
    setHighlight(new CachingHighlight(highlightState.getHighlightPattern(),
        file.getLineList()));
  }
}
