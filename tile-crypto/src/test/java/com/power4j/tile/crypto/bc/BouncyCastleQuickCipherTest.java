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

package com.power4j.tile.crypto.bc;

import com.power4j.tile.crypto.core.CipherBlobDetails;
import com.power4j.tile.crypto.core.UncheckedCipher;
import com.power4j.tile.crypto.core.Verified;
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
class BouncyCastleQuickCipherTest {

	private final byte[] testKey = new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C,
			0x0D, 0x0E, 0x0F, 0x10 };

	private final byte[] testIv = new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C,
			0x0D, 0x0E, 0x0F, 0x10 };

	@Test
	void encrypt() {
		byte[] plain = "hello".getBytes(StandardCharsets.UTF_8);
		BouncyCastleQuickCipher cipher = Sm4Util.builder(Spec.MODE_CBC, Spec.PADDING_PKCS7)
			.secretKey(testKey)
			.ivParameter(testIv)
			.checksumCalculator(null)
			.build();
		CipherBlobDetails details = cipher.encrypt(plain);

		Assertions.assertNotNull(details.getAlgorithm());
		Assertions.assertNotNull(details.getMode());
		Assertions.assertNotNull(details.getPadding());
		Assertions.assertNotNull(details.getIv());
		Assertions.assertNotNull(details.getCipher());
		Assertions.assertNotNull(details.getChecksum());

		byte[] decrypted = cipher.decrypt(details.getCipher());
		Assertions.assertArrayEquals(plain, decrypted);
	}

	@Test
	void decryptWithCheck() {
		byte[] plain = "hello".getBytes(StandardCharsets.UTF_8);
		Function<byte[], byte[]> hashFunc = (b) -> Arrays.copyOf(b, 8);

		BouncyCastleQuickCipher cipher = Sm4Util.builder(Spec.MODE_CBC, Spec.PADDING_PKCS7)
			.secretKey(testKey)
			.ivParameter(testIv)
			.checksumCalculator(hashFunc)
			.build();
		CipherBlobDetails details = cipher.encrypt(plain);

		UncheckedCipher store = UncheckedCipher.of(details.getCipher(), details.getChecksum());
		Verified<byte[]> verified = cipher.decrypt(store, false);
		Assertions.assertTrue(verified.isPass());
		Assertions.assertArrayEquals(plain, verified.getData());
	}

}
