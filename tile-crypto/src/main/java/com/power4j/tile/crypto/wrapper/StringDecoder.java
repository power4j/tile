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

package com.power4j.tile.crypto.wrapper;

import com.power4j.tile.crypto.core.encode.UnicodeEncoder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
public class StringDecoder implements InputDecoder<String> {

	private final InputDecoderAdapter adapter;

	public static final StringDecoder DEFAULT = new StringDecoder(Charset.defaultCharset());

	public static final StringDecoder UTF_8 = new StringDecoder(StandardCharsets.UTF_8);

	public static final StringDecoder ISO_8859_1 = new StringDecoder(StandardCharsets.ISO_8859_1);

	public StringDecoder(Charset charset) {
		this.adapter = new InputDecoderAdapter(charset);
	}

	@Override
	public byte[] decode(String data) throws DecodeException {
		return adapter.decode(data);
	}

	static class InputDecoderAdapter implements InputDecoder<String> {

		private final UnicodeEncoder encoder;

		InputDecoderAdapter(Charset charset) {
			this.encoder = new UnicodeEncoder(charset);
		}

		@Override
		public byte[] decode(String data) throws DecodeException {
			try {
				return encoder.decode(data);
			}
			catch (Exception e) {
				throw new DecodeException(e.getMessage(), e);
			}
		}

	}

}
