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

import com.power4j.tile.crypto.core.encode.HexEncoder;
import com.power4j.tile.crypto.utils.CryptoUtil;
import com.power4j.tile.crypto.wrapper.InputDecoder;
import com.power4j.tile.crypto.wrapper.OutputEncoder;

/**
 * 快速解密操作,适用于少量数据的解密
 *
 * @author CJ (power4j@outlook.com)
 * @since 1.6
 */
public interface QuickDec {

	/**
	 * 解密
	 * @param data 输入数据
	 * @return 返回解密数据
	 * @throws GeneralCryptoException
	 */
	default byte[] decrypt(byte[] data) throws GeneralCryptoException {
		Verified<byte[]> result = decrypt(UncheckedCipher.of(data), true);
		if (!result.isPass()) {
			Throwable throwable = result.getCause();
			if (throwable != null) {
				throw CryptoUtil.wrapGeneralCryptoException("Data verification failed", throwable);
			}
			throw new GeneralCryptoException("Data verification failed");
		}
		assert result.getData() != null;
		return result.getData();
	}

	/**
	 * 解密并验证校验和
	 * @param input 密文信息
	 * @param skipCheck 是否跳过校验
	 * @return 返回解密数据
	 * @throws GeneralCryptoException
	 */
	Verified<byte[]> decrypt(UncheckedCipher input, boolean skipCheck) throws GeneralCryptoException;

	/**
	 * 解密,支持输入和输出的转换
	 * @param decoder 输入解码器
	 * @param encoder 输出编码器
	 * @param input 输入对象
	 * @return 返回编码后的输出
	 * @param <T> 输入类型
	 * @param <R> 输出类型
	 * @throws GeneralCryptoException
	 */
	default <T, R> R decryptWith(InputDecoder<T> decoder, OutputEncoder<R> encoder, T input)
			throws GeneralCryptoException {
		byte[] data = decoder.decode(input);
		return encoder.encode(decrypt(data));
	}

	/**
	 * 数据解密,输入16进制字符串
	 * @param data 需要解密的数据,16进制字符串格式
	 * @return 解密后的数据
	 * @throws GeneralCryptoException
	 * @deprecated use {@link TextCipher } instead
	 * @see TextCipherBuilder
	 */
	default byte[] decryptHex(String data) throws GeneralCryptoException {
		return decryptWith(HexEncoder.DEFAULT::decode, OutputEncoder.NO_OP, data);
	}

}
