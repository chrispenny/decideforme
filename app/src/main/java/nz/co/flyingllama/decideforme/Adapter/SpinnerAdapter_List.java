package nz.co.flyingllama.decideforme.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import nz.co.flyingllama.decideforme.Model.Model_List;
import nz.co.flyingllama.decideforme.R;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class SpinnerAdapter_List extends BaseAdapter {
    List<Model_List> mLists;
    Context mContext;
    LayoutInflater mInflater;

    public SpinnerAdapter_List(Context context, LayoutInflater layoutInflater, List<Model_List> lists) {
        mContext = context;
        mInflater = layoutInflater;
        mLists = lists;
    }

    @Override
    public int getCount() {
        return mLists.size();
    }

    @Override
    public Model_List getItem(int position) {
        return mLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final Model_List list = getItem(position);

        ViewHolder holder;

        if (view == null) {
            view = mInflater.inflate(R.layout.spinner_basic, null);

            holder = new ViewHolder();
            holder.listName = (TextView) view.findViewById(R.id.list_name);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.listName.setText(list.getName());

        return view;
    }

    private static class ViewHolder {
        TextView listName;
    }
}
