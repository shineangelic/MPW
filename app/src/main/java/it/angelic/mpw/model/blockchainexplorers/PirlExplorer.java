package it.angelic.mpw.model.blockchainexplorers;

/**
 * Created by shine@angelic.it on 10/06/2018.
 */
public class PirlExplorer extends BlockChainExplorer {
    public PirlExplorer() {
        super("https://poseidon.pirl.io/explorer");
    }

    @Override
    public String getTransactionsPath() {
        return baseAddress.toString().concat("/transaction/");
    }
}
