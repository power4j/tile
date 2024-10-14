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

package com.power4j.tile.pipeline;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class DefaultPipelineTest {

	@Mock
	PipelineWorker<String> mock1;

	@Mock
	PipelineWorker<String> mock2;

	@Test
	void shouldThrowIfIndexOutOfBound() {
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> new DefaultPipeline<>(Collections.emptyList(), -1));
	}

	@Test
	void shouldRunByIndex() {
		DefaultPipeline<String> pipeline = new DefaultPipeline<>(Arrays.asList(mock1, mock2), 1);
		pipeline.run("hi");

		Mockito.verify(mock1, Mockito.never()).handle(Mockito.any(), Mockito.any());
		Mockito.verify(mock2, Mockito.times(1)).handle(Mockito.any(), Mockito.any());
	}

}
