package com.id.events;

import java.awt.event.KeyEvent;
import java.util.Arrays;

import com.id.editor.Editor;
import com.id.editor.Editor.FindMode;
import com.id.editor.Visual;

public class EditorKeyHandler {
  private final ShortcutTree normalTree;
  private final ShortcutTree visualTree;
  private final ShortcutTree insertTree;
  private Editor editor;

  public EditorKeyHandler() {
    normalTree = new ShortcutTree();
    visualTree = new ShortcutTree();
    insertTree = new ShortcutTree();

    normalTree.setShortcut(KeyStroke.fromString("j"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.down();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("k"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.up();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("h"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.left();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("l"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.right();
      }
    });
    normalTree.setShortcut(Arrays.asList(KeyStroke.up()), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.up();
      }
    });
    normalTree.setShortcut(Arrays.asList(KeyStroke.down()), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.down();
      }
    });
    normalTree.setShortcut(Arrays.asList(KeyStroke.left()), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.left();
      }
    });
    normalTree.setShortcut(Arrays.asList(KeyStroke.right()), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.right();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("dd"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.deleteLine();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("i"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        // TODO This should be handled at a higher level - we shouldn't be
        // stepping ShortcutTrees that are for modes we aren't in.
        if (editor.isInVisual()) {
          return;
        }
        editor.insert();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("s"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.substitute();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("u"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.undo();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("U"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.undoLine();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("W"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.wipe();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("r"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.redo();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("o"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.addEmptyLine();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("a"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.append();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("v"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.toggleVisual(Visual.Mode.CHAR);
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("x"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.delete();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("p"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.put();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("P"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.putBefore();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("0"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.moveCursorToStartOfLine();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("\\\\"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.clearHighlight();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("z"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.recenter();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("n"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.next();
      }
    });
    normalTree.setShortcut(Arrays.asList(KeyStroke.escape()), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.escape();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("gg"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.moveCursorToStartOfFile();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("gf"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.openFileUnderCursor();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("gi"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.insertAtLastInsert();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("gF"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.openFileMatchingWordUnderCursor();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("gv"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.reselectVisual();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("cc"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.substituteLine();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("O"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.addEmptyLinePrevious();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("A"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.appendEnd();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("I"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.insertStart();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("V"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.toggleVisual(Visual.Mode.LINE);
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("cw"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.changeWord();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("ciw"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.changeWordUnderCursor();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("ci("), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.changeContentBetween('(', ')');
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("ci)"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.changeContentBetween('(', ')');
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("ci["), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.changeContentBetween('[', ']');
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("ci]"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.changeContentBetween('[', ']');
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("ci\\<"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.changeContentBetween('<', '>');
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("ci\\>"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.changeContentBetween('<', '>');
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("ci{"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.changeContentBetween('{', '}');
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("ci}"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.changeContentBetween('{', '}');
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("ci'"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.changeContentBetween('\'');
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("ci\""), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.changeContentBetween('"');
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("c$"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.changeToEndOfLine();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("dw"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.deleteWord();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("d$"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.deleteToEndOfLine();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("dj"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.deleteDown();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("dk"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.deleteUp();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("C"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.changeToEndOfLine();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("D"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.deleteToEndOfLine();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("$"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.moveCursorToEndOfLine();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("%"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.findMatchingLetter();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("S"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.substituteLine();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("*"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.highlightWordUnderCursor();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("N"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.previous();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("G"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.moveCursorToEndOfFile();
      }
    });
    normalTree.setShortcut(Arrays.asList(KeyStroke.fromControlChar('f')),
        new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.downPage();
      }
    });
    normalTree.setShortcut(Arrays.asList(KeyStroke.fromControlChar('b')),
        new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.upPage();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("f"),
        new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.enterFindMode(FindMode.FIND_FORWARDS);
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("F"),
        new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.enterFindMode(FindMode.FIND_BACKWARDS);
      }
    });
    normalTree.setShortcut(KeyStroke.fromString(";"),
        new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.repeatLastFindForwards();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString(","),
        new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.repeatLastFindBackwards();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("/"),
        new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.enterSearch();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("\\>"),
        new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.indent();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("\\<"),
        new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.outdent();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString(" "),
        new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.saveViewport();
      }
    });

    visualTree.setShortcut(KeyStroke.fromString("y"),
        new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.yank();
      }
    });
    visualTree.setShortcut(KeyStroke.fromString("d"),
        new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.delete();
      }
    });
    visualTree.setShortcut(KeyStroke.fromString("J"),
        new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.join();
      }
    });
    visualTree.setShortcut(KeyStroke.fromString("K"),
        new ShortcutTree.Action() {
      @Override
      public void execute() {
        // Do nothing.
      }
    });
    visualTree.setShortcut(KeyStroke.fromString(";"),
        new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.makeSnippetFromVisual();
      }
    });
    visualTree.setShortcut(KeyStroke.fromString("o"),
        new ShortcutTree.Action() {
      @Override
      public void execute() {
        // Do nothing.
      }
    });

    insertTree.setShortcut(KeyStroke.fromString("<C-j>"),
        new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.highlightWordBeforeCursor();
      }
    });
    insertTree.setShortcut(KeyStroke.fromString("<C-k>"),
        new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.insertLastHighlightedWord();
      }
    });
    insertTree.setShortcut(KeyStroke.fromString("<S-TAB>"),
        new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.outdent();
      }
    });
    insertTree.setShortcut(KeyStroke.fromString("<BS>"),
        new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.backspace();
      }
    });
  }

  public boolean handleKeyPress(KeyStroke event, Editor editor) {
    this.editor = editor;
    if (editor.isInInsertMode()) {
      if (insertTree.stepAndExecute(event)) {
        return true;
      }
      boolean handled = true;
      if (event.isLetter()) {
        editor.onLetterTyped(event.getKeyChar());
      } else if (event.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
        editor.backspace();
      } else if (event.getKeyCode() == KeyEvent.VK_ENTER) {
        editor.enter();
      } else if (event.getKeyCode() == KeyEvent.VK_TAB) {
        editor.tab();
      } else if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
        editor.escape();
      } else {
        handled = false;
      }
      if (handled) {
        return true;
      }
    }

    if (editor.isInSearchMode() && editor.handleSearchKeyStroke(event)) {
      return true;
    }

    if (editor.isInFindMode()) {
      if (event.isLetter()) {
        editor.onFindLetter(event.getLetter());
      } else {
        editor.exitFindMode();
      }
      return true;
    }

    if (editor.isInVisual() && visualTree.stepAndExecute(event)) {
      return true;
    }
    return normalTree.stepAndExecute(event);
  }
}
