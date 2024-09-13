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

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
@UtilityClass
public class Pools {

	public KeyPool fixed(byte[] key) {
		return new FixedPool(key);
	}

	public KeyPool fixed(long id, byte[] key) {
		return new FixedPool(id, key);
	}

	public KeyPool rotation(byte[]... keys) {
		List<DynamicKey> list = new ArrayList<>(keys.length);
		for (int i = 0; i < keys.length; i++) {
			list.add(new DynamicKey(i, keys[i]));
		}
		return new RotationPool(list);
	}

	public KeyPool rotation(Collection<DynamicKey> keys) {
		return new RotationPool(keys);
	}

	public KeyPool empty() {
		return EmptyPool.INSTANCE;
	}

	static class FixedPool implements KeyPool {

		public static final long ID = 0L;

		private final DynamicKey key;

		public FixedPool(byte[] key) {
			this(ID, key);
		}

		public FixedPool(long id, byte[] key) {
			this.key = new DynamicKey(id, key);
		}

		@Override
		public Optional<DynamicKey> encryptKey(long param) {
			return Optional.of(key);
		}

		@Override
		public List<DynamicKey> decryptKeys(long param) {
			return Collections.singletonList(key);
		}

	}

	static class RotationPool implements KeyPool {

		private final DynamicKey[] keys;

		private final AtomicLong index = new AtomicLong(0);

		RotationPool(DynamicKey[] keys) {
			this.keys = keys;
		}

		RotationPool(Collection<DynamicKey> keys) {
			DynamicKey[] array = new DynamicKey[keys.size()];
			this.keys = keys.toArray(array);
		}

		@Override
		public Optional<DynamicKey> encryptKey(long param) {
			int pos = (int) (index.getAndIncrement() % keys.length);
			return Optional.of(keys[pos]);
		}

		@Override
		public List<DynamicKey> decryptKeys(long param) {
			return Arrays.asList(keys);
		}

	}

	static class EmptyPool implements KeyPool {

		static final EmptyPool INSTANCE = new EmptyPool();

		@Override
		public Optional<DynamicKey> encryptKey(long param) {
			return Optional.empty();
		}

		@Override
		public List<DynamicKey> decryptKeys(long param) {
			return Collections.emptyList();
		}

	}

}
