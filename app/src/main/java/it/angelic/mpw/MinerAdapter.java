package it.angelic.mpw;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import it.angelic.mpw.model.PoolEnum;
import it.angelic.mpw.model.db.MinerDBRecord;
import it.angelic.mpw.model.jsonpojos.miners.Miner;

/**
 * Created by shine@angelic.it on 02/02/2018.
 */

class MinerAdapter extends RecyclerView.Adapter<MinerAdapter.MinerViewHolder>  {

    private final PoolEnum mPool;
    private ArrayList<MinerDBRecord> minersArray;


    public MinerAdapter(ArrayList<MinerDBRecord> minatori, PoolEnum mCur) {
        super();
        mPool = mCur;
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
        holder.bindBlock(minersArray.get(position), mPool);
    }

    @Override
    public int getItemCount() {
        return minersArray==null?0:minersArray.size();
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

        public MinerViewHolder(View v) {
            super(v);
            ctx = v.getContext();
            mblockMinerAddress = v.findViewById(R.id.blockMinerAddress);
            isOffline = v.findViewById(R.id.checkBoxMinerOffline);
            textViewBlockWhenValue = v.findViewById(R.id.textViewBlockWhenValue);
            textViewHashrateValue = v.findViewById(R.id.textViewHashrateValue);

            imageView2 = v.findViewById(R.id.imageView2);

        }

        public void bindBlock(final MinerDBRecord game, PoolEnum cur) {
            mblockMinerAddress.setText(game.getAddress());
            View.OnClickListener list = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://etherscan.io/address/" + game.getAddress()));
                    ctx.startActivity(i);
                }
            };
            mblockMinerAddress.setOnClickListener(list);
            imageView2.setOnClickListener(list);
            isOffline.setChecked(game.getOffline());
            mblockMinerAddress.setText(game.getAddress());
            textViewBlockWhenValue.setText(MainActivity.yearFormatExtended.format(game.getLastSeen()));
            textViewHashrateValue.setText(Utils.formatBigNumber(game.getHashRate()));

        }
    }
    public ArrayList<MinerDBRecord> getMinersArray() {
        return minersArray;
    }

    public void setMinersArray(ArrayList<MinerDBRecord> minersArray) {
        this.minersArray = minersArray;
    }
}
