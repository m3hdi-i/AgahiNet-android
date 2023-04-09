package ir.m3hdi.agahinet.util

/*
 * Copyright 2022 Nicolas Haan.
 * Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE.md file.
 */
/**
 * A sealed class that encapsulates a successful outcome with a value of type [T]
 * or a failure with an arbitrary [Throwable] exception,
 * or a loading state
 * @see: This is a fork of Kotlin [kotlin.Result] class with an additional Loading state,
 * but preserving Result API
 */
public sealed class Resultx<out T> protected constructor() {

    /**
     * This type represent a successful outcome.
     * @param value The encapsulated successful value
     */
    public data class Success<out T>(public val value: T) : Resultx<T>() {
        override fun toString(): String = "Success($value)"
    }

    /**
     * This type represents a failed outcome.
     * @param exception The encapsulated exception value
     */
    public data class Failure(public val exception: Throwable) : Resultx<Nothing>() {
        override fun toString(): String = "Failure($exception)"
    }

    /**
     * This type represents a loading state.
     */
    public class Loading : Resultx<Nothing>() {
        override fun toString(): String = "Loading"
    }

    // discovery

    /**
     * Returns `true` if this instance represents a successful outcome.
     * In this case [isFailure] returns `false`.
     * In this case [isLoading] returns `false`.
     */
    public val isSuccess: Boolean get() = this is Success

    /**
     * Returns `true` if this instance represents a failed outcome.
     * In this case [isSuccess] returns `false`.
     * In this case [isLoading] returns `false`.
     */
    public val isFailure: Boolean get() = this is Failure

    /**
     * Returns `true` if this instance represents a loading outcome.
     * In this case [isSuccess] returns `false`.
     * In this case [isFailure] returns `false`.
     */
    public val isLoading: Boolean get() = this is Loading

    // value & exception retrieval

    /**
     * Returns the encapsulated value if this instance represents [success][Resultx.isSuccess] or `null`
     * if it is [failure][Resultx.isFailure] or [Resultx.Loading].
     */
    public inline fun getOrNull(): T? =
        when (this) {
            is Failure -> null
            is Loading -> null
            is Success -> value
        }

    /**
     * Returns the encapsulated [Throwable] exception if this instance represents [failure][isFailure] or `null`
     * if it is [success][isSuccess] or [loading][isLoading].
     */
    public fun exceptionOrNull(): Throwable? =
        when (this) {
            is Failure -> exception
            is Success -> null
            is Loading -> null
        }

    // companion with constructors

    /**
     * Companion object for [Resultx] class that contains its constructor functions
     * [success], [loading] and [failure].
     */
    public companion object {
        /**
         * Returns an instance that encapsulates the given [value] as successful value.
         */
        public inline fun <T> success(value: T): Resultx<T> = Success(value)

        /**
         * Returns an instance that encapsulates the given [Throwable] [exception] as failure.
         */
        public inline fun <T> failure(exception: Throwable): Resultx<T> = Failure(exception)

        /**
         * Returns an instance that represents the loading state.
         */
        public inline fun <T> loading(): Resultx<T> = Loading()
    }
}

private val loadingException = Throwable("No value available: Loading")


/**
 * Calls the specified function [block] with `this` value as its receiver and returns its encapsulated result if invocation was successful,
 * catching any [Throwable] exception that was thrown from the [block] function execution and encapsulating it as a failure.
 */
public inline fun <T, R> T.runCatchingL(block: T.() -> R): Resultx<R> {
    return try {
        Resultx.success(block())
    } catch (e: Throwable) {
        Resultx.failure(e)
    }
}

// -- extensions ---

/**
 * Returns the encapsulated value if this instance represents [success][Resultx.isSuccess] or throws the encapsulated [Throwable] exception
 * if it is [failure][Resultx.isFailure].
 *
 * This function is a shorthand for `getOrElse { throw it }` (see [getOrElse]).
 */
public fun <T> Resultx<T>.getOrThrow(): T {
    when (this) {
        is Resultx.Failure -> throw exception
        is Resultx.Loading -> throw loadingException
        is Resultx.Success -> return value
    }
}

/**
 * Returns the encapsulated value if this instance represents [success][Resultx.isSuccess] or the
 * result of [onFailure] function for the encapsulated [Throwable] exception if it is [failure][Resultx.isFailure]
 * or is [loading][Resultx.Loading].
 *
 * Note, that this function rethrows any [Throwable] exception thrown by [onFailure] function.
 *
 */
public fun <R, T : R> Resultx<T>.getOrElse(onFailure: (exception: Throwable) -> R): R {
    return when (this) {
        is Resultx.Failure -> onFailure(exception)
        is Resultx.Loading -> onFailure(loadingException)
        is Resultx.Success -> value
    }
}

/**
 * Returns the encapsulated value if this instance represents [success][Resultx.isSuccess] or the
 * [defaultValue] if it is [failure][Resultx.isFailure] or [loading][Resultx.isLoading].
 *
 * This function is a shorthand for `getOrElse { defaultValue }` (see [getOrElse]).
 */
public inline fun <R, T : R> Resultx<T>.getOrDefault(defaultValue: R): R {
    return when (this) {
        is Resultx.Failure -> defaultValue
        is Resultx.Loading -> defaultValue
        is Resultx.Success -> value
    }
}

