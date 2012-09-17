package com.id.ui.app;

import java.awt.FontMetrics;

import javax.swing.JPanel;

import com.id.app.App;
import com.id.editor.Point;
import com.id.ui.Constants;

@SuppressWarnings("serial")
public class LinewisePanel extends JPanel {
  private final int fontWidthPx;
  private final int fontHeightPx;
  private int topLine = 0;
  private final int fontDescentPx;

  public LinewisePanel() {
    FontMetrics fontMetrics = getFontMetrics(App.FONT);
    fontHeightPx = fontMetrics.getHeight();
    fontWidthPx = fontMetrics.getWidths()['a'];
    fontDescentPx = fontMetrics.getDescent();
    setBackground(Constants.BG_COLOR);
  }

  public int getFontWidthPx() {
    return fontWidthPx;
  }

  public int getFontHeightPx() {
    return fontHeightPx;
  }

  public int getFontDescentPx() {
    return fontDescentPx;
  }

  public int getLinesWide() {
    return getWidth() / getFontWidthPx();
  }

  public int getLinesHigh() {
    return getHeight() / getFontHeightPx();
  }

  public int getTopLine() {
    return topLine;
  }

  public void setTopLine(int topLine) {
    this.topLine = topLine;
  }

  public boolean isPointVisible(Point point) {
    return point.getY() >= topLine && point.getY() < topLine + getLinesHigh();
  }
}
