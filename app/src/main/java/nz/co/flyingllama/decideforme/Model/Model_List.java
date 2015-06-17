package nz.co.flyingllama.decideforme.Model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "list")
public class Model_List extends BaseDaoEnabled {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE_NUMBER = "phone_number";
    public static final String COLUMN_WEBSITE = "website";
    public static final String COLUMN_ACTIVE = "active";
    public static final String COLUMN_SORT = "sort";

    public static final String[] COLUMNS = {
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_PHONE_NUMBER,
            COLUMN_WEBSITE,
            COLUMN_ACTIVE,
            COLUMN_SORT,
    };

    @DatabaseField(generatedId = true, columnName = COLUMN_ID)
    private int id;

    @DatabaseField(columnName = COLUMN_NAME)
    private String name;

    @DatabaseField(columnName = COLUMN_PHONE_NUMBER)
    private int phoneNumber;

    @DatabaseField(columnName = COLUMN_WEBSITE)
    private int website;

    @DatabaseField(columnName = COLUMN_ACTIVE)
    private int active;

    @DatabaseField(columnName = COLUMN_SORT)
    private int sort;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean shouldDisplayPhoneNumber() {
        return phoneNumber == 1;
    }

    public int getWebsite() {
        return website;
    }

    public void setWebsite(int website) {
        this.website = website;
    }

    public boolean shouldDisplayWebsite() {
        return website == 1;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
