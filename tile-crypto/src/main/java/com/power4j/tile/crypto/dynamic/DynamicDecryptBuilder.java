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

import com.power4j.tile.crypto.bc.BouncyCastleBlockCipher;
import com.power4j.tile.crypto.bc.Spec;
import org.springframework.lang.Nullable;

import java.util.function.Function;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
public class DynamicDecryptBuilder {

	private final String algorithmName;

	private final String mode;

	private final String padding;

	@Nullable
	private KeyPool keyPool;

	@Nullable
	private KeyPool ivPool;

	private Function<byte[], byte[]> hashFunc;

	public DynamicDecryptBuilder(String algorithmName, String mode, String padding) {
		this.algorithmName = algorithmName;
		this.mode = mode;
		this.padding = padding;
	}

	public static DynamicDecryptBuilder of(String algorithmName, String mode, String padding) {
		return new DynamicDecryptBuilder(algorithmName, mode, padding);
	}

	public static DynamicDecryptBuilder sm4Ecb() {
		return of(Spec.ALGORITHM_SM4, Spec.MODE_ECB, Spec.PADDING_PKCS7);
	}

	public static DynamicDecryptBuilder sm4Cbc() {
		return of(Spec.ALGORITHM_SM4, Spec.MODE_CBC, Spec.PADDING_PKCS7);
	}

	public static DynamicDecryptBuilder sm4Cfb() {
		return of(Spec.ALGORITHM_SM4, Spec.MODE_CFB, Spec.PADDING_NO_PADDING);
	}

	public static DynamicDecryptBuilder sm4Ofb() {
		return of(Spec.ALGORITHM_SM4, Spec.MODE_OFB, Spec.PADDING_NO_PADDING);
	}

	public DynamicDecryptBuilder keyPool(KeyPool pool) {
		this.keyPool = pool;
		return this;
	}

	public DynamicDecryptBuilder ivPool(KeyPool pool) {
		this.ivPool = pool;
		return this;
	}

	public DynamicDecryptBuilder hashFunc(Function<byte[], byte[]> func) {
		this.hashFunc = func;
		return this;
	}

	public SimpleDynamicDecrypt simple() {

		if (isEmpty(algorithmName)) {
			throw new IllegalArgumentException("algorithmName must not be empty");
		}
		if (isEmpty(mode)) {
			throw new IllegalArgumentException("mode must not be empty");
		}
		if (isEmpty(padding)) {
			throw new IllegalArgumentException("padding must not be empty");
		}

		if (keyPool == null) {
			throw new IllegalArgumentException("key pool must not be null");
		}
		String transformation = BouncyCastleBlockCipher.transformation(algorithmName, mode, padding);
		return new SimpleDynamicDecrypt(transformation, keyPool, ivPool == null ? Pools.empty() : ivPool, hashFunc);

	}

	protected static boolean isEmpty(@Nullable String val) {
		return val == null || val.isEmpty();
	}

	protected static boolean isEmpty(@Nullable byte[] bytes) {
		return bytes == null || bytes.length == 0;
	}

}
