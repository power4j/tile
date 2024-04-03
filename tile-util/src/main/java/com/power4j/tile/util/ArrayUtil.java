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

package com.power4j.tile.util;

import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;

import java.util.Arrays;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
@UtilityClass
public class ArrayUtil {

	/**
	 * Copy data, and fill if necessary
	 * @param src data to copy
	 * @param length length to copy,truncate if length is smaller than src length, fill if
	 * length is larger than src length
	 * @param fill filler byte
	 * @param padLeft true means fill padding bytes to left
	 * @return copied data
	 */
	public byte[] copyOf(@Nullable byte[] src, int length, byte fill, boolean padLeft) {
		final byte[] input = src == null ? new byte[0] : src;
		final int readable = input.length;
		if (length > readable) {
			final byte[] bytes;
			if (padLeft) {
				bytes = new byte[length];
				final int offset = length - readable;
				Arrays.fill(bytes, 0, offset, fill);
				System.arraycopy(input, 0, bytes, offset, readable);
			}
			else {
				bytes = Arrays.copyOf(input, length);
				Arrays.fill(bytes, readable, length, fill);
			}
			return bytes;
		}
		return Arrays.copyOf(input, length);
	}

}
