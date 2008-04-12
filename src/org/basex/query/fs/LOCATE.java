package org.basex.query.fs;

import static org.basex.Text.*;

import java.io.IOException;

import org.basex.core.Context;
import org.basex.data.Data;
import org.basex.io.PrintOutput;
import org.basex.query.QueryException;
import org.basex.query.xpath.XPathProcessor;
import org.basex.data.Nodes;
import org.basex.util.GetOpts;

/**
 * Performs a locate command.
 * 
 * @author Workgroup DBIS, University of Konstanz 2005-08, ISC License
 * @author Hannes Schwarz - Hannes.Schwarz@gmail.com
 *
 */
public class LOCATE {


  /** Data reference. */
  private final Data data;

  /** Data reference. */
  private final Nodes input;

  /** PrintOutPutStream. */
  private PrintOutput out;

  /** Shows if an error occurs. */
  private boolean fError;

  /** Shows if an error occurs. */
  private boolean fAccomplished;

  /** limit output. */
  private boolean lFlag = false;

  /** Just print filesfound. */
  private boolean cFlag = false;

  /** filename to search for. */
  private String fileToFind;

  /** Counter of files found. */
  private int filesfound;

  /**
   * Simplified Constructor.
   * @param ctx data context
   * @param output output stream
   */
  public LOCATE(final Context ctx, final PrintOutput output) {    
    this.input = ctx.current();
    this.data = ctx.data();
    this.out = output;
    this.fError = false;
    this.fAccomplished = false;
    this.fileToFind = null;
    this.filesfound = 0;
  }

  /**
   * Performs an LOCATE command.
   * 
   * @param cmd - command line
   * @throws IOException - in case of problems with the PrintOutput 
   */
  public void locateMain(final String cmd) 
  throws IOException {

    GetOpts g = new GetOpts(cmd, "l:chV:");
    char version = (char) -1;
    int limit = -1;
    // get all Options
    int ch = g.getopt();
    while (ch != -1) {
      switch (ch) {
        case 'l':
          // Limit output to number of file names and exit.
          limit = Integer.parseInt(g.getOptarg());
          lFlag = true;
          break;
        case 'c':
          //Suppress normal output; instead print a 
          //count of matching file names.
          cFlag = true;
          break;
        case 'V':
          version = g.getOptarg().charAt(0);
          break;          
        case 'h':
          printHelp();
          fAccomplished = true;
          break;
        case ':':         
          fError = true;
          out.print("ls: missing argument");
          break;  
        case '?':         
          fError = true;
          out.print("ls: illegal option");
          break;
      }      
      if(fError || fAccomplished) {
        // more options ?
        return;
      }
      ch = g.getopt();
    }

    fileToFind = g.getPath();
    if(fileToFind == null) {
      out.print("usage: locate  [-l limit] [-c] [-h] -V 1 ...");
      out.print(NL);
    }

    // Version -  1 = use table
    //            2 = use xquery
    //            3 = use xquery + index
    switch (version) {
      case '1':
        locateTable(FSUtils.getROOTDIR(), limit);
        break;
      case '2':
        locateXQuery(limit);
        break;
      case '3':
        out.print("Not yet implemented");
        break;
      default:
        locateTable(FSUtils.getROOTDIR(), limit);
      break;        
    }

    if(cFlag) {
      printCount();
    }
  }

  /**
   *  Performs a locate command.
   * @param pre - the current dir.
   * @param limit - Limit output to number of file names and exit.
   *   
   * @throws IOException in case of problems with the PrintOutput 
   */
  private void locateTable(final int pre, final int limit) throws IOException {

    if(!lFlag || filesfound < limit) {            
      int[] dir = FSUtils.getAllOfDir(data, pre);        
      int[] allDir = new int[dir.length];
      int j = 0;
      for(int i : dir) {        
        if(FSUtils.isDir(data, i))
          allDir[j++] = i;         
        if(FSUtils.isFile(data, i)) {
          // if found print with path      
          String filefound = FSUtils.getFileName(data, i);
          if(fileToFind.equalsIgnoreCase(filefound)) {
            ++filesfound;
            if(!cFlag) {
              out.print(FSUtils.getPath(data, i));
              out.print(NL);
            }
          }
        }
      }
      // repeat for all dirs
      for(int i = 0; allDir[i] != 0 && i < allDir.length; i++) {
        locateTable(allDir[i], limit);
      }      
    }
  }



  /**
   *  Performs a locate command.
   *  
   * @param limit - Limit output to number of file names and exit.
   * 
   * @throws IOException in case of problems with the PrintOutput 
   */
  private void locateXQuery(final int limit) throws IOException {
    String query = "//file[@name='" + fileToFind + "']";    
    XPathProcessor qu = new XPathProcessor(query);
    try {
      Nodes result = qu.queryNodes(input);
      filesfound = result.size;
      if(!cFlag) {
        for(int i = 0; i < filesfound && 
        (!lFlag || i < limit); i++) {                
          out.print(FSUtils.getPath(data, result.pre[i]));
          out.print(NL);
        }
      }      
    } catch(QueryException e) {
      e.printStackTrace();
    }
  }

  /**
   * Print the number of files found.
   * 
   * @throws IOException in case of problems with the PrintOutput
   */  
  private void printCount() throws IOException {
    out.print("" + filesfound);
  }

  /**
   * Print the help.
   * 
   * @throws IOException in case of problems with the PrintOutput
   */
  private void printHelp() throws IOException {
    out.print("help");

  }

}

