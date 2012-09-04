package com.id.app;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.SwingUtilities;

import com.id.file.File;
import com.id.file.FilesRenameInterpreter;
import com.id.platform.FileSystem;
import com.id.platform.RealFileSystem;

public class App {
  public static final StaticSettings settings = StaticSettings.fromFile(".settings");
  public static final Font FONT = loadFont();

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        startApp();
      }
    });
  }

  private static void startApp() {
    FileSystem fileSystem = new RealFileSystem();
    BashShell shell = new BashShell(null);
    File file = getFilesFile(fileSystem, shell);
    AppParts appParts = new AppParts(fileSystem, shell, file);

    appParts.showSwingView();
    appParts.openFiles();
  }

  private static File getFilesFile(final FileSystem fileSystem,
                                   final Shell shell) {
    File file = fileSystem.getFileOrNewFile(".files");
    file.setSaveAction(new FilesRenameInterpreter(fileSystem, shell));
    return file;
  }

  public static boolean isMacOS() {
    return "Mac OS X".equals(System.getProperty("os.name"));
  }

  public static void configureFont(Graphics g) {
    g.setFont(App.FONT);
    // For some reason antialiasing is really slow on Mac, and it antialiases
    // the text even when it's off anyway.
    if (!isMacOS()) {
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
  }

  public static void dumpFonts() {
    GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
    Font[] fonts = e.getAllFonts(); // Get the fonts
    for (Font f : fonts) {
      System.out.println(f.getFontName());
    }
    System.exit(0);
  }

  public static Font loadFont() {
    Font font = null;
    try {
      ClassLoader classLoader = App.class.getClassLoader();
      URL fontUrl = classLoader.getResource(settings.getFontName());
      InputStream fontInputStream = fontUrl.openStream();
      font = Font.createFont(Font.TRUETYPE_FONT, fontInputStream);
    } catch (FontFormatException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return font.deriveFont(settings.getFontSize());
  }
}
