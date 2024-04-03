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

package com.power4j.tile.ffi;

import com.power4j.tile.error.ErrValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CStrTest {

	@Test
	void makeBuffer() {
		Assertions.assertArrayEquals(new byte[] { 49, 50, 51, 0 }, CStr.makeBuffer("123").getBytes());
		Assertions.assertArrayEquals(new byte[] { 49, 50, 51, 0 }, CStr.makeBuffer("123", 4).getBytes());
		Assertions.assertArrayEquals(new byte[] { 49, 50, 0 }, CStr.makeBuffer("123", 3).getBytes());
		Assertions.assertArrayEquals(new byte[] { 49, 50, 51, 0, 0 }, CStr.makeBuffer("123", 5).getBytes());
	}

	@Test
	void tryReadUntilNull() {
		Assertions.assertEquals("123", CStr.refOf(new byte[] { 49, 50, 51, 0, 0 }).tryReadUntilNull(0, 5).unwrap());
		Assertions.assertEquals("", CStr.refOf(new byte[] { 49, 50, 51, 0, 0 }).tryReadUntilNull(3, -1).unwrap());
		Assertions.assertTrue(CStr.refOf(new byte[] { 49, 50, 51 }).tryReadUntilNull(0, -1).isError());
	}

	@Test
	void readUntilNull() {
		Assertions.assertEquals("123", CStr.refOf(new byte[] { 49, 50, 51, 0, 0 }).readUntilNull(0, 5));
		Assertions.assertEquals("", CStr.refOf(new byte[] { 49, 50, 51, 0, 0 }).readUntilNull(3, -1));
		Assertions.assertThrows(InvalidCStrBytesException.class,
				() -> CStr.refOf(new byte[] { 49, 50, 51 }).readUntilNull(0, -1));
	}

	@Test
	@SuppressWarnings("unchecked")
	void tryReadWithNullCheck() {
		Assertions.assertEquals("123", CStr.refOf(new byte[] { 49, 50, 51, 0 }).tryReadWithNullCheck(0, -1).unwrap());
		Assertions.assertEquals("23", CStr.refOf(new byte[] { 49, 50, 51, 0 }).tryReadWithNullCheck(1, -1).unwrap());
		Assertions.assertEquals("", CStr.refOf(new byte[] { 49, 50, 51, 0 }).tryReadWithNullCheck(3, -1).unwrap());

		Assertions.assertInstanceOf(CStr.InteriorNul.class,
				CStr.refOf(new byte[] { 49, 50, 0, 51, 0 })
					.tryReadWithNullCheck(0, -1)
					.mapError(e -> (ErrValue<CStr.InteriorNul>) e)
					.unwrapError()
					.value());
		Assertions.assertInstanceOf(CStr.InteriorNul.class,
				CStr.refOf(new byte[] { 49, 50, 51, 0, 0 })
					.tryReadWithNullCheck(0, -1)
					.mapError(e -> (ErrValue<CStr.InteriorNul>) e)
					.unwrapError()
					.value());
		Assertions.assertInstanceOf(CStr.NotNulTerminated.class,
				CStr.refOf(new byte[] { 49, 50, 51 })
					.tryReadWithNullCheck(0, -1)
					.mapError(e -> (ErrValue<CStr.NotNulTerminated>) e)
					.unwrapError()
					.value());
	}

	@Test
	void readWithNullCheck() {
		Assertions.assertEquals("123", CStr.refOf(new byte[] { 49, 50, 51, 0 }).readWithNullCheck(0, -1));
		Assertions.assertEquals("23", CStr.refOf(new byte[] { 49, 50, 51, 0 }).readWithNullCheck(1, -1));
		Assertions.assertEquals("", CStr.refOf(new byte[] { 49, 50, 51, 0 }).readWithNullCheck(3, -1));

		Assertions.assertThrows(InvalidCStrBytesException.class,
				() -> CStr.refOf(new byte[] { 49, 50, 0, 51, 0 }).readWithNullCheck(0, -1));
		Assertions.assertThrows(InvalidCStrBytesException.class,
				() -> CStr.refOf(new byte[] { 49, 50, 51, 0, 0 }).readWithNullCheck(0, -1));
		Assertions.assertThrows(InvalidCStrBytesException.class,
				() -> CStr.refOf(new byte[] { 49, 50, 51 }).readWithNullCheck(0, -1));
	}

	@Test
	void readUntilEnd() {
		Assertions.assertEquals("23", CStr.refOf(new byte[] { 49, 50, 51, 0, 0 }).readUntilEnd(1, -1));
		Assertions.assertEquals("23", CStr.refOf(new byte[] { 49, 50, 51 }).readUntilEnd(1, -1));
		Assertions.assertEquals("", CStr.refOf(new byte[0]).readUntilEnd(0, -1));
	}

}