/**
 * Returns the result of [onSuccess] for the encapsulated value if this instance represents [success][Resultx.isSuccess]
 * or the result of [onFailure] function for the encapsulated [Throwable] exception if it is [failure][Resultx.isFailure].
 * or the result of [onLoading] function if it is [loading][Resultx.isLoading].
 *
 * Note, that this function rethrows any [Throwable] exception thrown by [onSuccess] or by [onFailure] function.
 */
public inline fun <R, T> Resultx<T>.fold(
    onSuccess: (value: T) -> R,
    onFailure: (exception: Throwable) -> R,
    onLoading: () -> R,
): R {
    return when (this) {
        is Resultx.Failure -> onFailure(exception)
        is Resultx.Loading -> onLoading()
        is Resultx.Success -> onSuccess(value)
    }
}

// transformation

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated value
 * if this instance represents [success][Resultx.isSuccess] or the
 * original encapsulated [Throwable] exception if it is [failure][Resultx.isFailure] or [loading][Resultx.Loading].
 *
 * Note, that this function rethrows any [Throwable] exception thrown by [transform] function.
 * See [mapCatching] for an alternative that encapsulates exceptions.
 */
public inline fun <R, T> Resultx<T>.map(transform: (value: T) -> R): Resultx<R> {
    return when (this) {
        is Resultx.Failure -> this
        is Resultx.Success -> Resultx.success(transform(value))
        is Resultx.Loading -> this
    }
}

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated value
 * if this instance represents [success][Resultx.isSuccess] or the
 * original encapsulated [Throwable] exception if it is [failure][Resultx.isFailure]
 * or the loading state if it [loading][Resultx.Loading].
 *
 * This function catches any [Throwable] exception thrown by [transform] function and encapsulates it as a failure.
 * See [map] for an alternative that rethrows exceptions from `transform` function.
 */
public inline fun <R, T> Resultx<T>.mapCatching(transform: (value: T) -> R): Resultx<R> {
    return when (this) {
        is Resultx.Failure -> Resultx.Failure(exception)
        is Resultx.Success -> runCatchingL { transform(value) }
        is Resultx.Loading -> this
    }
}

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated [Throwable] exception
 * if this instance represents [failure][Resultx.isFailure] or the
 * original encapsulated value if it is [success][Resultx.isSuccess].
 *
 * @param recoverLoading Whether loading state calls transform or exposes untouched loading state
 * Note, that this function rethrows any [Throwable] exception thrown by [transform] function.
 * See [recoverCatching] for an alternative that encapsulates exceptions.
 */
public inline fun <R, T : R> Resultx<T>.recover(
    recoverLoading: Boolean = false,
    transform: (exception: Throwable) -> R,
): Resultx<R> {

    return when (this) {
        is Resultx.Success -> this
        is Resultx.Failure -> Resultx.success(transform(exception))
        is Resultx.Loading -> if (!recoverLoading) {
            this
        } else {
            Resultx.success(transform(Throwable("No value available: Loading")))
        }
    }
}

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated [Throwable] exception
 * if this instance represents [failure][Resultx.isFailure] or the
 * original encapsulated value if it is [success][Resultx.isSuccess].
 *
 * @param recoverLoading Whether loading state calls transform or exposes untouched loading state
 * This function catches any [Throwable] exception thrown by [transform] function and encapsulates it as a failure.
 * See [recover] for an alternative that rethrows exceptions.
 */
public inline fun <R, T : R> Resultx<T>.recoverCatching(
    recoverLoading: Boolean = false,
    transform: (exception: Throwable) -> R,
): Resultx<R> {
    return when (this) {
        is Resultx.Success -> this
        is Resultx.Failure -> runCatchingL { transform(exception) }
        is Resultx.Loading -> if (!recoverLoading) {
            this
        } else {
            runCatchingL { transform(Throwable("No value available: Loading")) }
        }
    }
}

// "peek" onto value/exception and pipe

/**
 * Performs the given [action] on the encapsulated [Throwable] exception if this instance represents [failure][Resultx.isFailure].
 * Returns the original `Resultx` unchanged.
 */
public inline fun <T> Resultx<T>.onFailure(action: (exception: Throwable) -> Unit): Resultx<T> {
    exceptionOrNull()?.let { action(it) }
    return this
}

/**
 * Performs the given [action] on the encapsulated value if this instance represents [success][Resultx.isSuccess].
 * Returns the original `Resultx` unchanged.
 */
public inline fun <T> Resultx<T>.onSuccess(action: (value: T) -> Unit): Resultx<T> {
    if (this is Resultx.Success) action(value)
    return this
}

/**
 * Performs the given [action] on the encapsulated value if this instance represents [success][Resultx.isLoading].
 * Returns the original `Resultx` unchanged.
 */
public inline fun <T> Resultx<T>.onLoading(action: () -> Unit): Resultx<T> {
    if (this is Resultx.Loading) action()
    return this
}

/**
 * Convert Kotlin [Result] to Resultx type
 */
public fun <T> Result<T>.toResultx(): Resultx<T> = fold(
    onFailure = {
        Resultx.failure(it)
    }, onSuccess = {
        Resultx.success(it)
    }
)

/**
 * Convert [Resultx] to Kotlin [Result]
 * if [Resultx] is [Resultx.Loading], null is returned
 */
public fun <T> Resultx<T>.toResult(): Result<T>? {
    return when (this) {
        is Resultx.Loading -> null
        is Resultx.Success -> Result.success(this.value)
        is Resultx.Failure -> Result.failure(this.exception)
    }
}

// -------------------