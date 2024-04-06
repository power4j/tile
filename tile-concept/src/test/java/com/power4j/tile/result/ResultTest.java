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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
class ResultTest {

	@Test
	void nonNullTest() {
		Assertions.assertThrows(NullPointerException.class, () -> Result.ok(null));
		Assertions.assertThrows(NullPointerException.class, () -> Result.error(null));
	}

	@Test
	void isError() {
		Result<?, ?> result = new Result<>(null, "");
		Assertions.assertTrue(result.isError());
		Assertions.assertFalse(result.isOk());

		result = new Result<>("", "");
		Assertions.assertTrue(result.isError());
		Assertions.assertFalse(result.isOk());

		result = new Result<>(null, null);
		Assertions.assertFalse(result.isError());
		Assertions.assertTrue(result.isOk());
	}

	@Test
	void isErrorAnd() {
		Result<?, Integer> result = Result.error(123);
		Assertions.assertTrue(result.isErrorAnd(i -> i == 123));
		Assertions.assertFalse(result.isErrorAnd(i -> i == 1));

		result = Result.ok(Optional.empty());
		Assertions.assertFalse(result.isErrorAnd(i -> true));
	}

	@Test
	void isOkAnd() {
		Result<Integer, Integer> result = Result.ok(123);
		Assertions.assertTrue(result.isOkAnd(i -> true));
		Assertions.assertFalse(result.isOkAnd(i -> false));

		result = Result.error(-1);
		Assertions.assertFalse(result.isOkAnd(i -> true));
	}

	@Test
	void unwrap() {
		Result<String, ?> result1 = Result.error("xx");
		Assertions.assertThrows(IllegalStateException.class, result1::unwrap);

		Result<String, ?> result2 = Result.ok("xx");
		Assertions.assertEquals("xx", result2.unwrap());
	}

	@Test
	void unwrapOr() {
		// 错误使用默认值
		Result<String, ?> result = Result.error("xx");
		Assertions.assertEquals("yy", result.unwrapOr("yy"));

		// 非错误使用原始值
		result = Result.ok("xx");
		Assertions.assertEquals("xx", result.unwrapOr("yy"));
	}

	@Test
	void unwrapOrElse() {
		// 错误使用默认值
		Result<String, ?> result = Result.error("xx");
		Assertions.assertEquals("yy", result.unwrapOrElse(() -> "yy"));

		// 非错误使用原始值
		result = Result.ok("xx");
		Assertions.assertEquals("xx", result.unwrapOrElse(() -> "yy"));
	}

	@Test
	void unwrapError() {
		Result<?, String> result1 = Result.ok("xx");
		Assertions.assertThrows(IllegalStateException.class, result1::unwrapError);

		Result<?, String> result2 = Result.error("xx");
		Assertions.assertEquals("xx", result2.unwrapError());

		Assertions.assertInstanceOf(RuntimeException.class,
				Result.error(new IllegalArgumentException()).unwrapError(RuntimeException.class));
		Assertions.assertThrows(ClassCastException.class, () -> Result.error("xx").unwrapError(Integer.class));

	}

	@Test
	void tryUnwrapError() {
		Result<?, String> result = Result.ok("xx");
		Assertions.assertFalse(result.tryUnwrapError().isPresent());

		result = Result.error("xx");
		Assertions.assertEquals("xx", result.tryUnwrapError().get());
	}

	@Test
	void map() {
		Result<Integer, String> result = Result.error("xx");
		Result<String, String> other = result.map(Object::toString);
		Assertions.assertTrue(other.isError());

		result = Result.ok(123);
		other = result.map(Object::toString);
		Assertions.assertEquals("123", other.unwrap());
	}

	@Test
	void mapError() {
		Result<?, Integer> result = Result.error(123);
		Result<?, String> other = result.mapError(Object::toString);
		Assertions.assertEquals("123", other.unwrapError());

		result = Result.ok(123);
		other = result.mapError(Object::toString);
		Assertions.assertTrue(other.isOk());
	}

	@Test
	void mapOr() {
		Assertions.assertEquals(1, Result.ok(1).mapOr(Function.identity(), 2));
		Assertions.assertEquals(2, Result.error("ERR").mapOr(Function.identity(), 2));
	}

	@Test
	void mapOrElse() {
		Assertions.assertEquals(1, Result.error("ERR").mapOrElse(Function.identity(), () -> 1));
		Assertions.assertEquals(123, Result.ok(123).mapOrElse(Function.identity(), () -> 1));
	}

