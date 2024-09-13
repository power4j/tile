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
import com.power4j.tile.crypto.bc.Sm3Util;
import com.power4j.tile.crypto.bc.Spec;
import com.power4j.tile.crypto.core.encode.Base64Encoder;
import com.power4j.tile.crypto.core.encode.BufferEncoder;
import com.power4j.tile.crypto.core.encode.HexEncoder;
import com.power4j.tile.crypto.core.encode.UnicodeEncoder;
import lombok.Builder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.lang.Nullable;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.6
 */
public class TextCipherBuilder {

	private static final Function<byte[], byte[]> HASH_NONE = b -> new byte[0];

	private final String algorithmName;

	private final String mode;

	private final String padding;

	private BufferEncoder inputEncoder;

	private BufferEncoder outputEncoder;

	private Supplier<byte[]> keySupplier;

	private Supplier<byte[]> ivSupplier = () -> null;

	private Function<byte[], byte[]> hashFunction = HASH_NONE;

	public TextCipherBuilder(String algorithmName, String mode, String padding) {
		this.algorithmName = algorithmName;
		this.mode = mode;
		this.padding = padding;
	}

	public static TextCipherBuilder of(String algorithmName, String mode, String padding) {
		return new TextCipherBuilder(algorithmName, mode, padding);
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

	public TextCipherBuilder keySupplier(Supplier<byte[]> supplier) {
		this.keySupplier = supplier;
		return this;
	}

	public TextCipherBuilder key(byte[] key) {
		return keySupplier(() -> key);
	}

	public TextCipherBuilder keyHex(String hex) {
		this.keySupplier = () -> {
			try {
				return Hex.decodeHex(hex);
			}
			catch (DecoderException e) {
				throw new IllegalArgumentException(String.format("Not hex encoded hex:%s", hex));
			}
		};
		return this;
	}

	public TextCipherBuilder ivSupplier(Supplier<byte[]> supplier) {
		this.ivSupplier = supplier;
		return this;
	}

	public TextCipherBuilder iv(@Nullable byte[] iv) {
		return ivSupplier(() -> iv);
	}

	public TextCipherBuilder ivHex(@Nullable String hex) {
		if (null == hex) {
			return iv(null);
		}
		try {
			return iv(Hex.decodeHex(hex));
		}
		catch (DecoderException e) {
			throw new IllegalArgumentException(String.format("Not hex encoded IV:%s", hex));
		}
	}

	public TextCipherBuilder hashFunction(Function<byte[], byte[]> hashFunction) {
		this.hashFunction = hashFunction;
		return this;
	}

	public TextCipherBuilder hashSm3() {
		this.hashFunction = (b) -> Sm3Util.hash(b, null);
		return this;
	}

	public TextCipherBuilder reversedEncoder() {
		return TextCipherBuilder.of(algorithmName, mode, padding)
			.keySupplier(keySupplier)
			.ivSupplier(ivSupplier)
			.hashFunction(hashFunction)
			.inputEncoding(outputEncoder)
			.outputEncoding(inputEncoder);
	}

	public TextCipher build() {

		if (isEmpty(algorithmName)) {
			throw new IllegalArgumentException("algorithmName must not be empty");
		}
		if (isEmpty(mode)) {
			throw new IllegalArgumentException("mode must not be empty");
		}
		if (isEmpty(padding)) {
			throw new IllegalArgumentException("padding must not be empty");
		}
		byte[] key = keySupplier.get();
		if (isEmpty(key)) {
			throw new IllegalArgumentException("key must not be empty");
		}
		byte[] iv = ivSupplier.get();
		String transformation = BouncyCastleBlockCipher.transformation(algorithmName, mode, padding);
		SecretKeySpec keySpec = BouncyCastleBlockCipher.createKey(key, algorithmName);
		IvParameterSpec ivParameterSpec = isEmpty(iv) ? null : new IvParameterSpec(iv);
		BouncyCastleBlockCipher cipher = new BouncyCastleBlockCipher(transformation, keySpec, ivParameterSpec);

		return BouncyCastleTextCipher.builder()
			.cipher(cipher)
			.inputEncoder(inputEncoder)
			.outputEncoder(outputEncoder)
			.hashFunction(hashFunction)
			.build();

	}

	protected static boolean isEmpty(@Nullable String val) {
		return val == null || val.isEmpty();
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

		private final Function<byte[], byte[]> hashFunction;

		@Override
		public String encrypt(String data) throws GeneralCryptoException {
			return outputEncoder.encode(encryptData(inputEncoder.decode(data)));
		}

		@Override
		public CiphertextEnvelope encryptEnvelope(String data) throws GeneralCryptoException {
			CipherEnvelope envelope = cipher.encryptEnvelope(inputEncoder.decode(data), hashFunction);
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
