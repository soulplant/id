package com.id.ui.app;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class AppFrame extends JFrame implements KeyListener {
  private final AppPanel appPanel;
  private final boolean isFullscreen;
  private final Dimension originalSize;

  public AppFrame(AppPanel appPanel, boolean isFullscreen, Dimension originalSize) {
    this.appPanel = appPanel;
    this.isFullscreen = isFullscreen;
    this.originalSize = originalSize;
    appPanel.setSize(originalSize);
    getContentPane().add(appPanel);
    setTitle("id");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    if (isFullscreen) {
      setUndecorated(true);
      GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
      gd.setFullScreenWindow(this);
    }
    addKeyListener(this);
    addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        pack();
      }
    });
    pack();
    setVisible(true);
  }

  @Override
  public void keyPressed(KeyEvent keyEvent) {
    Dimension size = isFullscreen ? originalSize : getSize();
    if (keyEvent.getKeyCode() == KeyEvent.VK_F11) {
      setVisible(false);
      new AppFrame(appPanel, !isFullscreen, size);
    }
    appPanel.keyPressed(keyEvent);
    pack();
  }

  @Override
  public void keyReleased(KeyEvent arg0) {
    // Do nothing.
  }

  @Override
  public void keyTyped(KeyEvent arg0) {
    // Do nothing.
  }
}
