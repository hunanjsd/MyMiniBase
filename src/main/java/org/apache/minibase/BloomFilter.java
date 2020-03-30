package org.apache.minibase;

/**
 * 布隆过滤器
 *
 * @author simo*/
public class BloomFilter {
  /** hash 循环次数 */
  private int k;
  /** 表示每个 key 占用的二进制 bit 数 */
  private int bitsPerKey;
  private int bitLen;
  /** 布隆过滤器返回的结果 */
  private byte[] result;

  public BloomFilter(int k, int bitsPerKey) {
    this.k = k;
    this.bitsPerKey = bitsPerKey;
  }

  public byte[] generate(byte[][] keys) {
    assert keys != null;
    bitLen = keys.length * bitsPerKey;
    bitLen = (bitLen + 7) / 8 << 3; // align the bitLen.
    bitLen = Math.max(bitLen, 64);
    result = new byte[bitLen >> 3];
    for (byte[] key : keys) {
      assert key != null;
      int h = Bytes.hash(key);
      for (int t = 0; t < k; t++) {
        int idx = (h % bitLen + bitLen) % bitLen;
        result[idx / 8] |= (1 << (idx % 8));
        int delta = (h >> 17) | (h << 15);
        h += delta;
      }
    }
    return result;
  }

  public boolean contains(byte[] key) {
    assert result != null;
    int h = Bytes.hash(key);
    for (int t = 0; t < k; t++) {
      int idx = (h % bitLen + bitLen) % bitLen;
      if ((result[idx / 8] & (1 << (idx % 8))) == 0) {
        return false;
      }
      int delta = (h >> 17) | (h << 15);
      h += delta;
    }
    return true;
  }
}
