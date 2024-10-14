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

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
public interface Pipeline<T> {

	/**
	 * Run pipeline
	 * @param context
	 */
	void run(T context);

	/**
	 * Add a worker
	 * @param worker
	 */
	void add(PipelineWorker<T> worker);

	/**
	 * Add all workers
	 * @param workers
	 */
	void addAll(Collection<? extends PipelineWorker<T>> workers);

	/**
	 * configure worker list
	 * @param workerConsumer consumer
	 */
	void workers(Consumer<List<PipelineWorker<T>>> workerConsumer);

}
