package de.lehmann.lehmannorm.logic.mocks;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import de.lehmann.lehmannorm.stubs.PreparedStatementStub;

/**
 * Behavior mock
 *
 * @author Tim Lehmann
 */
public class StatementMock extends PreparedStatementStub {

    private final List<Object>     columnValues = new ArrayList<>(5);
    private final Consumer<Object> callback;

    public StatementMock(final Consumer<Object> callback) {
        super();
        this.callback = callback;
    }

    @Override
    public void setObject(final int parameterIndex, final Object x) throws SQLException {

        columnValues.add(x);
    }

    @Override
    public boolean execute() throws SQLException {

        callback.accept(columnValues.get(0));

        return true;
    }
}
