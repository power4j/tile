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
public class StringEncoder implements OutputEncoder<String> {

	private final OutputEncoderAdapter adapter;

	public static final StringEncoder DEFAULT = new StringEncoder(Charset.defaultCharset());

	public static final StringEncoder UTF_8 = new StringEncoder(StandardCharsets.UTF_8);

	public static final StringEncoder ISO_8859_1 = new StringEncoder(StandardCharsets.ISO_8859_1);

	public StringEncoder(Charset charset) {
		this.adapter = new OutputEncoderAdapter(charset);
	}

	@Override
	public String encode(byte[] data) throws EncodeException {
		return adapter.encode(data);
	}

	static class OutputEncoderAdapter implements OutputEncoder<String> {

		private final UnicodeEncoder encoder;

		OutputEncoderAdapter(Charset charset) {
			this.encoder = new UnicodeEncoder(charset);
		}

		@Override
		public String encode(byte[] data) throws EncodeException {
			return encoder.encode(data);
		}

	}

}
