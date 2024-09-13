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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.6
 */
@RequiredArgsConstructor
public class UnicodeEncoder extends AbstractEncoder implements BufferEncoder {

	private final Charset charset;

	public static final UnicodeEncoder UTF_8 = new UnicodeEncoder(StandardCharsets.UTF_8);

	public static final UnicodeEncoder UTF_16LE = new UnicodeEncoder(StandardCharsets.UTF_16LE);

	public static final UnicodeEncoder UTF_16BE = new UnicodeEncoder(StandardCharsets.UTF_16BE);

	public static final UnicodeEncoder US_ASCII = new UnicodeEncoder(StandardCharsets.US_ASCII);

	public static final UnicodeEncoder DEFAULT = UTF_8;

	@Override
	protected String doEncode(byte[] data, int offset, int length) {
		return new String(data, offset, length, charset);
	}

	@Override
	protected byte[] doDecode(String data) {
		return data.getBytes(charset);
	}

	@Override
	public String algorithm() {
		return "unicode/" + charset.name();
	}

}
