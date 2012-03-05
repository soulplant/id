package com.id.ui.app;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

import com.id.events.KeyStroke;
import com.id.events.KeyStrokeHandler;

@SuppressWarnings("serial")
public class AppPanel extends JPanel implements KeyListener {
  private final EditorSwapperView spotlightView;
  private final FileListView fileListView;
  private final Component stackView;
  private final KeyStrokeHandler handler;

  public AppPanel(FileListView fileListView, EditorSwapperView spotlightView, Component stackView, KeyStrokeHandler handler) {
    this.spotlightView = spotlightView;
    this.fileListView = fileListView;
    this.stackView = stackView;
    this.handler = handler;
    setLayout(new AppLayout());
    setFocusTraversalKeysEnabled(false);
    add(fileListView, "filelist");
    add(spotlightView, "spotlight");
    add(stackView, "stack");
  }

  @Override
  public void keyTyped(KeyEvent e) {
    // Do nothing.
  }

  @Override
  public void keyPressed(KeyEvent e) {
    // The editor doesn't care about shift being pressed.
    if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
      return;
    }
    KeyStroke keyStroke = KeyStroke.fromKeyEvent(e);
    logEventTranslationInfo(e, keyStroke);
    // NOTE KeyEvent.getKeyCode() is only defined in keyPressed when control
    // is down.
    if (keyStroke.getKeyChar() == 'q' && keyStroke.isControlDown()) {
      System.exit(0);
    }

    this.handler.handleKeyStroke(keyStroke);
    this.spotlightView.repaint();
    this.fileListView.repaint();
    this.stackView.repaint();
  }

  private void logEventTranslationInfo(KeyEvent event, KeyStroke keyStroke) {
//    System.out.println("event: " + event);
//    System.out.println("keystroke: " + keyStroke);
  }

  @Override
  public void keyReleased(KeyEvent e) {
    // Do nothing.
  }
}
