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

package com.power4j.tile.crypto.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
class SliceTest {

	@Test
	void range() {
		Slice bytes = Slice.range(new byte[] { 1, 2, 3 }, 1, 2);
		assertArrayEquals(new byte[] { 2, 3 }, bytes.unwrap());
	}

	@Test
	void remaining() {
		Slice bytes = Slice.remaining(new byte[] { 1, 2, 3 }, 1);
		assertArrayEquals(new byte[] { 2, 3 }, bytes.unwrap());
	}

	@Test
	void copyOf() {
		Slice bytes = Slice.copyOf(new byte[] { 1, 2, 3 });
		assertArrayEquals(new byte[] { 1, 2, 3 }, bytes.unwrap());
	}

	@Test
	void copyOfRange() {
		Slice bytes = Slice.copyOfRange(new byte[] { 1, 2, 3 }, 1, 2);
		assertArrayEquals(new byte[] { 2, 3 }, bytes.unwrap());
	}

	@Test
	void copyOfRemaining() {
		Slice bytes = Slice.copyOfRemaining(new byte[] { 1, 2, 3 }, 1);
		assertArrayEquals(new byte[] { 2, 3 }, bytes.unwrap());
	}

	@Test
	void unwrap() {
		Slice bytes = Slice.wrap(new byte[] { 1, 2, 3 });
		assertArrayEquals(new byte[] { 1, 2, 3 }, bytes.unwrap());
	}

	@Test
	void sub() {
		Slice bytes = Slice.wrap(new byte[] { 1, 2, 3 }).sub(1, 2);
		assertArrayEquals(new byte[] { 2, 3 }, bytes.unwrap());
	}

	@Test
	void dataEquals() {
		Slice one = Slice.wrap(new byte[] { 1, 2, 3 });
		Slice two = Slice.wrap(new byte[] { 1, 2, 3 });
		assertTrue(one.dataEquals(two));
		assertTrue(one.dataEquals(new byte[] { 1, 2, 3 }));
		assertFalse(one.dataEquals(new byte[] { 1, 2, 4 }));
	}

	@Test
	void shouldThrowIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> Slice.range(new byte[1], -1, 0));
		assertThrows(IllegalArgumentException.class, () -> Slice.range(new byte[1], 0, -1));
		assertThrows(IllegalArgumentException.class, () -> Slice.range(new byte[1], 1, 1));
	}

}
