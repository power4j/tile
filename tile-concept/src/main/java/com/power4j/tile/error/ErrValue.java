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

import com.power4j.tile.fmt.Display;
import org.springframework.lang.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
public class ErrValue<T> implements Err {

	protected final T value;

	protected final String message;

	@Nullable
	protected final Err source;

	public ErrValue(T value, @Nullable String message, @Nullable Err source) {
		this.value = Objects.requireNonNull(value);
		this.message = message == null ? "" : message;
		this.source = source;
	}

	public ErrValue(T value, @Nullable String message) {
		this(value, message, null);
	}

	/**
	 * The error code
	 * @return Error code
	 */
	public T value() {
		return value;
	}

	@Override
	public String message() {
		return message;
	}

	@Override
	public Optional<Err> source() {
		return Optional.ofNullable(source);
	}

	@Override
	public String display() {
		if (value instanceof Display) {
			return String.format("[%s] - %s", ((Display) value).display(), message);
		}
		return String.format("[%s] - %s", value, message);
	}

}
