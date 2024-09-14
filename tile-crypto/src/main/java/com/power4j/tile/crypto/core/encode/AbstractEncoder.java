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
public abstract class AbstractEncoder implements BufferEncoder {

	@Override
	public final String encode(byte[] data, int offset, int length) {
		try {
			return doEncode(data, offset, length);
		}
		catch (BufferEncodeException e) {
			throw e;
		}
		catch (Exception e) {
			throw new BufferEncodeException("Encode error:" + e.getMessage(), e);
		}
	}

	@Override
	public final byte[] decode(String data) {
		try {
			return doDecode(data);
		}
		catch (BufferEncodeException e) {
			throw e;
		}
		catch (Exception e) {
			throw new BufferEncodeException("Decode error:" + e.getMessage(), e);
		}
	}

	protected abstract String doEncode(byte[] data, int offset, int length);

	protected abstract byte[] doDecode(String data);

}
