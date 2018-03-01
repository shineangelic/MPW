package it.angelic.mpw;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import it.angelic.mpw.model.enums.CurrencyEnum;
import it.angelic.mpw.model.enums.PoolEnum;
import it.angelic.mpw.model.db.MinerDBRecord;

/**
 * Created by shine@angelic.it on 02/02/2018.
 */

class MinerAdapter extends RecyclerView.Adapter<MinerAdapter.MinerViewHolder> {

    private final PoolEnum mPool;
    private final CurrencyEnum mCur;
    private ArrayList<MinerDBRecord> minersArray;


    public MinerAdapter(ArrayList<MinerDBRecord> minatori, PoolEnum pool, CurrencyEnum currencyEnum) {
        super();
        mPool = pool;
        mCur = currencyEnum;
        minersArray = minatori;
    }

    @Override
    public MinerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_miner, parent, false);
        // Task 2
        return new MinerAdapter.MinerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MinerViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.bindBlock(minersArray.get(position), mPool, mCur);
    }

    @Override
    public int getItemCount() {
        return minersArray == null ? 0 : minersArray.size();
    }

    public ArrayList<MinerDBRecord> getMinersArray() {
        return minersArray;
    }

    public void setMinersArray(ArrayList<MinerDBRecord> minersArray) {
        this.minersArray = minersArray;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class MinerViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private final TextView mblockMinerAddress;
        private final CheckBox isOffline;
        private final TextView textViewBlockWhenValue;
        private final TextView textViewHashrateValue;

        private final Context ctx;
        private final ImageView imageView2;
        private final TextView textViewMinerBlockFoundValue;
        private final TextView textViewMinerPaidValue;
        private final TextView textViewMinerFirstSeenValue;

        public MinerViewHolder(View v) {
            super(v);
            ctx = v.getContext();
            mblockMinerAddress = v.findViewById(R.id.blockMinerAddress);
            isOffline = v.findViewById(R.id.checkBoxMinerOffline);
            textViewMinerFirstSeenValue = v.findViewById(R.id.textViewMinerFirstSeenValue);
            textViewBlockWhenValue = v.findViewById(R.id.textViewBlockWhenValue);
            textViewHashrateValue = v.findViewById(R.id.textViewHashrateValue);
            textViewMinerBlockFoundValue = v.findViewById(R.id.textViewMinerBlockFoundValue);
            textViewMinerPaidValue = v.findViewById(R.id.textViewMinerPaidValue);

            imageView2 = v.findViewById(R.id.imageViewMinersLink);

        }

        public void bindBlock(final MinerDBRecord game, PoolEnum pool,final CurrencyEnum cur) {
            mblockMinerAddress.setText(game.getAddress().toUpperCase());
            View.OnClickListener list = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cur.getScannerSite() != null) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(cur.getScannerSite() + "/address/" + game.getAddress()));
                        ctx.startActivity(i);
                    } else {
                        Snackbar.make(view, "Blockchain explorer not available for "+cur.toString(), Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }
                }
            };
            mblockMinerAddress.setOnClickListener(list);
            imageView2.setOnClickListener(list);
            isOffline.setChecked(game.getOffline());
            isOffline.setVisibility(game.getOffline()?View.VISIBLE:View.INVISIBLE);
            textViewBlockWhenValue.setText(MainActivity.yearFormatExtended.format(game.getLastSeen()));
            textViewMinerFirstSeenValue.setText(MainActivity.yearFormatExtended.format(game.getFirstSeen()));
            textViewHashrateValue.setText(Utils.formatBigNumber(game.getHashRate()));
            textViewMinerPaidValue.setText(game.getPaid()==null?"NA":Utils.formatCurrency(game.getPaid(), cur));
            textViewMinerBlockFoundValue.setText(game.getBlocksFound()==null?"NA":"" +game.getBlocksFound());
            mblockMinerAddress.setSelected(true);
        }
    }
}
