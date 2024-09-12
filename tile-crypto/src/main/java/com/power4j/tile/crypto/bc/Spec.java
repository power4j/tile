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

package com.power4j.tile.crypto.bc;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
public interface Spec {

	String ALGORITHM_SM4 = "SM4";

	String ALGORITHM_SM3 = "SM3";

	String MODE_ECB = "ECB";

	String MODE_CBC = "CBC";

	String MODE_CFB = "CFB";

	String MODE_OFB = "OFB";

	String PADDING_PKCS7 = "PKCS7Padding";

	String PADDING_NO_PADDING = "NoPadding";

}
