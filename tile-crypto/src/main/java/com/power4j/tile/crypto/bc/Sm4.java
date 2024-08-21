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
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.lang.Nullable;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;

/**
 * 安全建议<br>
 * <ul>
 * <li>不建议使用ECB模式,安全扫描会被判定为不安全代码</li>
 * <li>可以使用CBC模式,但是需保证IV在加密时必须是无法预测的</li>
 * <li>需要快速加密且对数据完整性要求较高,可以选择OFB模式</li>
 * <li>如果需要较高的安全性且对数据源的认证要求较高,可以选择CFB模式</li>
 * <li>初始化向量与密钥相比有不同的安全性需求,因此IV通常无须保密,然而在大多数情况中,不应当在使用同一密钥的情况下两次使用同一个IV</li>
 * </ul>
 *
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
public class Sm4 extends Provider implements BlockCipher {

	public static final int BLOCK_SIZE = 16;

	private static final String ALGO_NAME = "SM4";

	private static final String SM4_ECB = "SM4/ECB/PKCS7Padding";

	private static final String SM4_CBC = "SM4/CBC/PKCS7Padding";

	private static final String SM4_CFB = "SM4/CFB/NoPadding";

	private static final String SM4_OFB = "SM4/OFB/NoPadding";

	private final String transformation;

	private transient final SecretKeySpec key;

	@Nullable
	private transient final byte[] iv;

	public Sm4(String transformation, byte[] key, @Nullable byte[] iv) {
		if (key.length != BLOCK_SIZE) {
			throw new IllegalArgumentException(
					String.format("Invalid key length: %d, should be %d", key.length, BLOCK_SIZE));
		}
		this.key = createKey(key);
		this.transformation = transformation;
		this.iv = iv;
	}

	public static Sm4 useEcbWithPadding(byte[] key) throws GeneralCryptoException {
		return new Sm4(SM4_ECB, key, null);
	}

	public static Sm4 useCbcWithPadding(byte[] key, byte[] iv) throws GeneralCryptoException {
		if (iv.length != BLOCK_SIZE) {
			throw new IllegalArgumentException(
					String.format("Invalid IV length: %d, should be %d", iv.length, BLOCK_SIZE));
		}
		return new Sm4(SM4_CBC, key, iv);
	}

	public static Sm4 useCfb(byte[] key, byte[] iv) throws GeneralCryptoException {
		if (iv.length != BLOCK_SIZE) {
			throw new IllegalArgumentException(
					String.format("Invalid IV length: %d, should be %d", iv.length, BLOCK_SIZE));
		}
		return new Sm4(SM4_CFB, key, iv);
	}

	public static Sm4 useOfb(byte[] key, byte[] iv) throws GeneralCryptoException {
		if (iv.length != BLOCK_SIZE) {
			throw new IllegalArgumentException(
					String.format("Invalid IV length: %d, should be %d", iv.length, BLOCK_SIZE));
		}
		return new Sm4(SM4_OFB, key, iv);
	}

	public static Sm4 useEcbWithPadding(String hexKey) throws GeneralCryptoException {
		byte[] key = decodeHexKey(hexKey);
		return useEcbWithPadding(key);
	}

	public static Sm4 useCbcWithPadding(String hexKey, String hexIv) throws GeneralCryptoException {
		byte[] key = decodeHexKey(hexKey);
		byte[] iv = decodeHexIv(hexIv);
		return useCbcWithPadding(key, iv);
	}

	public static Sm4 useCfb(String hexKey, String hexIv) throws GeneralCryptoException {
		byte[] key = decodeHexKey(hexKey);
		byte[] iv = decodeHexIv(hexIv);
		return useCfb(key, iv);
	}

	public static Sm4 useOfb(String hexKey, String hexIv) throws GeneralCryptoException {
		byte[] key = decodeHexKey(hexKey);
		byte[] iv = decodeHexIv(hexIv);
		return useOfb(key, iv);
	}

	@Override
	public String getAlgorithmName() {
		return ALGO_NAME;
	}

	@Override
	public int getBlockSize() {
		return BLOCK_SIZE;
	}

	@Override
	public byte[] encrypt(byte[] data) throws GeneralCryptoException {
		try {
			Cipher cipher = createCipher(transformation);
			IvParameterSpec ivParameterSpec = null;
			if (iv != null) {
				ivParameterSpec = new IvParameterSpec(iv);
			}
			cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
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
			IvParameterSpec ivParameterSpec = null;
			if (iv != null) {
				ivParameterSpec = new IvParameterSpec(iv);
			}
			cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
			return cipher.doFinal(data);
		}
		catch (GeneralSecurityException e) {
			throw new GeneralCryptoException(e.getMessage(), e);
		}
	}

	protected static Cipher createCipher(String transformation) throws GeneralSecurityException {
		return Cipher.getInstance(transformation, BouncyCastleProvider.PROVIDER_NAME);
	}

	protected static SecretKeySpec createKey(byte[] key) {
		return new SecretKeySpec(key, ALGO_NAME);
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
