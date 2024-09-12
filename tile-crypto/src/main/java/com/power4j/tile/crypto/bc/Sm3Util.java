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
import org.springframework.lang.Nullable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
@UtilityClass
public class Sm3Util {

	public static final int SM3_BYTES = 32;

	/**
	 * 计算SM3摘要值
	 * @param input 原文
	 * @param salt 哈希盐
	 * @return 摘要值，对于SM3算法来说是32字节
	 */
	public byte[] hash(byte[] input, @Nullable byte[] salt) {
		return hash(input, 0, salt);
	}

	/**
	 * 计算SM3摘要值
	 * @param input 原文
	 * @param salt 哈希盐
	 * @param outputLen 对原始输出进行截断或者填充,如果 <=0 表示输出原始长度(32字节)
	 * @return 摘要值,对于SM3算法来说是32字节
	 */
	public byte[] hash(byte[] input, int outputLen, @Nullable byte[] salt) {
		MessageDigest digest = messageDigest(Spec.ALGORITHM_SM3);
		if (salt != null && salt.length > 0) {
			digest.update(salt);
		}
		digest.update(input);
		if (outputLen > 0) {
			return Arrays.copyOf(digest.digest(), outputLen);
		}
		return digest.digest();
	}

	/**
	 * 计算SM3摘要值
	 * @param decoder 输入解码器
	 * @param encoder 输出编码器
	 * @param input 输入对象
	 * @param outputLen 对原始输出进行截断或者填充,如果 <=0 表示输出原始长度(32字节)
	 * @param salt 哈希盐
	 * @return 输出对象
	 * @param <T> 输入类型
	 * @param <R> 输出类型
	 * @throws GeneralCryptoException 如果解码/编码异常
	 */
	public <T, R> R hash(InputDecoder<T> decoder, OutputEncoder<R> encoder, T input, int outputLen, @Nullable T salt)
			throws GeneralCryptoException {
		byte[] data = hash(decoder.decode(input), outputLen, salt == null ? null : decoder.decode(salt));
		return encoder.encode(data);
	}

	/**
	 * 计算SM3摘要值
	 * @param input 原文,HEX编码的二进制数据
	 * @param outputLen 对原始输出进行截断或者填充,如果 <=0 表示输出原始长度(32字节)
	 * @param salt 哈希盐
	 * @return 摘要值,Hex格式
	 * @throws GeneralCryptoException 如果解码/编码异常
	 */
	public String hashHex(String input, int outputLen, @Nullable String salt) throws GeneralCryptoException {
		return hash(HexDecoder.DEFAULT, HexEncoder.DEFAULT, input, outputLen, salt);
	}

	/**
	 * 计算SM3摘要值
	 * @param input 原文,HEX编码的二进制数据
	 * @param salt 哈希盐
	 * @return 摘要值,Hex格式
	 * @throws GeneralCryptoException 如果解码/编码异常
	 */
	public String hashHex(String input, @Nullable String salt) throws GeneralCryptoException {
		return hashHex(input, 0, salt);
	}

	/**
	 * 验证SM3
	 * @param data 原始数据
	 * @param hash sm3哈希值,如果小于完整长度(32字节),将对比前面的部分
	 * @param salt 哈希盐
	 * @return true 表示验证通过,false表示验证不通过
	 */
	public boolean verifyHead(byte[] data, byte[] hash, @Nullable byte[] salt) {
		byte[] ours = hash(data, hash.length, salt);
		return Arrays.equals(hash, ours);
	}

	/**
	 * 验证SM3
	 * @param data 原始数据
	 * @param hash sm3哈希值,必须是完整长度(32字节)
	 * @param salt 哈希盐
	 * @return true 表示验证通过,false表示验证不通过
	 */
	public boolean verify(byte[] data, byte[] hash, @Nullable byte[] salt) {
		byte[] ours = hash(data, 0, salt);
		return Arrays.equals(hash, ours);
	}

	/**
	 * 验证SM3
	 * @param data 原始数据,HEX编码的二进制数据
	 * @param hash sm3哈希值,HEX编码的二进制数据,如果小于完整长度(32字节),将对比前面的部分
	 * @param salt 哈希盐,HEX编码的二进制数据
	 * @return true 表示验证成功，false表示验证失败
	 * @throws GeneralCryptoException 如果解码/编码异常
	 */
	public boolean verifyHeadHex(String data, String hash, @Nullable String salt) throws GeneralCryptoException {
		byte[] dataBytes = HexDecoder.DEFAULT.decode(data);
		byte[] hashBytes = HexDecoder.DEFAULT.decode(hash);
		byte[] saltBytes = salt == null ? null : HexDecoder.DEFAULT.decode(salt);
		return verifyHead(dataBytes, hashBytes, saltBytes);
	}

	/**
	 * 验证SM3
	 * @param data 原始数据,HEX编码的二进制数据
	 * @param hash sm3哈希值,HEX编码的二进制数据,必须是完整长度(32字节)
	 * @param salt 哈希盐
	 * @return true 表示验证成功，false表示验证失败
	 * @throws GeneralCryptoException 如果解码/编码异常
	 */
	public boolean verifyHex(String data, String hash, @Nullable String salt) throws GeneralCryptoException {
		byte[] dataBytes = HexDecoder.DEFAULT.decode(data);
		byte[] hashBytes = HexDecoder.DEFAULT.decode(hash);
		byte[] saltBytes = salt == null ? null : HexDecoder.DEFAULT.decode(salt);
		return verify(dataBytes, hashBytes, saltBytes);
	}

	static MessageDigest messageDigest(String algorithm) throws GeneralCryptoException {
		try {
			return MessageDigest.getInstance(algorithm, GlobalBouncyCastleProvider.INSTANCE.getProvider());
		}
		catch (NoSuchAlgorithmException e) {
			throw new GeneralCryptoException(e.getMessage(), e);
		}
	}

}
