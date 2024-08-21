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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
class Sm3UtilTest {

	@Test
	void testHash() {

		// From [GBT.32918.2-2016] A.2 Example 1
		String input_1 = "0090414C494345313233405941484F4F2E434F4D787968B4FA32C3FD2417842E73BBFEFF2F3C848B6831D7E0EC65228B3937E49863E4C6D3B23B0C849CF84241484BFE48F61D59A5B16BA06E6E12D1DA27C5249A421DEBD61B62EAB6746434EBC3CC315E32220B3BADD50BDC4C4E6C147FEDD43D0680512BCBB42C07D47349D2153B70C4E5D7FDFCBFA36EA1A85841B9E46E09A20AE4C7798AA0F119471BEE11825BE46202BB79E2A5844495E97C04FF4DF2548A7C0240F88F1CD4E16352A73C17B7F16F07353E53A176D684A9FE0C6BB798E857";
		String ret_1 = Sm3Util.hashHex(input_1);
		assertEquals("f4a38489e32b45b6f876e3ac2168ca392362dc8f23459c1d1146fc3dbfb7bc9a", ret_1);

		// From [GBT.32918.2-2016] A.2 Example 2
		String input_2 = "F4A38489E32B45B6F876E3AC2168CA392362DC8F23459C1D1146FC3DBFB7BC9A6D65737361676520646967657374";
		String ret_2 = Sm3Util.hashHex(input_2);
		assertEquals("b524f552cd82b8b028476e005c377fb19a87e6fc682d48bb5d42e3d9b9effe76", ret_2);

	}

	@Test
	void testHashLength() {
		byte[] input = { 0x1, 0x2, 0x3 };
		byte[] ret = Sm3Util.hash(input, 0);
		assertEquals(Sm3Util.SM3_BYTES, ret.length);

		// truncate
		ret = Sm3Util.hash(input, 1);
		assertEquals(1, ret.length);

		// padding
		ret = Sm3Util.hash(input, 33);
		assertEquals(33, ret.length);
	}

	@Test
	void testVerify() {
		String data = "F4A38489E32B45B6F876E3AC2168CA392362DC8F23459C1D1146FC3DBFB7BC9A6D65737361676520646967657374";
		assertTrue(Sm3Util.verifyHeadHex(data, "b524f552cd82b8b028476e005c377fb19a87e6fc682d48bb5d42e3d9b9effe76"));
		// drop 1 byte from tail
		assertTrue(Sm3Util.verifyHeadHex(data, "b524f552cd82b8b028476e005c377fb19a87e6fc682d48bb5d42e3d9b9effe"));
		// drop 1 byte from tail
		assertFalse(Sm3Util.verifyHex(data, "b524f552cd82b8b028476e005c377fb19a87e6fc682d48bb5d42e3d9b9effe"));
	}

}
