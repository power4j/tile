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
import com.power4j.tile.crypto.bc.Spec;
import com.power4j.tile.crypto.utils.CryptoUtil;
import com.power4j.tile.crypto.utils.Validate;
import org.springframework.lang.Nullable;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * {@link QuickCipher} Builder<br/>
 * <ul>
 * <li>algorithmName: 密码算法的名称,如 AES,SM4</li>
 * <li>mode: 加密模式,如 ECB, CBC</li>
 * <li>padding: 填充算法名称,如 PKCS7Padding</li>
 * <li>secretKeySpecSupplier: 密钥生成器</li>
 * <li>ivParameterSpecSupplier: 可选,初始化向量生成器,有些密钥算法不需要</li>
 * <li>checksumCalculator: 可选,校验和计算函数,如需输出校验需要指定</li>
 * <li>checksumVerifier: 可选,校验和校验函数,需要校验解密数据数据时指定</li>
 * </ul>
 *
 * @author CJ (power4j@outlook.com)
 * @since 1.6
 * @see Spec
 * @see TextCipherBuilder
 */
public class QuickCipherBuilder {

	private final String algorithmName;

	private String mode;

	private String padding;

	private Supplier<SecretKeySpec> secretKeySpecSupplier;

	private Supplier<IvParameterSpec> ivParameterSpecSupplier;

	private Function<byte[], byte[]> checksumCalculator;

	private BiFunction<UncheckedCipher, byte[], Boolean> checksumVerifier;

	QuickCipherBuilder(String algorithmName) {
		this.algorithmName = algorithmName;
	};

	public static QuickCipherBuilder algorithm(String algorithmName) {
		return new QuickCipherBuilder(algorithmName);
	}

	public QuickCipherBuilder mode(String mode) {
		this.mode = mode;
		return this;
	}

	public QuickCipherBuilder padding(String padding) {
		this.padding = padding;
		return this;
	}

	public QuickCipherBuilder secretKeySpecSupplier(Supplier<SecretKeySpec> supplier) {
		this.secretKeySpecSupplier = supplier;
		return this;
	}

	public QuickCipherBuilder secretKey(byte[] key) {
		return secretKeySpecSupplier(() -> CryptoUtil.createKey(key, algorithmName));
	}

	public QuickCipherBuilder secretKeyHex(String val) {
		this.secretKeySpecSupplier = () -> CryptoUtil.createKey(CryptoUtil.decodeHex(val, null), algorithmName);
		return this;
	}

	public QuickCipherBuilder secretKeyBase64(String val) {
		this.secretKeySpecSupplier = () -> CryptoUtil.createKey(CryptoUtil.decodeBase64(val, null), algorithmName);
		return this;
	}

	public QuickCipherBuilder ivParameterSpecSupplier(Supplier<IvParameterSpec> supplier) {
		this.ivParameterSpecSupplier = supplier;
		return this;
	}

	public QuickCipherBuilder ivParameter(@Nullable byte[] iv) {
		if (iv == null) {
			return ivParameterSpecSupplier(() -> null);
		}
		return ivParameterSpecSupplier(() -> new IvParameterSpec(iv));
	}

	public QuickCipherBuilder ivParameterHex(@Nullable String val) {
		if (val == null) {
			return ivParameterSpecSupplier(() -> null);
		}
		return ivParameterSpecSupplier(() -> new IvParameterSpec(CryptoUtil.decodeHex(val, null)));
	}

	public QuickCipherBuilder ivParameterBase64(String val) {
		this.ivParameterSpecSupplier = () -> new IvParameterSpec(CryptoUtil.decodeBase64(val, null));
		return this;
	}

	public QuickCipherBuilder checksumCalculator(@Nullable Function<byte[], byte[]> calculator) {
		this.checksumCalculator = calculator;
		return this;
	}

	public QuickCipherBuilder sm3ChecksumCalculator() {
		this.checksumCalculator = CryptoUtil.SM3_CHECKSUM_CALCULATOR;
		return this;
	}

	public QuickCipherBuilder checksumVerifier(BiFunction<UncheckedCipher, byte[], Boolean> verifier) {
		this.checksumVerifier = verifier;
		return this;
	}

	public QuickCipherBuilder sm3ChecksumVerifier() {
		this.checksumVerifier = CryptoUtil.SM3_CHECKSUM_VERIFIER;
		return this;
	}

	public BouncyCastleQuickCipher build() {
		Validate.notEmpty(algorithmName, "algorithmName must not be empty");
		Validate.notEmpty(mode, "mode must not be empty");
		Validate.notEmpty(padding, "padding must not be empty");
		Validate.notNull(secretKeySpecSupplier, "secretKeySpecSupplier must not be null");

		Function<byte[], byte[]> calculator = checksumCalculator == null ? CryptoUtil.EMPTY_CHECKSUM_CALCULATOR
				: checksumCalculator;
		BiFunction<UncheckedCipher, byte[], Boolean> verifier = checksumVerifier == null
				? CryptoUtil.IGNORED_CHECKSUM_VERIFIER : checksumVerifier;
		Supplier<IvParameterSpec> ivSpecSupplier = ivParameterSpecSupplier == null ? () -> null
				: ivParameterSpecSupplier;
		String transformation = CryptoUtil.transformation(algorithmName, mode, padding);
		return new BouncyCastleQuickCipher(transformation, secretKeySpecSupplier, ivSpecSupplier, calculator, verifier);
	}

}
