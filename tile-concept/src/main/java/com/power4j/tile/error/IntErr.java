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

package com.power4j.tile.error;

import org.springframework.lang.Nullable;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
public class IntErr extends ErrValue<Integer> implements Err {

	IntErr(int code, @Nullable String message, @Nullable Err source) {
		super(code, message, source);
	}

	public static IntErr of(int code, @Nullable String message, @Nullable Err source) {
		return new IntErr(code, message, source);
	}

	public static IntErr of(int code, @Nullable String message) {
		return new IntErr(code, message, null);
	}

	/**
	 * The error code
	 * @return Error code
	 */
	public int code() {
		return value;
	}

	@Override
	public String display() {
		return String.format("[%s] - %s", value, message);
	}

	/**
	 * Convert {@link StrErr} to {@link IntErr}
	 * @param err error object
	 * @return new {@link IntErr}
	 * @throws NumberFormatException if error code is not an integer
	 */
	public static IntErr parse(StrErr err) {
		int code = Integer.parseInt(err.code());
		return new IntErr(code, err.message(), err.source().orElse(null));
	}

}
