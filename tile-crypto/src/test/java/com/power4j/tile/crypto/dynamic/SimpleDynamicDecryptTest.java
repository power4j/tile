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

import com.power4j.tile.crypto.bc.Sm4;
import com.power4j.tile.crypto.core.CipherEnvelope;
import com.power4j.tile.crypto.core.CipherStore;
import com.power4j.tile.crypto.core.Verified;
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

	private final Function<byte[], byte[]> hashFunc = (b) -> Arrays.copyOf(b, 8);

	@Test
	void decrypt() {
		byte[] plain = "hello".getBytes(StandardCharsets.UTF_8);
		KeyPool pool = Pools.rotation(testKey1, testKey2, testKey3);
		SimpleDynamicDecrypt dec = DynamicDecryptBuilder.sm4Cbc()
			.hashFunc(hashFunc)
			.keyPool(pool)
			.ivPool(Pools.fixed(testIv))
			.simple();

		Sm4 enc = Sm4.useCbcWithPadding(testKey3, testIv);
		CipherEnvelope envelope = enc.encryptEnvelope(plain, hashFunc);
		CipherStore store = new CipherStore(envelope.getCipher(), envelope.getChecksum());
		Verified<byte[]> verified = dec.decrypt(store);

		Assertions.assertTrue(verified.isPass());
		Assertions.assertArrayEquals(plain, verified.getData());
	}

}
