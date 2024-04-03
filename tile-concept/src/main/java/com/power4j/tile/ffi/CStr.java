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

import com.power4j.tile.error.Err;
import com.power4j.tile.error.ErrValue;
import com.power4j.tile.fmt.Display;
import com.power4j.tile.result.Result;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Represents a C-compatible, nul-terminated string.
 * <p>
 *
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
public class CStr {

	private final static Charset UTF8 = StandardCharsets.UTF_8;

	private final byte[] bytes;

	public static final CStr EMPTY = new CStr(new byte[1]);

	CStr(byte[] bytes) {
		this.bytes = bytes;
	}

	/**
	 * Wrap shared bytes for further use
	 * @param bytes shared bytes
	 * @return New CStr object
	 */
	public static CStr refOf(byte[] bytes) {
		return new CStr(bytes);
	}

	/**
	 * Copy bytes for further use
	 * @param bytes bytes to copy
	 * @param offset offset for copy
	 * @param length length for copy
	 * @return New CStr object
	 */
	public static CStr copyOf(byte[] bytes, int offset, int length) {
		byte[] copy = Arrays.copyOfRange(bytes, offset, offset + length);
		return new CStr(copy);
	}

	/**
	 * Create CStr from string, add null byte at the end.<pre>
	 *     Assertions.assertArrayEquals(new byte []{ 49, 50, 51,0 }, CStr.makeBuffer("123").getBytes());
	 * </pre>
	 * @param str string to crate from,Use UTF8 charset to get bytes
	 * @return New CStr object
	 */
	public static CStr makeBuffer(String str) {
		if (str.isEmpty()) {
			return EMPTY;
		}
		byte[] all = str.getBytes(UTF8);
		return refOf(Arrays.copyOf(all, all.length + 1));
	}

	/**
	 * Similar to {@link #makeBuffer(String)},drop or fill null bytes to ensure fixed
	 * length.<pre>
	 *     Assertions.assertArrayEquals(new byte []{ 49, 50, 51,0 }, CStr.makeBuffer("123",4).getBytes());
	 *     Assertions.assertArrayEquals(new byte []{ 49, 50,0 }, CStr.makeBuffer("123",3).getBytes());
	 *     Assertions.assertArrayEquals(new byte []{ 49, 50, 51,0,0 }, CStr.makeBuffer("123",5).getBytes());
	 * </pre>
	 * @param str string to crate from,Use UTF8 charset to get bytes
	 * @param length length to create for
	 * @return New CStr object
	 */
	public static CStr makeBuffer(String str, int length) {
		if (str.isEmpty()) {
			return EMPTY;
		}
		byte[] all = str.getBytes(UTF8);
		if (all.length == length) {
			all[length - 1] = 0;
			return refOf(all);
		}
		else if (all.length < length) {

			byte[] bytes = new byte[length];
			System.arraycopy(all, 0, bytes, 0, all.length);
			Arrays.fill(bytes, all.length, length, (byte) 0);
			return refOf(bytes);
		}
		else {
			all[length - 1] = 0;
			return copyOf(all, 0, length);
		}
	}

	/**
	 * Get wrapped bytes
	 * @return byte array
	 */
	public byte[] getBytes() {
		return bytes;
	}

	/**
	 * Read C string from range {@code [offset,offset + length) } until null byte
	 * found.<pre>
	 *     CStr.refOf(new byte []{ 49, 50, 51,0,0 }).tryReadUntilNull(0,5).unwrap() -> "123"
	 *     CStr.refOf(new byte []{ 49, 50, 51,0,0 }).tryReadUntilNull(3,2).unwrap() -> ""
	 * </pre>
	 * @param offset offset to read
	 * @param length length to read,-1 to read all remaining bytes
	 * @return String or error
	 */
	public Result<String, Err> tryReadUntilNull(int offset, int length) {
		if (length == -1) {
			length = bytes.length - offset;
		}
		for (int i = 0; i < length; i++) {
			if (bytes[offset + i] == 0) {
				String str = new String(bytes, offset, i, UTF8);
				return Result.ok(str);
			}
		}
		NotNulTerminated reason = new NotNulTerminated(offset, length);
		return Result.error(new ErrValue<>(reason, reason.getMessage()));
	}

