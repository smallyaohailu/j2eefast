package com.j2eefast.common.core.crypto;

import java.math.BigInteger;
import com.j2eefast.common.core.utils.HexUtil;
import com.j2eefast.common.core.utils.ToolUtil;
import org.apache.commons.net.util.Base64;
import org.bouncycastle.math.ec.ECPoint;

public class SM3Digest
{
    /** SM3值的长度 */
    private static final int BYTE_LENGTH = 32;

    /** SM3分组长度 */
    private static final int BLOCK_LENGTH = 64;

    /** 缓冲区长度 */
    private static final int BUFFER_LENGTH = BLOCK_LENGTH * 1;

    /** 缓冲区 */
    private byte[] xBuf = new byte[BUFFER_LENGTH];

    /** 缓冲区偏移量 */
    private int xBufOff;

    /** 初始向量 */
    private byte[] V = SM3.iv.clone();

    private int cntBlock = 0;

    public SM3Digest() {
    }

    public SM3Digest(SM3Digest t)
    {
        System.arraycopy(t.xBuf, 0, this.xBuf, 0, t.xBuf.length);
        this.xBufOff = t.xBufOff;
        System.arraycopy(t.V, 0, this.V, 0, t.V.length);
    }

    /**
     * SM3结果输出
     *
     * @param out 保存SM3结构的缓冲区
     * @param outOff 缓冲区偏移量
     * @return
     */
    public int doFinal(byte[] out, int outOff)
    {
        byte[] tmp = doFinal();
        System.arraycopy(tmp, 0, out, 0, tmp.length);
        return BYTE_LENGTH;
    }

    public void reset()
    {
        xBufOff = 0;
        cntBlock = 0;
        V = SM3.iv.clone();
    }

    /**
     * 明文输入
     *
     * @param in
     *            明文输入缓冲区
     * @param inOff
     *            缓冲区偏移量
     * @param len
     *            明文长度
     */
    public void update(byte[] in, int inOff, int len)
    {
        int partLen = BUFFER_LENGTH - xBufOff;
        int inputLen = len;
        int dPos = inOff;
        if (partLen < inputLen)
        {
            System.arraycopy(in, dPos, xBuf, xBufOff, partLen);
            inputLen -= partLen;
            dPos += partLen;
            doUpdate();
            while (inputLen > BUFFER_LENGTH)
            {
                System.arraycopy(in, dPos, xBuf, 0, BUFFER_LENGTH);
                inputLen -= BUFFER_LENGTH;
                dPos += BUFFER_LENGTH;
                doUpdate();
            }
        }

        System.arraycopy(in, dPos, xBuf, xBufOff, inputLen);
        xBufOff += inputLen;
    }

    private void doUpdate()
    {
        byte[] B = new byte[BLOCK_LENGTH];
        for (int i = 0; i < BUFFER_LENGTH; i += BLOCK_LENGTH)
        {
            System.arraycopy(xBuf, i, B, 0, B.length);
            doHash(B);
        }
        xBufOff = 0;
    }

    private void doHash(byte[] B)
    {
        byte[] tmp = SM3.CF(V, B);
        System.arraycopy(tmp, 0, V, 0, V.length);
        cntBlock++;
    }

    private byte[] doFinal()
    {
        byte[] B = new byte[BLOCK_LENGTH];
        byte[] buffer = new byte[xBufOff];
        System.arraycopy(xBuf, 0, buffer, 0, buffer.length);
        byte[] tmp = SM3.padding(buffer, cntBlock);
        for (int i = 0; i < tmp.length; i += BLOCK_LENGTH)
        {
            System.arraycopy(tmp, i, B, 0, B.length);
            doHash(B);
        }
        return V;
    }

    public void update(byte in)
    {
        byte[] buffer = new byte[] { in };
        update(buffer, 0, 1);
    }

    public static byte[] update(byte[] data) {
        byte[] md = new byte[32];
        SM3Digest sm3 = new SM3Digest();
        sm3.update(data, 0, data.length);
        sm3.doFinal(md, 0);
        return md;
    }

