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
import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.6
 */
@RequiredArgsConstructor
public class Base64Encoder extends AbstractEncoder implements BufferEncoder {

	private final boolean urlSafe;

	public static final Base64Encoder BASIC = new Base64Encoder(false);

	public static final Base64Encoder URL_SAFE = new Base64Encoder(true);

	public static final Base64Encoder DEFAULT = URL_SAFE;

	@Override
	protected String doEncode(byte[] data, int offset, int length) {
		return new String(Base64.encodeBase64(Arrays.copyOfRange(data, offset, offset + length), true, urlSafe),
				StandardCharsets.US_ASCII);
	}

	@Override
	protected byte[] doDecode(String data) {
		return Base64.decodeBase64(data);
	}

	@Override
	public String algorithm() {
		return "base64";
	}

}
