package com.id.events;

import java.awt.event.KeyEvent;

import javax.swing.JFrame;

import com.id.editor.Editor;
import com.id.editor.Visual;

public class EditorKeyHandler {
  static JFrame frame = new JFrame();
  public KeyEvent makeEventFromChar(char c) {
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

    if (event.isShiftDown()) {
      switch (event.getKeyCode()) {
      case KeyEvent.VK_O:
        editor.addEmptyLinePrevious();
        break;
      case KeyEvent.VK_A:
        editor.appendEnd();
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
      default:
        return false;
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
        editor.insert();
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
      case KeyEvent.VK_BACK_SPACE:
        editor.backspace();
        break;
      case KeyEvent.VK_ESCAPE:
        editor.escape();
        break;
      default:
        return false;
      }
    }
    return true;
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