    /**
     *  HMAC（K，M）=H（K⊕opad∣H（K⊕ipad∣M））
     *  B为计算hash过程中对信息分组时，每个信息块的长度（MD5算法中B=64）;
     *  L为哈希算法计算出的信息摘要长度（MD5算法中L=16）;
     *  K为任意长度的密钥，一般为了安全强度考虑，K的长度不小于L;
     *  Ipad为00110110（16进制为0x36）重复B次;
     *  Opad为01011100（16进制为0x5c）重复B次;
     *  M 代表一个消息输入，即待加密原文;
     *  H为哈希函数（例如SM3）。
     *  即:
     * （1）检查密钥K的长度。如果K的长度大于B则先使用摘要算法计算出一个长度为L的新密钥。如果后K的长度小于B，则在其后面追加0来使其长度达到B。
     * （2）将上一步生成的B字长的密钥字符串与ipad做异或运算，得到比特序列ipadkey。
     * （3）将ipadkey附加在消息M的开头；
     * （4）使用哈希函数H计算第3步中生成的数据流的信息摘要值。
     * （5） 将第1步生成的B字长密钥字符串与opad做异或运算，得到opadkey。
     * （6）再将第4步得到的结果填充到opadkey之后。
     * （7）使用哈希函数H计算第6步中生成的数据流的信息摘要值，输出结果就是最终的HMAC值。
     * @param data
     * @param key
     * @return
     */
    public static byte[] SM3HashMac(byte[] data,byte[] key){

        //1.填充0至key,或者hashkey,使其长度为sm3分组长度
        /** BLOCK_LENGTHSM3分组长度 ,64个字节,512位*/
        byte[] sm3_key;
        byte[] structured_key=new byte[BLOCK_LENGTH];
        byte[] IPAD=new byte[BLOCK_LENGTH];
        byte[] OPAD=new byte[BLOCK_LENGTH];
        if(key.length>BLOCK_LENGTH){
            sm3_key= update(key);
            System.arraycopy(sm3_key,0,structured_key,0,sm3_key.length);
        }else {
            System.arraycopy(key,0,structured_key,0,key.length);
        }

        //2.让处理之后的key 与ipad (分组长度的0x36)做异或运算
        for(int i=0;i<BLOCK_LENGTH;i++){
            IPAD[i]=0x36;
            OPAD[i]=0x5c;
        }
        byte[] ipadkey=HexUtil.XOR(structured_key,IPAD);

        //3.将2的结果与data拼接 将消息 data 附加到第二步产生的 64byte 字符串后面;
        int textLen= data.length;
        byte[] t3=new byte[BLOCK_LENGTH+textLen];
        System.arraycopy(ipadkey,0,t3,0,ipadkey.length);
        System.arraycopy(data,0,t3,ipadkey.length,data.length);

        //4.将3的结果sm3 哈希
        byte[] t4= update(t3);

        //5.让处理之后的key 与opad(分组长度的0x5c)做异或运算
        byte[] opadkey=HexUtil.XOR(structured_key,OPAD);

        //6. 4的结果拼接在5之后
        byte[] t6=new byte[BLOCK_LENGTH+t4.length];
        System.arraycopy(opadkey,0,t6,0,opadkey.length);
        System.arraycopy(t4,0,t6,opadkey.length,t4.length);

        //7.对6做hash
        return (update(t6));
    }

    public int getDigestSize()
    {
        return BYTE_LENGTH;
    }

    public static byte[] update(byte[] key, byte[] sourceData, byte[] userId)
    {
        SM2 sm2 = SM2.Instance();
        String keyStr = HexUtil.convertByteArrayToHexStr(key);
        BigInteger userD = new BigInteger(keyStr, 16);
        ECPoint userKey = sm2.ecc_point_g.multiply(userD);
        SM3Digest sm3 = new SM3Digest();
        if (!ToolUtil.isEmpty(userId)) {
            byte[] z = sm2.sm2GetZ(userId, userKey);
            sm3.update(z, 0, z.length);
        }
        sm3.update(sourceData, 0, sourceData.length);
        byte[] md = new byte[32];
        sm3.doFinal(md, 0);
        return md;
    }
}
