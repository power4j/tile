/*
 * Copyright 2019-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.power4j.tile.result;

import com.power4j.tile.fmt.Display;
import org.springframework.lang.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Similar to Rust's {@code Result<T, E>} type ,A single Result value can only encapsulate
 * either a value of type T or a value of type E.<br/>
 * It is usually used to indicate success or error
 *
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
public class Result<T, E> implements Display {

	@Nullable
	private final T value;

	@Nullable
	private final E error;

	/**
	 * Provide a value
	 * @param value The value, can not be null,Use {@code Optional#ofNullable} to wrap
	 * null
	 * @return New Result object
	 * @param <T> Type parameter of value
	 * @throws NullPointerException if value is null
	 */
	public static <T, E> Result<T, E> ok(T value) {
		return new Result<>(Objects.requireNonNull(value), null);
	}

	/**
	 * Provide a value,Use {@code Optional#ofNullable} to wrap nullable value
	 * @param value The value null
	 * @return New Result object
	 * @param <T> Type parameter of value
	 * @throws NullPointerException if value is null
	 */
	public static <T, E> Result<Optional<T>, E> some(@Nullable T value) {
		return new Result<>(Optional.ofNullable(value), null);
	}

	/**
	 * Provide an error
	 * @param err The error object, can not be null
	 * @return New Result object
	 * @param <E> Type parameter of error
	 * @throws NullPointerException if err is null
	 */
	public static <T, E> Result<T, E> error(E err) {
		return new Result<>(null, Objects.requireNonNull(err));
	}

	Result(@Nullable T value, @Nullable E error) {
		this.value = value;
		this.error = error;
	}

	/**
	 * Test if it is an error <pre>
	 *     Result.error(1).isError()    -> true
	 *     Result.ok(null).isError()    -> false
	 * </pre>
	 * @return true if it is an error
	 */
	public boolean isError() {
		return error != null;
	}

	/**
	 * Invoke the predicate on the error if it is an error <pre>
	 *     Result.error(-1).isErrorAnd(err -> err == -1)   -> true
	 *     Result.error(-1).isErrorAnd(err -> err == 0 )   -> false
	 *     Result.ok(null).isErrorAnd(err -> true)         -> false, predicate not used
	 * </pre>
	 * @param predicate Predicate function
	 * @return true means it is an error and the predicate is true
	 */
	public boolean isErrorAnd(Predicate<? super E> predicate) {
		return isError() && predicate.test(error);
	}

	/**
	 * Test if it is NOT an error <pre>
	 *     Result.error(1).isOk()       -> false
	 *     Result.ok(null).isOk()       -> true
	 *     Result.ok(1).isOk()          -> true
	 * </pre>
	 * @return true means it is not an error
	 */
	public boolean isOk() {
		return !isError();
	}

	/**
	 * Invoke the predicate on the value if it is not an error <pre>
	 *     Result.ok(-1).isOkAnd(val -> val == -1)       -> true
	 *     Result.ok(-1).isOkAnd(val -> val == 0 )       -> false
	 *     Result.error(-1).isOkAnd(val -> true)         -> false, predicate not used
	 * </pre>
	 * @param predicate Predicate function
	 * @return true means it is not an error and the predicate is true for the value
	 */
	public boolean isOkAnd(Predicate<? super T> predicate) {
		return isOk() && predicate.test(value);
	}

	/**
	 * Unpack the value <pre>
	 *     Result.ok(-1).unwrap()         -> -1
	 *     Result.ok(null).unwrap()       -> null
	 *     Result.error(-1).unwrap()      -> IllegalResultException
	 * </pre>
	 * @return The value, never null
	 * @throws IllegalStateException could not provide value because it is an error
	 * @see Result#isError()
	 * @see Result#unwrapOrElse(Supplier)
	 */
	public T unwrap() {
		if (isError()) {
			assert error != null;
			String info = error instanceof Display ? ((Display) error).display() : error.toString();
			throw new IllegalStateException("Result is error:" + info);
		}
		assert value != null;
		return value;
	}

	/**
	 * Unpack the value,throw exception if it is an error <pre>
	 *     Result.ok(1).unwrapOrRise(() -> new RuntimeException())             -> 1
	 *     Result.error(-1).unwrapOrRise(() -> new RuntimeException())         -> RuntimeException
	 * </pre>
	 * @param func Convert function, used to convert error to exception
	 * @return Unpacked value
	 */
	public T unwrapOrThrow(Function<? super E, ? extends RuntimeException> func) {
		if (isError()) {
			assert error != null;
			throw func.apply(error);
		}
		assert value != null;
		return value;
	}

	/**
	 * Unpack the value,fallback to default value if it is an error <pre>
	 *     Result.ok(1).unwrapOr(2)             -> 1
	 *     Result.error(-1).unwrapOr(2)         -> 2
	 * </pre>
	 * @param defVal the default value, can not be null,Use {@code Optional#ofNullable} to
	 * wrap null
	 * @return Unpacked value or default value
	 * @see Result#isError()
	 */
	public T unwrapOr(T defVal) {
		if (isError()) {
			return Objects.requireNonNull(defVal);
		}
		assert value != null;
		return value;
	}

	/**
	 * Similar to {@link #unwrapOr(T)},but use supplier instead <pre>
	 *     Result.ok(1).unwrapOr(() -> 2)             -> 1
	 *     Result.error(-1).unwrapOr(() -> 2)         -> 2
	 * </pre>
	 * @param supplier the supplier, used if it is an error,The supplier returns the null
	 * value is not allowed.
	 * @return Unpacked value or returned from supplier
	 */
	public T unwrapOrElse(Supplier<T> supplier) {
		if (isError()) {
			return Objects.requireNonNull(supplier.get());
		}
		assert value != null;
		return value;
	}

	/**
	 * Unpack the error object <pre>
	 *     Result.error(-1).unwrapError()         -> -1
	 *     Result.ok(null).unwrapError()          -> IllegalResultException
	 * </pre>
	 * @return E
	 * @throws IllegalStateException could not provide error because it is not an error
	 * @see Result#tryUnwrapError()
	 */
	public E unwrapError() {
		return tryUnwrapError().orElseThrow(() -> new IllegalStateException("not an error"));
	}

	/**
	 * Unpack the error object and cast it to clazz <pre>
	 * @return E
	 * @throws IllegalStateException could not provide error because it is not an error
	 * @throws ClassCastException if the object is not null and is not assignable to the
	 * type F.
	 * @see Result#tryUnwrapError()
	 */
	public <F> F unwrapError(Class<F> clazz) {
		E err = unwrapError();
		return clazz.cast(err);
	}

	/**
	 * Try to unpack the error object <pre>
	 *     Result.error(-1).tryUnwrapError()         -> Optional(-1)
	 *     Result.ok(null).tryUnwrapError()          -> Optional(empty)
	 * </pre>
	 * @return Optional<E> if it is an error, Optional.empty() otherwise
	 * @see Result#isError()
	 *
	 */
	public Optional<E> tryUnwrapError() {
		if (isError()) {
			assert error != null;
			return Optional.of(error);
		}
		return Optional.empty();
	}

	/**
	 * Value convert, Convert {@code Result<T,E>} to {@code Result<U,E>} <pre>
	 *     Result.ok(1).map(val -> "val "+ val)         -> Result.ok("val 1")
	 *     Result.error(1).map(val -> "val "+ val)      -> Result.error(1)
	 * </pre>
	 * @param func convert function,used if it is not an error
	 * @return New {@code Result<U,E>} object
	 * @param <U> Type parameter of new value
	 */
	public <U> Result<U, E> map(Function<? super T, ? extends U> func) {
		if (isOk()) {
			return Result.ok(func.apply(value));
		}
		assert error != null;
		return Result.error(error);
	}

	/**
	 * Error convert, Convert {@code Result<T,E>} to {@code Result<T,F>} <pre>
	 *     Result.error(1).mapError(err -> "error "+ err)      -> Result.error("error 1")
	 *     Result.ok(1).mapError(err -> "error "+ err)         -> Result.ok(1)
	 * </pre>
	 * @param func convert function,used if it is an error.Provide null is not allowed
	 * @return New {@code Result<T,F>} object
	 * @param <F> Type parameter of new error
	 */
	public <F> Result<T, F> mapError(Function<? super E, ? extends F> func) {
		if (isOk()) {
			assert value != null;
			return Result.ok(value);
		}
		return Result.error(func.apply(error));
	}

	/**
	 * Value convert or fallback, Convert {@code Result<T,E>} to {@code Result<T,F>} if it
	 * is NOT an error,Otherwise fallback to default value <pre>
	 *     Result.ok(1).mapOr(val -> val + 1,-1)           -> 2
	 *     Result.ok(1).mapOr(val -> val + 10,-1)          -> 11
	 *     Result.error(1).mapOr(val -> val + 1,-1)        -> -1 ,fallback to default value
	 * </pre>
	 * @param func Convert function,used if it is not an error.Return null is not allowed
	 * @param defVal default value,used if it is an error.Null is not allowed
	 * @return Converted value or default value
	 *
	 */
	public <U> U mapOr(Function<? super T, ? extends U> func, U defVal) {
		if (isOk()) {
			return Objects.requireNonNull(func.apply(value));
		}
		return Objects.requireNonNull(defVal);
	}

	/**
	 * Similar to {@link Result#mapOr(Function, Object)},the default value is provided by
	 * supplier <pre>
	 *     Result.ok(1).mapOr(val -> val + 1, () -> -1)          -> 2
	 *     Result.ok(1).mapOr(val -> val + 10,() -> -1)          -> 11
	 *     Result.error(1).mapOr(val -> val + 1,() -> -1)        -> -1 ,fallback to supplier
	 * </pre>
	 * @param func Convert function,used if it is not an error.Return null is not allowed
	 * @param supplier default value provider,used if it is an error.Return null is not
	 * allowed
	 * @return Converted value or default value
	 * @see Result#isError()
	 *
	 */
	public <U> U mapOrElse(Function<? super T, ? extends U> func, Supplier<? extends U> supplier) {
		if (isOk()) {
			return Objects.requireNonNull(func.apply(value));
		}
		return Objects.requireNonNull(supplier.get());
	}

	/**
	 * Logical "and" chain operation, use second if it is NOT an error <pre>
	 *     Result.ok(1).and(Result.ok("1"))           -> Result.ok("1"), use second
	 *     Result.ok(1).and(Result.error("1"))        -> Result.error("1"), use second
	 *     Result.error(1).and(Result.ok("1"))        -> Result.error(1), original error
	 *     Result.error(1).and(Result.error("1"))     -> Result.error(1) original error
	 * </pre>
	 * @param other Another result object, used if it is NOT an error
	 * @return New {@code Result<U,E>} object
	 */
	public <U> Result<U, E> and(Result<U, E> other) {
		if (isError()) {
			assert error != null;
			return Result.error(error);
		}
		else {
			return other;
		}
	}

	/**
	 * Logical "and" chain operation, Similar to {@link Result#and(Result) }, Use compute
	 * function to convert {@code Result<T,E>} if it is NOT an error <pre>
	 *     Result.ok(1).andThen(val -> Result.ok("1"))           -> Result.ok("1")
	 *     Result.ok(1).andThen(val -> Result.error("1"))        -> Result.error("1")
	 *     Result.error(1).andThen(val -> Result.ok("1"))        -> Result.error(1), original error
	 *     Result.error(1).andThen(val -> Result.error("1"))     -> Result.error(1), original error
	 * </pre>
	 * @param func Compute function
	 * @return New {@code Result<U,E>} object
	 */
	public <U> Result<U, E> andThen(Function<? super T, ? extends Result<U, E>> func) {
		if (isError()) {
			assert error != null;
			return Result.error(error);
		}
		else {
			return func.apply(value);
		}
	}

	/**
	 * Logic "or" chain operation, use second if it is an error <pre>
	 *     Result.error(1).or(Result.ok("1"))           ->  Result.ok("1"), use second
	 *     Result.error(1).or(Result.error("1"))        ->  Result.error("1"), use second
	 *     Result.ok(1).or(Result.ok("1"))              ->  Result.ok(1), original ok
	 *     Result.ok(1).or(Result.error("1"))           ->  Result.ok(1) original ok
	 * </pre>
	 * @param other Another result object
	 * @return New {@code Result<T,F>} object
	 */
	public <F> Result<T, F> or(Result<T, F> other) {
		if (isError()) {
			return other;
		}
		else {
			assert value != null;
			return Result.ok(value);
		}
	}

	/**
	 * Logic "or" chain operation, Similar to {@link Result#or(Result) }, Use compute
	 * function to convert {@code Result<T,E>} if it is an error <pre>
	 *     Result.error(1).orElse(err -> Result.ok("1"))           ->  Result.ok("1")
	 *     Result.error(1).orElse(err -> Result.error(""))         ->  Result.error("")
	 *     Result.ok(1).orElse(err -> Result.ok("1"))              ->  Result.ok(1), original ok
	 *     Result.ok(1).orElse(err -> Result.error(""))            ->  Result.ok(1) original ok
	 * </pre>
	 * @param func Convert function
	 * @return New {@code Result<T,F>} object
	 */
	public <F> Result<T, F> orElse(Function<? super E, ? extends Result<T, F>> func) {
		if (isError()) {
			return func.apply(error);
		}
		else {
			assert value != null;
			return Result.ok(value);
		}
	}

	/**
	 * Extract the inner Result object <pre>
	 *     Result.flatten(Result.ok(Result.ok(1)))              ->  Result.ok(1)
	 *     Result.flatten(Result.ok(Result.error(1)))           ->  Result.error(1)
	 *     Result.flatten(Result.ok(Result.ok(Result.ok(1))))   ->  Result.ok(Result.ok(1))
	 *     Result.flatten(Result.error(1))                      ->  Result.error(1)
	 * </pre>
	 * @param result Nested result object
	 * @return Inner result object
	 * @param <T> Type parameter of the value
	 * @param <E> Type parameter of the error
	 */
	public static <T, E> Result<T, E> flatten(Result<? extends Result<T, E>, E> result) {
		return result.andThen(Function.identity());
	}

	@Override
	public String display() {
		if (isOk()) {
			if (value instanceof Optional && !((Optional<?>) value).isPresent()) {
				return "Ok(empty)";
			}
			return "Ok";
		}
		return "Error(" + error + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Result<?, ?> result = (Result<?, ?>) o;
		return Objects.equals(value, result.value) && Objects.equals(error, result.error);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value, error);
	}

}
