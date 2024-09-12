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

import org.springframework.lang.Nullable;

import java.util.Optional;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.6
 */
public enum BufferEncoding {

	/**
	 * UTF_8
	 */
	ASCII("ASCII"),
	/**
	 * UTF_8
	 */
	UTF_8("UTF_8"),
	/**
	 * UTF_8
	 */
	UTF_16LE("UTF_16LE"),
	/**
	 * UTF_8
	 */
	UTF_16BE("UTF_16BE"),
	/**
	 * HEX
	 */
	HEX("HEX"),
	/**
	 * BASE64
	 */
	BASE64("BASE64"),
	/**
	 * BASE64_URL
	 */
	BASE64_URL("BASE64_URL");

	private final String value;

	BufferEncoding(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	/**
	 * Parse form value
	 * @param value the value
	 * @return return empty if parse fail
	 */
	public static Optional<BufferEncoding> parse(@Nullable final String value) {
		if (value == null) {
			return Optional.empty();
		}
		for (BufferEncoding o : BufferEncoding.values()) {
			if (o.getValue().equals(value)) {
				return Optional.of(o);
			}
		}
		return Optional.empty();
	}

	/**
	 * Get BufferEncoding from value
	 * @param value the value
	 * @return 如果解析失败抛出 IllegalArgumentException
	 * @throws IllegalArgumentException The value is invalid
	 */
	public static BufferEncoding fromValue(@Nullable final String value) throws IllegalArgumentException {
		return parse(value).orElseThrow(() -> new IllegalArgumentException("Invalid value : " + value));
	}

}
