package it.angelic.noobpoolstats;

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

import it.angelic.noobpoolstats.model.jsonpojos.blocks.Matured;

/**
 * Created by shine@angelic.it on 01/11/2017.
 */
public class BlockAdapter extends RecyclerView.Adapter<BlockAdapter.BlockViewHolder> {
    private Matured[] mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public BlockAdapter(Matured[] myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BlockViewHolder onCreateViewHolder(ViewGroup parent,
                                              int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_block, parent, false);
        // Task 2
        BlockViewHolder holder = new BlockViewHolder(view);
        return holder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(BlockViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.bindBlock(mDataset[position]);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class BlockViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewBlockWhenValue;
        private final TextView textViewBlockSharesValue;
        private final TextView textViewBlockDiffValue;
        private final TextView textViewBlockHeightValue;
        private final TextView textViewBlockRewardValue;
        private final TextView textViewBlockUncleHeightValue;
        private final TextView textViewBlockUncleHeight;
        private final Context ctx;
        private final ImageView imageView2;
        // each data item is just a string in this case
        public TextView mTextView;
        public CheckBox isOrphan;
        public CheckBox isUncle;

        public BlockViewHolder(View v) {
            super(v);
            ctx = v.getContext();
            mTextView = (TextView) v.findViewById(R.id.blockTransactionId);
            isOrphan = (CheckBox) v.findViewById(R.id.checkBoxBlockOrphan);
            isUncle = (CheckBox) v.findViewById(R.id.checkBoxBlockUncle);
            textViewBlockWhenValue = (TextView) v.findViewById(R.id.textViewBlockWhenValue);
            textViewBlockSharesValue = (TextView) v.findViewById(R.id.textViewBlockSharesValue);
            textViewBlockDiffValue = (TextView) v.findViewById(R.id.textViewBlockDiffValue);
            textViewBlockHeightValue = (TextView) v.findViewById(R.id.textViewBlockHeightValue);
            textViewBlockRewardValue = (TextView) v.findViewById(R.id.textViewBlockRewardValue);
            textViewBlockUncleHeight = (TextView) v.findViewById(R.id.textViewBlockUncleHeight);
            textViewBlockUncleHeightValue = (TextView) v.findViewById(R.id.textViewBlockUncleHeightValue);
            imageView2 = (ImageView) v.findViewById(R.id.imageView2);

        }

        public void bindBlock(final Matured game) {
            mTextView.setText(game.getHash().toUpperCase());
            View.OnClickListener list = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://etherscan.io/block/" + game.getHeight()));
                    ctx.startActivity(i);
                }
            };
            mTextView.setOnClickListener(list);
            imageView2.setOnClickListener(list);
            isUncle.setChecked(game.getUncle());
            isOrphan.setChecked(game.getOrphan());
            textViewBlockWhenValue.setText(MainActivity.yearFormatExtended.format(game.getTimestamp()));
            textViewBlockSharesValue.setText(Utils.formatBigNumber(game.getShares()));
            textViewBlockDiffValue.setText(Utils.formatBigNumber(game.getDifficulty()));
            textViewBlockHeightValue.setText(""+game.getHeight());
            textViewBlockRewardValue.setText(Utils.formatEthCurrency(game.getReward() / 1000000000));
            if (!game.getUncle()) {
                textViewBlockUncleHeight.setVisibility(View.INVISIBLE);
                textViewBlockUncleHeightValue.setVisibility(View.INVISIBLE);
            } else {
                textViewBlockUncleHeight.setVisibility(View.VISIBLE);
                textViewBlockUncleHeightValue.setVisibility(View.VISIBLE);
                textViewBlockUncleHeightValue.setText(""+game.getUncleHeight());
            }

            if (!game.getOrphan())
                isOrphan.setVisibility(View.INVISIBLE);
            else
                isOrphan.setVisibility(View.VISIBLE);
        }
    }
}