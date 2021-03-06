package com.home.teamnotifier.utils;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static java.util.stream.Collectors.toList;

public final class FutureUtils {
    private FutureUtils() {
        throw new AssertionError();
    }

    public static <V, T extends Collection<CompletableFuture<V>>> CompletableFuture<List<V>> allAsList(
            final T f,
            final Executor executor
    ) {
        return CompletableFuture
                .allOf(
                        f.toArray(new CompletableFuture[f.size()])
                )
                .thenApplyAsync(
                        ignored -> f.stream()
                                .map(CompletableFuture::join)
                                .collect(toList()),
                        executor
                );
    }
}
