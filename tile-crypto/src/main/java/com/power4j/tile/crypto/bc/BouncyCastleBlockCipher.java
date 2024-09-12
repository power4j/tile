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
import com.power4j.tile.crypto.core.GeneralCryptoException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.lang.Nullable;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.6
 */
@RequiredArgsConstructor
public class BouncyCastleBlockCipher implements BlockCipher {

	private final String transformation;

	private final SecretKeySpec key;

	@Nullable
	private final IvParameterSpec iv;

	@Override
	public byte[] encrypt(byte[] data) throws GeneralCryptoException {
		try {
			Cipher cipher = createCipher(transformation);
			cipher.init(Cipher.ENCRYPT_MODE, key, iv);
			return cipher.doFinal(data);
		}
		catch (GeneralSecurityException e) {
			throw new GeneralCryptoException(e.getMessage(), e);
		}
	}

	@Override
	public byte[] decrypt(byte[] data) throws GeneralCryptoException {
		try {
			Cipher cipher = createCipher(transformation);
			cipher.init(Cipher.DECRYPT_MODE, key, iv);
			return cipher.doFinal(data);
		}
		catch (GeneralSecurityException e) {
			throw new GeneralCryptoException(e.getMessage(), e);
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
