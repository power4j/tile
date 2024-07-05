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

package com.power4j.tile.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
class EnvUtilTest {

	private final String VALUE_TILE_TEST_KEY_1 = "TEST";

	private final String VALUE_TILE_SYS_KEY_1 = "SYS";

	@Test
	void lookupKeyPattern() {
		Map<String, String> map = EnvUtil.lookupKeyPrefix("^TILE_*$");
		Assertions.assertEquals(VALUE_TILE_TEST_KEY_1, map.get("TILE_TEST_KEY_1"));
		Assertions.assertEquals(VALUE_TILE_SYS_KEY_1, map.get("TILE_SYS_KEY_1"));
	}

	@Test
	void lookupKeyPrefix() {
		Map<String, String> map = EnvUtil.lookupKeyPrefix("TILE_");
		Assertions.assertEquals(VALUE_TILE_TEST_KEY_1, map.get("TILE_TEST_KEY_1"));
		Assertions.assertEquals(VALUE_TILE_SYS_KEY_1, map.get("TILE_SYS_KEY_1"));
	}

}
