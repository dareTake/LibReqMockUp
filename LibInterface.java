/**
 * Created with IntelliJ IDEA.
 * User: dare
 * Date: 5/29/13
 * Time: 5:21 PM
 */

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.io.*;
import java.util.Date;

public class LibInterface {
    static void logger(String msg) {
        //log messages with current timestamp
        try{
            PrintWriter app = new PrintWriter(new BufferedWriter(new FileWriter("log.txt",true)));
            Date dt = new Date();
            app.println( "[" + new Timestamp(dt.getTime()) +"]" + "[" +  msg +"]" );
            app.close();
        }catch(IOException e) {
            System.out.println("Cannot open log file...");
            System.exit(1);
        }
    }

    /**
     * Used to process requests of type 1
     * Req 7.3
     */
    static void process_req1() {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String data;

        try {

            //init steps
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/lib_db" ,
                    "lib_req",
                    "req");
            pst = con.prepareStatement("SELECT * FROM `book_details` order by `num_issues` desc limit 5;");
            rs = pst.executeQuery();

            //create json data
            data =  "{ \"frequent_list\": {";
            while(rs.next()) {
               data += "\n\t\"book\":{ \n\t\t\"id\": \" " + rs.getString(1) + "\",";
               data += "\n\t\t\"name\": \" " + rs.getString(2) + "\",";
               data += "\n\t\t\"isbn\": \" " + rs.getInt(3) + "\",";
               data += "\n\t\t\"copy\": \" " + rs.getInt(4) + "\"," ;
               data += "\n\t\t\"issues\": \" " + rs.getInt(5) + "\"" + "\n\t },";
            }
            data += " \n }}";

            //create file and write to file
            String stamp = new Timestamp((new Date()).getTime()).toString();
            stamp = stamp.replace(' ','_');
            stamp = stamp.replace('-','_');
            stamp = stamp.replace(':','_');
            stamp = stamp.replace('.','_');
            PrintWriter app = new PrintWriter(new BufferedWriter(new FileWriter(stamp + "_req1.json",true)));
            app.print(data);
            app.close();

        } catch (Exception e) {
            logger("Error: " + e.getMessage());
        } finally {
            //close resources
            try {
                if (rs != null)
                    rs.close();
                if (pst	!= null)
                    pst.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                logger("Error: " + e.getMessage());
            }

        }
    }
    public static void main(String[] args) {
        // the request is passed as a  program argument
        // since this is a mock up program

        assert (args.length == 1);
        String rgx_req1 = "^R=1";
        String rgx_req2or3 = "^R=[0-4]&(Cust_ID|Book_ID)=A[0-9]{6}";


        //check format

        // request 1
        if(args[0].matches(rgx_req1)) {
               process_req1();
        }
        // request 2 or 3
        if(args[0].matches(rgx_req2or3)) {
            ;
            //TODO
        }
    }
}
