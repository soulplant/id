package com.id.ui.app;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.id.app.App;

public class FullscreenSwapper implements KeyListener {

  private final AppFrame normalAppFrame;
  private final AppFrame fullscreenAppFrame;
  private AppFrame currentFrame = null;
  private Rectangle oldBounds = null;

  public FullscreenSwapper(AppFrame normalAppFrame, AppFrame fullscreenAppFrame) {
    this.normalAppFrame = normalAppFrame;
    this.fullscreenAppFrame = fullscreenAppFrame;
    normalAppFrame.addKeyListener(this);
    fullscreenAppFrame.addKeyListener(this);
    normalAppFrame.putOnScreen();
    currentFrame = normalAppFrame;
  }

  public void enterNormal() {
    if (App.isMacOS()) {
      if (oldBounds != null) {
        normalAppFrame.dispose();
        normalAppFrame.setUndecorated(false);
        normalAppFrame.setBounds(oldBounds);
        normalAppFrame.setVisible(true);
        oldBounds = null;
      }
    }
    fullscreenAppFrame.takeOffScreen();
    normalAppFrame.putOnScreen();
    currentFrame = normalAppFrame;
  }

  public void enterFullscreen() {
    if (App.isMacOS()) {
      // Unfortunately I don't think there's a way to make the window truly
      // fullscreen without disabling alt-tab on Mac, as alt-tabbing is kind of
      // important we need to do this elaborate dance, and then we still have
      // the mac toolbar hanging overhead.
      normalAppFrame.dispose();
      normalAppFrame.setUndecorated(true);
      oldBounds = normalAppFrame.getBounds();

      GraphicsConfiguration gc = GraphicsEnvironment
          .getLocalGraphicsEnvironment().getDefaultScreenDevice()
          .getDefaultConfiguration();
      Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
      screenRect.y = Toolkit.getDefaultToolkit().getScreenInsets(gc).top;
      screenRect.height -= screenRect.y;
      normalAppFrame.setBounds(screenRect);
      normalAppFrame.setVisible(true);
      return;
    }
    normalAppFrame.takeOffScreen();
    fullscreenAppFrame.putOnScreen();
    currentFrame = fullscreenAppFrame;
  }

  private void toggleFullscreen() {
    if (currentFrame == normalAppFrame && oldBounds == null) {
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
