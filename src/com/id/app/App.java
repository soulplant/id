package com.id.app;

import java.awt.Dimension;
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

import com.id.editor.Editor;
import com.id.fuzzy.FuzzyFinder;
import com.id.git.GitRepository;
import com.id.git.Repository;
import com.id.platform.FileSystem;
import com.id.platform.RealFileSystem;
import com.id.ui.app.AppFrame;
import com.id.ui.app.AppPanel;
import com.id.ui.app.EditorSwapperView;
import com.id.ui.app.FileListView;
import com.id.ui.app.FullscreenSwapper;
import com.id.ui.app.FuzzyFinderPanel;
import com.id.ui.app.StackView;

public class App {
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
    final ListModel<Editor> editors = new ListModel<Editor>();
    final ListModel<Editor> stack = new ListModel<Editor>();
    FileSystem fileSystem = new RealFileSystem();
    BashShell shell = new BashShell(null);
    Repository repository = new GitRepository(shell);
    FuzzyFinder fuzzyFinder = new FuzzyFinder(fileSystem);
    fuzzyFinder.addCurrentPathToIndex();
    HighlightState highlightState = new HighlightState();
    final Controller controller = new Controller(editors, fileSystem,
        fuzzyFinder, repository, highlightState, stack);

    final EditorSwapperView spotlightView = new EditorSwapperView(editors);
    final FileListView fileListView = new FileListView(editors);
    StackView stackView = new StackView(stack);
    FuzzyFinderPanel fuzzyFinderPanel = new FuzzyFinderPanel(fuzzyFinder);
    final AppPanel panel = new AppPanel(fileListView, spotlightView, stackView,
        controller, fuzzyFinderPanel);

    editors.addListener(fileListView);
    editors.addListener(spotlightView);

    AppFrame fullscreenAppFrame = new AppFrame(panel, true);
    AppFrame normalAppFrame = new AppFrame(panel, false, new Dimension(1024, 768));

    new FullscreenSwapper(normalAppFrame, fullscreenAppFrame);
  }

  public static void configureFont(Graphics g) {
    g.setFont(App.FONT);
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
      URL fontUrl = classLoader.getResource("Inconsolata.ttf");
      System.out.println("fontUrl: " + fontUrl);
      InputStream fontInputStream = fontUrl.openStream();
      font = Font.createFont(Font.TRUETYPE_FONT, fontInputStream);
    } catch (FontFormatException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return font.deriveFont(12f);
  }
}
