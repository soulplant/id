package com.id.app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.id.editor.Editor;
import com.id.file.File;
import com.id.file.FileView;

@SuppressWarnings("serial")
public class App extends JPanel {
  private static final String APP_NAME = "id";

  public App() {
    setPreferredSize(new Dimension(1024, 768));
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    g.drawRect(0, 0, (int) g.getClipBounds().getWidth() - 1, (int) g.getClipBounds().getHeight() - 1);
    g.drawString("hi there", 0, 30);
  }

  public static void runApp(final App app) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        createAndShowGui(app);
      }
    });
  }

  private static void createAndShowGui(App app) {
//    GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
//    Font[] fonts = e.getAllFonts(); // Get the fonts
//    for (Font f : fonts) {
//      System.out.println(f.getFontName());
//    }
//    System.exit(0);
    final JFrame frame = new JFrame(APP_NAME);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setFont(new Font("Monospaced.plain", Font.PLAIN, 12));

    File file = new File();
    for (int i = 0; i < 10; i++) {
      StringBuffer buffer = new StringBuffer();
      for (int j = 0; j < i; j++) {
        buffer.append('a');
      }
      file.insertLine(0, buffer.toString());
    }
    FileView fileView = new FileView(file, 0, 9);
    final Editor editor = new Editor(fileView, null);

    frame.getContentPane().add(new FileListPanel(), BorderLayout.LINE_START);
    final EditorPanel spotlight = new EditorPanel(editor);
    final EditorPanel stack = new EditorPanel(editor);
    frame.getContentPane().add(spotlight, BorderLayout.CENTER);
    frame.getContentPane().add(stack, BorderLayout.LINE_END);
    frame.pack();
    frame.setVisible(true);

    frame.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {
      }

      @Override
      public void keyReleased(KeyEvent e) {
      }

      @Override
      public void keyPressed(KeyEvent e) {
        // NOTE KeyEvent.getKeyCode() is only defined in keyPressed when control
        // is down.
        if (e.getKeyCode() == KeyEvent.VK_Q && e.isControlDown()) {
          System.exit(0);
        }

        if (editor.isInInsertMode() && isKeyCodeForLetter(e.getKeyCode())) {
          editor.onLetterTyped(e.getKeyChar());
          spotlight.repaint();
          return;
        }

        boolean redraw = true;
        switch (e.getKeyCode()) {
        case KeyEvent.VK_J:
          editor.down();
          break;
        case KeyEvent.VK_K:
          editor.up();
          break;
        case KeyEvent.VK_H:
          editor.left();
          break;
        case KeyEvent.VK_L:
          editor.right();
          break;
        case KeyEvent.VK_I:
          editor.insert();
          break;
        case KeyEvent.VK_U:
          editor.undo();
          break;
        case KeyEvent.VK_R:
          editor.redo();
          break;
        case KeyEvent.VK_ESCAPE:
          editor.escape();
          break;
        default:
          redraw = false;
          break;
        }
        if (redraw) {
          spotlight.repaint();
        }
      }

      private boolean isKeyCodeForLetter(int keyCode) {
        return ('a' <= keyCode && keyCode <= 'z') ||
            ('A' <= keyCode && keyCode <= 'Z') ||
            ('0' <= keyCode && keyCode <= '9') ||
            (" `~!@#$%^&*()-_=+[{]}\\|;:,<.>/?".indexOf(keyCode) != -1) ||
            keyCode == 39 /* single quote */ ||
            keyCode == 222 /* double quote */;
      }
    });
  }
  public static void main(String[] args) {
    runApp(new App());
  }
}
