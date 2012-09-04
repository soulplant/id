package com.id.app;

import com.id.editor.Minibuffer;
import com.id.events.KeyStroke;
import com.id.events.KeyStrokeHandler;
import com.id.events.ShortcutTree;

public class MinibufferSubsystem implements Minibuffer.Listener, KeyStrokeHandler {
  private boolean isActive = false;
  private final Minibuffer minibuffer;
  private final CommandExecutor commandExecutor;
  private final FocusManager focusManager;

  private final ShortcutTree shortcuts = new ShortcutTree();

  public MinibufferSubsystem(Minibuffer minibuffer,
      CommandExecutor commandExecutor, FocusManager focusManager) {
    this.minibuffer = minibuffer;
    this.commandExecutor = commandExecutor;
    this.focusManager = focusManager;

    minibuffer.addListener(this);

    shortcuts.setShortcut(KeyStroke.fromString(":"), new ShortcutTree.Action() {
      @Override
      public void execute() {
        activateMinibuffer();
      }
    });
  }

  @Override
  public boolean handleKeyStroke(KeyStroke keyStroke) {
    if (isActive) {
      return minibuffer.handleKeyStroke(keyStroke);
    }
    return shortcuts.stepAndExecute(keyStroke);
  }

  // Minibuffer.Listener.
  @Override
  public void onDone() {
    executeMinibufferCommand();
  }

  @Override
  public void onTextChanged() {
    // Do nothing.
  }

  @Override
  public void onQuit() {
    deactivateMinibuffer();
  }

  private void activateMinibuffer() {
    isActive = true;
  }

  private void deactivateMinibuffer() {
    isActive = false;
    minibuffer.clear();
  }

  private void executeMinibufferCommand() {
    commandExecutor.execute(minibuffer.getText(), focusManager.getFocusedEditor());
    deactivateMinibuffer();
  }
}
