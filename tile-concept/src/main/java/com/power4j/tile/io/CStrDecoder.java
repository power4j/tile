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
import com.power4j.tile.result.Result;
import org.springframework.lang.Nullable;

import java.nio.ByteBuffer;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
public class CStrDecoder implements ObjectDecoder<String, Integer> {

	public final static CStrDecoder INSTANCE = new CStrDecoder();

	/**
	 * Write String from byte buffer
	 * @param buffer buffer to read
	 * @param context Limit of length to read,null means read until null byte found
	 */
	@Override
	public Result<String, DecodeErr> decodeWith(ByteBuffer buffer, @Nullable Integer context) {
		if (context == null) {
			return CStr.copyOf(buffer.array(), buffer.position(), buffer.remaining())
				.tryReadUntilNull(0, -1)
				.mapError(e -> new DecodeErr(e.message(), e));
		}
		else {
			return CStr.copyOf(buffer.array(), buffer.position(), buffer.remaining())
				.tryReadUntilNull(0, context)
				.mapError(e -> new DecodeErr(e.message(), e));
		}
	}

}
