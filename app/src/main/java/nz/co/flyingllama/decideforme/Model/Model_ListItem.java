package nz.co.flyingllama.decideforme.Model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "list_item")
public class Model_ListItem extends BaseDaoEnabled {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE_NUMBER = "phone_number";
    public static final String COLUMN_WEBSITE = "website";
    public static final String COLUMN_CHECKED = "checked";
    public static final String COLUMN_LIST_ID = "list_id";
    public static final String COLUMN_SORT = "sort";

    public static final String[] COLUMNS = {
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_PHONE_NUMBER,
            COLUMN_WEBSITE,
            COLUMN_CHECKED,
            COLUMN_LIST_ID,
            COLUMN_SORT,
    };

    @DatabaseField(generatedId = true, columnName = COLUMN_ID)
    private int id;

    @DatabaseField(columnName = COLUMN_NAME)
    private String name;

    @DatabaseField(columnName = COLUMN_PHONE_NUMBER)
    private String phoneNumber;

    @DatabaseField(columnName = COLUMN_WEBSITE)
    private String website;

    @DatabaseField(columnName = COLUMN_CHECKED)
    private int checked;

    @DatabaseField(columnName = COLUMN_LIST_ID)
    private int listId;

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public int getChecked() {
        return checked;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }

    public boolean isChecked() {
        return this.checked == 1;
    }

    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
