package com.id.app;

import com.id.editor.Editor;
import com.id.editor.EditorList;
import com.id.editor.StackList;

public class FocusManager {
  private final EditorList editorList;
  private final StackList stackList;

  public FocusManager(EditorList editorList, StackList stackList) {
    this.editorList = editorList;
    this.stackList = stackList;
  }

  public Editor focusEditor(String filename, int linesFromTop) {
    Editor editor = editorList.getEditorByName(filename);
    if (editor == null) {
      System.err.println("Couldn't focus editor " + filename);
      return null;
    }
    editorList.setFocusedEditor(editor);
    editorList.setFocused(true);
    editor.setTopLineVisible(linesFromTop);
    stackList.setFocused(false);
    return editor;
  }
}
