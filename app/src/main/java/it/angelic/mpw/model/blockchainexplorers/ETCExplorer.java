package it.angelic.mpw.model.blockchainexplorers;

/**
 * Created by shine@angelic.it on 10/06/2018.
 */
public class ETCExplorer extends BlockChainExplorer {
    public ETCExplorer() {
        super("https://gastracker.io");
    }

    @Override
    public String getAccountsPath() {
        return baseAddress.toString().concat("/miner/");
    }
}
