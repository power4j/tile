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

import com.power4j.tile.crypto.bc.BouncyCastleQuickCipher;
import com.power4j.tile.crypto.bc.GlobalBouncyCastleProvider;
import com.power4j.tile.crypto.bc.Spec;
import com.power4j.tile.crypto.core.encode.Base64Encoder;
import com.power4j.tile.crypto.core.encode.BufferEncoder;
import com.power4j.tile.crypto.core.encode.HexEncoder;
import com.power4j.tile.crypto.core.encode.UnicodeEncoder;
import lombok.Builder;
import org.springframework.lang.Nullable;

import javax.crypto.Cipher;
import java.security.GeneralSecurityException;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.6
 */
public class TextCipherBuilder {

	private static final Function<byte[], byte[]> HASH_NONE = b -> new byte[0];

	private final QuickCipherBuilder quickCipherBuilder;

	private BufferEncoder inputEncoder;

	private BufferEncoder outputEncoder;

	public TextCipherBuilder(QuickCipherBuilder quickCipherBuilder) {
		this.quickCipherBuilder = quickCipherBuilder;
	}

	public static TextCipherBuilder of(String algorithmName, String mode, String padding) {
		QuickCipherBuilder builder = QuickCipherBuilder.algorithm(algorithmName).mode(mode).padding(padding);
		return new TextCipherBuilder(builder);
	}

	public static TextCipherBuilder sm4Ecb() {
		return of(Spec.ALGORITHM_SM4, Spec.MODE_ECB, Spec.PADDING_PKCS7);
	}

	public static TextCipherBuilder sm4Cbc() {
		return of(Spec.ALGORITHM_SM4, Spec.MODE_CBC, Spec.PADDING_PKCS7);
	}

	public static TextCipherBuilder sm4Cfb() {
		return of(Spec.ALGORITHM_SM4, Spec.MODE_CFB, Spec.PADDING_NO_PADDING);
	}

	public static TextCipherBuilder sm4Ofb() {
		return of(Spec.ALGORITHM_SM4, Spec.MODE_OFB, Spec.PADDING_NO_PADDING);
	}

	public TextCipherBuilder cipher(Consumer<QuickCipherBuilder> consumer) {
		consumer.accept(quickCipherBuilder);
		return this;
	}

	public TextCipherBuilder inputEncoding(BufferEncoding inputEncoding) {
		this.inputEncoder = getEncoder(inputEncoding);
		return this;
	}

	public TextCipherBuilder inputEncoding(BufferEncoder encoder) {
		this.inputEncoder = encoder;
		return this;
	}

	public TextCipherBuilder outputEncoding(BufferEncoding outputEncoding) {
		this.outputEncoder = getEncoder(outputEncoding);
		return this;
	}

	public TextCipherBuilder outputEncoding(BufferEncoder encoder) {
		this.outputEncoder = encoder;
		return this;
	}

	public TextCipherBuilder reversedEncoder() {
		return new TextCipherBuilder(quickCipherBuilder).inputEncoding(outputEncoder).outputEncoding(inputEncoder);
	}

	public TextCipher build() {
		BouncyCastleQuickCipher cipher = quickCipherBuilder.build();

		return BouncyCastleTextCipher.builder()
			.cipher(cipher)
			.inputEncoder(inputEncoder)
			.outputEncoder(outputEncoder)
			.build();

	}

	protected static boolean isEmpty(@Nullable byte[] bytes) {
		return bytes == null || bytes.length == 0;
	}

	protected BufferEncoder getEncoder(BufferEncoding encoding) {
		switch (encoding) {
			case ASCII:
				return UnicodeEncoder.US_ASCII;
			case UTF_8:
				return UnicodeEncoder.UTF_8;
			case UTF_16LE:
				return UnicodeEncoder.UTF_16LE;
			case UTF_16BE:
				return UnicodeEncoder.UTF_16BE;
			case HEX:
				return HexEncoder.DEFAULT;
			case BASE64:
				return Base64Encoder.BASIC;
			case BASE64_URL:
				return Base64Encoder.URL_SAFE;
			default:
				throw new IllegalArgumentException("Unsupported encoding: " + encoding);
		}
	}

	@Builder
	static class BouncyCastleTextCipher implements TextCipher {

		private final BufferEncoder inputEncoder;

		private final BufferEncoder outputEncoder;

		private final BouncyCastleQuickCipher cipher;

		@Override
		public String encrypt(String data) throws GeneralCryptoException {
			return outputEncoder.encode(encryptData(inputEncoder.decode(data)));
		}

		@Override
		public CiphertextDetails encryptEnvelope(String data) throws GeneralCryptoException {
			CipherBlobDetails details = cipher.encrypt(inputEncoder.decode(data));
			String iv = details.getIvOptional().map(outputEncoder::encode).orElse(null);
			return CiphertextDetails.builder()
				.encoding(outputEncoder.algorithm())
				.algorithm(details.getAlgorithm())
				.mode(details.getMode())
				.padding(details.getPadding())
				.ciphertext(outputEncoder.encode(details.getCipher()))
				.iv(iv)
				.checksum(outputEncoder.encode(details.getChecksum()))
				.build();
		}

		@Override
		public String decrypt(String data) throws GeneralCryptoException {
			return outputEncoder.encode(decryptData(inputEncoder.decode(data)));
		}

		private byte[] encryptData(byte[] data) throws GeneralCryptoException {
			return cipher.encrypt(data).getCipher();
		}

		private byte[] decryptData(byte[] data) throws GeneralCryptoException {
			return cipher.decrypt(data);
		}

		private static Cipher createCipher(String transformation) throws GeneralSecurityException {
			return Cipher.getInstance(transformation, GlobalBouncyCastleProvider.INSTANCE.getProvider());
		}

	}

}
