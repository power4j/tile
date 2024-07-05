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

package com.power4j.tile.time;

import lombok.experimental.UtilityClass;

import java.time.format.DateTimeFormatter;

/**
 * Common date-time formatter.
 *
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
@UtilityClass
public class Fmt {

	/**
	 * Format: {@code yyyy-MM-dd HH:mm:ss}
	 *
	 * Example: <pre>
	 *     LocalDateTime.parse("2020-01-01 12:00:00",Fmt.FMT_DATETIME)
	 * </pre>
	 */
	public static final DateTimeFormatter FMT_DATETIME = DateTimeFormatter.ofPattern(Patterns.DATETIME);

	/**
	 * Format: {@code yyyy-MM-dd HH:mm:ss,SSS}
	 */
	public static final DateTimeFormatter FMT_DATETIME_ISO8601 = DateTimeFormatter.ofPattern(Patterns.DATETIME_ISO8601);

	/**
	 * Format: {@code yyyyMMddHHmmss}
	 */
	public static final DateTimeFormatter FMT_DATETIME_PURE = DateTimeFormatter.ofPattern(Patterns.DATETIME_PURE);

	/**
	 * Format: {@code yyyyMMddHHmmssSSS}
	 */
	public static final DateTimeFormatter FMT_DATETIME_MS_PURE = DateTimeFormatter.ofPattern(Patterns.DATETIME_MS_PURE);

	/**
	 * The ISO date-time formatter that formats or parses a date-time with an offset
	 * <ul>
	 * <li>format output: {@code 2011-12-03T10:15:30+01:00} or
	 * {@code 2020-01-01T12:00:00Z}</li>
	 * <li>can parse input: {@code 2011-12-03T10:15:30+01:00} or
	 * {@code 2020-01-01T12:00:00Z} or {@code 20200101120000Z}</li>
	 * </ul>
	 * see <a href="https://zh.wikipedia.org/wiki/ISO_8601">ISO_8601</a>
	 */
	public static final DateTimeFormatter FMT_ISO_OFFSET_DATE_TIME = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

}
