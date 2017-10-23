package de.lehmann.lehmannorm.models;

public interface IDatabaseConnectionInformations {

    public String getDatabaseUrl();

    public String getDatabaseName();

    public String getDatabaseUserName();

    public String getDatabasePassword();

    static final IDatabaseConnectionInformations MARIA_DB = new DatabaseConnectionInformations(
            "jdbc:mysql://127.0.0.1:3306", "SYNTHETIC_HEART", "root", "+");
}
