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

/**
 * 树形节点路径索引
 *
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
public interface NodeIdx<ID, C extends NodeIdx<ID, C>> {

	/**
	 * 祖先节点
	 * @return 祖先节点ID
	 */
	ID getAncestor();

	/**
	 * 后代节点
	 * @return 后代节点ID
	 */
	ID getDescendant();

	/**
	 * 层距离,指向自己时距离为0
	 * @return 层距离
	 */
	int getDistance();

	/**
	 * 是否表示一个节点
	 * @return 返回true表示这是一个节点
	 */
	default boolean isNode() {
		return getDistance() == 0;
	}

	/**
	 * 是否父子关系
	 * @return 返回true表示两个节点是父子关系
	 */
	default boolean immediate() {
		return getDistance() == 1;
	}

	/**
	 * 是否一个节点或者父子关系
	 * @return 返回true表示两个节点是父子关系
	 */
	default boolean isNodeOrImmediate() {
		return isNode() || immediate();
	}

}
