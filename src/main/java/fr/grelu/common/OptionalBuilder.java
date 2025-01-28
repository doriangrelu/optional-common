package fr.grelu.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

/**
 * Utility class for combining two optional values.
 * <p>
 * This class simplifies the handling of two optional values by providing methods
 * to merge them or perform complex operations using functional interfaces.
 * It also supports asynchronous handling with {@link CompletableFuture}.
 * </p>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Combines two optional values using functional interfaces.</li>
 *   <li>Supports synchronous and asynchronous merging.</li>
 *   <li>Provides a default {@link Result} wrapper for the merged values.</li>
 * </ul>
 *
 * @param <E> the type of the first optional value
 * @param <F> the type of the second optional value
 *
 * @author Dorian
 * @since 01.2025
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OptionalBuilder<E, F> {

    private final E first;
    private final F second;

    /**
     * Combines the two values using a complex combiner function.
     * <p>
     * The combiner function receives the two values if both are present. Otherwise,
     * the result is an empty {@link Optional}.
     * </p>
     *
     * @param combiner a function that combines the two values into an {@link Optional}
     * @param <T>      the type of the result
     *
     * @return an {@link Optional} containing the result of the combination, or empty if any value is null
     */
    public <T> Optional<T> complexMerge(final BiFunction<E, F, Optional<T>> combiner) {
        if (this.first != null && this.second != null) {
            return combiner.apply(this.first, this.second);
        }
        return Optional.empty();
    }

    /**
     * Combines the two values using a simple combiner function.
     * <p>
     * If both values are present, the combiner function is applied and its result
     * is wrapped in an {@link Optional}. Otherwise, an empty {@link Optional} is returned.
     * </p>
     *
     * @param combiner a function that combines the two values into a single result
     * @param <T>      the type of the result
     *
     * @return an {@link Optional} containing the result of the combination, or empty if any value is null
     */
    public <T> Optional<T> merge(final BiFunction<E, F, T> combiner) {
        return this.complexMerge((firstResult, secondResult) -> Optional.ofNullable(combiner.apply(firstResult, secondResult)));
    }

    /**
     * Combines the two values into a {@link Result} wrapper.
     * <p>
     * If both values are present, they are wrapped in a {@link Result} object. Otherwise, an empty {@link Optional} is returned.
     * </p>
     *
     * @return an {@link Optional} containing the {@link Result}, or empty if any value is null
     */
    public Optional<Result<E, F>> merge() {
        return this.merge(Result::new);
    }

    /**
     * Creates an {@link OptionalBuilder} from two {@link Optional} values.
     *
     * @param first  the first optional value
     * @param second the second optional value
     * @param <Y>    the type of the first value
     * @param <Z>    the type of the second value
     *
     * @return an {@link OptionalBuilder} containing the two values
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <Y, Z> OptionalBuilder<Y, Z> of(final Optional<Y> first, final Optional<Z> second) {
        return new OptionalBuilder<>(first.orElse(null), second.orElse(null));
    }

    /**
     * Creates an {@link OptionalBuilder} from two asynchronous {@link CompletableFuture} values.
     * <p>
     * This method waits for both futures to complete and retrieves their {@link Optional} values.
     * If either future fails, an exception is thrown.
     * </p>
     *
     * @param first  the first future containing an {@link Optional}
     * @param second the second future containing an {@link Optional}
     * @param <Y>    the type of the first value
     * @param <Z>    the type of the second value
     *
     * @return an {@link OptionalBuilder} containing the resolved values
     *
     * @throws RuntimeException if any of the futures fail
     */
    public static <Y, Z> OptionalBuilder<Y, Z> ofAsync(final CompletableFuture<Optional<Y>> first, final CompletableFuture<Optional<Z>> second) {
        try {
            CompletableFuture.allOf(first, second).join();
            return of(first.join(), second.join());
        } catch (final Exception e) {
            throw new IllegalStateException("Failed to resolve one or both futures", e);
        }
    }

    /**
     * Wrapper class for holding two merged values.
     *
     * @param first  the first value
     * @param second the second value
     * @param <X>    the type of the first value
     * @param <Y>    the type of the second value
     */
    public record Result<X, Y>(X first, Y second) {
    }

}
