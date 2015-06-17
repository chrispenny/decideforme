package nz.co.flyingllama.decideforme.Database;

import android.content.Context;
import nz.co.flyingllama.decideforme.Model.Model_List;
import nz.co.flyingllama.decideforme.Model.Model_ListItem;

import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class Manager_Database {
    static private Manager_Database instance;

    static public void init(Context ctx) {
        if (null==instance) {
            instance = new Manager_Database(ctx);
        }
    }

    static public Manager_Database getInstance() {
        return instance;
    }

    private Base_Database helper;
    private Manager_Database(Context ctx) {
        helper = new Base_Database(ctx);
    }

    private Base_Database getHelper() {
        return helper;
    }

    public void resetActiveLists() {
        try {
            QueryBuilder<Model_List, Integer> queryBuilder = getHelper().getListDao().queryBuilder();
            queryBuilder.where().eq(Model_List.COLUMN_ACTIVE, 1);

            List<Model_List> lists = getHelper().getListDao().query(queryBuilder.prepare());

            for (Model_List list : lists) {
                list.setActive(0);
                getHelper().getListDao().update(list);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Model_List getActiveList() {
        Model_List list = null;

        try {
            QueryBuilder<Model_List, Integer> queryBuilder = getHelper().getListDao().queryBuilder();
            queryBuilder.where().eq(Model_List.COLUMN_ACTIVE, 1);

            list = getHelper().getListDao().queryForFirst(queryBuilder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public Model_List getList(int id) {
        Model_List list = null;

        try {
            list = getHelper().getListDao().queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Model_List> getLists() {
        List<Model_List> lists = null;

        try {
            lists = getHelper().getListDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lists;
    }

    public void addList(Model_List list) {
        try {
            getHelper().getListDao().create(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateList(Model_List list) {
        try {
            getHelper().getListDao().update(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteList(Model_List list) {
        try {
            List<Model_ListItem> listItems = getListItems(list.getId());

            for (Model_ListItem listItem : listItems) {
                getHelper().getListItemDao().delete(listItem);
            }

            getHelper().getListDao().delete(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Model_ListItem getListItem(int id) {
        Model_ListItem listItem = null;

        try {
            listItem = getHelper().getListItemDao().queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listItem;
    }

    public List<Model_ListItem> getListItems(int id) {
        List<Model_ListItem> listItems = null;

        try {
            QueryBuilder<Model_ListItem, Integer> queryBuilder = getHelper().getListItemDao().queryBuilder();
            queryBuilder.where().eq(Model_ListItem.COLUMN_LIST_ID, id);

            listItems = getHelper().getListItemDao().query(queryBuilder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listItems;
    }

    public void addListItem(Model_ListItem listItem) {
        try {
            getHelper().getListItemDao().create(listItem);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateListItem(Model_ListItem listItem) {
        try {
            getHelper().getListItemDao().update(listItem);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteListItem(Model_ListItem listItem) {
        try {
            getHelper().getListItemDao().delete(listItem);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
