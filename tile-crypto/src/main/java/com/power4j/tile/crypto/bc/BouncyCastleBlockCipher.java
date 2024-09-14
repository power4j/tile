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
import com.power4j.tile.crypto.core.CipherBlob;
import com.power4j.tile.crypto.core.CipherBlobEnvelope;
import com.power4j.tile.crypto.core.GeneralCryptoException;
import com.power4j.tile.crypto.core.Verified;
import com.power4j.tile.crypto.utils.CryptoUtil;

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

	private final Function<byte[], byte[]> checksumCalculator;

	private final BiFunction<CipherBlob, byte[], Boolean> checksumVerifier;

	public BouncyCastleBlockCipher(String transformation, Supplier<SecretKeySpec> keySupplier,
			Supplier<IvParameterSpec> ivParameterSpecSupplier, Function<byte[], byte[]> checksumCalculator,
			BiFunction<CipherBlob, byte[], Boolean> checksumVerifier) {
		this.transformation = transformation;
		this.keySupplier = keySupplier;
		this.ivParameterSpecSupplier = ivParameterSpecSupplier;
		this.checksumCalculator = checksumCalculator;
		this.checksumVerifier = checksumVerifier;
		this.transformationParts = transformation.split("/");
	}

	@Override
	public CipherBlobEnvelope encryptEnvelope(byte[] data) throws GeneralCryptoException {
		final IvParameterSpec ivParameter = ivParameterSpecSupplier.get();
		byte[] encrypted;
		byte[] checksum;
		try {
			checksum = checksumCalculator.apply(data);
			Cipher cipher = CryptoUtil.createCipher(transformation);
			cipher.init(Cipher.ENCRYPT_MODE, keySupplier.get(), ivParameter);
			encrypted = cipher.doFinal(data);
		}
		catch (GeneralSecurityException e) {
			throw new GeneralCryptoException(e.getMessage(), e);
		}
		byte[] ivBytes = ivParameter == null ? null : ivParameter.getIV();
		return CipherBlobEnvelope.builder()
			.algorithm(transformationParts[0])
			.mode(transformationParts[1])
			.padding(transformationParts[2])
			.iv(ivBytes)
			.checksum(checksum)
			.cipher(encrypted)
			.build();
	}

	@Override
	public Verified<byte[]> decrypt(CipherBlob store, boolean skipCheck) throws GeneralCryptoException {
		byte[] decrypted;
		try {
			Cipher cipher = CryptoUtil.createCipher(transformation);
			cipher.init(Cipher.DECRYPT_MODE, keySupplier.get(), ivParameterSpecSupplier.get());
			decrypted = cipher.doFinal(store.getCipher());
		}
		catch (GeneralSecurityException e) {
			return Verified.fail(null, e);
		}
		if (skipCheck) {
			return Verified.pass(decrypted);
		}
		return checksumVerifier.apply(store, decrypted) ? Verified.pass(decrypted) : Verified.fail(decrypted, null);

	}

}
