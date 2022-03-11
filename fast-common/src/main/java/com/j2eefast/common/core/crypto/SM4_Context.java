package com.j2eefast.common.core.crypto;

/**
 * SM4 加密
 */
public class SM4_Context {
	public int mode;

	public long[] sk;

	public boolean isPadding;

	public SM4_Context() 
	{
		this.mode = 1;
		this.isPadding = true;
		this.sk = new long[32];
	}

	public SM4_Context(boolean isPadding)
	{
		this.mode = 1;
		this.isPadding = isPadding;
		this.sk = new long[32];
	}
}
