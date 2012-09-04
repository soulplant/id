package com.id.app;

import com.id.editor.Editor;

public class CommandExecutor {
  private final EditorOpener editorOpener;
  private final FocusManager focusManager;

  public CommandExecutor(EditorOpener editorOpener, FocusManager focusManager) {
    this.editorOpener = editorOpener;
    this.focusManager = focusManager;
  }

  public void execute(String command, Editor editor) {
    String[] parts = command.split("\\s");
    if (parts[0].equals("e")) {
      if (parts.length == 2) {
        editorOpener.openFile(parts[1]);
      } else {
        // e by itself means re-open the file.
        editorOpener.reloadFile(editor.getFilename());
      }
      return;
    } else if (isInt(parts[0])) {
      jumpToLine(Integer.parseInt(parts[0]) - 1);
      return;
    }

    System.out.println("unknown command: " + command);
  }

  private void jumpToLine(int y) {
    focusManager.getFocusedEditor().jumpToLine(y);
  }

  private boolean isInt(String text) {
    try {
      Integer.parseInt(text);
    } catch (NumberFormatException e) {
      return false;
    }
    return true;
  }
}
