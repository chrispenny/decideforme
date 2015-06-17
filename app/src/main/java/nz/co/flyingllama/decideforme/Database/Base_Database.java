package nz.co.flyingllama.decideforme.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import nz.co.flyingllama.decideforme.Model.Model_List;
import nz.co.flyingllama.decideforme.Model.Model_ListItem;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class Base_Database extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "DecideForMeDB.sqlite";

    private static final int DATABASE_VERSION = 1;

    private Dao<Model_List, Integer> listDao = null;
    private Dao<Model_ListItem, Integer> listItemDao = null;

    public Base_Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Model_List.class);
            TableUtils.createTable(connectionSource, Model_ListItem.class);

            Model_List list = new Model_List();
            list.setName("Dinner");
            list.setPhoneNumber(1);
            list.setWebsite(1);
            list.setActive(0);
            list.setSort(99);

            getListDao().create(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i2) {
        try {
            TableUtils.dropTable(connectionSource, Model_List.class, true);
            TableUtils.dropTable(connectionSource, Model_ListItem.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            Log.e(Base_Database.class.getName(), "Failed upgrade", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<Model_List, Integer> getListDao() {
        if (listDao == null) {
            try {
                listDao = getDao(Model_List.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return listDao;
    }

    public Dao<Model_ListItem, Integer> getListItemDao() {
        if (listItemDao == null) {
            try {
                listItemDao = getDao(Model_ListItem.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return listItemDao;
    }
}
