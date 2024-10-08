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

import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.Optional;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
@Getter
@Builder
public class CiphertextDetails {

	private final String encoding;

	private final String algorithm;

	private final String padding;

	private final String mode;

	@Nullable
	private final String iv;

	private final String ciphertext;

	private final String checksum;

	public Optional<String> getIvOptional(boolean trimToNull) {
		if (iv == null) {
			return Optional.empty();
		}
		if (trimToNull && iv.trim().isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(iv);
	}

	@Override
	public String toString() {
		return "CiphertextDetails{" + "encoding='" + encoding + '\'' + ", algorithm='" + algorithm + '\''
				+ ", padding='" + padding + '\'' + ", mode='" + mode + '\'' + ", iv='" + iv + '\'' + ", ciphertext='"
				+ ciphertext + '\'' + ", checksum='" + checksum + '\'' + '}';
	}

}
