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

import java.math.BigInteger;

/**
 * Util for <a href="https://en.wikipedia.org/wiki/Binary-coded_decimal"> Binary-coded
 * decimal </a>
 * <p>
 * Most of the code comes from <a href="https://github.com/middagj/bcd-java">bcd-java</>
 * </p>
 *
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
public class BCD {

	private final static int MAX_NUMBER = 10;

	private final static int COMPRESS = 2;

	/**
	 * Encode string with numbers (decimal) to BCD encoded bytes (big endian) <pre>
	 *     encode("31")   -> [0x31]
	 *     encode("231")  -> [0x02, 0x31]
	 *     encode("A")    -> error
	 *     encode("")     -> error
	 * </pre>
	 * @param value Number that needs to be converted
	 * @return BCD encoded number
	 * @throws IllegalArgumentException if input is not a number
	 */
	public static byte[] encode(String value) {
		if (!isBcdString(value)) {
			throw new IllegalArgumentException("Can only encode numerical strings");
		}

		final byte[] bcd = new byte[(value.length() + 1) / COMPRESS];
		int i, j;
		if (value.length() % COMPRESS == 1) {
			bcd[0] = (byte) (value.codePointAt(0) & 0xF);
			i = 1;
			j = 1;
		}
		else {
			i = 0;
			j = 0;
		}
		for (; i < bcd.length; i++, j += COMPRESS) {
			bcd[i] = (byte) (((value.codePointAt(j) & 0xF) << 4) | (value.codePointAt(j + 1) & 0xF));
		}
		return bcd;
	}

	/**
	 * Encode value to BCD encoded bytes (big endian) <pre>
	 *     encode(BigInteger.ZERO)                -> [0x0]
	 *     BCD.encode(BigInteger.ONE.negate())    -> error
	 * </pre>
	 * @param value number
	 * @return BCD encoded number
	 * @throws IllegalArgumentException if input is negative
	 */
	public static byte[] encode(BigInteger value) {
		if (value.signum() == -1) {
			throw new IllegalArgumentException("Only non-negative values are supported");
		}
		if (value.bitLength() > 63) {
			return encode(value.toString());
		}
		else {
			return encode(value.longValue());
		}
	}

	/**
	 * Encode value to BCD encoded bytes (big endian) in a byte array of a specific length
	 * <pre>
	 *     encode(BigInteger.ZERO,2)                 -> [0x00, 0x00]
	 *     BCD.encode(BigInteger.valueOf(100), 1)    -> error
	 * </pre>
	 * @param value number
	 * @param length length of the byte array
	 * @return BCD encoded number
	 * @throws IllegalArgumentException if input is negative or does not fit in byte array
	 */
	public static byte[] encode(BigInteger value, int length) {
		if (value.signum() == -1) {
			throw new IllegalArgumentException("Only non-negative values are supported");
		}
		else if (value.bitLength() > length * Byte.SIZE) {
			throw new IllegalArgumentException("Value does not fit in byte array of length" + length);
		}
		if (value.bitLength() > 63) {
			return encode(String.format("%0" + (length * 2) + "d", value));
		}
		else {
			return encode(value.longValue(), length);
		}
	}

	/**
	 * Encode value to BCD encoded bytes (big endian) in a byte array of a specific length
	 * <pre>
	 *     BCD.encode(231, 4)    -> [0x00, 0x00, 0x02, 0x31]
	 *     BCD.encode(0, 2)      -> [0x00, 0x00]
	 * </pre>
	 * @param value number
	 * @param length length of the byte array
	 * @return BCD encoded number
	 * @throws IllegalArgumentException if input is negative or does not fit in byte array
	 */
	public static byte[] encode(long value, int length) {
		if (value < 0) {
			throw new IllegalArgumentException("Only non-negative values are supported");
		}
		else if (value == 0) {
			return new byte[length];
		}
		final byte[] bcd = new byte[length];

		for (int i = bcd.length - 1; i >= 0; i--) {
			int b = (int) (value % MAX_NUMBER);
			value /= 10;
			b |= (value % MAX_NUMBER) << 4;
			value /= MAX_NUMBER;
			bcd[i] = (byte) b;
		}
		if (value != 0) {
			throw new IllegalArgumentException("Value does not fit in byte array of length " + length);
		}

		return bcd;
	}

	/**
	 * Encode value to BCD encoded bytes (big endian) <pre>
	 *     BCD.encode(31)    -> [0x31]
	 *     BCD.encode(231)   -> [0x02, 0x31]
	 * </pre>
	 * @param value number
	 * @return BCD encoded number
	 * @throws IllegalArgumentException if input is negative
	 */
	public static byte[] encode(long value) {
		if (value < 0) {
			throw new IllegalArgumentException("Only non-negative values are supported");
		}
		else if (value == 0) {
			return new byte[1];
		}
		final int length = (int) Math.log10(value) + 1;
		return encode(value, (length + 1) / COMPRESS);
	}

	/**
	 * Decodes BCD encoded bytes to BigInteger <pre>
	 *     BCD.decode(new byte[] { 0x02, 0x31 }).intValue()   -> 231
	 *     BCD.decode(new byte[] { 0x31 }).intValue()         -> 31
	 *     BCD.decode(new byte[] { 0x0 }).intValue()          -> 0
	 *     BCD.decode(new byte[] {-48 })                      -> error
	 *     BCD.decode(new byte[] { 0x0d })                    -> error
	 * </pre>
	 * @param bcd BCD encoded bytes
	 * @return encoded number
	 * @throws IllegalArgumentException if an illegal byte is detected
	 */
	public static BigInteger decode(byte[] bcd) {
		BigInteger value = BigInteger.ZERO;
		for (int i = 0; i < bcd.length; i++) {
			final int high = ((int) bcd[i] & 0xFF) >> 4;
			final int low = (int) bcd[i] & 0xF;

			if (high > MAX_NUMBER || low > MAX_NUMBER) {
				throw new IllegalArgumentException(String.format("Illegal byte %x%x at %d", high, low, i));
			}

			value = value.multiply((BigInteger.TEN)).add(BigInteger.valueOf(high));
			value = value.multiply((BigInteger.TEN)).add(BigInteger.valueOf(low));
		}

		return value;
	}

	/**
	 * Decodes BCD encoded bytes directly to a decimal string <pre>
	 *     BCD.decodeAsString(new byte[] { 0x31 },true)            -> '31'
	 *     BCD.decodeAsString(new byte[] { 0x02, 0x31 }, true)     -> '231'
	 *     BCD.decodeAsString(new byte[] { 0x02, 0x31 }, false)    -> '0231'
	 *     BCD.decodeAsString(new byte[] { 0x00 }, false)          -> '00'
	 * </pre>
	 * @param bcd BCD encoded bytes
	 * @param stripLeadingZero strip leading zero if value is of odd length
	 * @return encoded number as String
	 * @throws IllegalArgumentException if an illegal byte is detected
	 */
	public static String decodeAsString(byte[] bcd, boolean stripLeadingZero) {
		final StringBuilder buf = new StringBuilder(bcd.length * 2);
		for (int i = 0; i < bcd.length; i++) {
			final int high = ((int) bcd[i] & 0xFF) >> 4;
			final int low = (int) bcd[i] & 0xF;

			if (high > MAX_NUMBER || low > MAX_NUMBER) {
				throw new IllegalArgumentException(String.format("Illegal byte %x%x at %d", high, low, i));
			}
			buf.append((char) (0x30 | high));
			buf.append((char) (0x30 | low));
		}
		return stripLeadingZero && buf.charAt(0) == '0' ? buf.substring(1) : buf.toString();
	}

	/**
	 * Check if input value is BCD string
	 * @param cs input
	 * @return false if not a BCD string
	 */
	public static boolean isBcdString(CharSequence cs) {
		if (cs.length() == 0) {
			return false;
		}
		return !cs.chars().filter(c -> !isBcdChar(c)).findFirst().isPresent();
	}

	/**
	 * Check if input value is BCD character
	 * @param ch input
	 * @return false if not a BCD string
	 */
	public static boolean isBcdChar(int ch) {
		return '0' <= ch && ch <= '9';
	}

}
