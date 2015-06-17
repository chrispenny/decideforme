package nz.co.flyingllama.decideforme;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import nz.co.flyingllama.decideforme.Adapter.ListAdapter_List;
import nz.co.flyingllama.decideforme.Adapter.SpinnerAdapter_List;
import nz.co.flyingllama.decideforme.Database.Base_Database;
import nz.co.flyingllama.decideforme.Database.Manager_Database;

import nz.co.flyingllama.decideforme.Model.Model_List;
import nz.co.flyingllama.decideforme.Model.Model_ListItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends OrmLiteBaseActivity<Base_Database> implements View.OnClickListener {
    ListView mListView_Lists;
    ListAdapter_List mListAdapter_List;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Manager_Database.init(this);

        mListView_Lists = (ListView) findViewById(R.id.listview_lists);
        mListAdapter_List = new ListAdapter_List(this, getLayoutInflater(), Manager_Database.getInstance());

        mListView_Lists.setAdapter(mListAdapter_List);

        Button addList = (Button) findViewById(R.id.add_list);
        Button sendList = (Button) findViewById(R.id.send_list);
        Button importList = (Button) findViewById(R.id.import_list);

        addList.setOnClickListener(this);
        sendList.setOnClickListener(this);
        importList.setOnClickListener(this);

        populateLists();

        Model_List list = Manager_Database.getInstance().getActiveList();
        if (list != null) {
            Intent intent = new Intent(this, ListActivity.class);
            intent.putExtra("listId", list.getId());

            this.startActivity(intent);
        } else {
            Manager_Database.getInstance().resetActiveLists();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Manager_Database.getInstance().resetActiveLists();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_list) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);

            final View textEntryView = inflater.inflate(R.layout.form_add_list, null);
            final EditText lName = (EditText) textEntryView.findViewById(R.id.list_name);
            final CheckBox lIncludePhoneNumber = (CheckBox) textEntryView.findViewById(R.id.include_phone_number_checkbox);
            final CheckBox lIncludeWebsite = (CheckBox) textEntryView.findViewById(R.id.include_website_checkbox);

            builder.setView(textEntryView);
            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
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

                    Model_List list = new Model_List();
                    list.setName(listName);
                    list.setPhoneNumber(includePhoneNumber);
                    list.setWebsite(includeWebsite);
                    list.setActive(0);
                    list.setSort(99);

                    Manager_Database.getInstance().addList(list);

                    populateLists();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {}
            });

            builder.show();
        } else if (view.getId() == R.id.send_list) {
            if (Manager_Database.getInstance().getLists().size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = LayoutInflater.from(this);

                final View textEntryView = inflater.inflate(R.layout.form_send_list, null);
                final Spinner selectList = (Spinner) textEntryView.findViewById(R.id.select_list);
                final SpinnerAdapter_List adapter = new SpinnerAdapter_List(this, inflater, Manager_Database.getInstance().getLists());
                selectList.setAdapter(adapter);

                builder.setView(textEntryView);
                builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Model_List list = adapter.getItem(selectList.getSelectedItemPosition());
                        List<Model_ListItem> listItems = Manager_Database.getInstance().getListItems(list.getId());

                        JSONObject jList = new JSONObject();

                        try {
                            JSONArray jListItems = new JSONArray();

                            for (Model_ListItem listItem : listItems) {
                                JSONObject jListItem = new JSONObject();

                                jListItem.put(Model_ListItem.COLUMN_NAME, listItem.getName());
                                jListItem.put(Model_ListItem.COLUMN_PHONE_NUMBER, listItem.getPhoneNumber());
                                jListItem.put(Model_ListItem.COLUMN_WEBSITE, listItem.getWebsite());
                                jListItem.put(Model_ListItem.COLUMN_CHECKED, listItem.getChecked());

                                jListItems.put(jListItem);
                            }

                            jList.put(Model_List.COLUMN_NAME, list.getName());
                            jList.put(Model_List.COLUMN_PHONE_NUMBER, list.getPhoneNumber());
                            jList.put(Model_List.COLUMN_WEBSITE, list.getWebsite());
                            jList.put("list_items", jListItems);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String stringToSend = "Copy this message in it's entirety and past it into the 'Import' feature\r\n\r\n" + jList.toString();

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, stringToSend);
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                builder.show();
            } else {
                Toast.makeText(this, "You have no lists to send", Toast.LENGTH_LONG).show();
            }
        } else if (view.getId() == R.id.import_list) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);

            final View textEntryView = inflater.inflate(R.layout.form_import_list, null);
            final EditText importText = (EditText) textEntryView.findViewById(R.id.import_text);

            builder.setView(textEntryView);
            builder.setPositiveButton("Import", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (importText.getText().length() == 0) {
                        Toast.makeText(getApplicationContext(), "Please enter the import string before hitting 'Import'", Toast.LENGTH_LONG).show();
                    } else {
                        try {
                            String textString = importText.getText().toString();
                            textString = textString.replace("Copy this message in it's entirety and past it into the 'Import' feature", "");
                            textString = textString.trim();

                            JSONObject jList = new JSONObject(textString);

                            Model_List list = new Model_List();
                            list.setName(jList.getString(Model_List.COLUMN_NAME));
                            list.setPhoneNumber(jList.getInt(Model_List.COLUMN_PHONE_NUMBER));
                            list.setWebsite(jList.getInt(Model_List.COLUMN_WEBSITE));
                            list.setActive(0);
                            list.setSort(99);

                            Manager_Database.getInstance().addList(list);

                            JSONArray jListItems = jList.getJSONArray("list_items");
                            for (i = 0; i < jListItems.length(); i++)
                            {
                                JSONObject jListItem = jListItems.getJSONObject(i);

                                Model_ListItem listItem = new Model_ListItem();
                                listItem.setListId(list.getId());
                                listItem.setName(jListItem.getString(Model_ListItem.COLUMN_NAME));
                                listItem.setPhoneNumber(jListItem.getString(Model_ListItem.COLUMN_PHONE_NUMBER));
                                listItem.setWebsite(jListItem.getString(Model_ListItem.COLUMN_WEBSITE));
                                listItem.setChecked(jListItem.getInt(Model_ListItem.COLUMN_CHECKED));
                                listItem.setSort(99);

                                Manager_Database.getInstance().addListItem(listItem);
                            }

                            populateLists();
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Failed to import list. Please make sure you pasted in the entire 'import' message", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });

            builder.show();
        }
    }

    public void populateLists() {
        List<Model_List> restaurants = Manager_Database.getInstance().getLists();
        mListAdapter_List.updateData(restaurants);
    }
}
