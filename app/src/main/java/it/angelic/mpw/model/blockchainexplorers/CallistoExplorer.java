package it.angelic.mpw.model.blockchainexplorers;

/**
 * Created by shine@angelic.it on 10/06/2018.
 */
public class CallistoExplorer extends BlockChainExplorer {
    public CallistoExplorer() {
        super("https://explorer.callisto.network");
    }

    @Override
    public String getAccountsPath() {
        return baseAddress.toString().concat("/account/");
    }

}
