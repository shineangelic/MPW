package it.angelic.mpw;

import android.content.ClipData;
import android.content.ClipboardManager;
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
import java.util.List;

import it.angelic.mpw.model.enums.CurrencyEnum;
import it.angelic.mpw.model.jsonpojos.blocks.Matured;

/**
 * Created by shine@angelic.it on 01/11/2017.
 */
public class BlockAdapter extends RecyclerView.Adapter<BlockAdapter.BlockViewHolder> {
    private final CurrencyEnum cur;
    private List<Matured> blocksArray;

    // Provide a suitable constructor (depends on the kind of dataset)
    public BlockAdapter(List<Matured> myDataset, CurrencyEnum curr) {
        blocksArray = myDataset;
        cur = curr;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BlockViewHolder onCreateViewHolder(ViewGroup parent,
                                              int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_block, parent, false);
        // Task 2
        return new BlockViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(BlockViewHolder holder, int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.bindBlock(blocksArray.get(position), cur);

    }

    public void setBlocksArray( List<Matured> blocksArray) {
        this.blocksArray = blocksArray;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return blocksArray.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class BlockViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private final TextView mTextView;
        private final CheckBox isOrphan;
        private final CheckBox isUncle;
        private final TextView textViewBlockWhenValue;
        private final TextView textViewBlockSharesValue;
        private final TextView textViewBlockDiffValue;
        private final TextView textViewBlockHeightValue;
        private final TextView textViewBlockRewardValue;
        private final TextView textViewBlockUncleHeightValue;
        private final TextView textViewBlockUncleHeight;
        private final Context ctx;
        private final ImageView imageView2;

        public BlockViewHolder(View v) {
            super(v);
            ctx = v.getContext();
            mTextView = v.findViewById(R.id.blockTransactionId);
            isOrphan = v.findViewById(R.id.checkBoxBlockOrphan);
            isUncle = v.findViewById(R.id.checkBoxBlockUncle);
            textViewBlockWhenValue = v.findViewById(R.id.textViewBlockWhenValue);
            textViewBlockSharesValue = v.findViewById(R.id.textViewBlockSharesValue);
            textViewBlockDiffValue = v.findViewById(R.id.textViewBlockDiffValue);
            textViewBlockHeightValue = v.findViewById(R.id.textViewBlockHeightValue);
            textViewBlockRewardValue = v.findViewById(R.id.textViewBlockRewardValue);
            textViewBlockUncleHeight = v.findViewById(R.id.textViewBlockUncleHeight);
            textViewBlockUncleHeightValue = v.findViewById(R.id.textViewBlockUncleHeightValue);
            imageView2 = v.findViewById(R.id.imageView2);

        }

        public void bindBlock(final Matured game, final CurrencyEnum cur) {
            mTextView.setText(game.getHash().toUpperCase());
            View.OnClickListener list = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cur.getScannerSite() != null) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(cur.getScannerSite() + "/block/" + game.getHeight()));
                        ctx.startActivity(i);
                    } else {
                        //ClipboardManager clipboard = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
                        //ClipData clip = ClipData.newPlainText("NoobPool Client", "0xbba4e04fe3692ae8ddc8599a65f64cdc00606a13");
                        //clipboard.setPrimaryClip(clip);
                        Snackbar.make(view, "Blockchain explorer not available for "+cur.toString(), Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }
                }
            };
            mTextView.setOnClickListener(list);
            imageView2.setOnClickListener(list);
            isUncle.setChecked(game.getUncle());
            isOrphan.setChecked(game.getOrphan());
            textViewBlockWhenValue.setText(MainActivity.yearFormatExtended.format(game.getTimestamp()));
            textViewBlockSharesValue.setText(Utils.formatBigNumber(game.getShares()));
            textViewBlockDiffValue.setText(Utils.formatBigNumber(game.getDifficulty()));
            textViewBlockHeightValue.setText("" + game.getHeight());
            try {
                textViewBlockRewardValue.setText(Utils.formatCurrency(ctx,Long.valueOf(game.getReward()) / 1000000000, cur));
            }catch (Exception io ){
                textViewBlockRewardValue.setText("NA");
            }
            if (!game.getUncle()) {
                isUncle.setVisibility(View.INVISIBLE);
                textViewBlockUncleHeight.setVisibility(View.INVISIBLE);
                textViewBlockUncleHeightValue.setVisibility(View.INVISIBLE);
            } else {
                isUncle.setVisibility(View.VISIBLE);
                textViewBlockUncleHeight.setVisibility(View.VISIBLE);
                textViewBlockUncleHeightValue.setVisibility(View.VISIBLE);
                textViewBlockUncleHeightValue.setText("" + game.getUncleHeight());
            }

            if (!game.getOrphan())
                isOrphan.setVisibility(View.INVISIBLE);
            else
                isOrphan.setVisibility(View.VISIBLE);
        }
    }
}