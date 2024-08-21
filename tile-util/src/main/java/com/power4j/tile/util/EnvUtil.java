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

package com.power4j.tile.util;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
@UtilityClass
public class EnvUtil {

	/**
	 * Lookup environment variables.
	 * @param keyFilter Filter for key
	 * @return Environment variables
	 */
	public Map<String, String> lookupKey(Predicate<String> keyFilter) {
		return System.getenv()
			.entrySet()
			.stream()
			.filter(entry -> keyFilter.test(entry.getKey()))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y));
	}

	/**
	 * Lookup environment variables by regex match.
	 * @param regex Key regex
	 * @return Environment variables
	 */
	public Map<String, String> lookupKeyPattern(String regex) {
		Pattern p = Pattern.compile(regex);
		return lookupKey(key -> p.matcher(key).matches());
	}

	/**
	 * Lookup environment variables by prefix.
	 * @param keyPrefix Key prefix
	 * @return Environment variables
	 */
	public Map<String, String> lookupKeyPrefix(String keyPrefix) {
		return lookupKey(key -> key.startsWith(keyPrefix));
	}

	/**
	 * Dump environment variables by key filter.
	 * @param keyFilter Filter for key
	 * @param out PrintStream
	 * @throws IllegalStateException if an I/O error occurs during {@code output.write()}
	 */
	public void dumpKey(Predicate<String> keyFilter, OutputStream out) {
		Map<String, String> map = lookupKey(keyFilter);
		write(map, out);
	}

	/**
	 * Dump environment variables by prefix.
	 * @param keyPrefix Key prefix
	 * @param out PrintStream
	 * @throws IllegalStateException if an I/O error occurs during {@code output.write()}
	 */
	public void dumpKeyPrefix(String keyPrefix, OutputStream out) {
		Map<String, String> map = lookupKeyPrefix(keyPrefix);
		write(map, out);
	}

	/**
	 * Dump environment variables by prefix.
	 * @param regex Key regex
	 * @param out PrintStream
	 * @throws IllegalStateException if an I/O error occurs during {@code output.write()}
	 */
	public void dumpKeyPattern(String regex, OutputStream out) {
		Map<String, String> map = lookupKeyPattern(regex);
		write(map, out);
	}

	static void write(Map<String, String> map, OutputStream out) {
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String line = entry.getKey() + "=" + entry.getValue();
			try {
				out.write(line.getBytes(StandardCharsets.UTF_8));
			}
			catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
	}

}
