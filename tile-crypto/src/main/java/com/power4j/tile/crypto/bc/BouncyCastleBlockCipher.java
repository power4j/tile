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

import com.power4j.tile.crypto.core.BlockCipher;
import com.power4j.tile.crypto.core.CipherEnvelope;
import com.power4j.tile.crypto.core.CipherStore;
import com.power4j.tile.crypto.core.GeneralCryptoException;
import com.power4j.tile.crypto.core.Verified;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.lang.Nullable;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.6
 */
public class BouncyCastleBlockCipher implements BlockCipher {

	private final String transformation;

	private final Supplier<SecretKeySpec> keySupplier;

	private final Supplier<IvParameterSpec> ivParameterSpecSupplier;

	private final String[] transformationParts;

	public BouncyCastleBlockCipher(String transformation, SecretKeySpec key, @Nullable IvParameterSpec iv) {
		this(transformation, () -> key, () -> iv);
	}

	public BouncyCastleBlockCipher(String transformation, byte[] key, @Nullable byte[] iv) {
		this.transformation = transformation;
		this.transformationParts = transformation.split("/");
		this.keySupplier = () -> createKey(key, transformationParts[0]);
		this.ivParameterSpecSupplier = () -> iv == null ? null : new IvParameterSpec(iv);
	}

	public BouncyCastleBlockCipher(String transformation, Supplier<SecretKeySpec> keySupplier,
			Supplier<IvParameterSpec> ivParameterSpecSupplier) {
		this.transformation = transformation;
		this.keySupplier = keySupplier;
		this.ivParameterSpecSupplier = ivParameterSpecSupplier;
		this.transformationParts = transformation.split("/");
	}

	@Override
	public CipherEnvelope encryptEnvelope(byte[] data, Function<byte[], byte[]> hash) throws GeneralCryptoException {
		final IvParameterSpec ivParameter = ivParameterSpecSupplier.get();
		byte[] encrypted;
		byte[] checksum;
		try {
			checksum = hash.apply(data);
			Cipher cipher = createCipher(transformation);
			cipher.init(Cipher.ENCRYPT_MODE, keySupplier.get(), ivParameter);
			encrypted = cipher.doFinal(data);
		}
		catch (GeneralSecurityException e) {
			throw new GeneralCryptoException(e.getMessage(), e);
		}
		byte[] ivBytes = ivParameter == null ? null : ivParameter.getIV();
		return CipherEnvelope.builder()
			.algorithm(transformationParts[0])
			.mode(transformationParts[1])
			.padding(transformationParts[2])
			.iv(ivBytes)
			.checksum(checksum)
			.cipher(encrypted)
			.build();
	}

	@Override
	public Verified<byte[]> decryptWithCheck(CipherStore store, BiFunction<CipherStore, byte[], Boolean> verifier)
			throws GeneralCryptoException {
		byte[] decrypted;
		try {
			Cipher cipher = createCipher(transformation);
			cipher.init(Cipher.DECRYPT_MODE, keySupplier.get(), ivParameterSpecSupplier.get());
			decrypted = cipher.doFinal(store.getCipher());
		}
		catch (GeneralSecurityException e) {
			return Verified.fail(null);
		}
		if (verifier.apply(store, decrypted)) {
			return Verified.pass(decrypted);
		}
		else {
			return Verified.fail(decrypted);
		}

	}

	public static Cipher createCipher(String transformation) throws GeneralSecurityException {
		return Cipher.getInstance(transformation, GlobalBouncyCastleProvider.INSTANCE.getProvider());
	}

	public static SecretKeySpec createKey(byte[] key, String algorithmName) {
		return new SecretKeySpec(key, algorithmName);
	}

	public static String transformation(String algorithmName, String mode, String padding) {
		return algorithmName + "/" + mode + "/" + padding;
	}

	protected static byte[] decodeHexKey(String hex) {
		try {
			return Hex.decodeHex(hex);
		}
		catch (DecoderException e) {
			throw new IllegalArgumentException(String.format("Not hex encoded key:%s", hex));
		}
	}

	protected static byte[] decodeHexIv(String iv) {

		try {
			return Hex.decodeHex(iv);
		}
		catch (DecoderException e) {
			throw new IllegalArgumentException(String.format("Not hex encoded iv:%s", iv));
		}
	}

}
