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

import lombok.Data;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 * @param <ID> ID 类型
 */
@Data
public class TreeNode<ID> implements Node<ID, TreeNode<ID>>, Serializable {

	private static final long serialVersionUID = 1L;

	private ID id;

	@Nullable
	private ID parentId;

	@Nullable
	private List<TreeNode<ID>> children;

	public static <ID> TreeNode<ID> of(ID id, @Nullable ID parentId) {
		TreeNode<ID> node = new TreeNode<>();
		node.setId(id);
		node.setParentId(parentId);
		return node;
	}

	@Override
	public void appendChild(TreeNode<ID> child) {
		if (Objects.isNull(children)) {
			children = new ArrayList<>(2);
		}
		children.add(child);
	}

}
