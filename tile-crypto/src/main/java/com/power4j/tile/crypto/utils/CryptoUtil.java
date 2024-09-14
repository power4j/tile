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

package com.power4j.tile.crypto.utils;

import com.power4j.tile.crypto.bc.GlobalBouncyCastleProvider;
import com.power4j.tile.crypto.core.CipherBlob;
import com.power4j.tile.crypto.core.GeneralCryptoException;
import lombok.experimental.UtilityClass;
import org.apache.commons.codec.binary.Hex;
import org.springframework.lang.Nullable;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.6
 */
@UtilityClass
public class CryptoUtil {

	public static final Function<byte[], byte[]> SM3_CHECKSUM_CALCULATOR = (b) -> Sm3Util.hash(b, null);

	public static final BiFunction<CipherBlob, byte[], Boolean> SM3_CHECKSUM_VERIFIER = (cipherBlob, b) -> Arrays
		.equals(SM3_CHECKSUM_CALCULATOR.apply(b), cipherBlob.getChecksum());

	public static final BiFunction<CipherBlob, byte[], Boolean> IGNORED_CHECKSUM_VERIFIER = (blob, bytes) -> true;

	public static final Function<byte[], byte[]> EMPTY_CHECKSUM_CALCULATOR = b -> new byte[0];

	public Cipher createCipher(String transformation) throws GeneralSecurityException {
		return Cipher.getInstance(transformation, GlobalBouncyCastleProvider.INSTANCE.getProvider());
	}

	public SecretKeySpec createKey(byte[] key, String algorithmName) {
		return new SecretKeySpec(key, algorithmName);
	}

	public String transformation(String algorithmName, String mode, String padding) {
		return algorithmName + "/" + mode + "/" + padding;
	}

	public byte[] decodeHex(String val, @Nullable String errorMsg) {
		try {
			return Hex.decodeHex(val);
		}
		catch (Exception e) {
			throw wrapGeneralCryptoException(errorMsg, e);
		}
	}

	public byte[] decodeBase64(String val, @Nullable String errorMsg) {
		try {
			return Hex.decodeHex(val);
		}
		catch (Exception e) {
			throw wrapGeneralCryptoException(errorMsg, e);
		}
	}

	public GeneralCryptoException wrapGeneralCryptoException(@Nullable String msg, Throwable cause) {
		if (cause instanceof GeneralCryptoException) {
			return (GeneralCryptoException) cause;
		}
		return msg == null ? new GeneralCryptoException(cause) : new GeneralCryptoException(msg, cause);
	}

}
