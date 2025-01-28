package fr.grelu.common;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Tests for {@link OptionalBuilder}.
 * <p>
 * Validates the behavior of merging and handling {@link Optional} values and asynchronous {@link CompletableFuture}.
 * </p>
 */
class OptionalBuilderTest {

    @Test
    void shouldMergeTwoPresentOptionals() {
        final Optional<Integer> first = Optional.of(10);
        final Optional<Integer> second = Optional.of(20);

        final Optional<Integer> result = OptionalBuilder.of(first, second)
                                                        .merge(Integer::sum);

        Assertions.assertThat(result).isPresent()
                  .contains(30);
    }

    @Test
    void shouldReturnResultWrapperWhenMergingTwoPresentOptionals() {
        final Optional<Integer> first = Optional.of(10);
        final Optional<String> second = Optional.of("Hello");

        final Optional<OptionalBuilder.Result<Integer, String>> result = OptionalBuilder.of(first, second).merge();

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().first()).isEqualTo(10);
        Assertions.assertThat(result.get().second()).isEqualTo("Hello");
    }

    @Test
    void shouldReturnEmptyResultWhenAnyOptionalIsMissing() {
        final Optional<Integer> first = Optional.of(10);
        final Optional<String> second = Optional.empty();

        final Optional<OptionalBuilder.Result<Integer, String>> result = OptionalBuilder.of(first, second).merge();

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void shouldHandleComplexMerge() {
        final Optional<Integer> first = Optional.of(5);
        final Optional<Integer> second = Optional.of(10);

        final Optional<Integer> result = OptionalBuilder.of(first, second)
                                                        .complexMerge((a, b) -> (a + b) > 10 ? Optional.of(a + b) : Optional.empty());

        Assertions.assertThat(result).isPresent()
                  .contains(15);
    }

    @Test
    void shouldHandleAsyncOptionals() {
        final CompletableFuture<Optional<Integer>> future1 = CompletableFuture.completedFuture(Optional.of(10));
        final CompletableFuture<Optional<Integer>> future2 = CompletableFuture.completedFuture(Optional.of(20));

        final Optional<Integer> result = OptionalBuilder.ofAsync(future1, future2)
                                                        .merge(Integer::sum);

        Assertions.assertThat(result).isPresent()
                  .contains(30);
    }

    @Test
    void shouldReturnEmptyForAsyncOptionalsWhenMissingValues() {
        final CompletableFuture<Optional<Integer>> future1 = CompletableFuture.completedFuture(Optional.empty());
        final CompletableFuture<Optional<Integer>> future2 = CompletableFuture.completedFuture(Optional.of(20));

        final Optional<Integer> result = OptionalBuilder.ofAsync(future1, future2)
                                                        .merge(Integer::sum);

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void shouldThrowExceptionForFailedAsyncOptionals() {
        final CompletableFuture<Optional<Integer>> future1 = CompletableFuture.completedFuture(Optional.of(10));
        final CompletableFuture<Optional<Integer>> future2 = CompletableFuture.failedFuture(new RuntimeException("Future failed"));

        Assertions.assertThatThrownBy(() -> OptionalBuilder.ofAsync(future1, future2))
                  .isInstanceOf(IllegalStateException.class)
                  .hasMessageContaining("Failed to resolve one or both futures");
    }
}
