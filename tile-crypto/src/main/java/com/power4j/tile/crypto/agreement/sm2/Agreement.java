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

package com.power4j.tile.crypto.agreement.sm2;

import com.power4j.tile.crypto.utils.Sm2Util;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithID;
import org.bouncycastle.crypto.params.SM2KeyExchangePublicParameters;
import org.bouncycastle.util.encoders.Hex;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
@UtilityClass
public class Agreement {

	/**
	 * 发起方生成协商数据
	 * @param context 密钥协商上下文
	 * @return 返回密钥协商数据,需要发送给密钥协商的另一方
	 */
	public ExchangeRequest init(ExchangeContext context) {
		return context.share(null);
	}

	/**
	 * 响应方生成密钥和协商数据
	 * @param context 密钥协商上下文
	 * @param request 密钥协商数据,由init方法产生
	 * @return 密钥和协商数据
	 */
	public Pair<byte[], ExchangeRequest> generateKeyAndExchangeRequest(ExchangeContext context, int keyLen,
			ExchangeRequest request) {
		byte[][] result = context.exchanger(false)
			.calculateKeyWithConfirmation(keyLen, null, calculateKeyParam(request));
		assert result.length >= 2;
		byte[] key = result[0];
		byte[] confirm = result[1];
		return Pair.of(key, context.share(Hex.toHexString(confirm)));
	}

	/**
	 * 发起方生成密钥并验证
	 * @param context 密钥协商上下文
	 * @param request 密钥协商数据,由init方法产生
	 * @return 密钥和协商数据
	 */
	public byte[] generateKeyAndVerify(ExchangeContext context, int keyLen, ExchangeRequest request) {
		byte[] tag = null;
		if (StringUtils.isNotEmpty(request.getConfirmation())) {
			tag = Hex.decodeStrict(request.getConfirmation());
		}
		byte[][] result = context.exchanger(true).calculateKeyWithConfirmation(keyLen, tag, calculateKeyParam(request));
		return result[0];
	}

	ParametersWithID calculateKeyParam(ExchangeRequest request) {
		ECDomainParameters domainParam = Sm2Util.P256V1_DOMAIN_PARAM;
		ECPublicKeyParameters staticPubKey = new ECPublicKeyParameters(
				domainParam.getCurve().decodePoint(Hex.decodeStrict(request.getStaticPublicKey())), domainParam);
		ECPublicKeyParameters ephemeralPublicKey = new ECPublicKeyParameters(
				domainParam.getCurve().decodePoint(Hex.decodeStrict(request.getEphemeralPublicKey())), domainParam);
		byte[] userId = new byte[0];
		if (StringUtils.isNotEmpty(request.getUserId())) {
			userId = Hex.decodeStrict(request.getUserId());
		}
		SM2KeyExchangePublicParameters exchangePublicParameters = new SM2KeyExchangePublicParameters(staticPubKey,
				ephemeralPublicKey);
		return new ParametersWithID(exchangePublicParameters, userId);
	}

}
