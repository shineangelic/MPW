package it.angelic.noobpoolstats;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import it.angelic.noobpoolstats.model.jsonpojos.blocks.Matured;

/**
 * Created by shine@angelic.it on 01/11/2017.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.BlockViewHolder> {
    private Matured[] mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class BlockViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public CheckBox isOrphan;
        public CheckBox isUncle;
        public BlockViewHolder( View v) {
            super(v);
            mTextView = (TextView)v.findViewById(R.id.blockTransactionId);
            isOrphan = (CheckBox) v.findViewById(R.id.checkBoxBlockOrphan);
            isUncle = (CheckBox) v.findViewById(R.id.checkBoxBlockUncle);
        }
        public void bindBlock(Matured game) {
            mTextView.setText(game.getHash());
            isUncle.setChecked(game.getUncle());
            isOrphan.setChecked(game.getOrphan());
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Matured[] myDataset) {
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
}