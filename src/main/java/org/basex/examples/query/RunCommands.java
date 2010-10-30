package org.basex.examples.query;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.*;

/**
 * This class demonstrates database creation and dropping.
 * It then shows how to add indexes to the database and retrieve
 * some information on the database structures.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-10, ISC License
 * @author BaseX Team
 */
public final class RunCommands {
  /**
   * Runs the example code.
   * @param args (ignored) command-line arguments
   * @throws BaseXException if a database command fails
   */
  public static void main(final String[] args) throws BaseXException {
    /** Database context. */
    Context context = new Context();

    System.out.println("=== DBExample ===\n");

    // ------------------------------------------------------------------------
    // Create a database from a local or remote XML document or XML String
    System.out.println("* Create a database.");

    new CreateDB("DBExample", "etc/xml/input.xml").execute(context);

    // ------------------------------------------------------------------------
    // Close and reopen the database
    System.out.println("* Close and reopen database.");

    new Close().execute(context);
    new Open("DBExample").execute(context);

    // ------------------------------------------------------------------------
    // Additionally create a full-text index
    System.out.println("* Create a full-text index.");

    new CreateIndex("fulltext").execute(context);

    // ------------------------------------------------------------------------
    // Show information on the currently opened database
    System.out.println("* Show database information:");

    System.out.println(new InfoDB().execute(context));

    // ------------------------------------------------------------------------
    // Optimize the internal database structures.
    // This command is recommendable after all kinds of database updates
    System.out.println("* Optimize the database.");

    new Optimize().execute(context);

    // ------------------------------------------------------------------------
    // Drop indexes to save disk space
    System.out.println("* Drop indexes.");

    new DropIndex("text").execute(context);
    new DropIndex("attribute").execute(context);
    new DropIndex("fulltext").execute(context);
    new DropIndex("path").execute(context);

    // ------------------------------------------------------------------------
    // Show all existing databases
    System.out.println("* Show existing databases:");

    System.out.println(new List().execute(context));

    // ------------------------------------------------------------------------
    // Drop the database
    System.out.println("* Drop the database.");

    new DropDB("DBExample").execute(context);

    // ------------------------------------------------------------------------
    // Close the database context
    context.close();
  }
}
