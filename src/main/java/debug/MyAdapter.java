package debug;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yndongyong.widget.refreshlayout.DYRecylerAdapter;
import com.yndongyong.widget.refreshlayout.R;

/**
 * Created by dongzhiyong on 2017/3/26.
 */

public class MyAdapter extends DYRecylerAdapter<String> {

    public MyAdapter(Context _context, int model) {
        super(_context, model);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new StringViewHolder(mInflater.inflate(R.layout.recylcerview_footer_layout, parent, false
        ));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, String item, int position) {
        StringViewHolder viewHolder = (StringViewHolder) holder;
        viewHolder.tvName.setText(item);
        viewHolder.pbProgress.setVisibility(View.GONE);
    }

    private static class StringViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        ProgressBar pbProgress;

        public StringViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_footer);
            pbProgress = (ProgressBar) itemView.findViewById(R.id.pv_footer);
        }
    }

}
