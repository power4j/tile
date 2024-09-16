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

package com.power4j.tile.crypto.dynamic;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.6
 */
public interface KeyPool {

	/**
	 * 获取一个密钥
	 * @return 密钥信息,没有可用密钥返回 {@link Optional#empty()} }
	 */
	DynamicKey one(long param);

	/**
	 * 获取一组密钥
	 * @param param 密钥参数
	 * @return 密钥列表,没有可用密钥返回 {@link Collections#emptyList() }
	 */
	List<DynamicKey> some(long param);

}
