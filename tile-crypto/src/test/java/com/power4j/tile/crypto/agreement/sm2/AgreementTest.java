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

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
class AgreementTest {

	private static final String CLIENT_KEY_PRI = "a09a8cdea50ce62e172c6aab13d1c74cc7b6b3b1f76b3789bfde4db1c4a95d06";

	private static final String CLIENT_KEY_PUB = "0256fbc5499c97e4b3e0b242c78e97f7792b416cbdec84357a7d1e9f52f133982d";

	private static final String CLIENT_ID = "11111111";

	private static final String SERVER_KEY_PRI = "1baa9c7d28281970da2502730abf275d0300ec410390c3d7c2611daa3d9b9892";

	private static final String SERVER_KEY_PUB = "029754599adf0d8f71e8cc6bd7c284f0b1e4750c4cb1409d42fe0e4c5690cff705";

	private static final String SERVER_ID = "22222222";

	private static final int KEY_LEN = 128;

	@Test
	void exchangeTest() {
		ExchangeContext clientCtx = ExchangeContext.builder()
			.randomEphemeralKey()
			.staticPrivateKeyHex(CLIENT_KEY_PRI)
			.staticPublicKeyHex(CLIENT_KEY_PUB)
			.userIdHex(CLIENT_ID)
			.dump(System.out, "clientCtx")
			.build();
		ExchangeStageStore store = clientCtx.stageStore();
		ExchangeRequest req1 = Agreement.init(clientCtx);

		ExchangeContext serverCtx = ExchangeContext.builder()
			.randomEphemeralKey()
			.staticPrivateKeyHex(SERVER_KEY_PRI)
			.staticPublicKeyHex(SERVER_KEY_PUB)
			.userIdHex(SERVER_ID)
			.dump(System.out, "serverCtx")
			.build();
		Pair<byte[], ExchangeRequest> serverResult = Agreement.generateKeyAndExchangeRequest(serverCtx, KEY_LEN, req1);

		ExchangeContext clientCtx2 = ExchangeContext.builder()
			.ephemeralPrivateKeyHex(store.getEphemeralPrivateKey())
			.ephemeralPublicKeyHex(store.getEphemeralPublicKey())
			.staticPrivateKeyHex(CLIENT_KEY_PRI)
			.staticPublicKeyHex(CLIENT_KEY_PUB)
			.userIdHex(CLIENT_ID)
			.build();
		byte[] key = Agreement.generateKeyAndVerify(clientCtx2, KEY_LEN, serverResult.getRight());

		Assertions.assertArrayEquals(key, serverResult.getLeft());
		System.out.println("agreement key:" + Hex.encodeHexString(key));
	}

}
