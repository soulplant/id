package com.id.ui.app;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class FullscreenSwapper implements KeyListener {

  private final AppFrame normalAppFrame;
  private final AppFrame fullscreenAppFrame;
  private AppFrame currentFrame = null;

  public FullscreenSwapper(AppFrame normalAppFrame, AppFrame fullscreenAppFrame) {
    this.normalAppFrame = normalAppFrame;
    this.fullscreenAppFrame = fullscreenAppFrame;
    normalAppFrame.addKeyListener(this);
    fullscreenAppFrame.addKeyListener(this);
    normalAppFrame.putOnScreen();
    currentFrame = normalAppFrame;
  }

  public void enterNormal() {
    fullscreenAppFrame.takeOffScreen();
    normalAppFrame.putOnScreen();
    currentFrame = normalAppFrame;
  }

  public void enterFullscreen() {
    normalAppFrame.takeOffScreen();
    fullscreenAppFrame.putOnScreen();
    currentFrame = fullscreenAppFrame;
  }

  private void toggleFullscreen() {
    if (currentFrame == normalAppFrame) {
      enterFullscreen();
    } else {
      enterNormal();
    }
  }

  @Override
  public void keyPressed(KeyEvent keyEvent) {
    if (isFullscreenKeyEvent(keyEvent)) {
      toggleFullscreen();
      return;
    }
    currentFrame.keyPressed(keyEvent);
  }

  private boolean isFullscreenKeyEvent(KeyEvent keyEvent) {
    if (keyEvent.isMetaDown() && keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
      return true;
    }
    return keyEvent.getKeyCode() == KeyEvent.VK_F11;
  }

  @Override
  public void keyReleased(KeyEvent keyEvent) {
    // Do nothing.
  }

  @Override
  public void keyTyped(KeyEvent keyEvent) {
    // Do nothing.
  }
}
