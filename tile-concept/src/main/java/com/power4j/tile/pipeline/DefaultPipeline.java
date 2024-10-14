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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
public class DefaultPipeline<T> implements Pipeline<T> {

	private final List<PipelineWorker<T>> workers;

	private final int index;

	public DefaultPipeline(Collection<PipelineWorker<T>> workers, int index) {
		if (index < 0) {
			throw new IllegalArgumentException("Invalid index");
		}
		this.workers = new ArrayList<>(workers);
		this.index = index;
	}

	public DefaultPipeline() {
		this(new ArrayList<>(8), 0);
	}

	@Override
	public void run(T context) {
		nextWorker().ifPresent(worker -> worker.handle(context, new DefaultPipeline<>(workers, index + 1)));
	}

	@Override
	public void add(PipelineWorker<T> worker) {
		workers.add(worker);
	}

	@Override
	public void addAll(Collection<? extends PipelineWorker<T>> pipelineWorkers) {
		workers.addAll(pipelineWorkers);
	}

	@Override
	public void workers(Consumer<List<PipelineWorker<T>>> workerConsumer) {
		workerConsumer.accept(workers);
	}

	public Optional<PipelineWorker<T>> nextWorker() {
		if (index >= 0 && index < workers.size()) {
			return Optional.of(workers.get(index));
		}
		return Optional.empty();
	}

}
