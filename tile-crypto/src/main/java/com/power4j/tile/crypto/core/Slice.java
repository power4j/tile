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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.Arrays;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public class Slice {

	private final byte[] data;

	private final int offset;

	private final int length;

	public static Slice wrap(@Nullable byte[] data) {
		if (data == null) {
			return range(new byte[0], 0, 0);
		}
		return range(data, 0, data.length);
	}

	public static Slice range(byte[] data, int offset, int length) {
		if (offset < 0 || length < 0 || offset + length > data.length) {
			throw new IllegalArgumentException("Illegal offset or length");
		}
		return new Slice(data, offset, length);
	}

	public static Slice remaining(byte[] data, int offset) {
		return range(data, offset, data.length - offset);
	}

	public static Slice copyOf(byte[] data) {
		return copyOfRange(data, 0, data.length);
	}

	public static Slice copyOfRange(byte[] data, int offset, int length) {
		return wrap(Arrays.copyOfRange(data, offset, offset + length));
	}

	public static Slice copyOfRemaining(byte[] data, int offset) {
		return copyOfRange(data, offset, data.length - offset);
	}

	public boolean dataEquals(Slice other) {
		return dataEquals(other.data, other.offset, other.length);
	}

	public byte[] unwrap() {
		return Arrays.copyOfRange(data, offset, offset + length);
	}

	public Slice sub(int offset, int length) {
		if (offset < 0 || length < 0 || offset + length > this.length) {
			throw new IllegalArgumentException("Illegal offset or length");
		}
		return new Slice(this.data, this.offset + offset, length);
	}

	public boolean dataEquals(byte[] other) {
		return dataEquals(other, 0, other.length);
	}

	public boolean dataEquals(byte[] other, int otherOffset, int otherLength) {
		if (length != otherLength) {
			return false;
		}
		for (int i = 0; i < length; i++) {
			if (data[offset + i] != other[otherOffset + i]) {
				return false;
			}
		}
		return true;

	}

}
