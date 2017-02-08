package cnedu.ustcjd.scrollingtest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jd5737 on 2017/2/6.
 */

public class RecyclerViewListAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<String> infoList;

    public RecyclerViewListAdapter(Context context, List<String> infoList) {
        this.mContext = context;
        this.infoList = infoList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_list_item, parent, false);
        SimpleVH sVH = new SimpleVH(rootView);
        return sVH;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final SimpleVH sVH = (SimpleVH) holder;
        sVH.tvName.setText(infoList.get(position));
        sVH.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoList.remove(sVH.getAdapterPosition());
                notifyItemRemoved(sVH.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    class SimpleVH  extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvDelete;
        public SimpleVH(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvDelete = (TextView) itemView.findViewById(R.id.tv_delete);
        }
    }
}