	/**
	 * Use {@link #tryReadUntilNull(int, int)} to read C string,throws error when failed
	 * @param offset offset to read
	 * @param length length to read,-1 to read all
	 * @return String
	 * @throws InvalidCStrBytesException
	 */
	public String readUntilNull(int offset, int length) {
		return tryReadUntilNull(offset, length).unwrapOrThrow(e -> new InvalidCStrBytesException(e.message()));
	}

	/**
	 * Read C string from range {@code [offset,offset + length) }, and check if it
	 * contains an interior null byte at the end
	 * @param offset offset to read
	 * @param length length to read,-1 to read all remaining bytes
	 * @return String or error
	 */
	public Result<String, Err> tryReadWithNullCheck(int offset, int length) {
		if (length == -1) {
			length = bytes.length - offset;
		}
		int index = indexOf(offset, length, (byte) 0);
		if (index == -1) {
			NotNulTerminated reason = new NotNulTerminated(offset, length);
			return Result.error(new ErrValue<>(reason, reason.getMessage()));
		}
		else if (index != length - 1) {
			InteriorNul reason = new InteriorNul(offset, index);
			return Result.error(new ErrValue<>(reason, reason.getMessage()));
		}
		else {
			return Result.ok(new String(bytes, offset, length - 1, UTF8));
		}
	}

	/**
	 * Use {@link #tryReadWithNullCheck(int, int)} to read C string,throws error when
	 * failed
	 * @param offset offset to read
	 * @param length length to read,-1 to read all remaining bytes
	 * @return String
	 * @throws InvalidCStrBytesException
	 */
	public String readWithNullCheck(int offset, int length) {
		return tryReadWithNullCheck(offset, length).unwrapOrThrow(e -> new InvalidCStrBytesException(e.message()));
	}

	/**
	 * Read C string from range {@code [offset,offset + length) }, if no null byte found,
	 * return entire string <pre>
	 *     CStr.refOf(new byte []{ 49, 50, 51,0 }).readUntilEnd(0,4).unwrap() -> "123"
	 *     CStr.refOf(new byte []{ 49, 50, 51,0 }).readUntilEnd(0,3).unwrap() -> ""
	 *</pre>
	 * @param offset offset to read
	 * @param length length to read,-1 to read all remaining bytes
	 * @return String
	 */
	public String readUntilEnd(int offset, int length) {
		if (length == -1) {
			length = bytes.length - offset;
		}
		for (int i = 0; i < length; i++) {
			if (bytes[offset + i] == 0) {
				return new String(bytes, offset, i, UTF8);
			}
		}
		return new String(bytes, offset, length, UTF8);
	}

	protected int indexOf(int offset, int length, byte b) {
		int pos = -1;
		for (int i = 0; i < length; ++i) {
			if (b == bytes[offset + i]) {
				pos = i;
				break;
			}
		}
		return pos;
	}

	/**
	 * Data contains an interior nul byte
	 * <p>
	 *
	 * @author CJ (power4j@outlook.com)
	 * @since 1.0
	 */
	public static class InteriorNul implements Display {

		private final int offset;

		private final int pos;

		public InteriorNul(int offset, int pos) {
			this.offset = offset;
			this.pos = pos;
		}

		public int getOffset() {
			return offset;
		}

		public int getPos() {
			return pos;
		}

		public String getMessage() {
			return "Contains an interior nul byte at pos " + pos + " with offset " + offset;
		}

		@Override
		public String display() {
			return getClass().getSimpleName();
		}

	}

	/**
	 * Data is not nul terminated.
	 * <p>
	 *
	 * @author CJ (power4j@outlook.com)
	 * @since 1.0
	 */
	public static class NotNulTerminated implements Display {

		private final int offset;

		private final int length;

		public NotNulTerminated(int offset, int length) {
			this.offset = offset;
			this.length = length;
		}

		public int getOffset() {
			return offset;
		}

		public int getLength() {
			return length;
		}

		public String getMessage() {
			return "Data is not nul terminated at pos " + (length + offset) + " with offset " + offset;
		}

		@Override
		public String display() {
			return getClass().getSimpleName();
		}

	}

}
