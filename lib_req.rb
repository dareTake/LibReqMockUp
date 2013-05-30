require 'mysql2'

class JsonRequest1

	def initialize(req_str)
		match = (req_str =~ /^R=1$/)
		if match.nil? 
			raise "Wrong request"
		end 
		@data = "{ \"frequent_list\": {"
	end
	def process_req()
		conn = Mysql2::Client.new(
			:host => "localhost",
			:username => "lib_req",
			:password => "req",
			:database => "lib_db")
		conn.query("SELECT * FROM `book_details` order by `num_issues` desc limit 5;").each do |row|
			 @data += "\n\t\"book\":{ \n\t\t\"id\": \" " + row["Book_ID"] + "\",";
             @data += "\n\t\t\"name\": \" " + row["Name"] + "\",";
             @data += "\n\t\t\"isbn\": \" " + row["ISBN"].to_s + "\",";
             @data += "\n\t\t\"copy\": \" " + row["Copy_ID"].to_s + "\"," ;
             @data += "\n\t\t\"issues\": \" " + row["Num_Issues"].to_s  + "\"" + "\n\t },";
		end
		@data += " \n }}";
		@data
	end
end

begin
	jreq = JsonRequest1.new("R=1")
	json_data = jreq.process_req
	stamp = Time.now.to_s
	stamp.gsub!(':','_')
	stamp.gsub!('-','_')
	stamp.gsub!(' ','_')
	json_file = File.new("#{stamp}_req1.json", "w") 
	json_file.write(json_data)
rescue Exception =>e
	begin
		log = File.open("log.txt", "a")
		log.puts("[" + Time.now.to_s + "]" + "[" + e.message + "]") 
	rescue Exception => ex
		puts ex.message
	ensure
		log.close unless log == nil
	end
	 e.message
ensure
	json_file.close unless json_file == nil
end