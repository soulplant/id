package com.id.app;

import com.id.events.KeyStroke;
import com.id.events.KeyStrokeHandler;
import com.id.events.ShortcutTree;

public class NavigationKeyHandler<T> implements KeyStrokeHandler {
  private final ShortcutTree shortcuts = new ShortcutTree();
  private final Producer<ListModel<T>> listModelProducer;

  public NavigationKeyHandler(final Producer<ListModel<T>> listModelProducer) {
    this.listModelProducer = listModelProducer;
    shortcuts.setShortcut(KeyStroke.fromString("J"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        getListModel().moveDown();
      }
    });
    shortcuts.setShortcut(KeyStroke.fromString("<C-j>"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        getListModel().moveFocusedItemDown();
      }
    });
    shortcuts.setShortcut(KeyStroke.fromString("K"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        getListModel().moveUp();
      }
    });
    shortcuts.setShortcut(KeyStroke.fromString("<C-k>"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        getListModel().moveFocusedItemUp();
      }
    });
  }

  private ListModel<T> getListModel() {
    return listModelProducer.produce();
  }

  @Override
  public boolean handleKeyStroke(KeyStroke keyStroke) {
    return shortcuts.stepAndExecute(keyStroke);
  }
}
