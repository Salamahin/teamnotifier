package com.home.teamnotifier.utils;

import java.util.List;
import java.util.concurrent.*;
import static java.util.stream.Collectors.toList;

public final class FutureUtils {
  private FutureUtils() {
    throw new AssertionError();
  }

  public static <V, T extends List<CompletableFuture<V>>> CompletableFuture<List<V>> allAsList(
      final T f,
      final Executor executor) {
    return CompletableFuture.allOf(f.toArray(new CompletableFuture[f.size()]))
        .thenApplyAsync(ignored -> f.stream().map(CompletableFuture::join).collect(toList()),
            executor);
  }
}
