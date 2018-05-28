package de.lehmann.lehmannorm.logic.sqlbuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;

import de.lehmann.lehmannorm.entity.structure.EntityColumnInfo;

/**
 * @author Tim Lehmann
 */
public interface IStatementBuilder
{
  PreparedStatement buildStatement(final String tableName, final Set<EntityColumnInfo<Object>> entityColumnInfos,
      final Connection connection) throws SQLException;

  PreparedStatement buildStatement(final String tableName, final String columnsSeperatedByComma, final String tail,
      final Connection connection) throws SQLException;

  String generateStatementTail(final Set<EntityColumnInfo<Object>> entityColumnInfos);

  /**
   * @param entityColumnInfos
   * @return column names separated by ',' in brackets
   */
  static String processEntityColumns(final Set<EntityColumnInfo<Object>> entityColumnInfos)
  {
    final StringBuilder columnsBuilder;
    final Iterator<EntityColumnInfo<Object>> it = entityColumnInfos.iterator();

    // Put all column names in a string.

    columnsBuilder = new StringBuilder(it.next().getColumnName());

    while (it.hasNext())
    {

      final EntityColumnInfo<?> entityColumnInfo = it.next();

      if (entityColumnInfo.getColumnName() != null)
        columnsBuilder.append(",").append(entityColumnInfo.getColumnName());
      /*
       * If columnName is null the entity references to another entity in object model
       * but in relation model the other entity holds the foreign key for this entity.
       */
    }

    return columnsBuilder.toString();
  }

  public enum DefaultBuilderBundle
  {
    DEFAULT_INSERT_STATEMENT_BUILDER(new InsertStatementBuilder()),
    DEFAULT_SELECT_STATEMENT_BUILDER(new SelectStatementBuilder());

    private final IStatementBuilder param;

    private DefaultBuilderBundle(final IStatementBuilder param)
    {
      this.param = param;
    }

    public IStatementBuilder getStatementBuilder()
    {
      return param;
    }
  }
}