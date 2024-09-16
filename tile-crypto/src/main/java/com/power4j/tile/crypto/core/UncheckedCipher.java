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

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public class UncheckedCipher {

	private final Slice cipher;

	private final Slice checksum;

	public static UncheckedCipher of(byte[] cipher, @Nullable byte[] checksum) {
		if (checksum == null) {
			checksum = new byte[0];
		}
		return new UncheckedCipher(Slice.wrap(cipher), Slice.wrap(checksum));
	}

	public static UncheckedCipher of(byte[] cipher) {
		return of(cipher, null);
	}

}
