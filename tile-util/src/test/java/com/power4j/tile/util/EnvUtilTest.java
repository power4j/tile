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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
class EnvUtilTest {

	private final String VALUE_TILE_TEST_KEY_1 = "TEST";

	private final String VALUE_TILE_SYS_KEY_1 = "SYS";

	@Test
	void lookupKeyPatternTest() {
		Map<String, String> map = EnvUtil.lookupKeyPattern("^TILE_.*");
		Assertions.assertEquals(VALUE_TILE_TEST_KEY_1, map.get("TILE_TEST_KEY_1"));
		Assertions.assertEquals(VALUE_TILE_SYS_KEY_1, map.get("TILE_SYS_KEY_1"));
	}

	@Test
	void lookupKeyPrefixTest() {
		Map<String, String> map = EnvUtil.lookupKeyPrefix("TILE_");
		Assertions.assertEquals(VALUE_TILE_TEST_KEY_1, map.get("TILE_TEST_KEY_1"));
		Assertions.assertEquals(VALUE_TILE_SYS_KEY_1, map.get("TILE_SYS_KEY_1"));
	}

	@Test
	void dumpKeyTest() {
		OutputStreamCapture capture = new OutputStreamCapture(System.out);
		EnvUtil.dumpKey("TILE_SYS_KEY_1"::equals, capture);
		Assertions.assertTrue(capture.getContent().contains("=" + VALUE_TILE_SYS_KEY_1));
	}

	@Test
	void dumpKeyPrefixTest() {
		OutputStreamCapture capture = new OutputStreamCapture(System.out);
		EnvUtil.dumpKeyPrefix("TILE_TEST_KEY_", capture);
		Assertions.assertTrue(capture.getContent().contains("=" + VALUE_TILE_TEST_KEY_1));
	}

	@Test
	void dumpKeyPatternTest() {
		OutputStreamCapture capture = new OutputStreamCapture(System.out);
		EnvUtil.dumpKeyPattern("^TILE_.*", capture);
		Assertions.assertTrue(capture.getContent().contains("=" + VALUE_TILE_TEST_KEY_1));
		Assertions.assertTrue(capture.getContent().contains("=" + VALUE_TILE_SYS_KEY_1));
	}

	static class OutputStreamCapture extends OutputStream {

		private final PrintStream systemStream;

		private final StringBuilder copy;

		OutputStreamCapture(PrintStream systemStream) {
			this.systemStream = systemStream;
			this.copy = new StringBuilder();
		}

		public void write(int b) throws IOException {
			this.write(new byte[] { (byte) (b & 255) });
		}

		public void write(byte[] b, int off, int len) throws IOException {
			this.copy.append(new String(b, off, len));
			this.systemStream.write(b, off, len);
		}

		public void flush() throws IOException {
			this.systemStream.flush();
		}

		public String getContent() {
			return this.copy.toString();
		}

	}

}
