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

package com.power4j.tile.crypto.core.encode;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.6
 */
public interface BufferEncoder {

	/**
	 * byte[] -> String
	 * @param data input bytes
	 * @param offset offset to encode
	 * @param length length to encode
	 * @return Encoded string
	 * @throws BufferEncodeException encode error
	 */
	String encode(byte[] data, int offset, int length) throws BufferEncodeException;

	/**
	 * byte[] -> String
	 * @param data input bytes
	 * @return Encoded string
	 * @throws BufferEncodeException
	 */
	default String encode(byte[] data) throws BufferEncodeException {
		return encode(data, 0, data.length);
	}

	/**
	 * String -> byte[]
	 * @param data input string(encoded by {@link BufferEncoder#encode(byte[], int, int)})
	 * @return Decoded bytes
	 * @throws BufferEncodeException decode error
	 */
	byte[] decode(String data) throws BufferEncodeException;

}
