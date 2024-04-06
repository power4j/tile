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

package com.power4j.tile.error;

import com.power4j.tile.fmt.Display;

import java.util.Optional;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
public interface Err extends Display {

	/**
	 * Description of the error
	 * @return Error message
	 */
	String message();

	/**
	 * The lower-level source of this error, if any.
	 * @return Optional error source
	 */
	Optional<Err> source();

	@Override
	default String display() {
		return message();
	}

}
