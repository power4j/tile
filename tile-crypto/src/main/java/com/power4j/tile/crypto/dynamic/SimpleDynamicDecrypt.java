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

import com.power4j.tile.crypto.bc.BouncyCastleBlockCipher;
import com.power4j.tile.crypto.core.CipherStore;
import com.power4j.tile.crypto.core.GeneralCryptoException;
import com.power4j.tile.crypto.core.Verified;
import lombok.Builder;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
@Builder
public class SimpleDynamicDecrypt implements DynamicDecrypt {

	private final String transformation;

	private final KeyPool keyPool;

	private final KeyPool ivPool;

	private final Function<byte[], byte[]> hashFunc;

	@Override
	public Verified<byte[]> decrypt(CipherStore store) {
		final long timestamp = System.currentTimeMillis();
		List<DynamicKey> keyList = keyPool.decryptKeys(timestamp);
		List<DynamicKey> ivList = ivPool.decryptKeys(timestamp);
		if (keyList.isEmpty()) {
			throw new GeneralCryptoException("No key found");
		}
		Verified<byte[]> result;
		for (DynamicKey key : keyList) {
			if (ivList.isEmpty()) {
				result = tryOne(store, key, null);
				if (result.isPass()) {
					return result;
				}
			}
			else {
				for (DynamicKey iv : ivList) {
					result = tryOne(store, key, iv);
					if (result.isPass()) {
						return result;
					}
				}
			}
		}
		return Verified.fail(null);
	}

	protected Verified<byte[]> tryOne(CipherStore store, DynamicKey key, @Nullable DynamicKey iv) {
		try {
			BouncyCastleBlockCipher cipher = new BouncyCastleBlockCipher(transformation, key.getKey(),
					iv == null ? null : iv.getKey());
			return cipher.decryptWithCheck(store, this::verify);
		}
		catch (GeneralCryptoException e) {
			throw e;
		}
		catch (Exception e) {
			throw new GeneralCryptoException(e.getMessage(), e);
		}
	}

	protected boolean verify(CipherStore store, byte[] plain) {
		return Arrays.equals(store.getChecksum(), hashFunc.apply(plain));
	}

}
