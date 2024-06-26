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

import lombok.experimental.UtilityClass;

import java.nio.ByteBuffer;

/**
 * Converts hexadecimal Strings. <br/>
 * Codes copied from {@code org.apache.commons.codec.binary.Hex}
 *
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
@UtilityClass
public class Hex {

	/**
	 * Used to build output as hex.
	 */
	private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
			'e', 'f' };

	/**
	 * Used to build output as hex.
	 */
	private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
			'E', 'F' };

	/**
	 * Converts an array of characters representing hexadecimal values into an array of
	 * bytes of those same values. The returned array will be half the length of the
	 * passed array, as it takes two characters to represent any given byte. An exception
	 * is thrown if the passed char array has an odd number of elements.
	 * @param data An array of characters containing hexadecimal digits
	 * @return A byte array containing binary data decoded from the supplied char array.
	 * @throws DecodeException Thrown if an odd number of characters or illegal characters
	 * are supplied
	 */
	public static byte[] decodeHex(final char[] data) throws DecodeException {
		final byte[] out = new byte[data.length >> 1];
		decodeHex(data, out, 0);
		return out;
	}

	/**
	 * Converts an array of characters representing hexadecimal values into an array of
	 * bytes of those same values. The returned array will be half the length of the
	 * passed array, as it takes two characters to represent any given byte. An exception
	 * is thrown if the passed char array has an odd number of elements.
	 * @param data An array of characters containing hexadecimal digits
	 * @param out A byte array to contain the binary data decoded from the supplied char
	 * array.
	 * @param outOffset The position within {@code out} to start writing the decoded
	 * bytes.
	 * @return the number of bytes written to {@code out}.
	 * @throws DecodeException Thrown if an odd number of characters or illegal characters
	 * are supplied
	 */
	public static int decodeHex(final char[] data, final byte[] out, final int outOffset) throws DecodeException {
		final int len = data.length;

		if ((len & 0x01) != 0) {
			throw new DecodeException("Odd number of characters.");
		}

		final int outLen = len >> 1;
		if (out.length - outOffset < outLen) {
			throw new DecodeException("Output array is not large enough to accommodate decoded data.");
		}

		// two characters form the hex value.
		for (int i = outOffset, j = 0; j < len; i++) {
			int f = toDigit(data[j], j) << 4;
			j++;
			f = f | toDigit(data[j], j);
			j++;
			out[i] = (byte) (f & 0xFF);
		}

		return outLen;
	}

	/**
	 * Converts an array of bytes into an array of characters representing the hexadecimal
	 * values of each byte in order. The returned array will be double the length of the
	 * passed array, as it takes two characters to represent any given byte.
	 * @param data a byte[] to convert to hex characters
	 * @return A char[] containing lower-case hexadecimal characters
	 */
	public static char[] encodeHex(final byte[] data) {
		return encodeHex(data, true);
	}

	/**
	 * Converts an array of bytes into an array of characters representing the hexadecimal
	 * values of each byte in order. The returned array will be double the length of the
	 * passed array, as it takes two characters to represent any given byte.
	 * @param data a byte[] to convert to Hex characters
	 * @param toLowerCase {@code true} converts to lowercase, {@code false} to uppercase
	 * @return A char[] containing hexadecimal characters in the selected case
	 */
	public static char[] encodeHex(final byte[] data, final boolean toLowerCase) {
		return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
	}

	/**
	 * Converts an array of bytes into an array of characters representing the hexadecimal
	 * values of each byte in order. The returned array will be double the length of the
	 * passed array, as it takes two characters to represent any given byte.
	 * @param data a byte[] to convert to hex characters
	 * @param toDigits the output alphabet (must contain at least 16 chars)
	 * @return A char[] containing the appropriate characters from the alphabet For best
	 * results, this should be either upper- or lower-case hex.
	 */
	protected static char[] encodeHex(final byte[] data, final char[] toDigits) {
		final int dataLength = data.length;
		final char[] out = new char[dataLength << 1];
		encodeHex(data, 0, dataLength, toDigits, out, 0);
		return out;
	}

	/**
	 * Converts an array of bytes into an array of characters representing the hexadecimal
	 * values of each byte in order.
	 * @param data a byte[] to convert to hex characters
	 * @param dataOffset the position in {@code data} to start encoding from
	 * @param dataLen the number of bytes from {@code dataOffset} to encode
	 * @param toLowerCase {@code true} converts to lowercase, {@code false} to uppercase
	 * @return A char[] containing the appropriate characters from the alphabet For best
	 * results, this should be either upper- or lower-case hex.
	 */
	public static char[] encodeHex(final byte[] data, final int dataOffset, final int dataLen,
			final boolean toLowerCase) {
		final char[] out = new char[dataLen << 1];
		encodeHex(data, dataOffset, dataLen, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER, out, 0);
		return out;
	}

	/**
	 * Converts an array of bytes into an array of characters representing the hexadecimal
	 * values of each byte in order.
	 * @param data a byte[] to convert to hex characters
	 * @param dataOffset the position in {@code data} to start encoding from
	 * @param dataLen the number of bytes from {@code dataOffset} to encode
	 * @param toLowerCase {@code true} converts to lowercase, {@code false} to uppercase
	 * @param out a char[] which will hold the resultant appropriate characters from the
	 * alphabet.
	 * @param outOffset the position within {@code out} at which to start writing the
	 * encoded characters.
	 */
	public static void encodeHex(final byte[] data, final int dataOffset, final int dataLen, final boolean toLowerCase,
			final char[] out, final int outOffset) {
		encodeHex(data, dataOffset, dataLen, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER, out, outOffset);
	}

	/**
	 * Converts an array of bytes into an array of characters representing the hexadecimal
	 * values of each byte in order.
	 * @param data a byte[] to convert to hex characters
	 * @param dataOffset the position in {@code data} to start encoding from
	 * @param dataLen the number of bytes from {@code dataOffset} to encode
	 * @param toDigits the output alphabet (must contain at least 16 chars)
	 * @param out a char[] which will hold the resultant appropriate characters from the
	 * alphabet.
	 * @param outOffset the position within {@code out} at which to start writing the
	 * encoded characters.
	 */
	private static void encodeHex(final byte[] data, final int dataOffset, final int dataLen, final char[] toDigits,
			final char[] out, final int outOffset) {
		// two characters form the hex value.
		for (int i = dataOffset, j = outOffset; i < dataOffset + dataLen; i++) {
			out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
			out[j++] = toDigits[0x0F & data[i]];
		}
	}

	/**
	 * Converts a byte buffer into an array of characters representing the hexadecimal
	 * values of each byte in order. The returned array will be double the length of the
	 * passed array, as it takes two characters to represent any given byte.
	 *
	 * <p>
	 * All bytes identified by {@link ByteBuffer#remaining()} will be used; after this
	 * method the value {@link ByteBuffer#remaining() remaining()} will be zero.
	 * </p>
	 * @param data a byte buffer to convert to hex characters
	 * @return A char[] containing lower-case hexadecimal characters
	 */
	public static char[] encodeHex(final ByteBuffer data) {
		return encodeHex(data, true);
	}

	/**
	 * Converts a byte buffer into an array of characters representing the hexadecimal
	 * values of each byte in order. The returned array will be double the length of the
	 * passed array, as it takes two characters to represent any given byte.
	 *
	 * <p>
	 * All bytes identified by {@link ByteBuffer#remaining()} will be used; after this
	 * method the value {@link ByteBuffer#remaining() remaining()} will be zero.
	 * </p>
	 * @param data a byte buffer to convert to hex characters
	 * @param toLowerCase {@code true} converts to lowercase, {@code false} to uppercase
	 * @return A char[] containing hexadecimal characters in the selected case
	 */
	public static char[] encodeHex(final ByteBuffer data, final boolean toLowerCase) {
		return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
	}

	/**
	 * Converts a byte buffer into an array of characters representing the hexadecimal
	 * values of each byte in order. The returned array will be double the length of the
	 * passed array, as it takes two characters to represent any given byte.
	 *
	 * <p>
	 * All bytes identified by {@link ByteBuffer#remaining()} will be used; after this
	 * method the value {@link ByteBuffer#remaining() remaining()} will be zero.
	 * </p>
	 * @param byteBuffer a byte buffer to convert to hex characters
	 * @param toDigits the output alphabet (must be at least 16 characters)
	 * @return A char[] containing the appropriate characters from the alphabet For best
	 * results, this should be either upper- or lower-case hex.
	 */
	protected static char[] encodeHex(final ByteBuffer byteBuffer, final char[] toDigits) {
		return encodeHex(toByteArray(byteBuffer), toDigits);
	}

	/**
	 * Converts an array of bytes into a String representing the hexadecimal values of
	 * each byte in order. The returned String will be double the length of the passed
	 * array, as it takes two characters to represent any given byte.
	 * @param data a byte[] to convert to hex characters
	 * @return A String containing lower-case hexadecimal characters
	 */
	public static String encodeHexString(final byte[] data) {
		return new String(encodeHex(data));
	}

	/**
	 * Converts an array of bytes into a String representing the hexadecimal values of
	 * each byte in order. The returned String will be double the length of the passed
	 * array, as it takes two characters to represent any given byte.
	 * @param data a byte[] to convert to hex characters
	 * @param toLowerCase {@code true} converts to lowercase, {@code false} to uppercase
	 * @return A String containing lower-case hexadecimal characters
	 */
	public static String encodeHexString(final byte[] data, final boolean toLowerCase) {
		return new String(encodeHex(data, toLowerCase));
	}

	/**
	 * Converts a byte buffer into a String representing the hexadecimal values of each
	 * byte in order. The returned String will be double the length of the passed array,
	 * as it takes two characters to represent any given byte.
	 *
	 * <p>
	 * All bytes identified by {@link ByteBuffer#remaining()} will be used; after this
	 * method the value {@link ByteBuffer#remaining() remaining()} will be zero.
	 * </p>
	 * @param data a byte buffer to convert to hex characters
	 * @return A String containing lower-case hexadecimal characters
	 */
	public static String encodeHexString(final ByteBuffer data) {
		return new String(encodeHex(data));
	}

	/**
	 * Converts a byte buffer into a String representing the hexadecimal values of each
	 * byte in order. The returned String will be double the length of the passed array,
	 * as it takes two characters to represent any given byte.
	 *
	 * <p>
	 * All bytes identified by {@link ByteBuffer#remaining()} will be used; after this
	 * method the value {@link ByteBuffer#remaining() remaining()} will be zero.
	 * </p>
	 * @param data a byte buffer to convert to hex characters
	 * @param toLowerCase {@code true} converts to lowercase, {@code false} to uppercase
	 * @return A String containing lower-case hexadecimal characters
	 */
	public static String encodeHexString(final ByteBuffer data, final boolean toLowerCase) {
		return new String(encodeHex(data, toLowerCase));
	}

	/**
	 * Convert the byte buffer to a byte array. All bytes identified by
	 * {@link ByteBuffer#remaining()} will be used.
	 * @param byteBuffer the byte buffer
	 * @return the byte[]
	 */
	private static byte[] toByteArray(final ByteBuffer byteBuffer) {
		final int remaining = byteBuffer.remaining();
		// Use the underlying buffer if possible
		if (byteBuffer.hasArray()) {
			final byte[] byteArray = byteBuffer.array();
			if (remaining == byteArray.length) {
				byteBuffer.position(remaining);
				return byteArray;
			}
		}
		// Copy the bytes
		final byte[] byteArray = new byte[remaining];
		byteBuffer.get(byteArray);
		return byteArray;
	}

	/**
	 * Converts a hexadecimal character to an integer.
	 * @param ch A character to convert to an integer digit
	 * @param index The index of the character in the source
	 * @return An integer
	 * @throws DecodeException Thrown if ch is an illegal hex character
	 */
	protected static int toDigit(final char ch, final int index) throws DecodeException {
		final int digit = Character.digit(ch, 16);
		if (digit == -1) {
			throw new DecodeException("Illegal hexadecimal character " + ch + " at index " + index);
		}
		return digit;
	}

}
