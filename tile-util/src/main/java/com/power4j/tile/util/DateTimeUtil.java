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
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * DateTime util
 * <p>
 *
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
@UtilityClass
public class DateTimeUtil {

	public static final LocalDateTime EPOCH = LocalDateTime.of(1970, 1, 1, 0, 0, 0);

	public static final LocalDateTime FOREVER = LocalDateTime.of(9999, 12, 31, 23, 59, 59);

	public LocalDateTime utcNow() {
		return LocalDateTime.now(ZoneOffset.UTC);
	}

	// ~ Converter
	// ===================================================================================================

	@Nullable
	public LocalDateTime startDateTime(@Nullable LocalDate date) {
		if (date == null) {
			return null;
		}
		return LocalDateTime.of(date, LocalTime.of(0, 0, 0));
	}

	@Nullable
	public LocalDateTime endDateTime(@Nullable LocalDate date) {
		if (date == null) {
			return null;
		}
		return LocalDateTime.of(date, LocalTime.of(23, 59, 59));
	}

	@Nullable
	public Date toDate(@Nullable LocalDateTime localDateTime) {
		return toDate(localDateTime, ZoneId.systemDefault());
	}

	@Nullable
	public LocalDateTime toLocalDateTime(@Nullable Date date) {
		return toLocalDateTime(date, ZoneId.systemDefault());
	}

	@Nullable
	public Date toDate(@Nullable LocalDateTime localDateTime, ZoneId zoneId) {
		if (localDateTime == null) {
			return null;
		}
		return Date.from(localDateTime.atZone(zoneId).toInstant());
	}

	@Nullable
	public LocalDateTime toLocalDateTime(@Nullable Date date, ZoneId zoneId) {
		if (date == null) {
			return null;
		}
		return date.toInstant().atZone(zoneId).toLocalDateTime();
	}

	@Nullable
	public Date toDate(@Nullable LocalDateTime localDateTime, ZoneOffset offset) {
		if (localDateTime == null) {
			return null;
		}
		return Date.from(localDateTime.atOffset(offset).toInstant());
	}

	@Nullable
	public LocalDateTime toLocalDateTime(@Nullable Date date, ZoneOffset offset) {
		if (date == null) {
			return null;
		}
		return date.toInstant().atOffset(offset).toLocalDateTime();
	}

	// ~ Time Zone
	// ===================================================================================================

	@Nullable
	public ZonedDateTime toZonedDateTime(@Nullable LocalDateTime dateTime, ZoneId zoneId) {
		if (dateTime == null) {
			return null;
		}
		return dateTime.atZone(zoneId);
	}

	@Nullable
	public OffsetDateTime toOffsetDateTime(@Nullable LocalDateTime dateTime, ZoneOffset offset) {
		if (dateTime == null) {
			return null;
		}
		return dateTime.atOffset(offset);
	}

	@Nullable
	public ZonedDateTime toZonedDateTimeUtc(@Nullable LocalDateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		return toZonedDateTime(dateTime, ZoneOffset.UTC);
	}

	@Nullable
	public LocalDateTime toLocalDateTime(@Nullable ZonedDateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		return dateTime.toLocalDateTime();
	}

}
