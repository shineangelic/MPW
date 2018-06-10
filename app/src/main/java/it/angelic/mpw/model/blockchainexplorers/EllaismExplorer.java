package it.angelic.mpw.model.blockchainexplorers;

/**
 * Created by shine@angelic.it on 10/06/2018.
 */
public class EllaismExplorer extends BlockChainExplorer {
    public EllaismExplorer() {
        super("https://explorer.ellaism.org");
    }

    @Override
    public String getAccountsPath() {
        return baseAddress.toString().concat("/account/");
    }
}
