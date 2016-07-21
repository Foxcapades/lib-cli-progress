package io.vulpine.progress;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class Stack implements Runnable
{
  protected final Set< Bar > bars;

  public Stack ( final Set < Bar > bars )
  {
    this.bars = bars;
  }

  public Stack ()
  {
    this.bars = new LinkedHashSet <>();
  }

  public Stack addBar( final Bar bar )
  {
    this.bars.add(bar);
    bar.printUpdate();
    return this;
  }

  public void update()
  {
    bars.forEach(Bar::printReset);

    System.out.print("\033[0J");

    bars.forEach(Bar::printUpdate);

    try {
      Thread.sleep(50);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run ()
  {
    final ForkJoinPool fjp = new ForkJoinPool(1);

    while (0 < bars.stream().filter(b -> !b.isComplete()).count()) {
      fjp.awaitQuiescence(1, TimeUnit.SECONDS);
      fjp.execute(this::update);
    }

    fjp.shutdown();
  }
}
