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
import com.power4j.tile.crypto.core.GeneralCryptoException;
import com.power4j.tile.crypto.core.QuickCipher;
import com.power4j.tile.crypto.core.Slice;
import com.power4j.tile.crypto.core.UncheckedCipher;
import com.power4j.tile.crypto.core.Verified;
import com.power4j.tile.crypto.utils.CryptoUtil;
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
public class BouncyCastleQuickCipher implements QuickCipher {

	private final Cipher cipher;

	private final Supplier<SecretKeySpec> keySupplier;

	private final Supplier<IvParameterSpec> ivParameterSpecSupplier;

	private final String[] transformationParts;

	private final Function<byte[], byte[]> checksumCalculator;

	private final BiFunction<UncheckedCipher, byte[], Boolean> checksumVerifier;

	public BouncyCastleQuickCipher(String transformation, Supplier<SecretKeySpec> keySupplier,
			Supplier<IvParameterSpec> ivParameterSpecSupplier, Function<byte[], byte[]> checksumCalculator,
			BiFunction<UncheckedCipher, byte[], Boolean> checksumVerifier) {
		this.cipher = CryptoUtil.createCipher(transformation);
		;
		this.keySupplier = keySupplier;
		this.ivParameterSpecSupplier = ivParameterSpecSupplier;
		this.checksumCalculator = checksumCalculator;
		this.checksumVerifier = checksumVerifier;
		this.transformationParts = transformation.split("/");
	}

	@Override
	public CipherBlobDetails encrypt(byte[] data, int offset, int length) throws GeneralCryptoException {
		final IvParameterSpec ivParameter = ivParameterSpecSupplier.get();
		byte[] encrypted;
		byte[] checksum;
		try {
			checksum = checksumCalculator.apply(data);
			encrypted = oneStep(Cipher.ENCRYPT_MODE, keySupplier.get(), ivParameter, Slice.wrap(data));
		}
		catch (Exception e) {
			throw CryptoUtil.wrapGeneralCryptoException(null, e);
		}
		byte[] ivBytes = ivParameter == null ? null : ivParameter.getIV();
		return CipherBlobDetails.builder()
			.algorithm(transformationParts[0])
			.mode(transformationParts[1])
			.padding(transformationParts[2])
			.iv(ivBytes)
			.checksum(checksum)
			.cipher(encrypted)
			.build();
	}

	@Override
	public Verified<byte[]> decrypt(UncheckedCipher input, boolean skipCheck) throws GeneralCryptoException {
		byte[] decrypted;
		try {
			decrypted = oneStep(Cipher.DECRYPT_MODE, keySupplier.get(), ivParameterSpecSupplier.get(),
					input.getCipher());
		}
		catch (GeneralSecurityException e) {
			return Verified.fail(null, e);
		}
		if (skipCheck) {
			return Verified.pass(decrypted);
		}
		return checksumVerifier.apply(input, decrypted) ? Verified.pass(decrypted) : Verified.fail(decrypted, null);

	}

	protected final synchronized byte[] oneStep(int mode, SecretKeySpec key, @Nullable IvParameterSpec iv, Slice data)
			throws GeneralSecurityException {
		cipher.init(mode, key, iv);
		return cipher.doFinal(data.getData(), data.getOffset(), data.getLength());
	}

}
