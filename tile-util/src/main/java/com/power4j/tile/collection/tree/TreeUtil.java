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

package com.power4j.tile.collection.tree;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
@UtilityClass
public class TreeUtil {

	/**
	 * 在给定的集合中查找根节点
	 * @param source 数据源
	 * @param idGetter ID取值方法
	 * @param pidGetter PID取值方法
	 * @param <ID> ID 类型
	 * @param <T> 对象
	 * @return 返回根节点MAP
	 */
	public <ID, T> Map<ID, T> findAncestors(Map<ID, T> source, Function<? super T, ID> idGetter,
			Function<? super T, ID> pidGetter) {
		Map<ID, T> output = new HashMap<>(8);
		Set<ID> skipSet = new HashSet<>(8);
		for (T node : source.values()) {
			if (skipSet.contains(idGetter.apply(node))) {
				continue;
			}
			T ancestor = findAncestor(source, node, skipSet, idGetter, pidGetter);
			output.put(idGetter.apply(ancestor), ancestor);
		}
		return output;
	}

	<ID, T> T findAncestor(Map<ID, T> source, T child, Set<ID> skipSet, Function<? super T, ID> idGetter,
			Function<? super T, ID> pidGetter) {
		T parent = source.get(pidGetter.apply(child));
		if (Objects.nonNull(parent)) {
			skipSet.add(idGetter.apply(child));
			return findAncestor(source, parent, skipSet, idGetter, pidGetter);
		}
		return child;
	}

	/**
	 * 填充子级
	 * @param source 数据源
	 * @param roots 根节点
	 * @param nodeOp 各种操作函数
	 * @param <ID> ID 类型
	 * @param <T> 节点类型
	 */
	public <ID, T> void fetch(Collection<T> source, Collection<T> roots, NodeOp<ID, T> nodeOp) {
		Map<ID, T> sourceMap = source.stream()
			.collect(Collectors.toMap(nodeOp.idGetter, Function.identity(), (x, y) -> y, LinkedHashMap::new));
		Map<ID, T> rootMap = roots.stream()
			.collect(Collectors.toMap(nodeOp.idGetter, Function.identity(), (x, y) -> y, LinkedHashMap::new));
		fetch(sourceMap, rootMap, nodeOp);
	}

	/**
	 * 填充子级
	 * @param source 数据源
	 * @param roots 根节点
	 * @param nodeOp 各种操作函数
	 * @param <ID> ID 类型
	 * @param <T> 节点类型
	 */
	public <ID, T> void fetch(Map<ID, T> source, Map<ID, T> roots, NodeOp<ID, T> nodeOp) {
		if (roots.isEmpty()) {
			return;
		}
		for (T node : source.values()) {
			if (null == node) {
				continue;
			}
			final ID id = Objects.requireNonNull(nodeOp.idGetter.apply(node));
			if (roots.containsKey(id)) {
				continue;
			}
			final ID parentId = nodeOp.pidGetter.apply(node);
			final T parentNode = Optional.ofNullable(roots.get(parentId)).orElseGet(() -> source.get(parentId));
			if (null != parentNode) {
				nodeOp.childConsumer.accept(parentNode, node);
			}
		}
	}

	/**
	 * 迭代树节点
	 * @param roots 根节点
	 * @param accessOp 各种操作函数
	 * @param consumer 节点消费函数
	 * @param <ID> ID 类型
	 * @param <T> 节点类型
	 */
	public <ID, T> void treeWalk(Collection<? extends T> roots, AccessOp<ID, T> accessOp,
			Consumer<? super T> consumer) {
		for (final T node : roots) {
			consumer.accept(node);
			Collection<? extends T> children = accessOp.childrenGetter.apply(node);
			if (children != null && !children.isEmpty()) {
				treeWalk(children, accessOp, consumer);
			}
		}
	}

	/**
	 * 树节点转list
	 * @param roots 根节点
	 * @param accessOp 各种操作函数
	 * @param <ID> ID 类型
	 * @param <T> 节点类型
	 * @return 返回节点MAP
	 */
	public <ID, T> List<T> flatten(Collection<T> roots, AccessOp<ID, T> accessOp) {
		List<T> list = new ArrayList<>(roots.size());
		treeWalk(roots, accessOp, list::add);
		return list;
	}

	@Getter
	@Builder
	public static class NodeOp<ID, T> {

		/**
		 * 取ID的方法
		 */
		private final Function<? super T, ID> idGetter;

		/**
		 * 取PID的方法
		 */
		private final Function<? super T, ID> pidGetter;

		/**
		 * 添加子元素的方法
		 */
		private final BiConsumer<? super T, T> childConsumer;

	}

	@Getter
	@Builder
	public static class AccessOp<ID, T> {

		/**
		 * 取ID的方法
		 */
		private final Function<? super T, ID> idGetter;

		/**
		 * 取PID的方法
		 */
		private final Function<? super T, ID> pidGetter;

		/**
		 * 访问子元素列表的方法
		 */
		private final Function<? super T, Collection<T>> childrenGetter;

	}

}
