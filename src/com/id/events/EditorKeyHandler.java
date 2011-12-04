package com.id.events;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import com.id.editor.Editor;
import com.id.editor.Visual;

public class EditorKeyHandler {
  private static Map<Character, Character> shiftSymbols = new HashMap<Character, Character>();
  static {
    shiftSymbols.put('!', '1');
    shiftSymbols.put('@', '2');
    shiftSymbols.put('#', '3');
    shiftSymbols.put('$', '4');
    shiftSymbols.put('%', '5');
    shiftSymbols.put('^', '6');
    shiftSymbols.put('&', '7');
    shiftSymbols.put('*', '8');
    shiftSymbols.put('(', '9');
    shiftSymbols.put(')', '0');
  }

  public KeyStroke makeEventFromChar(char c) {
    if (shiftSymbols.containsKey(c)) {
      char keyChar = shiftSymbols.get(c);
      return new KeyStroke(keyChar, KeyEvent.SHIFT_MASK);
    }
    int mask = 0;
    if (Character.isUpperCase(c)) {
      mask = KeyEvent.SHIFT_MASK;
    }
    return new KeyStroke(c, mask);
  }

  public KeyStroke makeEventFromChar(char c, int modifiers) {
    return new KeyStroke(c, modifiers);
  }

  public KeyStroke makeEventFromVKey(int keyCode) {
    return new KeyStroke((char) keyCode, 0);
  }

  public KeyStroke escape() {
    return makeEventFromVKey(KeyEvent.VK_ESCAPE);
  }

  public boolean handleChar(char c, Editor editor) {
    return handleKeyPress(makeEventFromChar(c), editor);
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
      case KeyEvent.VK_4:
        editor.moveCursorToEndOfLine();
        break;
      case KeyEvent.VK_S:
        editor.subsituteLine();
        break;
      case KeyEvent.VK_8:
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
      switch (event.getKeyCode()) {
      case KeyEvent.VK_J:
        editor.down();
        break;
      case KeyEvent.VK_K:
        editor.up();
        break;
      case KeyEvent.VK_H:
        editor.left();
        break;
      case KeyEvent.VK_L:
        editor.right();
        break;
      case KeyEvent.VK_W:
        editor.moveCursorToNextWord();
        break;
      case KeyEvent.VK_I:
        if (!editor.isInVisual()) {
          editor.insert();
        } else {
          handled = false;
        }
        break;
      case KeyEvent.VK_S:
        editor.substitute();
        break;
      case KeyEvent.VK_U:
        editor.undo();
        break;
      case KeyEvent.VK_R:
        editor.redo();
        break;
      case KeyEvent.VK_O:
        editor.addEmptyLine();
        break;
      case KeyEvent.VK_A:
        editor.append();
        break;
      case KeyEvent.VK_V:
        editor.toggleVisual(Visual.Mode.CHAR);
        break;
      case KeyEvent.VK_X:
        editor.delete();
        break;
      case KeyEvent.VK_0:
        editor.moveCursorToStartOfLine();
        break;
      case KeyEvent.VK_BACK_SPACE:
        editor.backspace();
        break;
      case KeyEvent.VK_BACK_SLASH:
        editor.clearHighlight();
        break;
      case KeyEvent.VK_Z:
        editor.recenter();
        break;
      case KeyEvent.VK_N:
        editor.next();
        break;
      case KeyEvent.VK_ESCAPE:
        editor.escape();
        break;
      default:
        handled = false;
      }
    }
    return handled;
  }

  public KeyStroke makeEventFromControlChar(char c) {
    return makeEventFromChar(c, KeyEvent.CTRL_MASK);
  }
}
