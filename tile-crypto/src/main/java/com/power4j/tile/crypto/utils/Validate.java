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

package com.power4j.tile.crypto.utils;

import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.6
 */
@UtilityClass
public class Validate {

	public void notNull(@Nullable Object obj, @Nullable String errorMsg) {
		if (obj == null) {
			if (errorMsg == null) {
				throw new IllegalArgumentException("Null is not allowed");
			}
			else {
				throw new IllegalArgumentException(errorMsg);
			}
		}
	}

	public void notEmpty(@Nullable String val, @Nullable String errorMsg) {
		if (val == null || val.isEmpty()) {
			if (errorMsg == null) {
				throw new IllegalArgumentException("Empty value is not allowed.");
			}
			else {
				throw new IllegalArgumentException(errorMsg);
			}
		}
	}

}
