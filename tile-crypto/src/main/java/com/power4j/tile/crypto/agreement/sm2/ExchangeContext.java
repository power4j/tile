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
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bouncycastle.crypto.agreement.SM2KeyExchange;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithID;
import org.bouncycastle.crypto.params.SM2KeyExchangePrivateParameters;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.lang.Nullable;

import java.io.PrintStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.Objects;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
@Getter
@Builder
public class ExchangeContext {

	private final static boolean COMPRESS = false;

	private final ECPrivateKeyParameters staticPrivateKey;

	private final ECPublicKeyParameters staticPublicKey;

	private final ECPrivateKeyParameters ephemeralPrivateKey;

	private final ECPublicKeyParameters ephemeralPublicKey;

	private final byte[] userId;

	/**
	 * 密钥交换引擎
	 * @param initiator 是否为发起方
	 * @return SM2KeyExchange
	 */
	public SM2KeyExchange exchanger(boolean initiator) {
		SM2KeyExchangePrivateParameters privateParameters = new SM2KeyExchangePrivateParameters(initiator,
				staticPrivateKey, ephemeralPrivateKey);

		SM2KeyExchange exchange = new SM2KeyExchange();
		exchange.init(new ParametersWithID(privateParameters, userId));
		return exchange;
	}

	/**
	 * 导出临时密钥等信息,以便于以后恢复 context,这个方法一般是发起方使用
	 * @return ExchangeResumeStore
	 */
	public ExchangeStageStore stageStore() {
		ExchangeStageStore store = new ExchangeStageStore();
		store.setEphemeralPrivateKey(ephemeralPrivateKey.getD().toString(16));
		store.setEphemeralPublicKey(Hex.toHexString(ephemeralPublicKey.getQ().getEncoded(COMPRESS)));
		return store;
	}

	/**
	 * 生成密钥交换请求
	 * @param confirmation 验证信息,响应方需要将次信息发送给发起方
	 * @return ExchangeRequest
	 */
	public ExchangeRequest share(@Nullable String confirmation) {
		ExchangeRequest data = new ExchangeRequest();
		data.setStaticPublicKey(Hex.toHexString(staticPublicKey.getQ().getEncoded(COMPRESS)));
		data.setEphemeralPublicKey(Hex.toHexString(ephemeralPublicKey.getQ().getEncoded(COMPRESS)));
		data.setUserId(Hex.toHexString(userId));
		data.setConfirmation(confirmation);
		return data;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private byte[] staticPrivateKey;

		private byte[] staticPublicKey;

		private byte[] ephemeralPrivateKey;

		private byte[] ephemeralPublicKey;

		private byte[] userId;

		Builder() {
			// private use
		}

		@SneakyThrows
		public Builder randomEphemeralKey() {
			KeyPair keyPair = Sm2Util.genKeyPair();
			return ephemeralPrivateKey(Sm2Util.extractPrivateKeyBlob(keyPair).toByteArray())
				.ephemeralPublicKey(Sm2Util.extractPublicKeyBlob(keyPair, COMPRESS));
		}

		public Builder ephemeralPrivateKey(byte[] val) {
			this.ephemeralPrivateKey = Objects.requireNonNull(val);
			return this;
		}

		public Builder ephemeralPrivateKeyHex(String val) {
			this.ephemeralPrivateKey = Hex.decodeStrict(val);
			return this;
		}

		public Builder ephemeralPrivateKeyBase64(String val) {
			this.staticPrivateKey = Base64.decode(val);
			return this;
		}

		public Builder ephemeralPublicKey(byte[] val) {
			this.ephemeralPublicKey = Objects.requireNonNull(val);
			return this;
		}

		public Builder ephemeralPublicKeyHex(String val) {
			this.ephemeralPublicKey = Hex.decodeStrict(val);
			return this;
		}

		public Builder ephemeralPublicKeyBase64(String val) {
			this.ephemeralPublicKey = Base64.decode(val);
			return this;
		}

		public Builder staticPrivateKey(byte[] val) {
			this.staticPrivateKey = Objects.requireNonNull(val);
			return this;
		}

		public Builder staticPrivateKeyHex(String val) {
			this.staticPrivateKey = Hex.decodeStrict(val);
			return this;
		}

		public Builder staticPrivateKeyBase64(String val) {
			this.staticPrivateKey = Base64.decode(val);
			return this;
		}

		public Builder staticPublicKey(byte[] val) {
			this.staticPublicKey = Objects.requireNonNull(val);
			return this;
		}

		public Builder staticPublicKeyHex(String val) {
			this.staticPublicKey = Hex.decodeStrict(val);
			return this;
		}

		public Builder staticPublicKeyBase64(String val) {
			this.staticPublicKey = Base64.decode(val);
			return this;
		}

		public Builder userId(byte[] val) {
			this.userId = Objects.requireNonNull(val);
			return this;
		}

		public Builder userIdHex(String val) {
			this.userId = Hex.decodeStrict(val);
			return this;
		}

		public Builder userIdBase64(String val) {
			this.userId = Base64.decode(val);
			return this;
		}

		public Builder userIdUtf8(String val) {
			this.userId = val.getBytes(StandardCharsets.UTF_8);
			return this;
		}

		/**
		 * for test
		 */
		Builder dump(PrintStream ps, @Nullable String title) {
			if (null != title) {
				ps.println(title);
			}
			ps.println("staticPrivateKey:" + (staticPrivateKey == null ? "null" : Hex.toHexString(staticPrivateKey)));
			ps.println("staticPublicKey:" + (staticPublicKey == null ? "null" : Hex.toHexString(staticPublicKey)));
			ps.println("ephemeralPrivateKey:"
					+ (ephemeralPrivateKey == null ? "null" : Hex.toHexString(ephemeralPrivateKey)));
			ps.println("ephemeralPublicKey:"
					+ (ephemeralPublicKey == null ? "null" : Hex.toHexString(ephemeralPublicKey)));
			ps.println("userId:" + (userId == null ? "null" : Hex.toHexString(userId)));
			return this;
		}

		public ExchangeContext build() {
			ECDomainParameters domainParam = Sm2Util.P256V1_DOMAIN_PARAM;

			ECPrivateKeyParameters staticPri = new ECPrivateKeyParameters(new BigInteger(1, staticPrivateKey),
					domainParam);
			ECPublicKeyParameters staticPub = new ECPublicKeyParameters(
					domainParam.getCurve().decodePoint(staticPublicKey), domainParam);

			ECPrivateKeyParameters ephemeralPri = new ECPrivateKeyParameters(new BigInteger(1, ephemeralPrivateKey),
					domainParam);
			ECPublicKeyParameters ephemeralPub = new ECPublicKeyParameters(
					domainParam.getCurve().decodePoint(ephemeralPublicKey), domainParam);

			return new ExchangeContext(staticPri, staticPub, ephemeralPri, ephemeralPub,
					userId == null ? new byte[0] : userId);
		}

	}

}
