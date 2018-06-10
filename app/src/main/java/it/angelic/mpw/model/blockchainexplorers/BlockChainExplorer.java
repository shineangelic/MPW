package it.angelic.mpw.model.blockchainexplorers;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by shine@angelic.it on 10/06/2018.
 */
public abstract class BlockChainExplorer {
    public URL baseAddress;


    public BlockChainExplorer(URL baseAddress) {
        this.baseAddress = baseAddress;
    }

    public BlockChainExplorer(String baseAddress) {
        try {
            this.baseAddress = new URL(baseAddress);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public URL getBaseAddress() {
        return baseAddress;
    }


    public String getAccountsPath() {
        return baseAddress.toString().concat("/address/");
    }

    public String getBlocksPath() {
        return baseAddress.toString().concat("/block/");
    }

    public String getTransactionsPath() {
        return baseAddress.toString().concat("/tx/");
    }
}
