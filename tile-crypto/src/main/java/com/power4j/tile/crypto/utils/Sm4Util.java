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

package com.power4j.tile.crypto.utils;

import com.power4j.tile.crypto.bc.Spec;
import com.power4j.tile.crypto.core.GeneralCryptoException;
import com.power4j.tile.crypto.core.QuickCipher;
import com.power4j.tile.crypto.core.QuickCipherBuilder;
import lombok.experimental.UtilityClass;

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
 * @since 1.6
 */
@UtilityClass
public class Sm4Util {

	public static final int BLOCK_SIZE = 16;

	public QuickCipher useEcbWithPadding(byte[] key) throws GeneralCryptoException {
		return builderWithVerifySupport(Spec.MODE_ECB, Spec.PADDING_PKCS7).secretKey(key).build();
	}

	public QuickCipher useCbcWithPadding(byte[] key, byte[] iv) throws GeneralCryptoException {
		if (iv.length != BLOCK_SIZE) {
			throw new IllegalArgumentException(
					String.format("Invalid IV length: %d, should be %d", iv.length, BLOCK_SIZE));
		}
		return builderWithVerifySupport(Spec.MODE_CBC, Spec.PADDING_PKCS7).secretKey(key).ivParameter(iv).build();
	}

	public QuickCipher useCfb(byte[] key, byte[] iv) throws GeneralCryptoException {
		if (iv.length != BLOCK_SIZE) {
			throw new IllegalArgumentException(
					String.format("Invalid IV length: %d, should be %d", iv.length, BLOCK_SIZE));
		}
		return builderWithVerifySupport(Spec.MODE_CFB, Spec.PADDING_NO_PADDING).secretKey(key).ivParameter(iv).build();
	}

	public QuickCipher useOfb(byte[] key, byte[] iv) throws GeneralCryptoException {
		if (iv.length != BLOCK_SIZE) {
			throw new IllegalArgumentException(
					String.format("Invalid IV length: %d, should be %d", iv.length, BLOCK_SIZE));
		}
		return builderWithVerifySupport(Spec.MODE_OFB, Spec.PADDING_NO_PADDING).secretKey(key).ivParameter(iv).build();
	}

	public QuickCipher useEcbWithPadding(String hexKey) throws GeneralCryptoException {
		byte[] key = CryptoUtil.decodeHex(hexKey, null);
		return useEcbWithPadding(key);
	}

	public QuickCipher useCbcWithPadding(String hexKey, String hexIv) throws GeneralCryptoException {
		byte[] key = CryptoUtil.decodeHex(hexKey, null);
		byte[] iv = CryptoUtil.decodeHex(hexIv, null);
		return useCbcWithPadding(key, iv);
	}

	public QuickCipher useCfb(String hexKey, String hexIv) throws GeneralCryptoException {
		byte[] key = CryptoUtil.decodeHex(hexKey, null);
		byte[] iv = CryptoUtil.decodeHex(hexIv, null);
		return useCfb(key, iv);
	}

	public QuickCipher useOfb(String hexKey, String hexIv) throws GeneralCryptoException {
		byte[] key = CryptoUtil.decodeHex(hexKey, null);
		byte[] iv = CryptoUtil.decodeHex(hexIv, null);
		return useOfb(key, iv);
	}

	public QuickCipherBuilder builder(String mode, String padding) {
		return QuickCipherBuilder.algorithm(Spec.ALGORITHM_SM4).mode(mode).padding(padding);
	}

	public QuickCipherBuilder builderWithVerifySupport(String mode, String padding) {
		return builder(mode, padding).sm3ChecksumCalculator().sm3ChecksumVerifier();
	}

}
