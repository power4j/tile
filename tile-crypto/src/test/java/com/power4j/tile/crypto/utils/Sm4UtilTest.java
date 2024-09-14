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

package com.power4j.tile.crypto.utils;

import com.power4j.tile.crypto.core.BlockCipher;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
class Sm4UtilTest {

	@Test
	void testEcbWithPadding() {

		BlockCipher sm4 = Sm4Util.useEcbWithPadding("2aa67d8833e28fa88b9ad09aaaa90619");
		byte[] enc = sm4.encrypt(Hex.decode("0001515641703792"));
		assertEquals("9e8078a5d816f67ed3a1d536294c8509", Hex.toHexString(enc));

		byte[] plain = sm4.decrypt(enc);
		assertEquals("0001515641703792", Hex.toHexString(plain));

		sm4 = Sm4Util.useEcbWithPadding("4aa697a7d8394925884767a5888a0c8a");
		String rootEnc = Hex.toHexString(sm4.encrypt("root".getBytes(StandardCharsets.UTF_8)));
		System.out.println("SM4 ECB Enc(root):" + rootEnc);
		Assertions.assertEquals("683ed60b4f4bca8ec7962742a9183ce4", rootEnc);
	}

	@Test
	void testCbcWithPadding() {
		BlockCipher sm4 = Sm4Util.useCbcWithPadding("2aa67d8833e28fa88b9ad09aaaa90619",
				"82ac066e232a6b19e203f531855a809e");
		byte[] enc = sm4.encrypt(Hex.decode("0001515641703792"));
		assertEquals("f702064157cf93cf3f589d0246388450", Hex.toHexString(enc));

		byte[] plain = sm4.decrypt(enc);
		assertEquals("0001515641703792", Hex.toHexString(plain));

		sm4 = Sm4Util.useCbcWithPadding("4aa697a7d8394925884767a5888a0c8a", "80c0b436a7a15b89e8622436c6d6f04e");
		String rootEnc = Hex.toHexString(sm4.encrypt("root".getBytes(StandardCharsets.UTF_8)));
		System.out.println("SM4 CBC Enc(root):" + rootEnc);
		Assertions.assertEquals("6172a52d39ab724781bc835775ce2a1b", rootEnc);
	}

	@Test
	void testOfb() {
		BlockCipher sm4 = Sm4Util.useOfb("2aa67d8833e28fa88b9ad09aaaa90619", "ccbb066e232a6b19e203f531855a809e");
		byte[] enc = sm4.encrypt(Hex.decode("0001515641703792"));
		assertEquals("127ea1f04ce83d57", Hex.toHexString(enc));

		byte[] plain = sm4.decrypt(enc);
		assertEquals("0001515641703792", Hex.toHexString(plain));
	}

	@Test
	void testCfb() {
		BlockCipher sm4 = Sm4Util.useCfb("2aa67d8833e28fa88b9ad09aaaa90619", "aabb066e232a6b19e203f531855a809e");
		byte[] enc = sm4.encrypt(Hex.decode("0001515641703792"));
		assertEquals("caeeb2acacadf53a", Hex.toHexString(enc));

		byte[] plain = sm4.decrypt(enc);
		assertEquals("0001515641703792", Hex.toHexString(plain));
	}

	@Test
	void testHexProcess() {
		BlockCipher sm4 = Sm4Util.useCbcWithPadding("2aa67d8833e28fa88b9ad09aaaa90619",
				"82ac066e232a6b19e203f531855a809e");
		byte[] plain = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
		String enc = sm4.encryptHex(plain);

		byte[] dec = sm4.decryptHex(enc);
		assertArrayEquals(plain, dec);
	}

	@Test
	void shouldThrowIfIvIsInvalid() {
		byte[] key = new byte[Sm4Util.BLOCK_SIZE];
		byte[] iv = new byte[Sm4Util.BLOCK_SIZE - 1];
		assertThrows(IllegalArgumentException.class, () -> Sm4Util.useCbcWithPadding(key, iv));
	}

}
