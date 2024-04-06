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

import com.power4j.tile.collection.tree.domain.Node;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
@UtilityClass
public class TreeNodeUtil {

	/**
	 * 迭代树节点
	 * @param roots 根节点
	 * @param consumer 节点消费函数
	 * @param <ID> ID 类型
	 * @param <N> Node 子类
	 */
	public <ID, N extends Node<ID, N>> void treeWalk(Collection<? extends N> roots, Consumer<? super N> consumer) {
		for (final N node : roots) {
			consumer.accept(node);
			List<N> children = node.getChildren();
			if (children != null && !children.isEmpty()) {
				treeWalk(children, consumer);
			}
		}
	}

	/**
	 * 根据属性排序
	 * @param list 根节点
	 * @param keyExtractor 属性取值函数
	 * @param <ID> ID 类型
	 * @param <N> Node 子类
	 */
	public static <ID, N extends Node<ID, N>, U extends Comparable<? super U>> void sortNodes(final List<N> list,
			final Function<? super N, ? extends U> keyExtractor) {
		list.sort(Comparator.comparing(keyExtractor));
		for (N node : list) {
			final List<N> children = node.getChildren();
			if (children != null && !children.isEmpty()) {
				sortNodes(children, keyExtractor);
			}
		}
	}

	/**
	 * 树节点转list
	 * @param roots 根节点
	 * @param <ID> ID 类型
	 * @param <N> Node 子类
	 * @return 返回节点MAP
	 */
	public <ID, N extends Node<ID, N>> List<N> flatten(List<N> roots) {
		List<N> list = new ArrayList<>(roots.size());
		treeWalk(roots, list::add);
		return list;
	}

	/**
	 * 填充子级
	 * @param source 数据源
	 * @param roots 根节点
	 * @param <ID> ID 类型
	 * @param <N> Node 子类
	 */
	public <ID, N extends Node<ID, N>> void fetch(Map<ID, N> source, Map<ID, N> roots) {
		if (roots.isEmpty()) {
			return;
		}
		for (N node : source.values()) {
			if (null == node) {
				continue;
			}
			final ID id = Objects.requireNonNull(node.getId());
			if (roots.containsKey(id)) {
				continue;
			}
			final ID parentId = node.getParentId();
			final N parentNode = Optional.ofNullable(roots.get(parentId)).orElseGet(() -> source.get(parentId));
			if (null != parentNode) {
				parentNode.appendChild(node);
			}
		}
	}

	/**
	 * 转换为其他类型,无接口依赖
	 * @param src 数据源
	 * @param op 转换所需各种函数
	 * @param dist 用于保存结果
	 * @param <ID> ID 类型
	 * @param <N> 原类型
	 * @param <U> 目标类型
	 */
	public <ID, N extends Node<ID, N>, U> void convert(Collection<? extends N> src, ConvertOp<N, U> op,
			Collection<? super U> dist) {
		for (N node : src) {
			final U target = op.objectConvert.apply(node);
			dist.add(target);
			final List<N> children = node.getChildren();
			if (children != null && !children.isEmpty()) {
				final List<U> list = new ArrayList<>(2);
				convert(children, op, list);
				op.childSetter.accept(target, list);
			}
		}
	}

	/**
	 * 转换为其他类型,无接口依赖
	 * @param src 数据源
	 * @param op 转换所需各种函数
	 * @param <ID> ID 类型
	 * @param <N> 原类型
	 * @param <U> 目标类型
	 * @return 返回转换后的列表
	 */
	public <ID, N extends Node<ID, N>, U> List<U> convertToList(Collection<? extends N> src, ConvertOp<N, U> op) {
		List<U> list = new ArrayList<>(2);
		convert(src, op, list);
		return list;
	}

	@Getter
	@Builder
	public static class ConvertOp<S, T> {

		/**
		 * 取ID的方法
		 */
		private final Function<? super S, ? extends T> objectConvert;

		/**
		 * 添加子元素的方法
		 */
		private final BiConsumer<? super T, Collection<T>> childSetter;

	}

}
