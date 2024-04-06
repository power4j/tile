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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ErrValueTest {

	static class Num implements Comparable<Num>, Display {

		private final int value;

		public Num(int value) {
			this.value = value;
		}

		@Override
		public String display() {
			return "0x" + Integer.toHexString(value);
		}

		@Override
		public int compareTo(Num o) {
			return Integer.compare(value, o.value);
		}

	}

	@Test
	void display() {
		ErrValue<Num> err = new ErrValue<>(new Num(0x7F), "ok", null);
		Assertions.assertEquals("[0x7f] - ok", err.display());

		ErrValue<Integer> err2 = new ErrValue<>(0x7F, "ok", null);
		Assertions.assertEquals("[127] - ok", err2.display());
	}

}
