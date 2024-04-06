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

package com.power4j.tile.collection.tree.domain;

import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
public interface Node<ID, C extends Node<ID, C>> {

	/**
	 * 父节点ID,可为null
	 * @return 返回父节点ID
	 */
	@Nullable
	ID getParentId();

	/**
	 * 本节点ID
	 * @return 返回ID
	 */
	ID getId();

	/**
	 * 添加子节点
	 * @param child 子节点
	 */
	void appendChild(C child);

	/**
	 * 添加子节点
	 * @param list 子节点
	 */
	default void appendChildren(Collection<? extends C> list) {
		list.forEach(this::appendChild);
	}

	/**
	 * 子节点
	 * @return 返回子节点,无子节点返回null
	 */
	@Nullable
	List<C> getChildren();

	/**
	 * 访问子节点
	 * @param index 索引
	 * @return 子节点不存在时返回null
	 */
	@Nullable
	default C childAt(int index) {
		final List<C> list = getChildren();
		if (Objects.isNull(list) || list.isEmpty()) {
			return null;
		}
		return (index >= 0 && index < list.size()) ? list.get(index) : null;
	}

	/**
	 * 查找子节点
	 * @param predicate 断言
	 * @return 子节点不存在返回Empty
	 */
	default Optional<C> findFirstChild(Predicate<? super C> predicate) {
		final List<C> list = getChildren();
		if (Objects.isNull(list) || list.isEmpty()) {
			return Optional.empty();
		}
		return list.stream().filter(predicate).findFirst();
	}

	/**
	 * 是否存在父级
	 * @return true表示父级存在
	 */
	default boolean hasParent() {
		return Objects.nonNull(getParentId());
	}

	/**
	 * 是否存在子节点
	 * @return ture表示至少存在一个子节点
	 */
	default boolean hasChildren() {
		return Objects.nonNull(getChildren()) && !getChildren().isEmpty();
	}

}
