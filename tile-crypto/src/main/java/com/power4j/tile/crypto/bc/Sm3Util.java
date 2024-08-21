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

import com.power4j.tile.crypto.core.GeneralCryptoException;
import com.power4j.tile.crypto.wrapper.HexDecoder;
import com.power4j.tile.crypto.wrapper.HexEncoder;
import com.power4j.tile.crypto.wrapper.InputDecoder;
import com.power4j.tile.crypto.wrapper.OutputEncoder;
import lombok.experimental.UtilityClass;
import org.bouncycastle.crypto.digests.SM3Digest;

import java.util.Arrays;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
@UtilityClass
public class Sm3Util extends Provider {

	public static final int SM3_BYTES = 32;

	/**
	 * 计算SM3摘要值
	 * @param input 原文
	 * @return 摘要值，对于SM3算法来说是32字节
	 */
	public byte[] hash(byte[] input) {
		return hash(input, 0);
	}

	/**
	 * 计算SM3摘要值
	 * @param input 原文
	 * @param outputLen 对原始输出进行截断或者填充,如果 <=0 表示输出原始长度(32字节)
	 * @return 摘要值,对于SM3算法来说是32字节
	 */
	public byte[] hash(byte[] input, int outputLen) {
		SM3Digest digest = new SM3Digest();
		digest.update(input, 0, input.length);
		byte[] hash = new byte[digest.getDigestSize()];
		digest.doFinal(hash, 0);
		if (outputLen > 0) {
			hash = Arrays.copyOf(hash, outputLen);
		}
		return hash;
	}

	/**
	 * 计算SM3摘要值
	 * @param decoder 输入解码器
	 * @param encoder 输出编码器
	 * @param input 输入对象
	 * @param outputLen 对原始输出进行截断或者填充,如果 <=0 表示输出原始长度(32字节)
	 * @return 输出对象
	 * @param <T> 输入类型
	 * @param <R> 输出类型
	 * @throws GeneralCryptoException 如果解码/编码异常
	 */
	public <T, R> R hash(InputDecoder<T> decoder, OutputEncoder<R> encoder, T input, int outputLen)
			throws GeneralCryptoException {
		byte[] data = hash(decoder.decode(input), outputLen);
		return encoder.encode(data);
	}

	/**
	 * 计算SM3摘要值
	 * @param input 原文,HEX编码的二进制数据
	 * @param outputLen 对原始输出进行截断或者填充,如果 <=0 表示输出原始长度(32字节)
	 * @return 摘要值,Hex格式
	 * @throws GeneralCryptoException 如果解码/编码异常
	 */
	public String hashHex(String input, int outputLen) throws GeneralCryptoException {
		return hash(HexDecoder.DEFAULT, HexEncoder.DEFAULT, input, outputLen);
	}

	/**
	 * 计算SM3摘要值
	 * @param input 原文,HEX编码的二进制数据
	 * @return 摘要值,Hex格式
	 * @throws GeneralCryptoException 如果解码/编码异常
	 */
	public String hashHex(String input) throws GeneralCryptoException {
		return hashHex(input, 0);
	}

	/**
	 * 验证SM3
	 * @param data 原始数据
	 * @param hash sm3哈希值,如果小于完整长度(32字节),将对比前面的部分
	 * @return true 表示验证通过,false表示验证不通过
	 */
	public boolean verifyHead(byte[] data, byte[] hash) {
		byte[] ours = hash(data, hash.length);
		return Arrays.equals(hash, ours);
	}

	/**
	 * 验证SM3
	 * @param data 原始数据
	 * @param hash sm3哈希值,必须是完整长度(32字节)
	 * @return true 表示验证通过,false表示验证不通过
	 */
	public boolean verify(byte[] data, byte[] hash) {
		byte[] ours = hash(data, 0);
		return Arrays.equals(hash, ours);
	}

	/**
	 * 验证SM3
	 * @param data 原始数据,HEX编码的二进制数据
	 * @param hash sm3哈希值,HEX编码的二进制数据,如果小于完整长度(32字节),将对比前面的部分
	 * @return true 表示验证成功，false表示验证失败
	 * @throws GeneralCryptoException 如果解码/编码异常
	 */
	public boolean verifyHeadHex(String data, String hash) throws GeneralCryptoException {
		byte[] dataBytes = HexDecoder.DEFAULT.decode(data);
		byte[] hashBytes = HexDecoder.DEFAULT.decode(hash);
		return verifyHead(dataBytes, hashBytes);
	}

	/**
	 * 验证SM3
	 * @param data 原始数据,HEX编码的二进制数据
	 * @param hash sm3哈希值,HEX编码的二进制数据,必须是完整长度(32字节)
	 * @return true 表示验证成功，false表示验证失败
	 * @throws GeneralCryptoException 如果解码/编码异常
	 */
	public boolean verifyHex(String data, String hash) throws GeneralCryptoException {
		byte[] dataBytes = HexDecoder.DEFAULT.decode(data);
		byte[] hashBytes = HexDecoder.DEFAULT.decode(hash);
		return verify(dataBytes, hashBytes);
	}

}
