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
  private Dimension originalSize;

  public AppFrame(AppPanel appPanel, boolean isFullscreen) {
    this(appPanel, isFullscreen, null);
  }

  public AppFrame(AppPanel appPanel, boolean isFullscreen, Dimension originalSize) {
    this.appPanel = appPanel;
    this.isFullscreen = isFullscreen;
    this.originalSize = originalSize;
    setTitle("id - " + System.getProperty("user.dir"));
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setFocusTraversalKeysEnabled(false);
    setUndecorated(isFullscreen);
    addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        pack();
      }
    });
  }

  public void putOnScreen() {
    getContentPane().add(appPanel);
    if (originalSize != null) {
      appPanel.setSize(originalSize);
    }
    pack();
    if (isFullscreen) {
      GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
      gd.setFullScreenWindow(this);
    }
    setVisible(true);
  }

  public void takeOffScreen() {
    GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    if (isFullscreen) {
      gd.setFullScreenWindow(null);
    }
    this.originalSize = appPanel.getSize();
    setVisible(false);
    getContentPane().removeAll();
  }

  @Override
  public void keyPressed(KeyEvent keyEvent) {
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
