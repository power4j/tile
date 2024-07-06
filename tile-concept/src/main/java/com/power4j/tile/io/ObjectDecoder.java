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
import org.springframework.lang.Nullable;

import java.nio.ByteBuffer;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
public interface ObjectDecoder<T, C> {

	/**
	 * Decode value from byte buffer
	 * @param buffer byte buffer to decode
	 * @return Result<T,DecodeErr>
	 */
	default Result<T, DecodeErr> decode(ByteBuffer buffer) {
		return decodeWith(buffer, null);
	}

	/**
	 * Decode value from byte buffer
	 * @param buffer byte buffer to decode
	 * @param context context used for decode
	 * @return Result<T,DecodeErr>
	 */
	Result<T, DecodeErr> decodeWith(ByteBuffer buffer, @Nullable C context);

}
