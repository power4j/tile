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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.6
 */
public class TimeBasedPool implements KeyPool {

	private final KeyGenerator generator;

	private final int windowSize;

	private final long intervalMills;

	private final int keySize;

	public static Builder ofSize(int keySize) {
		return new Builder(keySize);
	}

	TimeBasedPool(KeyGenerator generator, int keySize, int windowSize, long intervalMills) {
		if (windowSize <= 0) {
			throw new IllegalArgumentException("window size must > 0");
		}
		if (intervalMills <= 0) {
			throw new IllegalArgumentException("interval must > 0");
		}
		this.generator = generator;
		this.keySize = keySize;
		this.windowSize = windowSize;
		this.intervalMills = intervalMills;
	}

	@Override
	public DynamicKey one(long param) {
		return atOffset(param, 0);
	}

	@Override
	public List<DynamicKey> some(long param) {
		List<DynamicKey> keys = new ArrayList<>(windowSize * 2 + 1);
		keys.add(atOffset(param, 0));
		for (int i = 1; i <= windowSize; i++) {
			keys.add(atOffset(param, i));
			keys.add(atOffset(param, -i));
		}
		return keys;
	}

	protected DynamicKey atOffset(long timestamp, int offset) {
		long seed = (timestamp + (long) offset * intervalMills) / intervalMills;
		return new DynamicKey(timestamp + ":" + offset, generator.generate(seed, keySize));
	}

	static class KeyGenerator {

		private final byte[] padding;

		KeyGenerator(byte[] padding) {
			if (padding.length == 0) {
				throw new IllegalArgumentException("padding must not be null or empty");
			}
			this.padding = Arrays.copyOf(padding, padding.length);
		}

		public byte[] generate(long timestamp, int keySize) {
			byte[] base = longBe(timestamp);
			int fillAt = base.length;
			byte[] key = Arrays.copyOf(base, keySize);
			while (fillAt < keySize) {
				key[fillAt] = padding[fillAt % padding.length];
				fillAt++;
			}
			return key;
		}

		static byte[] longBe(long value) {
			ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES).order(ByteOrder.BIG_ENDIAN);
			buffer.putLong(0, value);
			return buffer.array();
		}

	}

	public static class Builder {

		private final int keySize;

		private int windowSize;

		private long interval;

		private byte[] fillBytes;

		Builder(int keySize) {
			this.keySize = keySize;
		}

		public Builder windowSize(int windowSize) {
			this.windowSize = windowSize;
			return this;
		}

		public Builder intervalSeconds(long seconds) {
			this.interval = seconds * 1000L;
			return this;
		}

		public Builder interval(Duration interval) {
			this.interval = interval.toMillis();
			return this;
		}

		public Builder fillBytes(byte[] fillBytes) {
			this.fillBytes = fillBytes;
			return this;
		}

		public TimeBasedPool build() {
			return new TimeBasedPool(new KeyGenerator(fillBytes), keySize, windowSize, interval);
		}

	}

}
