package it.angelic.mpw;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.List;

import it.angelic.mpw.model.enums.CurrencyEnum;
import it.angelic.mpw.model.jsonpojos.blocks.Matured;

/**
 * Created by shine@angelic.it on 01/11/2017.
 */
public class BlockAdapter extends RecyclerView.Adapter<BlockAdapter.BlockViewHolder> {
    private final CurrencyEnum cur;
    private final Context ctx;
    private List<Matured> blocksArray;

    // Provide a suitable constructor (depends on the kind of dataset)
    public BlockAdapter(List<Matured> myDataset, CurrencyEnum curr, Context v) {
        blocksArray = myDataset;
        cur = curr;
        ctx = v;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public BlockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_block, parent, false);
        // Task 2
        return new BlockViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull BlockViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.bindBlock(blocksArray.get(position), cur);

    }

    public void setBlocksArray(List<Matured> blocksArray) {
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
    class BlockViewHolder extends RecyclerView.ViewHolder {
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
        //
        private final ImageView imageView2;
        private final TextView textViewBlockDiff;
        private final TextView textViewBlockWhen;

        BlockViewHolder(View v) {
            super(v);
            //ctx = v.getContext();
            mTextView = v.findViewById(R.id.blockTransactionId);
            isOrphan = v.findViewById(R.id.checkBoxBlockOrphan);
            isUncle = v.findViewById(R.id.checkBoxBlockUncle);
            textViewBlockWhenValue = v.findViewById(R.id.textViewBlockWhenValue);
            textViewBlockWhen = v.findViewById(R.id.textViewBlockWhen);
            textViewBlockSharesValue = v.findViewById(R.id.textViewBlockSharesValue);
            textViewBlockDiffValue = v.findViewById(R.id.textViewBlockDiffValue);
            textViewBlockHeightValue = v.findViewById(R.id.textViewBlockHeightValue);
            textViewBlockRewardValue = v.findViewById(R.id.textViewBlockRewardValue);
            textViewBlockUncleHeight = v.findViewById(R.id.textViewBlockUncleHeight);
            textViewBlockUncleHeightValue = v.findViewById(R.id.textViewBlockUncleHeightValue);
            textViewBlockDiff = v.findViewById(R.id.textViewBlockDiff);
            imageView2 = v.findViewById(R.id.imageView2);

        }

        void bindBlock(final Matured game, final CurrencyEnum cur) {
            mTextView.setText(game.getHash().toUpperCase());
            View.OnClickListener list = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cur.getScannerSite() != null) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(cur.getScannerSite().getBlocksPath() + game.getHeight()));
                        ctx.startActivity(i);
                    } else {
                        Snackbar.make(view, "Blockchain explorer not available for " + cur.toString(), Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }
                }
            };
            mTextView.setOnClickListener(list);
            imageView2.setOnClickListener(list);
            isUncle.setChecked(game.getUncle());
            isOrphan.setChecked(game.getOrphan());
            textViewBlockWhen.setText(new StringBuilder().append("Block found ").append(Utils.getTimeAgo(game.getTimestamp())).toString());
            textViewBlockWhenValue.setText(MainActivity.yearFormatExtended.format(game.getTimestamp()));
            textViewBlockSharesValue.setText(Utils.formatBigNumber(game.getShares()));
            textViewBlockDiffValue.setText(Utils.formatBigNumber(game.getDifficulty()));
            textViewBlockHeightValue.setText("" + game.getHeight());
            try {
                textViewBlockRewardValue.setText(Utils.formatCurrency(ctx, Double.valueOf(game.getReward()) / 1000000000, cur));
            } catch (Exception e){
                textViewBlockRewardValue.setText("NA");
            }
            try {
                BigDecimal bd3 = Utils.computeBlockVariance(game.getShares(), game.getDifficulty());
                textViewBlockDiff.setText(String.format("Difficulty (variance %s%%)", bd3.stripTrailingZeros().toPlainString()));
            } catch (Exception e) {
                Log.e(Constants.TAG, "Errore refresh variance: " + e.getMessage());
                e.printStackTrace();
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