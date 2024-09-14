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

package com.power4j.tile.crypto.dynamic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
@RequiredArgsConstructor
public class DynamicDecryptResult {

	private final DecryptInfo matched;

	@Getter
	private final List<DecryptInfo> tried;

	public static DynamicDecryptResult success(DecryptInfo matched, List<DecryptInfo> tried) {
		if (tried.isEmpty()) {
			throw new IllegalArgumentException("Empty tried list");
		}
		return new DynamicDecryptResult(matched, tried);
	}

	public static DynamicDecryptResult fail(List<DecryptInfo> tried) {
		return new DynamicDecryptResult(null, tried);
	}

	public Optional<DecryptInfo> getMatched() {
		return Optional.ofNullable(matched);
	}

	public DecryptInfo requiredMatched() {
		return getMatched().orElseThrow(() -> new IllegalStateException("No matched due to decryption failure"));
	}

	public boolean success() {
		return matched != null;
	}

}
