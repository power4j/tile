
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
import com.power4j.tile.collection.tree.domain.NodeIdx;
import com.power4j.tile.collection.tree.domain.TreeNode;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 * @param <ID> ID 类型
 * @param <N> Node 子类
 */
public class TreeMaker<ID, N extends Node<ID, N>> {

	private final Map<ID, N> data;

	TreeMaker(Map<ID, N> data) {
		this.data = data;
	}

	/**
	 * 构造方法
	 * @param data 业务数据
	 * @param <ID> ID 类型
	 * @param <N> Node 子类
	 * @return 返回实例
	 */
	public static <ID, N extends Node<ID, N>> TreeMaker<ID, N> use(Collection<N> data) {
		Map<ID, N> sourceData = data.stream()
			.collect(Collectors.toMap(Node::getId, Function.identity(), (x, y) -> y, LinkedHashMap::new));
		return new TreeMaker<>(sourceData);
	}

	/**
	 * 构造方法
	 * @param nodes 树形节点索引
	 * @param <ID> ID 类型
	 * @return 返回实例
	 */
	public static <ID> TreeMaker<ID, TreeNode<ID>> useIdx(Collection<? extends NodeIdx<ID, ?>> nodes) {
		Function<ID, TreeNode<ID>> treeMapper = id -> TreeNode.of(id, null);
		Map<ID, ID> parentMap = nodes.stream()
			.filter(o -> (1 == o.getDistance()))
			.collect(Collectors.toMap(NodeIdx::getDescendant, NodeIdx::getAncestor));
		Map<ID, TreeNode<ID>> data = nodes.stream()
			.filter(o -> (0 == o.getDistance()))
			.map(NodeIdx::getAncestor)
			.map(treeMapper)
			.collect(Collectors.toMap(TreeNode::getId, Function.identity(), (x, y) -> y, LinkedHashMap::new));
		data.values().forEach(o -> o.setParentId(parentMap.get(o.getId())));
		return new TreeMaker<>(data);
	}

	/**
	 * 构建树形结构
	 * @param id 根点ID,必须存在于数据源中,并且不是顶层节点
	 * @return 返回树形结构,如果数据源不包含根节点数据则返回empty
	 */
	public Optional<N> build(ID id) {
		List<N> roots = build(o -> Objects.equals(o.getId(), id));
		return roots.stream().findFirst();
	}

	/**
	 * 构建树形结构,自动推测根节点
	 * @return 返回根节点列表
	 */
	public List<N> build() {
		return makeTree(TreeMaker::findTopNodes);
	}

	/**
	 * 构建树形结构
	 * @param rootPred 根节点断言
	 * @return 返回根节点列表,如果数据源不包含根节点数据则返回empty
	 */
	public List<N> build(Predicate<N> rootPred) {
		// @formatter:off
		Function<Map<ID, N>,Map<ID, N>> rootSelect = map -> map.entrySet()
				.stream()
				.filter(et -> rootPred.test(et.getValue()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new));
		// @formatter:on
		return makeTree(rootSelect);
	}

	/**
	 * 构建树形结构 支持多个根节点
	 * @param rootSelect 根节选择器
	 * @return 返回根节点列表,如果数据源不包含根节点数据则返回empty
	 */
	protected List<N> makeTree(Function<Map<ID, N>, Map<ID, N>> rootSelect) {
		if (ObjectUtils.isEmpty(data)) {
			return Collections.emptyList();
		}
		// 根节点
		final Map<ID, N> roots = rootSelect.apply(data);
		if (ObjectUtils.isEmpty(roots)) {
			return Collections.emptyList();
		}
		TreeNodeUtil.fetch(data, roots);
		return new ArrayList<>(roots.values());
	}

	// ~ Utils
	// ===================================================================================================

	protected static <ID, N extends Node<ID, N>> Map<ID, N> findTopNodes(Map<ID, N> input) {
		return TreeUtil.findAncestors(input, N::getId, N::getParentId);
	}

}
