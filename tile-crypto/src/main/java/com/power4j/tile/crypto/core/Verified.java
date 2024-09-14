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

import java.util.Objects;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public class Verified<T> {

	private final boolean pass;

	@Nullable
	private final T data;

	@Nullable
	private final Throwable cause;

	public static <T> Verified<T> pass(T data) {
		return new Verified<>(true, Objects.requireNonNull(data), null);
	}

	public static <T> Verified<T> fail(@Nullable T data, @Nullable Throwable cause) {
		return new Verified<>(false, data, cause);
	}

}
