package server.db;

public abstract class AbstractCRUD {

    public abstract String getListAllCmd();

    public abstract String getAddCmd();

    public abstract String getRetrieveCmd();

    public abstract String getUpdateCmd();

    public abstract String getDeleteCmd();

}
