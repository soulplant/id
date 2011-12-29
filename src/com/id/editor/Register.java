package com.id.editor;

public class Register {
  private TextFragment contents = null;

  public void setContents(TextFragment contents) {
    this.contents = contents;
  }

  public TextFragment getContents() {
    return this.contents;
  }

  public boolean isEmpty() {
    return contents == null;
  }
}
