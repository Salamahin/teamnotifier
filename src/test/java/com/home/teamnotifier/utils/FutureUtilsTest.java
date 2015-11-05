package com.home.teamnotifier.utils;

import com.google.common.collect.Lists;
import org.junit.Test;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;
import static org.assertj.core.api.Assertions.assertThat;

public class FutureUtilsTest {

  private final Executor executor = Executors.newCachedThreadPool();

  @Test
  public void testAllAsList()
  throws Exception {
    final CompletableFuture<Integer> f1 = CompletableFuture
        .supplyAsync(dummyTask(1, 1000), executor);
    final CompletableFuture<Integer> f2 = CompletableFuture
        .supplyAsync(dummyTask(2, 2000), executor);

    List<CompletableFuture<Integer>> futures = Lists.newArrayList(f1, f2);

    final List<Integer> integers = FutureUtils.allAsList(futures, executor).join();

    assertThat(integers)
        .contains(1)
        .contains(2)
        .hasSize(futures.size());
  }

  private Supplier<Integer> dummyTask(final int returnVal, final int delayMs) {
    return () -> {
      try {
        TimeUnit.MILLISECONDS.sleep(delayMs);
      } catch (InterruptedException e) {
        throw new IllegalStateException(e);
      }
      return returnVal;
    };
  }
}