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

package com.power4j.tile.io;

import com.power4j.tile.result.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
class CStrDecoderTest {

	@Test
	void decodeWith() {
		String value = CStrDecoder.INSTANCE.decode(ByteBuffer.wrap(new byte[] { 49, 50, 51, 0, 0 })).unwrap();

		Assertions.assertEquals("123", value);
	}

	@Test
	void decodeWithWithLimit() {
		String value = CStrDecoder.INSTANCE.decodeWith(ByteBuffer.wrap(new byte[] { 49, 50, 51, 0, 0 }), 4).unwrap();

		Assertions.assertEquals("123", value);
	}

	@Test
	void decodeWithFailWhenNotNullTerminated() {
		Result<String, DecodeErr> ret = CStrDecoder.INSTANCE
			.decodeWith(ByteBuffer.wrap(new byte[] { 49, 50, 51, 0, 0 }), 3);
		Assertions.assertTrue(ret.isError());
	}

}
