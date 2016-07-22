package io.vulpine.util.cli.progress;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class ProgressStack implements Runnable
{
  protected boolean clearScreen = false;

  protected final Set< ProgressBar > bars;

  public ProgressStack()
  {
    this.bars = new LinkedHashSet <>();
  }

  public ProgressStack enableScreenClearing( final boolean clearScreen )
  {
    this.clearScreen = clearScreen;

    return this;
  }

  public ProgressStack addBar( final ProgressBar bar )
  {
    this.bars.add(bar);
    bar.printUpdate();

    return this;
  }

  private void update()
  {
    if (clearScreen) {
      System.out.print("\033[2J");
    } else {
      for ( final ProgressBar b : bars )
        b.printReset();
      System.out.print("\033[0J");
    }

    for ( final ProgressBar b : bars )
      b.printUpdate();

    try {
      Thread.sleep(50);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private int countActive()
  {
    int x = 0;

    for ( final ProgressBar b : bars )
      if (!b.isComplete())
        x++;

    return x;
  }

  @Override
  public void run ()
  {
    final ForkJoinPool fjp = new ForkJoinPool(1);
    final Runnable r = this::update;

    while (0 < countActive()) {
      fjp.awaitQuiescence(1, TimeUnit.SECONDS);
      fjp.execute(r);
    }

    fjp.shutdown();
  }
}
