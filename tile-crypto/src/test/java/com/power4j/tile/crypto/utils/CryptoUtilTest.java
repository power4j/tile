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

import com.power4j.tile.crypto.core.GeneralCryptoException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
class CryptoUtilTest {

	@Test
	void shouldThrowIfValueIsInvalid() {
		Assertions.assertThrows(GeneralCryptoException.class, () -> CryptoUtil.decodeHex("G", null));
		Assertions.assertThrows(GeneralCryptoException.class, () -> CryptoUtil.decodeBase64("R", null));
	}

}
