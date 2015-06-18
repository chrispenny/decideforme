package nz.co.flyingllama.decideforme.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import nz.co.flyingllama.decideforme.Database.Manager_Database;
import nz.co.flyingllama.decideforme.Model.Model_List;
import nz.co.flyingllama.decideforme.Model.Model_ListItem;
import nz.co.flyingllama.decideforme.R;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter_ListItem extends BaseAdapter {
    List<Model_ListItem> mListItems;
    Context mContext;
    LayoutInflater mInflater;
    Manager_Database mDb;
    Model_List mList;

    public ListAdapter_ListItem(Context context, LayoutInflater layoutInflater, Manager_Database managerDatabase, Model_List list) {
        mContext = context;
        mInflater = layoutInflater;
        mListItems = new ArrayList<Model_ListItem>();
        mDb = managerDatabase;
        mList = list;
    }

    @Override
    public int getCount() {
        return mListItems.size();
    }

    @Override
    public Model_ListItem getItem(int position) {
        return mListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final Model_ListItem listItem = getItem(position);

        ViewHolder holder;

        if (view == null) {
            view = mInflater.inflate(R.layout.list_list_item_row, null);

            holder = new ViewHolder();
            holder.id = listItem.getId();
            holder.position = position;
            holder.sort = listItem.getSort();
            holder.checkBox = (CheckBox) view.findViewById(R.id.restaurant_checkbox);
            holder.listItemName = (TextView) view.findViewById(R.id.list_item_name);
            holder.editListItem = (Button) view.findViewById(R.id.edit_list_item);
            holder.deleteListItem = (Button) view.findViewById(R.id.delete_list_item);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        String restaurantName = listItem.getName();
        holder.listItemName.setText(restaurantName);

        final int restaurantChecked = listItem.getChecked();
        if (restaurantChecked == 1) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;

                if (checkBox.isChecked()) {
                    listItem.setChecked(1);
                } else {
                    listItem.setChecked(0);
                }

                mDb.updateListItem(listItem);
            }
        });

        holder.editListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View textEntryView;
                final EditText iName;
                final EditText iNumber;
                final EditText iWebsite;

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                LayoutInflater inflater = LayoutInflater.from(mContext);
                textEntryView = inflater.inflate(R.layout.form_add_list_item, null);

                iName = (EditText) textEntryView.findViewById(R.id.list_item_name);
                iNumber = (EditText) textEntryView.findViewById(R.id.list_item_phone_number);
                iWebsite = (EditText) textEntryView.findViewById(R.id.list_item_website);

                if (mList.getPhoneNumber() == 1) {
                    iNumber.setVisibility(View.VISIBLE);
                } else {
                    iNumber.setVisibility(View.GONE);
                }

                if (mList.getWebsite() == 1) {
                    iWebsite.setVisibility(View.VISIBLE);
                } else {
                    iWebsite.setVisibility(View.GONE);
                }

                iName.setText(listItem.getName());
                iNumber.setText(listItem.getPhoneNumber());
                iWebsite.setText(listItem.getWebsite());

                builder.setView(textEntryView);
                builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String listItemName = iName.getText().toString();
                        String listItemNumber = iNumber.getText().toString();
                        String ListItemWebsite = iWebsite.getText().toString();

                        listItem.setName(listItemName);
                        listItem.setPhoneNumber(listItemNumber);
                        listItem.setWebsite(ListItemWebsite);

                        mDb.updateListItem(listItem);

                        updateData(mDb.getListItems(mList.getId()));
                    }
                });

                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                builder.show();
            }
        });

        holder.deleteListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setMessage("Delete \"" + listItem.getName() + "\"?");
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDb.deleteListItem(listItem);

                        updateData(mDb.getListItems(mList.getId()));
                    }
                });

                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                builder.show();
            }
        });

        return view;
    }

    public void updateData(List<Model_ListItem> listItems) {
        if (listItems != null) {
            mListItems = listItems;
            notifyDataSetChanged();
        }
    }

    public List<Model_ListItem> getListItems() {
        return mListItems;
    }

    private static class ViewHolder {
        public int id;
        public int position;
        public int sort;
        public CheckBox checkBox;
        public TextView listItemName;
        public Button editListItem;
        public Button deleteListItem;
    }
}
