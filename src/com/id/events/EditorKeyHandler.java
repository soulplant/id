package com.id.events;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import com.id.editor.Editor;
import com.id.editor.Visual;

public class EditorKeyHandler {
  private static JFrame frame = new JFrame();
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

  public KeyEvent makeEventFromChar(char c) {
    if (shiftSymbols.containsKey(c)) {
      char keyChar = shiftSymbols.get(c);
      return new KeyEvent(frame, KeyEvent.KEY_PRESSED, 0L, KeyEvent.SHIFT_DOWN_MASK, keyChar, keyChar);
    }
    int mask = 0;
    if (Character.isUpperCase(c)) {
      mask = KeyEvent.SHIFT_DOWN_MASK;
    }
    return new KeyEvent(frame, KeyEvent.KEY_PRESSED, 0L, mask, Character.toUpperCase(c), c);
  }

  public KeyEvent makeEventFromVKey(int keyCode) {
    return new KeyEvent(frame, KeyEvent.KEY_PRESSED, 0L, 0, keyCode, (char) keyCode);
  }

  public KeyEvent escape() {
    return makeEventFromVKey(KeyEvent.VK_ESCAPE);
  }

  public boolean handleChar(char c, Editor editor) {
    return handleKeyPress(makeEventFromChar(c), editor);
  }

  public boolean handleKeyPress(KeyEvent event, Editor editor) {
    if (editor.isInInsertMode()) {
      boolean handled = true;
      if (isKeyCodeForLetter(event.getKeyCode())) {
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
        editor.endOfLine();
        break;
      case KeyEvent.VK_S:
        editor.subsituteLine();
        break;
      default:
        handled = false;
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
      case KeyEvent.VK_ESCAPE:
        editor.escape();
        break;
      default:
        handled = false;
      }
    }
    return handled;
  }

  private boolean isKeyCodeForLetter(int keyCode) {
    return ('a' <= keyCode && keyCode <= 'z') ||
        ('A' <= keyCode && keyCode <= 'Z') ||
        ('0' <= keyCode && keyCode <= '9') ||
        (" `~!@#$%^&*()-_=+[{]}\\|;:,<.>/?".indexOf(keyCode) != -1) ||
        keyCode == 39 /* single quote */ ||
        keyCode == 222 /* double quote */;
  }
}
