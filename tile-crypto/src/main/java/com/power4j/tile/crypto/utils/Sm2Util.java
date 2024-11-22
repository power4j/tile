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

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.tuple.Pair;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi;
import org.bouncycastle.jce.spec.ECParameterSpec;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.SecureRandom;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
@UtilityClass
public class Sm2Util {

	public static final X9ECParameters X_9_EC_PARAM = GMNamedCurves.getByName("sm2p256v1");

	/** SM2曲线参数 */
	public static final ECDomainParameters P256V1_DOMAIN_PARAM = new ECDomainParameters(Sm2Util.X_9_EC_PARAM.getCurve(),
			Sm2Util.X_9_EC_PARAM.getG(), Sm2Util.X_9_EC_PARAM.getN());

	/**
	 * 生成sm2密钥对
	 * <ul>
	 * <li>BC库的公钥:64+1个字节(04标志位),BC库的私钥:32字节</li>
	 * <li>SM2秘钥的组成部分:私钥D,公钥X.公钥Y,他们都可以用长度为64的16进制的HEX串表示</li>
	 * <li>SM2公钥并不是直接由X+Y表示,而是额外添加了一个头,当启用压缩时:公钥=有头+公钥X,即省略了公钥Y的部分</li>
	 * </ul>
	 * @return KeyPair
	 */
	public static KeyPair genKeyPair() throws InvalidAlgorithmParameterException {
		// 创建密钥生成器
		KeyPairGeneratorSpi.EC spi = new KeyPairGeneratorSpi.EC();
		// 构造spec参数
		ECParameterSpec parameterSpec = new ECParameterSpec(X_9_EC_PARAM.getCurve(), X_9_EC_PARAM.getG(),
				X_9_EC_PARAM.getN());
		SecureRandom secureRandom = new SecureRandom();
		// 初始化生成器,带上随机数
		spi.initialize(parameterSpec, secureRandom);
		// 生成密钥对
		return spi.generateKeyPair();
	}

	/**
	 * 取SM2公钥 Q
	 * @param keyPair SM2 密钥对
	 * @param compress 是否压缩
	 * @return
	 */
	public byte[] extractPublicKeyBlob(KeyPair keyPair, boolean compress) {
		BCECPublicKey key = (BCECPublicKey) keyPair.getPublic();
		// 公钥前面的02或者03表示是压缩公钥,04表示未压缩公钥,04的时候,可以去掉前面的04
		return key.getQ().getEncoded(compress);
	}

	/**
	 * 取SM2私钥 D
	 * @param keyPair SM2 密钥对
	 * @return BigInteger
	 */
	public BigInteger extractPrivateKeyBlob(KeyPair keyPair) {
		return ((BCECPrivateKey) keyPair.getPrivate()).getD();
	}

	/**
	 * 取SM2私钥D和公钥 Q
	 * @param keyPair SM2 密钥对
	 * @param compress 是否压缩公钥
	 * @return 私钥D和公钥 Q
	 */
	public Pair<BigInteger, byte[]> extractKeyBlob(KeyPair keyPair, boolean compress) {
		return Pair.of(extractPrivateKeyBlob(keyPair), extractPublicKeyBlob(keyPair, compress));
	}

}
