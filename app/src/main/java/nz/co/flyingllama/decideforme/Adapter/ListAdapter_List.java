package nz.co.flyingllama.decideforme.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import nz.co.flyingllama.decideforme.Database.Manager_Database;
import nz.co.flyingllama.decideforme.ListActivity;
import nz.co.flyingllama.decideforme.Model.Model_List;
import nz.co.flyingllama.decideforme.R;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter_List extends BaseAdapter {
    List<Model_List> mLists;
    Context mContext;
    LayoutInflater mInflater;
    Manager_Database mDb;

    public ListAdapter_List(Context context, LayoutInflater layoutInflater, Manager_Database managerDatabase) {
        mContext = context;
        mInflater = layoutInflater;
        mLists = new ArrayList<Model_List>();
        mDb = managerDatabase;
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
            view = mInflater.inflate(R.layout.list_list_row, null);

            holder = new ViewHolder();
            holder.id = list.getId();
            holder.position = position;
            holder.sort = list.getSort();
            holder.container = (LinearLayout) view.findViewById(R.id.list_row_container);
            holder.listName = (TextView) view.findViewById(R.id.list_name);
            holder.editList = (Button) view.findViewById(R.id.edit_list);
            holder.deleteList = (Button) view.findViewById(R.id.delete_list);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.listName.setText(list.getName());

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDb.resetActiveLists();

                Intent intent = new Intent(mContext, ListActivity.class);
                intent.putExtra("listId", list.getId());

                mContext.startActivity(intent);
            }
        });

        holder.editList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View textEntryView;
                final EditText lName;
                final CheckBox lIncludePhoneNumber;
                final CheckBox lIncludeWebsite;

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                LayoutInflater inflater = LayoutInflater.from(mContext);
                textEntryView = inflater.inflate(R.layout.form_add_list, null);

                lName = (EditText) textEntryView.findViewById(R.id.list_name);
                lIncludePhoneNumber = (CheckBox) textEntryView.findViewById(R.id.include_phone_number_checkbox);
                lIncludeWebsite = (CheckBox) textEntryView.findViewById(R.id.include_website_checkbox);

                lName.setText(list.getName());
                lIncludePhoneNumber.setChecked(false);
                lIncludeWebsite.setChecked(false);

                if (list.getPhoneNumber() == 1) {
                    lIncludePhoneNumber.setChecked(true);
                }

                if (list.getWebsite() == 1) {
                    lIncludeWebsite.setChecked(true);
                }

                builder.setView(textEntryView);
                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String listName = lName.getText().toString();
                        int includePhoneNumber = 0;
                        int includeWebsite = 0;

                        if (lIncludePhoneNumber.isChecked()) {
                            includePhoneNumber = 1;
                        }

                        if (lIncludeWebsite.isChecked()) {
                            includeWebsite = 1;
                        }

                        list.setName(listName);
                        list.setPhoneNumber(includePhoneNumber);
                        list.setWebsite(includeWebsite);
                        list.setActive(0);
                        list.setSort(99);

                        mDb.updateList(list);

                        updateData(mDb.getLists());
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                });

                builder.show();
            }
        });

        holder.deleteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setMessage("Delete this list and all it's items?");
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDb.deleteList(list);

                        updateData(mDb.getLists());
                    }
                });

                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });

                builder.show();
            }
        });

        return view;
    }

    public void updateData(List<Model_List> lists) {
        if (lists != null) {
            mLists = lists;
            notifyDataSetChanged();
        }
    }

    public List<Model_List> getLists() {
        return mLists;
    }

    private static class ViewHolder {
        public int id;
        public int position;
        public int sort;
        public LinearLayout container;
        public TextView listName;
        public Button editList;
        public Button deleteList;
    }
}
