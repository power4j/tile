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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
class TimeBasedPoolTest {

	@Test
	void keyGenerateTest() {
		byte[] fills = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

		Assertions.assertArrayEquals(new byte[] { 1 },
				new TimeBasedPool.KeyGenerator(fills).generate(0x0102030405060708L, 1));

		Assertions.assertArrayEquals(new byte[] { 1, 2 },
				new TimeBasedPool.KeyGenerator(fills).generate(0x0102030405060708L, 2));

		Assertions.assertArrayEquals(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 },
				new TimeBasedPool.KeyGenerator(fills).generate(0x0102030405060708L, 8));

		Assertions.assertArrayEquals(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 8 },
				new TimeBasedPool.KeyGenerator(fills).generate(0x0102030405060708L, 9));

		Assertions.assertArrayEquals(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 8, 9 },
				new TimeBasedPool.KeyGenerator(fills).generate(0x0102030405060708L, 10));

		Assertions.assertArrayEquals(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 8, 9, 0 },
				new TimeBasedPool.KeyGenerator(fills).generate(0x0102030405060708L, 11));
	}

	@Test
	void shouldGenerateSameKey() {
		TimeBasedPool.KeyGenerator generator = new TimeBasedPool.KeyGenerator(new byte[] { 1 });
		long input = 1L;
		byte[] last = null;
		for (int i = 0; i < 100; i++) {
			byte[] key = generator.generate(input, 32);
			if (last != null) {
				Assertions.assertArrayEquals(last, key);
			}
			last = key;
		}
	}

	@Test
	void keyPoolGeneratorTest() {
		long input = 1L;
		TimeBasedPool pool = TimeBasedPool.ofSize(16)
			.windowSize(5)
			.interval(Duration.ofMinutes(1))
			.fillBytes(new byte[] { 1 })
			.build();
		final List<DynamicKey> decryptKeys = pool.decryptKeys(input);
		Assertions.assertEquals(11, decryptKeys.size());
		final DynamicKey encryptKey = pool.encryptKey(input).orElse(null);
		Assertions.assertNotNull(encryptKey);
		Assertions.assertArrayEquals(encryptKey.getKey(), decryptKeys.get(0).getKey());
	}

}
