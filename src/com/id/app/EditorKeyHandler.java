package com.id.app;

import java.awt.event.KeyEvent;

import com.id.editor.Editor;

public class EditorKeyHandler {
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
      if (event.isShiftDown()) {
        editor.addEmptyLinePrevious();
      } else {
        editor.addEmptyLine();
      }
      break;
    case KeyEvent.VK_A:
      if (event.isShiftDown()) {
        editor.appendEnd();
      } else {
        editor.append();
      }
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
