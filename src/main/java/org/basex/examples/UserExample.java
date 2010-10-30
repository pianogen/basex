package org.basex.examples;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.*;

/**
 * This class presents methods to directly access
 * core maintenance and administration features.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-10, ISC License
 * @author BaseX Team
 */
public class UserExample {
  /** Default constructor. */
  protected UserExample() { }

  /**
   * Runs the example code.
   * @param args (ignored) command-line arguments
   * @throws BaseXException if a database command fails
   */
  public static void main(final String[] args) throws BaseXException {
    /** Database context. */
    final Context context = new Context();

    System.out.println("=== AdministrationExample ===\n");

    // ----------------------------------------------------------------------
    // Create a database
    System.out.println("* Create a database.");

    new CreateDB("input", "etc/xml/input.xml").execute(context);

    // ------------------------------------------------------------------------
    // Create a new user
    System.out.println("* Create a user.");

    new CreateUser("testuser", "password").execute(context);

    // ------------------------------------------------------------------------
    // Remove global user rights
    System.out.println("* Remove global user rights.");

    new Grant("NONE", "testuser").execute(context);

    // ------------------------------------------------------------------------
    // Grant local user rights on database 'input'
    System.out.println("* Grant local user rights.");

    new Grant("WRITE", "testuser", "input").execute(context);

    // ------------------------------------------------------------------------
    // Show global users
    System.out.println("* Show global users.");

    System.out.println(new ShowUsers().execute(context));

    // ------------------------------------------------------------------------
    // Show local users on a single database
    System.out.println("* Show local users.");

    System.out.println(new ShowUsers("input").execute(context));

    // ------------------------------------------------------------------------
    // Change user password
    System.out.println("* Alter a user's password.");

    new AlterUser("testuser", "newpass").execute(context);

    // ------------------------------------------------------------------------
    // Drop the database and user
    System.out.println("* Drop the user and database.");

    new DropUser("testuser").execute(context);
    new DropDB("input").execute(context);

    // ------------------------------------------------------------------------
    // Close the database context
    context.close();
  }
}
