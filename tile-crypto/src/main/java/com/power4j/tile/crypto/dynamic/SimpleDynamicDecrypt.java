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

import com.power4j.tile.crypto.core.BlockCipher;
import com.power4j.tile.crypto.core.BlockCipherBuilder;
import com.power4j.tile.crypto.core.CipherBlob;
import com.power4j.tile.crypto.core.GeneralCryptoException;
import com.power4j.tile.crypto.core.Verified;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.6
 * @see DynamicDecryptBuilder
 */
@RequiredArgsConstructor
public class SimpleDynamicDecrypt implements DynamicDecrypt {

	private final String algorithmName;

	private final String mode;

	private final String padding;

	private final KeyPool keyPool;

	private final KeyPool ivPool;

	private final Function<byte[], byte[]> checksumCalculator;

	private final Supplier<Long> paramterSupplier;

	@Override
	public DynamicDecryptResult decrypt(CipherBlob store) {
		final long timestamp = paramterSupplier.get();
		List<DynamicKey> keyList = keyPool.decryptKeys(timestamp);
		List<DynamicKey> ivList = ivPool.decryptKeys(timestamp);
		if (keyList.isEmpty()) {
			throw new GeneralCryptoException("No key found");
		}
		List<DecryptInfo> tried = new ArrayList<>(keyList.size() + ivList.size());
		DecryptInfo result;
		for (DynamicKey key : keyList) {
			if (ivList.isEmpty()) {
				result = tryOne(store, key, null);
				tried.add(result);
				if (result.isMatched()) {
					return DynamicDecryptResult.success(result, tried);
				}
			}
			else {
				for (DynamicKey iv : ivList) {
					result = tryOne(store, key, iv);
					tried.add(result);
					if (result.isMatched()) {
						return DynamicDecryptResult.success(result, tried);
					}
				}
			}
		}
		return DynamicDecryptResult.fail(tried);
	}

	protected DecryptInfo tryOne(CipherBlob store, DynamicKey key, @Nullable DynamicKey iv) {
		try {
			BlockCipher cipher = BlockCipherBuilder.algorithm(algorithmName)
				.mode(mode)
				.padding(padding)
				.secretKey(key.getKey())
				.ivParameter(iv == null ? null : iv.getKey())
				.checksumCalculator(checksumCalculator)
				.checksumVerifier(
						(cipherBlob, bytes) -> Arrays.equals(cipherBlob.getChecksum(), checksumCalculator.apply(bytes)))
				.build();
			Verified<byte[]> verified = cipher.decrypt(store, false);
			return DecryptInfo.builder()
				.matched(verified.isPass())
				.keyTag(key.getTag())
				.checksum(store.getChecksum())
				.data(verified.getData())
				.build();
		}
		catch (GeneralCryptoException e) {
			throw e;
		}
		catch (Exception e) {
			throw new GeneralCryptoException(e.getMessage(), e);
		}
	}

}
