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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
class FmtTest {

	@Test
	void localDateTimeTest() {
		LocalDateTime dt = LocalDateTime.parse("2020-01-01 12:00:00", Fmt.FMT_DATETIME);
		Assertions.assertEquals(LocalDateTime.of(2020, 1, 1, 12, 0, 0), dt);

		dt = LocalDateTime.parse("2020-01-01 12:00:00,999", Fmt.FMT_DATETIME_ISO8601);
		Assertions.assertEquals(LocalDateTime.of(2020, 1, 1, 12, 0, 0, 999_000_000), dt);

		dt = LocalDateTime.parse("20200101120000", Fmt.FMT_DATETIME_PURE);
		Assertions.assertEquals(LocalDateTime.of(2020, 1, 1, 12, 0, 0), dt);

		dt = LocalDateTime.parse("20200101120000999", Fmt.FMT_DATETIME_MS_PURE);
		Assertions.assertEquals(LocalDateTime.of(2020, 1, 1, 12, 0, 0, 999_000_000), dt);

	}

	@Test
	void offsetDateTimeTest() {

		OffsetDateTime odt = OffsetDateTime.parse("2020-01-01T12:00:00Z", Fmt.FMT_ISO_OFFSET_DATE_TIME);
		Assertions.assertEquals(OffsetDateTime.of(2020, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC), odt);
		Assertions.assertEquals("2020-01-01T12:00:00Z", Fmt.FMT_ISO_OFFSET_DATE_TIME.format(odt));

		odt = OffsetDateTime.parse("2011-12-03T10:15:30+01:00", Fmt.FMT_ISO_OFFSET_DATE_TIME);
		Assertions.assertEquals(OffsetDateTime.of(2011, 12, 3, 10, 15, 30, 0, ZoneOffset.ofHours(1)), odt);
		Assertions.assertEquals("2011-12-03T10:15:30+01:00", Fmt.FMT_ISO_OFFSET_DATE_TIME.format(odt));

	}

}
