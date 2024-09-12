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
import org.springframework.lang.Nullable;

import javax.crypto.spec.IvParameterSpec;

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
public class Sm4 extends BouncyCastleBlockCipher implements BlockCipher {

	public static final int BLOCK_SIZE = 16;

	private static final String ALGO_NAME = "SM4";

	private static final String SM4_ECB = "SM4/ECB/PKCS7Padding";

	private static final String SM4_CBC = "SM4/CBC/PKCS7Padding";

	private static final String SM4_CFB = "SM4/CFB/NoPadding";

	private static final String SM4_OFB = "SM4/OFB/NoPadding";

	public Sm4(String transformation, byte[] key, @Nullable byte[] iv) {
		super(transformation, createKey(key, ALGO_NAME), iv == null ? null : new IvParameterSpec(iv));
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

	public String getAlgorithmName() {
		return ALGO_NAME;
	}

	public int getBlockSize() {
		return BLOCK_SIZE;
	}

}
