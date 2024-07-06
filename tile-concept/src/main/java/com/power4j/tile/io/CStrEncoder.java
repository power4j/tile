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

import com.power4j.tile.ffi.CStr;
import org.springframework.lang.Nullable;

import java.nio.ByteBuffer;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
public class CStrEncoder implements ObjectEncoder<String, Integer> {

	public final static CStrEncoder INSTANCE = new CStrEncoder();

	/**
	 * Write String to byte buffer
	 * @param value String to write
	 * @param context The length to write, used to generate fixed length buffer
	 */
	@Override
	public void encodeWith(String value, ByteBuffer buffer, @Nullable Integer context) {
		CStr object;
		if (context == null) {
			object = CStr.makeBuffer(value);
		}
		else {
			object = CStr.makeBuffer(value, context);
		}
		buffer.put(object.getBytes());
	}

}
