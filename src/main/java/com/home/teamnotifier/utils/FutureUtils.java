package com.home.teamnotifier.utils;

import com.google.common.collect.Lists;

import java.lang.reflect.Array;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public final class FutureUtils {
  private FutureUtils() {
    throw new AssertionError();
  }

  public static <V, T extends List<CompletableFuture<V>>> CompletableFuture<List<V>> allAsList(
      final T f,
      final Executor executor
  ) {
    final List<V> values = Lists.newCopyOnWriteArrayList();
    final List<CompletableFuture<Void>> resultFutures = IntStream.range(0, f.size())
        .mapToObj(i -> f.get(i).thenAcceptAsync(values::add, executor))
        .collect(toList());

    final CompletableFuture<Void>[] futuresArray = toGenericArray(resultFutures);

    return CompletableFuture
        .allOf(futuresArray)
        .thenApplyAsync(ignored -> values, executor);
  }

  @SuppressWarnings("unchecked")
  private static CompletableFuture<Void>[] toGenericArray(
      final List<CompletableFuture<Void>> futures) {
    /**
     * Hack to create generic array in order to use
     * {@link CompletableFuture#allOf(CompletableFuture[])}
     * which is use arrays
     */
    return (CompletableFuture<Void>[]) Array.newInstance(
        CompletableFuture.class,
        futures.size()
    );
  }
}
