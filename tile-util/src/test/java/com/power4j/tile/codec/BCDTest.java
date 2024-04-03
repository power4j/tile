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

package com.power4j.tile.codec;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BCDTest {

	@Test
	public void shouldEncodeStringEven() {
		assertArrayEquals(new byte[] { 0x31 }, BCD.encode("31"));
	}

	@Test
	public void shouldEncodeStringOdd() {
		assertArrayEquals(new byte[] { 0x02, 0x31 }, BCD.encode("231"));
	}

	@Test
	public void shouldEncodeStringZero() {
		assertArrayEquals(new byte[] { 0x00 }, BCD.encode("0"));
	}

	@Test
	public void shouldEncodeStringAndKeepExplicitZero() {
		assertArrayEquals(new byte[] { 0x00, 0x31 }, BCD.encode("031"));
	}

	@Test
	public void encodeStringShouldThrowExceptionForEmptyString() {
		Throwable thrown = assertThrows(IllegalArgumentException.class, () -> BCD.encode(""));
		assertEquals("Can only encode numerical strings", thrown.getMessage());
	}

	@Test
	public void encodeStringShouldThrowExceptionForNonDecimalString() {
		Throwable thrown = assertThrows(IllegalArgumentException.class, () -> BCD.encode("A"));
		assertEquals("Can only encode numerical strings", thrown.getMessage());
	}

	@Test
	public void shouldEncodeLongEven() {
		assertArrayEquals(new byte[] { 0x31 }, BCD.encode(31));
	}

	@Test
	public void shouldEncodeLongOdd() {
		assertArrayEquals(new byte[] { 0x02, 0x31 }, BCD.encode(231));
	}

	@Test
	public void shouldEncodeLongZero() {
		assertArrayEquals(new byte[] { 0x00 }, BCD.encode(0));
	}

	@Test
	public void encodeLongShouldThrowExceptionForNegative() {
		Throwable thrown = assertThrows(IllegalArgumentException.class, () -> BCD.encode(-1));
		assertEquals("Only non-negative values are supported", thrown.getMessage());
	}

	@Test
	public void shouldEncodeLongWithLengthEven() {
		assertArrayEquals(new byte[] { 0x00, 0x31 }, BCD.encode(31, 2));
	}

	@Test
	public void shouldEncodeLongWithLengthOdd() {
		assertArrayEquals(new byte[] { 0x00, 0x00, 0x02, 0x31 }, BCD.encode(231, 4));
	}

	@Test
	public void shouldEncodeLongWithLengthZero() {
		assertArrayEquals(new byte[] { 0x00, 0x00 }, BCD.encode(0, 2));
	}

	@Test
	public void encodeLongWithLengthShouldThrowExceptionForNegative() {
		Throwable thrown = assertThrows(IllegalArgumentException.class, () -> BCD.encode(-1, 2));
		assertEquals("Only non-negative values are supported", thrown.getMessage());
	}

	@Test
	public void encodeLongWithLengthShouldThrowExceptionIfLengthIsTooSmall() {
		Throwable thrown = assertThrows(IllegalArgumentException.class, () -> BCD.encode(100, 1));
		assertEquals("Value does not fit in byte array of length 1", thrown.getMessage());
	}

	@Test
	public void shouldEncodeBigIntegerSmall() {
		assertArrayEquals(new byte[] { 0x09, 0x22, 0x33, 0x72, 0x03, 0x68, 0x54, 0x77, 0x58, 0x07 },
				BCD.encode(BigInteger.valueOf(Long.MAX_VALUE)));
	}

	@Test
	public void shouldEncodeBigIntegerBig() {
		assertArrayEquals(new byte[] { 0x09, 0x22, 0x33, 0x72, 0x03, 0x68, 0x54, 0x77, 0x58, 0x08 },
				BCD.encode(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE)));
	}

	@Test
	public void shouldEncodeBigIntegerZero() {
		assertArrayEquals(new byte[] { 0x00 }, BCD.encode(BigInteger.ZERO));
	}

	@Test
	public void encodeBigIntegerShouldThrowExceptionForNegative() {
		Throwable thrown = assertThrows(IllegalArgumentException.class, () -> BCD.encode(BigInteger.ONE.negate()));
		assertEquals("Only non-negative values are supported", thrown.getMessage());
	}

	@Test
	public void shouldEncodeBigIntegerWithLengthSmall() {
		assertArrayEquals(new byte[] { 0x00, 0x09, 0x22, 0x33, 0x72, 0x03, 0x68, 0x54, 0x77, 0x58, 0x07 },
				BCD.encode(BigInteger.valueOf(Long.MAX_VALUE), 11));
	}

	@Test
	public void shouldEncodeBigIntegerWithLengthBig() {
		assertArrayEquals(new byte[] { 0x00, 0x09, 0x22, 0x33, 0x72, 0x03, 0x68, 0x54, 0x77, 0x58, 0x08 },
				BCD.encode(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE), 11));
	}

	@Test
	public void shouldEncodeBigIntegerWithLengthZero() {
		assertArrayEquals(new byte[] { 0x00, 0x00 }, BCD.encode(BigInteger.ZERO, 2));
	}

	@Test
	public void encodeBigIntegerWithLengthShouldThrowExceptionForNegative() {
		Throwable thrown = assertThrows(IllegalArgumentException.class, () -> BCD.encode(BigInteger.ONE.negate()));
		assertEquals("Only non-negative values are supported", thrown.getMessage());
	}

	@Test
	public void encodeBigIntegerWithLengthShouldThrowExceptionIfLengthIsTooSmall() {
		Throwable thrown = assertThrows(IllegalArgumentException.class, () -> BCD.encode(BigInteger.valueOf(100), 1));
		assertEquals("Value does not fit in byte array of length 1", thrown.getMessage());
	}

	@Test
	public void shouldDecodeEven() {
		assertEquals(31, BCD.decode(new byte[] { 0x31 }).intValue());
	}

	@Test
	public void shouldDecodeOdd() {
		assertEquals(231, BCD.decode(new byte[] { 0x02, 0x31 }).intValue());
	}

	@Test
	public void shouldDecodeZero() {
		assertEquals(0, BCD.decode(new byte[] { 0x0 }).intValue());
	}

	@Test
	public void decodeShouldThrowExceptionOnHighNibble() {
		Throwable thrown = assertThrows(IllegalArgumentException.class, () -> BCD.decode(new byte[] { -48 }));
		assertEquals("Illegal byte d0 at 0", thrown.getMessage());
	}

	@Test
	public void decodeShouldThrowExceptionOnLowNibble() {
		Throwable thrown = assertThrows(IllegalArgumentException.class, () -> BCD.decode(new byte[] { 0x0d }));
		assertEquals("Illegal byte 0d at 0", thrown.getMessage());
	}

	@Test
	public void shouldDecodeAsStringEven() {
		assertEquals("31", BCD.decodeAsString(new byte[] { 0x31 }, true));
	}

	@Test
	public void shouldDecodeAsStringOddStripLeadingZero() {
		assertEquals("231", BCD.decodeAsString(new byte[] { 0x02, 0x31 }, true));
	}

	@Test
	public void shouldDecodeAsStringOddKeepLeadingZero() {
		assertEquals("0231", BCD.decodeAsString(new byte[] { 0x02, 0x31 }, false));
	}

	@Test
	public void shouldDecodeAsStringZeroStripLeadingZero() {
		assertEquals("0", BCD.decodeAsString(new byte[] { 0x00 }, true));
	}

	@Test
	public void shouldDecodeAsStringZeroKeepLeadingZero() {
		assertEquals("00", BCD.decodeAsString(new byte[] { 0x00 }, false));
	}

	@Test
	public void decodeAsStringShouldThrowExceptionOnHighNibble() {
		Throwable thrown = assertThrows(IllegalArgumentException.class,
				() -> BCD.decodeAsString(new byte[] { -48 }, true));
		assertEquals("Illegal byte d0 at 0", thrown.getMessage());
	}

	@Test
	public void decodeAsStringShouldThrowExceptionOnLowNibble() {
		Throwable thrown = assertThrows(IllegalArgumentException.class,
				() -> BCD.decodeAsString(new byte[] { 0x0d }, true));
		assertEquals("Illegal byte 0d at 0", thrown.getMessage());
	}

}
