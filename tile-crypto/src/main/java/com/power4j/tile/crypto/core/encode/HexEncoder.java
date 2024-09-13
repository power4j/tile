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

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.6
 */
@RequiredArgsConstructor
public class HexEncoder extends AbstractEncoder implements BufferEncoder {

	private final boolean lowerCase;

	public static final HexEncoder LOWER = new HexEncoder(true);

	public static final HexEncoder UPPER = new HexEncoder(false);

	public static final HexEncoder DEFAULT = LOWER;

	@Override
	protected String doEncode(byte[] data, int offset, int length) {
		return Hex.encodeHexString(data, lowerCase);
	}

	@Override
	protected byte[] doDecode(String data) {
		try {
			return Hex.decodeHex(data);
		}
		catch (DecoderException e) {
			throw new BufferEncodeException("Hex Decode error:" + e.getMessage(), e);
		}
	}

	@Override
	public String algorithm() {
		return "hex";
	}

}
