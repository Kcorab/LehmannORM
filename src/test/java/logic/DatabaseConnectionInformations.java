package logic;

public class DatabaseConnectionInformations implements IDatabaseConnectionInformations {

    private final String databaseUrl;
    private final String databaseName;
    private final String databaseUserName;
    private final String DatabasePassword;

    public DatabaseConnectionInformations(final String databaseUrl, final String databaseName,
            final String databaseUserName, final String databasePassword) {
        super();
        this.databaseUrl = databaseUrl;
        this.databaseName = databaseName;
        this.databaseUserName = databaseUserName;
        DatabasePassword = databasePassword;
    }

    @Override
    public String getDatabaseUrl() {
        return databaseUrl;
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public String getDatabaseUserName() {
        return databaseUserName;
    }

    @Override
    public String getDatabasePassword() {
        return DatabasePassword;
    }
}