	@Test
	void and() {
		Assertions.assertTrue(Result.error("xx").and(Result.ok("aaa")).isError());
		Assertions.assertTrue(Result.error("xx").and(Result.ok(123)).isError());
		Assertions.assertTrue(Result.ok(123).and(Result.error("xx")).isError());

		Assertions.assertEquals(123, Result.ok("aaa").and(Result.ok(123)).unwrap());
		Assertions.assertEquals("aaa", Result.ok(123).and(Result.ok("aaa")).unwrap());
	}

	@Test
	void andThen() {
		Assertions.assertTrue(Result.error("xx").andThen(o -> Result.ok("aaa")).isError());
		Assertions.assertTrue(Result.error("xx").andThen(o -> Result.ok(123)).isError());
		Assertions.assertTrue(Result.ok(123).andThen(o -> Result.error("xx")).isError());

		Assertions.assertEquals(123, Result.ok("aaa").andThen(o -> Result.ok(123)).unwrap());
		Assertions.assertEquals("aaa", Result.ok(123).andThen(o -> Result.ok("aaa")).unwrap());
	}

	@Test
	void or() {
		Assertions.assertTrue(Result.error("xx").or(Result.ok(1)).isOk());
		Assertions.assertTrue(Result.error("xx").or(Result.error(1)).isError());
		Assertions.assertTrue(Result.ok(1).or(Result.ok(1)).isOk());
		Assertions.assertTrue(Result.ok(1).or(Result.error(1)).isOk());

		Assertions.assertEquals(1, Result.error("xx").or(Result.ok(1)).unwrap());
		Assertions.assertEquals("x", Result.ok("x").or(Result.ok("y")).unwrap());
	}

	@Test
	void orElse() {
		Assertions.assertTrue(Result.error("xx").orElse(e -> Result.ok(1)).isOk());
		Assertions.assertTrue(Result.error("xx").orElse(e -> Result.error(1)).isError());
		Assertions.assertTrue(Result.ok(1).orElse(e -> Result.ok(1)).isOk());
		Assertions.assertTrue(Result.ok(1).orElse(e -> Result.error(1)).isOk());

		Assertions.assertEquals(1, Result.error("xx").or(Result.ok(1)).unwrap());
	}

	@Test
	void flatten() {

		Assertions.assertEquals(Result.ok(1), Result.flatten(Result.ok(Result.ok(1))));
		Assertions.assertEquals(Result.error(1), Result.flatten(Result.ok(Result.error(1))));
		Assertions.assertEquals(Result.ok(Result.ok(1)), Result.flatten(Result.ok(Result.ok(Result.ok(1)))));
		Assertions.assertEquals(Result.error(1), Result.flatten(Result.error(1)));

	}

	@Test
	void displayTest() {

		Result<?, ?> result1 = Result.error("failed");
		Assertions.assertEquals("Error(failed)", result1.display());

		result1 = Result.error(-1);
		Assertions.assertEquals("Error(-1)", result1.display());

		result1 = Result.ok(-1);
		Assertions.assertEquals("Ok", result1.display());

		result1 = Result.ok(Optional.empty());
		Assertions.assertEquals("Ok(empty)", result1.display());

		result1 = Result.ok(Optional.of(1));
		Assertions.assertEquals("Ok", result1.display());
	}

	@Test
	void unwrapOrThrow() {

		Assertions.assertThrows(RuntimeException.class,
				() -> Result.error(-1).unwrapOrThrow(e -> new RuntimeException()));
	}

	@Test
	void equalAndHash() {
		Result<?, ?> lh;
		Result<?, ?> rh;

		lh = Result.some(null);
		rh = Result.some(null);
		Assertions.assertEquals(lh, rh);
		Assertions.assertEquals(lh.hashCode(), rh.hashCode());

		lh = Result.ok(BigInteger.valueOf(100));
		rh = Result.ok(BigInteger.valueOf(100));
		Assertions.assertEquals(lh, rh);
		Assertions.assertEquals(lh.hashCode(), rh.hashCode());

		lh = Result.ok(1);
		rh = Result.ok(1L);
		Assertions.assertEquals(lh, rh);
		Assertions.assertEquals(lh.hashCode(), rh.hashCode());

		lh = Result.ok("1");
		rh = Result.ok(Integer.toString(1));
		Assertions.assertEquals(lh, rh);
		Assertions.assertEquals(lh.hashCode(), rh.hashCode());

		lh = Result.ok(Result.ok("1"));
		rh = Result.ok(Result.ok(Integer.toString(1)));
		Assertions.assertEquals(lh, rh);
		Assertions.assertEquals(lh.hashCode(), rh.hashCode());

		lh = Result.ok(1);
		rh = Result.ok(2);
		Assertions.assertNotEquals(lh, rh);
		Assertions.assertNotEquals(lh.hashCode(), rh.hashCode());

		lh = Result.ok(1L);
		rh = Result.ok(1);
		Assertions.assertNotEquals(lh, rh);

	}

}
