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

/**
 * 针对文本数据的加密
 *
 * @author CJ (power4j@outlook.com)
 * @since 1.6
 */
public interface TextEnc {

	/**
	 * 加密
	 * @param data 输入数据
	 * @return 返回密文
	 * @throws GeneralCryptoException
	 */
	default String encrypt(String data) throws GeneralCryptoException {
		return encryptEnvelope(data).getCiphertext();
	}

	/**
	 * 加密
	 * @param data 输入数据
	 * @return CiphertextEnvelope
	 * @throws GeneralCryptoException
	 */
	CiphertextDetails encryptEnvelope(String data) throws GeneralCryptoException;

}
