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

import com.power4j.tile.crypto.wrapper.HexDecoder;
import com.power4j.tile.crypto.wrapper.HexEncoder;
import com.power4j.tile.crypto.wrapper.InputDecoder;
import com.power4j.tile.crypto.wrapper.OutputEncoder;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
public interface BlockCipher {

	/**
	 * 加密
	 * @param data 输入数据
	 * @return 返回密文
	 * @throws GeneralCryptoException
	 */
	byte[] encrypt(byte[] data) throws GeneralCryptoException;

	/**
	 * 解密
	 * @param data 输入数据
	 * @return 返回解密数据
	 * @throws GeneralCryptoException
	 */
	byte[] decrypt(byte[] data) throws GeneralCryptoException;

	/**
	 * 加密,支持输入和输出的转换
	 * @param decoder 输入解码器
	 * @param encoder 输出编码器
	 * @param input 输入对象
	 * @return 返回编码后的输出
	 * @param <T> 输入类型
	 * @param <R> 输出类型
	 * @throws GeneralCryptoException
	 */
	default <T, R> R encryptWith(InputDecoder<T> decoder, OutputEncoder<R> encoder, T input)
			throws GeneralCryptoException {
		byte[] data = decoder.decode(input);
		return encoder.encode(encrypt(data));
	}

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
	 * 数据加密,输出16进制字符串
	 * @param data 需要加密的数据
	 * @return 加密后的16进制字符串
	 * @throws GeneralCryptoException
	 */
	default String encryptHex(byte[] data) throws GeneralCryptoException {
		return encryptWith(InputDecoder.NO_OP, HexEncoder.DEFAULT, data);
	}

	/**
	 * 数据解密,输入16进制字符串
	 * @param data 需要解密的数据,16进制字符串格式
	 * @return 解密后的数据
	 * @throws GeneralCryptoException
	 */
	default byte[] decryptHex(String data) throws GeneralCryptoException {
		return decryptWith(HexDecoder.DEFAULT, OutputEncoder.NO_OP, data);
	}

}
