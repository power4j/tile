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

package com.power4j.tile.common.util;

import com.power4j.tile.util.ArrayUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
class ArrayUtilTest {

	@Test
	void copyWithFill() {
		byte[] input = { (byte) 1, (byte) 2, (byte) 3, (byte) 4 };
		Assertions.assertArrayEquals(new byte[] { (byte) 0xFF, (byte) 1, (byte) 2, (byte) 3, (byte) 4 },
				ArrayUtil.copyOf(input, 5, (byte) 0xFF, true));

		Assertions.assertArrayEquals(new byte[] { (byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 0xFF },
				ArrayUtil.copyOf(input, 5, (byte) 0xFF, false));

		Assertions.assertArrayEquals(new byte[] { (byte) 1, (byte) 2 }, ArrayUtil.copyOf(input, 2, (byte) 0xFF, false));

		Assertions.assertArrayEquals(new byte[] { (byte) 0xFF, (byte) 0xFF },
				ArrayUtil.copyOf(null, 2, (byte) 0xFF, false));
		Assertions.assertArrayEquals(new byte[] { (byte) 0xFF, (byte) 0xFF },
				ArrayUtil.copyOf(new byte[0], 2, (byte) 0xFF, false));
	}

}
