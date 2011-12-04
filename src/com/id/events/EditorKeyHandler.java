package com.id.events;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

import com.id.editor.Editor;
import com.id.editor.Visual;
import com.id.events.ShortcutTree.Action;

public class EditorKeyHandler {
  private final ShortcutTree normalTree;
  private Editor editor;

  public EditorKeyHandler() {
    normalTree = new ShortcutTree();

    setNormal("j", new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.down();
      }
    });
    setNormal("k", new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.up();
      }
    });
    setNormal("h", new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.left();
      }
    });
    setNormal("l", new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.right();
      }
    });
    setNormal("w", new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.moveCursorToNextWord();
      }
    });
    setNormal("i", new ShortcutTree.Action() {
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
    setNormal("s", new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.substitute();
      }
    });
    setNormal("u", new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.undo();
      }
    });
    setNormal("r", new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.redo();
      }
    });
    setNormal("o", new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.addEmptyLine();
      }
    });
    setNormal("a", new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.append();
      }
    });
    setNormal("v", new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.toggleVisual(Visual.Mode.CHAR);
      }
    });
    setNormal("x", new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.delete();
      }
    });
    setNormal("0", new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.moveCursorToStartOfLine();
      }
    });
    setNormal(KeyStroke.backspace(), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.backspace();
      }
    });
    setNormal("\\", new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.clearHighlight();
      }
    });
    setNormal("z", new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.recenter();
      }
    });
    setNormal("n", new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.next();
      }
    });
    setNormal(KeyStroke.escape(), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.escape();
      }
    });
    setNormal(KeyStroke.fromString("gg"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.moveCursorToStartOfFile();
      }
    });
    setNormal(KeyStroke.fromString("cc"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        editor.substituteLine();
      }
    });
  }

  private void setNormal(List<KeyStroke> keys, Action action) {
    normalTree.setShortcut(keys, action);
  }

  private void setNormal(KeyStroke key, Action action) {
    normalTree.setShortcut(Arrays.asList(key), action);
  }

  private void setNormal(String string, Action action) {
    normalTree.setShortcut(KeyStroke.fromString(string), action);
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

    if (editor.isInVisual()) {
      if (event.isShiftDown()) {
        boolean handled = true;
        switch (event.getKeyCode()) {
        case KeyEvent.VK_J:
          editor.join();
          break;
        case KeyEvent.VK_K:
          // Do nothing.
          break;
        default:
          handled = false;
          break;
        }
        if (handled) {
          return true;
        }
      }
    }
    boolean handled = true;
    if (event.isShiftDown()) {
      switch (event.getKeyCode()) {
      case KeyEvent.VK_O:
        editor.addEmptyLinePrevious();
        break;
      case KeyEvent.VK_A:
        editor.appendEnd();
        break;
      case KeyEvent.VK_I:
        editor.insertStart();
        break;
      case KeyEvent.VK_V:
        editor.toggleVisual(Visual.Mode.LINE);
        break;
      case KeyEvent.VK_C:
        editor.changeLine();
        break;
      case KeyEvent.VK_D:
        editor.deleteLine();
        break;
      case '$':
        editor.moveCursorToEndOfLine();
        break;
      case KeyEvent.VK_S:
        editor.substituteLine();
        break;
      case '*':
        editor.highlightWordUnderCursor();
        break;
      case KeyEvent.VK_N:
        editor.previous();
        break;
      default:
        handled = false;
      }
    } else if (event.isControlDown()) {
      switch (event.getKeyCode()) {
      case KeyEvent.VK_F:
        editor.downPage();
        break;
      case KeyEvent.VK_B:
        editor.upPage();
        break;
      }
    } else {
      this.editor = editor;
      return normalTree.stepAndExecute(event);
    }
    return handled;
  }
}
