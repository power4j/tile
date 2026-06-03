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

import com.power4j.tile.crypto.core.encode.HexEncoder;
import lombok.experimental.UtilityClass;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;

import java.security.MessageDigest;
import java.util.Base64;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.9
 */
@UtilityClass
public class HmacSm3Util {

	public static final int HMAC_SM3_BYTES = 32;

	public byte[] sign(byte[] data, byte[] key) {
		HMac hmac = new HMac(new SM3Digest());
		hmac.init(new KeyParameter(key));
		hmac.update(data, 0, data.length);
		byte[] out = new byte[HMAC_SM3_BYTES];
		hmac.doFinal(out, 0);
		return out;
	}

	public String signHex(byte[] data, byte[] key) {
		return HexEncoder.DEFAULT.encode(sign(data, key));
	}

	public String signBase64(byte[] data, byte[] key) {
		return Base64.getEncoder().encodeToString(sign(data, key));
	}

	public boolean verify(byte[] data, byte[] key, byte[] mac) {
		byte[] ours = sign(data, key);
		return MessageDigest.isEqual(ours, mac);
	}

	public boolean verifyHex(byte[] data, byte[] key, String macHex) {
		return verify(data, key, HexEncoder.DEFAULT.decode(macHex));
	}

	public boolean verifyBase64(byte[] data, byte[] key, String macBase64) {
		return verify(data, key, Base64.getDecoder().decode(macBase64));
	}

}
