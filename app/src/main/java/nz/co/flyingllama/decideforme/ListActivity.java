package nz.co.flyingllama.decideforme;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import nz.co.flyingllama.decideforme.Adapter.ListAdapter_ListItem;
import nz.co.flyingllama.decideforme.Database.Base_Database;
import nz.co.flyingllama.decideforme.Database.Manager_Database;

import nz.co.flyingllama.decideforme.Model.Model_List;
import nz.co.flyingllama.decideforme.Model.Model_ListItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ListActivity extends OrmLiteBaseActivity<Base_Database> implements View.OnClickListener {
    ListView mListView_ListItems;
    ListAdapter_ListItem mListAdapter_ListItem;
    Model_List mList;
    Model_ListItem mListItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Manager_Database.init(this);

        mList = null;

        if (getIntent().hasExtra("listId")) {
            mList = Manager_Database.getInstance().getList(getIntent().getIntExtra("listId", 0));

            Manager_Database.getInstance().resetActiveLists();
        } else {
            mList = Manager_Database.getInstance().getActiveList();
        }

        if (mList == null) {
            ListActivity.this.startActivity(new Intent(ListActivity.this, MainActivity.class));
        } else {
            mList.setActive(1);

            Manager_Database.getInstance().updateList(mList);

            mListView_ListItems = (ListView) findViewById(R.id.listview_list_items);
            mListAdapter_ListItem = new ListAdapter_ListItem(this, getLayoutInflater(), Manager_Database.getInstance(), mList);

            mListView_ListItems.setAdapter(mListAdapter_ListItem);

            Button addRestaurant = (Button) findViewById(R.id.add_list_item);
            Button chooseRestaurant = (Button) findViewById(R.id.make_selection);
            Button callRestaurant = (Button) findViewById(R.id.call_selection);
            Button webRestaurant = (Button) findViewById(R.id.web_selection);
            Button hideRestaurant = (Button) findViewById(R.id.hide_selection);
            Button backToLists = (Button) findViewById(R.id.back_to_lists);

            addRestaurant.setOnClickListener(this);
            chooseRestaurant.setOnClickListener(this);
            callRestaurant.setOnClickListener(this);
            webRestaurant.setOnClickListener(this);
            hideRestaurant.setOnClickListener(this);
            backToLists.setOnClickListener(this);

            populateListItems();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back_to_lists) {
            Manager_Database.getInstance().resetActiveLists();

            Intent intent = new Intent(ListActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            ListActivity.this.startActivity(intent);
            finish();
        } else if (view.getId() == R.id.add_list_item) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);

            final View textEntryView = inflater.inflate(R.layout.form_add_list_item, null);
            final EditText iName = (EditText) textEntryView.findViewById(R.id.list_item_name);
            final EditText iNumber = (EditText) textEntryView.findViewById(R.id.list_item_phone_number);
            final EditText iWebsite = (EditText) textEntryView.findViewById(R.id.list_item_website);

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

            builder.setView(textEntryView);
            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String itemName = iName.getText().toString();
                    String itemPhoneNumber = "";
                    String itemWebsite = "";

                    if (mList.getPhoneNumber() == 1) {
                        itemPhoneNumber = iNumber.getText().toString();
                    }

                    if (mList.getWebsite() == 1) {
                        itemWebsite = iWebsite.getText().toString();
                    }

                    Model_ListItem item = new Model_ListItem();
                    item.setName(itemName);
                    item.setPhoneNumber(itemPhoneNumber);
                    item.setWebsite(itemWebsite);
                    item.setChecked(1);
                    item.setSort(99);
                    item.setListId(mList.getId());

                    Manager_Database.getInstance().addListItem(item);

                    populateListItems();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });

            builder.show();
        } else if (view.getId() == R.id.make_selection) {
            ArrayList<Integer> selectedRestaurants = new ArrayList<Integer>();

            for (int i = 0; i < mListAdapter_ListItem.getListItems().size(); i++) {
                Model_ListItem restaurant = mListAdapter_ListItem.getItem(i);

                if (restaurant.isChecked()) {
                    selectedRestaurants.add(restaurant.getId());
                }
            }

            int restaurantId = selectedRestaurants.get(new Random().nextInt(selectedRestaurants.size()));
            mListItem = Manager_Database.getInstance().getListItem(restaurantId);

            TextView currentSelection = (TextView) findViewById(R.id.selection_text);
            LinearLayout selectionContainer = (LinearLayout) findViewById(R.id.selection_container);

            currentSelection.setText(mListItem.getName());
            selectionContainer.setVisibility(View.VISIBLE);

            findViewById(R.id.call_selection).setVisibility(View.GONE);
            findViewById(R.id.web_selection).setVisibility(View.GONE);

            if (mList.getPhoneNumber() == 1 && mListItem.getPhoneNumber().length() > 0) {
                findViewById(R.id.call_selection).setVisibility(View.VISIBLE);
            }

            if (mList.getWebsite() == 1 && mListItem.getWebsite().length() > 0) {
                findViewById(R.id.web_selection).setVisibility(View.VISIBLE);
            }
        } else if (view.getId() == R.id.call_selection) {
            if (mListItem.getPhoneNumber().length() > 0) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mListItem.getPhoneNumber()));
                startActivity(intent);
            } else {
                Toast.makeText(this, mListItem.getName() + " doesn't have a phone number set", Toast.LENGTH_LONG).show();
            }
        } else if (view.getId() == R.id.web_selection) {
            if (mListItem.getWebsite().length() > 0) {
                String url = mListItem.getWebsite();
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "http://" + url;
                }

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            } else {
                Toast.makeText(this, mListItem.getName() + " doesn't have a website set", Toast.LENGTH_LONG).show();
            }
        } else if (view.getId() == R.id.hide_selection) {
            LinearLayout dinnerContainer = (LinearLayout) findViewById(R.id.selection_container);
            dinnerContainer.setVisibility(View.GONE);
        }
    }

    public void populateListItems() {
        List<Model_ListItem> listItems = Manager_Database.getInstance().getListItems(mList.getId());
        mListAdapter_ListItem.updateData(listItems);
    }
}
