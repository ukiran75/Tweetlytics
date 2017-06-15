package storm;


import com.mysql.cj.jdbc.*;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import java.sql.*;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by uday on 5/1/2017.
 */
public class DBBolt extends BaseRichBolt {

    static Connection connection = null;
    transient PreparedStatement preparedStatement = null;
    String insertQueryStatement = "INSERT  INTO  tweet  VALUES  (?,?,?)";


    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://analytics.cvebv7iblwmp.us-west-1.rds.amazonaws.com:3306/tweets","uday","udayisbad");
            preparedStatement = connection.prepareStatement(insertQueryStatement);
        } catch (SQLException e) {
            e.printStackTrace();

            return;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void execute(Tuple tuple) {
        //System.out.println(tuple.getString(0));
        String hashtag = (String)tuple.getValueByField("hashtag");
        Double longitude = (Double)tuple.getValueByField("longitude");
        Double latitude = (Double)tuple.getValueByField("latitude");
        try {
            preparedStatement.setString(1,hashtag);
            preparedStatement.setDouble(2,longitude);
            preparedStatement.setDouble(3,latitude);
            preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {


    }
}
