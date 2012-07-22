package com.id.editor;

import com.id.app.ListModel;

public class Stack extends ListModel<Editor> {
  private String name = null;

  public Stack() {
    setFocusLatest(false);
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
