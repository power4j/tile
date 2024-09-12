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

package com.power4j.tile.crypto.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.5
 */
class Sm4TextCipherTest {

	private final BufferEncoding[] unicodeEncodings = new BufferEncoding[] { BufferEncoding.UTF_8,
			BufferEncoding.UTF_16BE, BufferEncoding.UTF_16LE };

	private final BufferEncoding[] binaryEncodings = new BufferEncoding[] { BufferEncoding.HEX, BufferEncoding.BASE64,
			BufferEncoding.BASE64_URL };

	private final String key = "2aa67d8833e28fa88b9ad09aaaa90619";

	private final String iv = "82ac066e232a6b19e203f531855a809e";

	private final String plain = "Hello,你好,こんにちは";

	@Test
	void sm4EcbUnicodeTest() {
		TextCipherBuilder builder = TextCipherBuilder.sm4Ecb().keyHex(key);
		for (BufferEncoding inputEncoding : unicodeEncodings) {
			for (BufferEncoding outputEncoding : binaryEncodings) {
				String hint = String.format("sm4Ecb encrypt:%s -> %s,decrypt:%s -> %s", inputEncoding, outputEncoding,
						outputEncoding, inputEncoding);
				TextCipher enc = builder.inputEncoding(inputEncoding).outputEncoding(outputEncoding).build();
				TextCipher dec = builder.reverseEncoder().build();
				String cipher = enc.encrypt(plain);
				System.out.printf("[%s]%n plain = %s,cipher = %s%n", hint, plain, cipher);
				Assertions.assertEquals(plain, dec.decrypt(cipher), hint);
			}
		}
	}

	@Test
	void sm4CbcUnicodeTest() {
		TextCipherBuilder builder = TextCipherBuilder.sm4Cbc().keyHex(key).ivHex(iv);
		for (BufferEncoding inputEncoding : unicodeEncodings) {
			for (BufferEncoding outputEncoding : binaryEncodings) {
				String hint = String.format("sm4Cbc encrypt:%s -> %s,decrypt:%s -> %s", inputEncoding, outputEncoding,
						outputEncoding, inputEncoding);
				TextCipher enc = builder.inputEncoding(inputEncoding).outputEncoding(outputEncoding).build();
				TextCipher dec = builder.reverseEncoder().build();
				String cipher = enc.encrypt(plain);
				System.out.printf("[%s]%n plain = %s,cipher = %s%n", hint, plain, cipher);
				Assertions.assertEquals(plain, dec.decrypt(cipher), hint);
			}
		}
	}

	@Test
	void sm4CfbUnicodeTest() {
		TextCipherBuilder builder = TextCipherBuilder.sm4Cfb().keyHex(key).ivHex(iv);
		for (BufferEncoding inputEncoding : unicodeEncodings) {
			for (BufferEncoding outputEncoding : binaryEncodings) {
				String hint = String.format("sm4Cfb encrypt:%s -> %s,decrypt:%s -> %s", inputEncoding, outputEncoding,
						outputEncoding, inputEncoding);
				TextCipher enc = builder.inputEncoding(inputEncoding).outputEncoding(outputEncoding).build();
				TextCipher dec = builder.reverseEncoder().build();
				String cipher = enc.encrypt(plain);
				System.out.printf("[%s]%n plain = %s,cipher = %s%n", hint, plain, cipher);
				Assertions.assertEquals(plain, dec.decrypt(cipher), hint);
			}
		}
	}

	@Test
	void sm4OfbUnicodeTest() {
		TextCipherBuilder builder = TextCipherBuilder.sm4Ofb().keyHex(key).ivHex(iv);
		for (BufferEncoding inputEncoding : unicodeEncodings) {
			for (BufferEncoding outputEncoding : binaryEncodings) {
				String hint = String.format("sm4Ofb encrypt:%s -> %s,decrypt:%s -> %s", inputEncoding, outputEncoding,
						outputEncoding, inputEncoding);
				TextCipher enc = builder.inputEncoding(inputEncoding).outputEncoding(outputEncoding).build();
				TextCipher dec = builder.reverseEncoder().build();
				String cipher = enc.encrypt(plain);
				System.out.printf("[%s]%n plain = %s,cipher = %s%n", hint, plain, cipher);
				Assertions.assertEquals(plain, dec.decrypt(cipher), hint);
			}
		}
	}

}
