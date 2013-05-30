using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using MySql.Data.MySqlClient;

namespace json_request
{
    class LibInterface
    {
        static void Main(string[] args)
        {
            if (args.Length < 1)
                throw new Exception("No arguments provided");
            if (args[0] != "RQ=1")
                throw new Exception("Invalid Request");
            String conn_str = @"server=localhost;database=lib_db;userid=lib_req;password=req;";
            MySqlConnection con = null;
            MySqlDataReader reader = null;
            MySqlCommand cmd = null;
            String CurrentDate = DateTime.Now.ToString("yyyy-MM-dd-HH-mm-ss");
            System.IO.StreamWriter json_file = null,log = null ;
            String json_data = "{ \"frequent_list\": {";
            try
            {
                con = new MySqlConnection(conn_str);
                con.Open();
                cmd = new MySqlCommand("SELECT * FROM `book_details` order by `num_issues` desc limit 5;",con);
                reader = cmd.ExecuteReader();
                while (reader.Read())
                {
                    json_data += "\n\t\"book\":{ \n\t\t\"id\": \" " + reader.GetString(0) + "\",";
                    json_data += "\n\t\t\"name\": \" " + reader.GetString(1) + "\",";
                    json_data += "\n\t\t\"isbn\": \" " + reader.GetString(2) + "\",";
                    json_data += "\n\t\t\"copy\": \" " + reader.GetString(3) + "\",";
                    json_data += "\n\t\t\"issues\": \" " + reader.GetString(4) + "\"" + "\n\t },";
                }
                json_data += " \n }}";
                json_file = new System.IO.StreamWriter(CurrentDate.ToString().Replace('-', '_').Replace(':', '_').Replace(' ', '_') + "req1.json");
                json_file.Write(json_data);
            }
            catch (Exception e)
            {
                try
                {
                    log = new System.IO.StreamWriter("log.txt", true);
                    log.WriteLine("[" + CurrentDate + "] " + "[" + e.Message + "]");
                }
                catch (Exception ex)
                {
                    Console.WriteLine("Error logging to file!!");
                    
                }
                finally
                {
                    if (log != null)
                        log.Close();
                }

            }
            finally
            {
                if (reader != null)
                    reader.Close();
                if (con != null)
                    con.Close();
                if (json_file != null)
                    json_file.Close();
            }
        }
    }
}
