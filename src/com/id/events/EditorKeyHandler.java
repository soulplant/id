package com.id.events;

import java.awt.event.KeyEvent;
import java.util.Arrays;

import com.id.editor.Editor;
import com.id.editor.Visual;

public class EditorKeyHandler {
  private final ShortcutTree normalTree;
  private final ShortcutTree visualTree;
  private Editor editor;

  public EditorKeyHandler() {
    normalTree = new ShortcutTree();
    visualTree = new ShortcutTree();

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
    normalTree.setShortcut(Arrays.asList(KeyStroke.tab()), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.tab();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("dd"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.deleteLine();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("w"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.moveCursorToNextWord();
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
    normalTree.setShortcut(Arrays.asList(KeyStroke.backspace()), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.backspace();
      }
    });
    normalTree.setShortcut(KeyStroke.fromString("\\"), new ShortcutTree.Action() {
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
    normalTree.setShortcut(KeyStroke.fromString("C"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.changeLine();
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
  }

  public boolean handleKeyPress(KeyStroke event, Editor editor) {
    if (editor.isInInsertMode()) {
      boolean handled = true;
      if (event.isLetter()) {
        editor.onLetterTyped(event.getKeyChar());
      } else if (event.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
        editor.backspace();
      } else if (event.getKeyCode() == KeyEvent.VK_ENTER) {
        editor.enter();
      } else {
        handled = false;
      }
      if (handled) {
        return true;
      }
    }

    this.editor = editor;
    return (editor.isInVisual() && visualTree.stepAndExecute(event))
        || normalTree.stepAndExecute(event);
  }
}
