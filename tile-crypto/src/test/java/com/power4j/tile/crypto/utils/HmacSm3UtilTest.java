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

import com.power4j.tile.crypto.core.encode.HexEncoder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author CJ (power4j@outlook.com)
 */
class HmacSm3UtilTest {

	private static final String KEY = "7365637265742d6b65792d7631";

	@Test
	void signHexUsesKnownVector() {
		String data = "68656c6c6f";

		String mac = HmacSm3Util.signHex(HexEncoder.DEFAULT.decode(data), HexEncoder.DEFAULT.decode(KEY));

		assertEquals("a73edc403df55644424c3c49103b93b98fffe37caf937d31706fea967d54c523", mac);
	}

	@Test
	void signBase64UsesStandardBase64() {
		String data = "68656c6c6f";

		String mac = HmacSm3Util.signBase64(HexEncoder.DEFAULT.decode(data), HexEncoder.DEFAULT.decode(KEY));

		assertEquals("pz7cQD31VkRCTDxJEDuTuY//43yvk30xcG/qln1UxSM=", mac);
	}

	@Test
	void signSupportsEmptyDataBinaryInputAndLongKey() {
		byte[] emptyMac = HmacSm3Util.sign(new byte[0], HexEncoder.DEFAULT.decode(KEY));
		assertArrayEquals(HexEncoder.DEFAULT.decode("7f1a4726a2f255ae63b661c5bc77cae0c9ec523ba1ea97d0b42a5b4cceb0f2bb"),
				emptyMac);

		byte[] binaryMac = HmacSm3Util.sign(HexEncoder.DEFAULT.decode("0001027fff80"),
				HexEncoder.DEFAULT.decode("000102030405060708090a0b0c0d0e0f"));
		assertArrayEquals(HexEncoder.DEFAULT.decode("1fc0b0d778befb82134538a7ea823e39740c310fee1bf4efbc5b7b623a4477cb"),
				binaryMac);

		byte[] longKeyMac = HmacSm3Util.sign(HexEncoder.DEFAULT.decode("686d61632d736d332d6c6f6e672d6b6579"),
				HexEncoder.DEFAULT
					.decode("000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f2021222324252627"));
		assertArrayEquals(HexEncoder.DEFAULT.decode("8863efabfccef71e804d8153ba0d5dc74d974ca1202d4be642e6aca3f1f40dda"),
				longKeyMac);
	}

	@Test
	void verifyRejectsWrongMacAndWrongKey() {
		byte[] data = HexEncoder.DEFAULT.decode("68656c6c6f");
		byte[] key = HexEncoder.DEFAULT.decode(KEY);
		byte[] mac = HexEncoder.DEFAULT.decode("a73edc403df55644424c3c49103b93b98fffe37caf937d31706fea967d54c523");

		assertTrue(HmacSm3Util.verify(data, key, mac));
		assertTrue(
				HmacSm3Util.verifyHex(data, key, "a73edc403df55644424c3c49103b93b98fffe37caf937d31706fea967d54c523"));
		assertTrue(HmacSm3Util.verifyBase64(data, key, "pz7cQD31VkRCTDxJEDuTuY//43yvk30xcG/qln1UxSM="));

		assertFalse(HmacSm3Util.verify(data, key,
				HexEncoder.DEFAULT.decode("0000000000000000000000000000000000000000000000000000000000000000")));
		assertFalse(HmacSm3Util.verify(data, HexEncoder.DEFAULT.decode("77726f6e672d6b6579"), mac));
		assertFalse(HmacSm3Util.verify(data, key,
				HexEncoder.DEFAULT.decode("a73edc403df55644424c3c49103b93b98fffe37caf937d31706fea967d54c5")));
	}

}
