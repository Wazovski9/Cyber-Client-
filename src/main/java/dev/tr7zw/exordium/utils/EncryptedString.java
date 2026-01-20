package dev.tr7zw.exordium.utils;

import org.jetbrains.annotations.NotNull;

public class EncryptedString implements CharSequence {
	private final String value;

	public EncryptedString(String s) {
		this.value = s;
	}

	public static EncryptedString of(String s) {
		return new EncryptedString(s);
	}

	public static EncryptedString of(String encrypted, String key) {
		return new EncryptedString(encrypted);
	}

	@Override
	public int length() {
		return value.length();
	}

	@Override
	public char charAt(int index) {
		return value.charAt(index);
	}

	@Override
	public @NotNull String toString() {
		return value;
	}

	@Override
	public @NotNull CharSequence subSequence(int start, int end) {
		return value.subSequence(start, end);
	}
}