package io.vulpine.progress;

import java.util.function.Supplier;

import static java.util.Arrays.fill;

public class Bar
{
  protected final static char    glyph      = '=';
  protected final static char    opening    = '[';
  protected final static char    closing    = ']';
  protected final static int     width      = 80;
  protected volatile     boolean isComplete = false;

  protected final String header;

  protected final Supplier < Integer > maxValueCall;
  protected final Supplier < Integer > curValueCall;
  protected final String               reset;
  protected final Supplier < Boolean > isCompleteCall;
  protected final Supplier < String >  statusCall;

  protected String status = "";

  public Bar (
    final String header,
    final Supplier < Integer > curValueCall,
    final Supplier < Integer > maxValueCall,
    final Supplier < Boolean > isCompleteCall,
    final Supplier < String > statusCall
  )
  {
    final char[] res = new char[width];
    this.header = header;
    this.curValueCall = curValueCall;
    this.maxValueCall = maxValueCall;
    this.isCompleteCall = isCompleteCall;
    this.statusCall = statusCall;
    this.reset = new String(res);

    fill(res, '\b');
  }

  public Bar (
    final Supplier < Integer > c,
    final Supplier < Integer > m,
    final Supplier < Boolean > d,
    final Supplier < String > s
  )
  {
    this("", c, m, d, s);
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

    sb = new StringBuilder();
    max = maxValueCall.get();
    cur = done ? max : curValueCall.get();

    // For newline character subtract 1
    barWidth = calcBarWidth(max, width - 1);

    // For opening and closing characters subtract 2
    fill = calcFillWidth(cur, max, barWidth - 2);

    bar = new char[barWidth];

    fill(bar, ' ');
    if (fill > 1) fill(bar, 1, fill+1, glyph);

    bar[0] = opening;
    bar[barWidth - 1] = closing;

    return sb
      .append(header).append(ln)
      .append(bar).append(String.format(" %d / %d", cur, max)).append(ln)
      .append(this.status).append(ln)
      .toString();
  }

  public static int calcBarWidth ( final int max, final int width )
  {
    return width - (String.valueOf(max).length() * 2 + 4);
  }

  public static int calcFillWidth ( final double cur, final double max, final double width )
  {
    return (int) Math.ceil(cur / max * width);
  }

  public void printReset ()
  {
    System.out.print("\033[3A");
    System.out.print("\033[2k");
  }

  public void printUpdate ()
  {
    System.out.print(buildUpdate());
  }
}
