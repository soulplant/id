package com.id.app;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

import com.id.events.KeyStroke;
import com.id.events.KeyStrokeHandler;
import com.id.ui.app.AppLayout;
import com.id.ui.app.EditorSwapperView;
import com.id.ui.app.FileListView;

@SuppressWarnings("serial")
public class AppFrame extends JFrame implements KeyListener {
  private final EditorSwapperView spotlightView;
  private final FileListView fileListView;
  private final Component stackView;
  private final KeyStrokeHandler handler;

  public AppFrame(FileListView fileListView, EditorSwapperView spotlightView, Component stackView, KeyStrokeHandler handler) {
    this.spotlightView = spotlightView;
    this.fileListView = fileListView;
    this.stackView = stackView;
    this.handler = handler;
    setTitle("id");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new AppLayout());
    getContentPane().add(fileListView, "filelist");
    getContentPane().add(spotlightView, "spotlight");
    getContentPane().add(stackView, "stack");
    setSize(new Dimension(1024, 768));
    addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        pack();
      }
    });
    addKeyListener(this);
  }

  @Override
  public void keyTyped(KeyEvent e) {
    // Do nothing.
  }

  @Override
  public void keyPressed(KeyEvent e) {
    // NOTE KeyEvent.getKeyCode() is only defined in keyPressed when control
    // is down.
    if (e.getKeyCode() == KeyEvent.VK_Q && e.isControlDown()) {
      System.exit(0);
    }
    AppFrame.this.handler.handleKeyStroke(KeyStroke.fromKeyEvent(e));
    AppFrame.this.spotlightView.repaint();
    AppFrame.this.fileListView.repaint();
    AppFrame.this.stackView.repaint();
    pack();
  }

  @Override
  public void keyReleased(KeyEvent e) {
    // Do nothing.
  }
}
