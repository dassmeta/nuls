/**
 * MIT License
 **
 * Copyright (c) 2017-2018 nuls.io
 **
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 **
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 **
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.nuls.protocol.context;

import io.nuls.core.constant.ErrorCode;
import io.nuls.core.exception.NulsRuntimeException;
import io.nuls.core.utils.cfg.IniEntity;
import io.nuls.core.utils.log.Log;
import io.nuls.core.utils.pass.PackingPasswordUtils;
import io.nuls.core.utils.spring.lite.core.SpringLiteContext;
import io.nuls.protocol.model.Block;
import io.nuls.protocol.model.Na;

import javax.xml.bind.annotation.XmlType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Niels
 */
public class NulsContext {

    public static short DEFAULT_CHAIN_ID = 1;

    private static String CACHED_PASSWORD_OF_WALLET  ;
    public static String DEFAULT_ACCOUNT_ID;

    public static int getMagicNumber() {
        return MAGIC_NUMBER;
    }

    public static void setMagicNumber(int magicNumber) {
        MAGIC_NUMBER = magicNumber;
    }

    public static int MAGIC_NUMBER;
    /**
     * cache the best block
     */
    private Block bestBlock;
    private Block genesisBlock;
    private Long netBestBlockHeight = 0L;

    public static Set<String> LOCAL_ADDRESS_LIST = ConcurrentHashMap.newKeySet();


    public Block getGenesisBlock() {
        while (genesisBlock == null) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
            }
        }
        return genesisBlock;
    }

    public void setGenesisBlock(Block block) {
        this.genesisBlock = block;
    }

    public String getModuleVersion(String module) {
        return "";
    }

    public Block getBestBlock() {
        if (bestBlock == null) {
            bestBlock = getGenesisBlock();
        }
        return bestBlock;
    }

    public long getBestHeight() {
        if (bestBlock == null) {
            bestBlock = getGenesisBlock();
        }
        return bestBlock.getHeader().getHeight();
    }

    private NulsContext() {

    }

    private static final NulsContext NC = new NulsContext();

    /**
     * get zhe only instance of NulsContext
     */
    public static final NulsContext getInstance() {
        return NC;
    }


    public void setBestBlock(Block bestBlock) {
        if(bestBlock==null){
            throw new NulsRuntimeException(ErrorCode.FAILED,"best block set to null!");
        }
        this.bestBlock = bestBlock;
//        Log.info("best height:"+bestBlock.getHeader().getHeight()+", hash:"+bestBlock.getHeader().getHash());
    }

    public static final <T> T getServiceBean(Class<T> tClass) {
        try {

            return SpringLiteContext.getBean(tClass);
        } catch (Exception e) {
            return getServiceBean(tClass, 0L);
        }
    }

    private static <T> T getServiceBean(Class<T> tClass, long l) {
        try {
            Thread.sleep(200L);
//            System.out.println("获取service失败！"+tClass);
        } catch (InterruptedException e1) {
            Log.error(e1);
        }
        try {
            return SpringLiteContext.getBean(tClass);
        } catch (Exception e) {
            if (l > 1200) {
                Log.error(e);
                return null;
            }
            return getServiceBean(tClass, l + 10L);
        }
    }

    public Long getNetBestBlockHeight() {
        if (null != bestBlock && netBestBlockHeight < bestBlock.getHeader().getHeight()) {
            return bestBlock.getHeader().getHeight();
        }
        if (null == netBestBlockHeight) {
            return 0L;
        }
        return netBestBlockHeight;
    }

    public Long getNetBestBlockHeightWithNull() {
        return netBestBlockHeight;
    }

    public void setNetBestBlockHeight(Long netBestBlockHeight) {
        this.netBestBlockHeight = netBestBlockHeight;
    }

    public static <T> List<T> getServiceBeanList(Class<T> tClass) {
        try {
            return SpringLiteContext.getBeanList(tClass);
        } catch (Exception e) {
            Log.error(e);
        }
        return null;
    }

    public static String getCachedPasswordOfWallet() {
        if(null==CACHED_PASSWORD_OF_WALLET){
            CACHED_PASSWORD_OF_WALLET = PackingPasswordUtils.read();
        }
        if(null==CACHED_PASSWORD_OF_WALLET){
            CACHED_PASSWORD_OF_WALLET = "nuls123456";
        }
        return CACHED_PASSWORD_OF_WALLET;
    }

    public static void setCachedPasswordOfWallet(String cachedPasswordOfWallet) {
        CACHED_PASSWORD_OF_WALLET = cachedPasswordOfWallet;
        //todo A temporary solution;
        PackingPasswordUtils.write(cachedPasswordOfWallet);
    }
}
