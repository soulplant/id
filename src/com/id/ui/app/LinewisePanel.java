package com.id.ui.app;

import javax.swing.JPanel;

import com.id.app.App;
import com.id.ui.Constants;

@SuppressWarnings("serial")
public class LinewisePanel extends JPanel {
  private final int fontWidthPx;
  private final int fontHeightPx;

  public LinewisePanel() {
    fontHeightPx = getFontMetrics(App.FONT).getHeight();
    fontWidthPx = getFontMetrics(App.FONT).getWidths()['a'];
    setBackground(Constants.BG);
  }

  public int getFontWidthPx() {
    return fontWidthPx;
  }

  public int getFontHeightPx() {
    return fontHeightPx;
  }
}
