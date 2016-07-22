package io.vulpine.util.cli.progress;

import java.util.Arrays;
import java.util.function.Supplier;

public class ProgressBar
{
  protected final static char opening = '[';
  protected final static char closing = ']';

  protected static char    glyph       = '=';
  protected static int     width       = 80;

  protected volatile boolean isComplete = false;

  protected final String header;

  protected final Supplier < Integer > maxValueCall;
  protected final Supplier < Integer > curValueCall;
  protected final Supplier < Boolean > isCompleteCall;
  protected final Supplier < String >  statusCall;

  protected String status = "";

  public ProgressBar(
    final String header,
    final Supplier < Integer > curValueCall,
    final Supplier < Integer > maxValueCall,
    final Supplier < Boolean > isCompleteCall,
    final Supplier < String > statusCall
  )
  {
    this.header = header;
    this.curValueCall = curValueCall;
    this.maxValueCall = maxValueCall;
    this.isCompleteCall = isCompleteCall;
    this.statusCall = statusCall;
  }

  public ProgressBar(
    final Supplier < Integer > c,
    final Supplier < Integer > m,
    final Supplier < Boolean > d,
    final Supplier < String > s
  )
  {
    this("", c, m, d, s);
  }

  public static void setGlyph( final char glyph )
  {
    ProgressBar.glyph = glyph;
  }

  public static void setWidth( final int width )
  {
    ProgressBar.width = width;
  }

  public synchronized boolean isComplete ()
  {
    isComplete = isCompleteCall.get();
    return isComplete;
  }

  public String buildUpdate ()
  {
    final StringBuilder sb;
    final int           cur, max, barWidth, fill;
    final char[]        bar;
    final boolean       done;
    final String        ln, status;

    status = statusCall.get();

    if (status != null) {
      this.status = status;
    }

    ln = System.lineSeparator();

    done = isComplete();

    sb  = new StringBuilder();
    max = maxValueCall.get();
    cur = done ? max : curValueCall.get();

    // For newline character subtract 1
    barWidth = calcBarWidth(max, width - 1);

    // For opening and closing characters subtract 2
    fill = calcFillWidth(cur, max, barWidth - 2);

    bar = new char[barWidth];

    Arrays.fill(bar, ' ');
    if (fill > 1) Arrays.fill(bar, 1, fill+1, glyph);

    bar[0] = opening;
    bar[barWidth - 1] = closing;

    return sb
      .append(header).append(ln)
      .append(bar).append(String.format(" %d / %d", cur, max)).append(ln)
      .append(this.status).append(ln)
      .toString();
  }

  protected static int calcBarWidth ( final int max, final int width )
  {
    return width - (String.valueOf(max).length() * 2 + 4);
  }

  protected static int calcFillWidth ( final double cur, final double max, final double width )
  {
    return (int) Math.ceil(cur / max * width);
  }

  protected void printReset ()
  {
    System.out.print("\033[3A");
    System.out.print("\033[2k");
  }

  protected void printUpdate ()
  {
    System.out.print(buildUpdate());
  }
}
