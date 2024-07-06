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

package com.power4j.tile.io;

import com.power4j.tile.error.Err;
import com.power4j.tile.error.IntErr;
import org.springframework.lang.Nullable;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
public class DecodeErr extends IntErr {

	public static final int CODE = 1;

	protected DecodeErr(@Nullable String message, @Nullable Err source) {
		super(CODE, message, source);
	}

	public static DecodeErr of(@Nullable String message, @Nullable Err source) {
		return new DecodeErr(message, source);
	}

	public static DecodeErr of(@Nullable String message) {
		return new DecodeErr(message, null);
	}

}
