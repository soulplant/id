package com.id.app;

import com.id.editor.Editor;
import com.id.editor.EditorList;
import com.id.editor.StackList;
import com.id.util.StringUtils;

public class FocusManager {
  private final EditorList editorList;
  private final StackList stackList;

  public FocusManager(EditorList editorList, StackList stackList) {
    this.editorList = editorList;
    this.stackList = stackList;

    editorList.focus();
    stackList.blur();
  }

  public Editor focusEditor(String filename, int linesFromTop) {
    Editor editor = focusEditor(filename);
    if (editor != null) {
      editor.setTopLineVisible(linesFromTop);
    }
    return editor;
  }

  public void focusEditorList() {
    if (editorList.isFocused()) {
      return;
    }
    stackList.blur();
    editorList.focus();
  }

  public void focusStackList() {
    if (stackList.isFocused() || stackList.isEmpty()) {
      return;
    }
    editorList.blur();
    stackList.focus();
  }

  public Editor getFocusedEditor() {
    if (editorList.isFocused()) {
      return editorList.getFocusedItem();
    } else {
      return stackList.getFocusedEditor();
    }
  }

  private ListModel<Editor> getFocusedList() {
    return editorList.isFocused() ? editorList : stackList.getFocusedItem();
  }

  public void closeCurrentFile() {
    getFocusedList().removeFocused();
    while (!stackList.isEmpty() && stackList.getFocusedItem().isEmpty()) {
      stackList.removeFocused();
    }
    if (stackList.isEmpty()) {
      focusEditorList();
    }
  }

  public Editor focusEditor(String filename) {
    filename = StringUtils.normalizePath(filename);
    Editor editor = editorList.getEditorByName(filename);
    if (editor == null) {
      return null;
    }
    editorList.setFocusedEditor(editor);
    editorList.setFocused(true);
    stackList.setFocused(false);
    return editor;
  }

  public void focusTopFileInFileList() {
    if (editorList.isEmpty()) {
      return;
    }
    editorList.setFocusedIndex(0);
  }
}
