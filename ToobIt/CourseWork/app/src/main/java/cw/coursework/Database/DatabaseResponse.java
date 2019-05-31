package cw.coursework.Database;

/**
 * Created by pavlo on 02/03/2018.
 */

public class DatabaseResponse {


    private DatabaseAsyncTask task;
    private DatabaseRes res ;

    public DatabaseResponse(DatabaseAsyncTask task, DatabaseRes res)
    {
        this.task = task;
        this.res = res;
    }

    public DatabaseAsyncTask getTask()
    {
        return task;
    }
}
