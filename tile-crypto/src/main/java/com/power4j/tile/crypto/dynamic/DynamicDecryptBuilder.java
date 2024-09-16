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

package com.power4j.tile.crypto.dynamic;

import com.power4j.tile.crypto.bc.Spec;
import com.power4j.tile.crypto.utils.Validate;
import org.springframework.lang.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
public class DynamicDecryptBuilder {

	private final String algorithmName;

	private final String mode;

	private final String padding;

	private KeyPool keyPool;

	private KeyPool ivPool;

	private Function<byte[], byte[]> checksumCalculator;

	private Supplier<Long> paramterSupplier;

	public DynamicDecryptBuilder(String algorithmName, String mode, String padding) {
		this.algorithmName = algorithmName;
		this.mode = mode;
		this.padding = padding;
	}

	public static DynamicDecryptBuilder of(String algorithmName, String mode, String padding) {
		return new DynamicDecryptBuilder(algorithmName, mode, padding);
	}

	public static DynamicDecryptBuilder sm4(String mode, String padding) {
		return of(Spec.ALGORITHM_SM4, mode, padding);
	}

	public static DynamicDecryptBuilder sm4Ecb() {
		return sm4(Spec.MODE_ECB, Spec.PADDING_PKCS7);
	}

	public static DynamicDecryptBuilder sm4Cbc() {
		return sm4(Spec.MODE_CBC, Spec.PADDING_PKCS7);
	}

	public static DynamicDecryptBuilder sm4Cfb() {
		return sm4(Spec.MODE_CFB, Spec.PADDING_NO_PADDING);
	}

	public static DynamicDecryptBuilder sm4Ofb() {
		return sm4(Spec.MODE_OFB, Spec.PADDING_NO_PADDING);
	}

	public DynamicDecryptBuilder keyPool(KeyPool pool) {
		this.keyPool = pool;
		return this;
	}

	public DynamicDecryptBuilder ivPool(KeyPool pool) {
		this.ivPool = pool;
		return this;
	}

	public DynamicDecryptBuilder checksumCalculator(Function<byte[], byte[]> checksumCalculator) {
		this.checksumCalculator = checksumCalculator;
		return this;
	}

	public DynamicDecryptBuilder parameterSupplier(Supplier<Long> supplier) {
		this.paramterSupplier = supplier;
		return this;
	}

	public SimpleDynamicDecrypt simple() {

		Validate.notEmpty(algorithmName, "algorithmName must not be empty");
		Validate.notEmpty(mode, "mode must not be empty");
		Validate.notEmpty(padding, "padding must not be empty");
		Validate.notNull(keyPool, "keyPool must not be null");
		Validate.notNull(checksumCalculator, "checksumCalculator must not be null");

		Supplier<Long> paramSupplier = paramterSupplier == null ? () -> 0L : paramterSupplier;

		return new SimpleDynamicDecrypt(algorithmName, mode, padding, keyPool, ivPool == null ? Pools.empty() : ivPool,
				checksumCalculator, paramSupplier);

	}

	protected static boolean isEmpty(@Nullable String val) {
		return val == null || val.isEmpty();
	}

	protected static boolean isEmpty(@Nullable byte[] bytes) {
		return bytes == null || bytes.length == 0;
	}

}
