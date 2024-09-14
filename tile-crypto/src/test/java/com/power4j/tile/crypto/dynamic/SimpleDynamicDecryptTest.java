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

import com.power4j.tile.crypto.bc.Spec;
import com.power4j.tile.crypto.core.BlockCipher;
import com.power4j.tile.crypto.core.CipherBlob;
import com.power4j.tile.crypto.core.CipherBlobEnvelope;
import com.power4j.tile.crypto.utils.Sm4Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Function;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
class SimpleDynamicDecryptTest {

	private final byte[] testKey1 = new byte[] { 0x0a, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C,
			0x0D, 0x0E, 0x0F, 0x10 };

	private final byte[] testKey2 = new byte[] { 0x0b, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C,
			0x0D, 0x0E, 0x0F, 0x10 };

	private final byte[] testKey3 = new byte[] { 0x0c, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C,
			0x0D, 0x0E, 0x0F, 0x10 };

	private final byte[] testIv = new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C,
			0x0D, 0x0E, 0x0F, 0x10 };

	private final Function<byte[], byte[]> checksumCalculator = (b) -> Arrays.copyOf(b, 8);

	@Test
	void rotationDecryptTest() {
		byte[] plain = "hello".getBytes(StandardCharsets.UTF_8);
		KeyPool pool = Pools.rotation(testKey1, testKey2, testKey3);
		SimpleDynamicDecrypt dec = DynamicDecryptBuilder.sm4Cbc()
			.checksumCalculator(checksumCalculator)
			.keyPool(pool)
			.ivPool(Pools.fixed(testIv))
			.simple();

		BlockCipher enc = Sm4Util.builder(Spec.MODE_CBC, Spec.PADDING_PKCS7)
			.secretKey(testKey3)
			.ivParameter(testIv)
			.checksumCalculator(checksumCalculator)
			.build();
		CipherBlobEnvelope envelope = enc.encryptEnvelope(plain);
		CipherBlob store = new CipherBlob(envelope.getCipher(), envelope.getChecksum());
		DynamicDecryptResult result = dec.decrypt(store);

		Assertions.assertTrue(result.success());
		Assertions.assertEquals(3, result.getTried().size());
		Assertions.assertArrayEquals(plain, result.requiredMatched().getData());
	}

	@Test
	void timeBasedDecryptTest() {
		byte[] plain = "hello".getBytes(StandardCharsets.UTF_8);
		long time = System.currentTimeMillis();
		int windowSize = 3;
		int intervalSeconds = 60;
		KeyPool pool = TimeBasedPool.ofSize(16)
			.fillBytes(new byte[] { 0x01, 0x02, 0x03, 0x04 })
			.windowSize(windowSize)
			.intervalSeconds(intervalSeconds)
			.build();

		DynamicDecryptBuilder builder = DynamicDecryptBuilder.sm4Cbc()
			.checksumCalculator(checksumCalculator)
			.keyPool(pool)
			.ivPool(Pools.fixed(testIv));

		BlockCipher enc = Sm4Util.builder(Spec.MODE_CBC, Spec.PADDING_PKCS7)
			.secretKey(testKey3)
			.ivParameter(testIv)
			.checksumCalculator(checksumCalculator)
			.build();
		CipherBlobEnvelope envelope = enc.encryptEnvelope(plain);
		CipherBlob store = new CipherBlob(envelope.getCipher(), envelope.getChecksum());

		DynamicDecryptResult result = builder.parameterSupplier(() -> time).simple().decrypt(store);
		Assertions.assertTrue(result.success());
		Assertions.assertArrayEquals(plain, result.requiredMatched().getData());

		result = builder.parameterSupplier(() -> time + intervalSeconds * 1000 * windowSize).simple().decrypt(store);
		Assertions.assertTrue(result.success());
		Assertions.assertArrayEquals(plain, result.requiredMatched().getData());

		result = builder.parameterSupplier(() -> time - intervalSeconds * 1000 * windowSize).simple().decrypt(store);
		Assertions.assertTrue(result.success());
		Assertions.assertArrayEquals(plain, result.requiredMatched().getData());
	}

}
