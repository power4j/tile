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

import com.power4j.tile.crypto.bc.BouncyCastleBlockCipher;
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

	private final BlockCipherBuilder blockCipherBuilder;

	private BufferEncoder inputEncoder;

	private BufferEncoder outputEncoder;

	public TextCipherBuilder(BlockCipherBuilder blockCipherBuilder) {
		this.blockCipherBuilder = blockCipherBuilder;
	}

	public static TextCipherBuilder of(String algorithmName, String mode, String padding) {
		BlockCipherBuilder builder = BlockCipherBuilder.algorithm(algorithmName).mode(mode).padding(padding);
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

	public TextCipherBuilder cipher(Consumer<BlockCipherBuilder> consumer) {
		consumer.accept(blockCipherBuilder);
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
		return new TextCipherBuilder(blockCipherBuilder).inputEncoding(outputEncoder).outputEncoding(inputEncoder);
	}

	public TextCipher build() {
		BouncyCastleBlockCipher cipher = blockCipherBuilder.build();

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

		private final BouncyCastleBlockCipher cipher;

		@Override
		public String encrypt(String data) throws GeneralCryptoException {
			return outputEncoder.encode(encryptData(inputEncoder.decode(data)));
		}

		@Override
		public CiphertextEnvelope encryptEnvelope(String data) throws GeneralCryptoException {
			CipherBlobEnvelope envelope = cipher.encryptEnvelope(inputEncoder.decode(data));
			String iv = envelope.getIvOptional().map(outputEncoder::encode).orElse(null);
			return CiphertextEnvelope.builder()
				.encoding(outputEncoder.algorithm())
				.algorithm(envelope.getAlgorithm())
				.mode(envelope.getMode())
				.padding(envelope.getPadding())
				.ciphertext(outputEncoder.encode(envelope.getCipher()))
				.iv(iv)
				.checksum(outputEncoder.encode(envelope.getChecksum()))
				.build();
		}

		@Override
		public String decrypt(String data) throws GeneralCryptoException {
			return outputEncoder.encode(decryptData(inputEncoder.decode(data)));
		}

		private byte[] encryptData(byte[] data) throws GeneralCryptoException {
			return cipher.encrypt(data);
		}

		private byte[] decryptData(byte[] data) throws GeneralCryptoException {
			return cipher.decrypt(data);
		}

		private static Cipher createCipher(String transformation) throws GeneralSecurityException {
			return Cipher.getInstance(transformation, GlobalBouncyCastleProvider.INSTANCE.getProvider());
		}

	}

}
