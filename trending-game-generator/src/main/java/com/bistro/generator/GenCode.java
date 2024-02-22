package com.bistro.generator;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.StringWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenCode {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://47.108.189.93:8014/bistrodev?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "bistro8888";

    private static final String SQL = "SELECT * FROM ";// 数据库操作

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接
     *
     * @return
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
          e.printStackTrace();
        }
        return conn;
    }

    /**
     * 关闭数据库连接
     * @param conn
     */
    public static void closeConnection(Connection conn) {
        if(conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取数据库下的所有表名
     */
    public static List<String> getTableNames(String tableName) {
        List<String> tableNames = new ArrayList<>();
        Connection conn = getConnection();
        ResultSet rs = null;
        try {
            //获取数据库的元数据
            DatabaseMetaData db = conn.getMetaData();
            //从元数据中获取到所有的表名
            rs = db.getTables(null, null, tableName, new String[] { "TABLE" });
            while(rs.next()) {
                tableNames.add(rs.getString(3));
            }
        } catch (SQLException e) {
           e.printStackTrace();
        } finally {
            try {
                rs.close();
                closeConnection(conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return tableNames;
    }


    /**
     * 获取表中字段的所有注释
     * @param tableName
     * @return
     */
      public static List<TableColumnSchema> getColumns(String tableName,boolean sql) {
        List<TableColumnSchema> columnNames = new ArrayList<>();
        //与数据库的连接
        Connection conn = getConnection();
        PreparedStatement pStemt = null;
        String tableSql = SQL + tableName;
        List<String> columnComments = new ArrayList<>();//列名注释集合
        ResultSet rs = null;
        try {
            if(sql){
                pStemt = conn.prepareStatement(tableName);
                rs= pStemt.executeQuery();
                ResultSetMetaData metaData = rs.getMetaData();
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    TableColumnSchema tableColumnSchema=new TableColumnSchema();
                    int idx=i+1;
                    String name=metaData.getColumnName(idx);
                    tableColumnSchema.setName(name);
                    tableColumnSchema.setType( metaData.getColumnTypeName(idx));
                    columnNames.add(tableColumnSchema);
                }

            }else{
                pStemt = conn.prepareStatement(tableSql);
                rs = pStemt.executeQuery("show full columns from " + tableName);
                while (rs.next()) {
                    TableColumnSchema tableColumnSchema=new TableColumnSchema();
                    tableColumnSchema.setComment(rs.getString("Comment"));
                    tableColumnSchema.setName(rs.getString("Field"));
                    tableColumnSchema.setType(rs.getString("Type"));
                    tableColumnSchema.updatePk(rs.getString("Key"));
                    columnNames.add(tableColumnSchema);
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                    closeConnection(conn);
                } catch (SQLException e) {
                }
            }
        }
        return columnNames;
    }

    public static String toCamelCase(String underlineStr) {
        if (underlineStr == null) {
            return null;
        }
        // 分成数组
        char[] charArray = underlineStr.toCharArray();
        // 判断上次循环的字符是否是"_"
        boolean underlineBefore = false;
        StringBuffer buffer = new StringBuffer();
        for (int i = 0, l = charArray.length; i < l; i++) {
            // 判断当前字符是否是"_",如果跳出本次循环
            if (charArray[i] == 95) {
                underlineBefore = true;
            } else if (underlineBefore) {
                // 如果为true，代表上次的字符是"_",当前字符需要转成大写
                buffer.append(charArray[i] -= 32);
                underlineBefore = false;
            } else {
                // 不是"_"后的字符就直接追加
                buffer.append(charArray[i]);
            }
        }
        return buffer.toString();
    }
    public static String toPascal(String s){
       String name= toCamelCase(s);

       return  Character.toUpperCase(name.charAt(0))+name.substring(1);
    }

    public static void main(String[] args) {
      //  genTanTableSql("game_base_info");
        genPOJO("select \n" +
                "g.`bet_id`,\n" +
                "gbase.`code` as game_code,\n" +
                "gbase.`name` as game_name,\n" +
                "g.uid,\n" +
                "gu.`name` as user_name,\n" +
                "m.`merchant_code`,\n" +
                "gb.bet_amount,\n" +
                "gb.multiplier,\n" +
                "gb.payout_amount,\n" +
                "gb.currency,\n" +
                "gb.status,\n" +
                "g.game_content\n" +
                "\n" +
                " from  game_bet gb \n" +
                " join game_user_info gu on gb.uid=gu.id\n" +
                "\n" +
                " join `merchant_info` m on gu.`from_code`=m.`merchant_code`\n" +
                " \n" +
                " left join  `game_record_info` g   on g.bet_id=gb.id\n" +
                " left join  `game_base_info` gbase on g.`game_code` =gbase.`code`");
//        ttttt();
    }


    private static void  ttttt(){
        String s="[QUOTE_NO]\n" +
                "      ,[QUOTE_REL_NO]\n" +
                "      ,[ENTITY_CODE]\n" +
                "      ,[COMPANY]\n" +
                "      ,[OPERATION_ID]\n" +
                "      ,[OPERATION_NO]\n" +
                "      ,[SUB_ITEM]\n" +
                "      ,[OPEATION_DESCRIPTION]\n" +
                "      ,[WORK_CENTER_NO]\n" +
                "      ,[MACH_SETUP_HRS]\n" +
                "      ,[MACH_RUN_FACTOR]\n" +
                "      ,[SETUP_LABOR_CLASS]\n" +
                "      ,[LABOR_SETUP_HRS]\n" +
                "      ,[SETUP_CREW_SIZE]\n" +
                "      ,[LABOR_CLASS]\n" +
                "      ,[LABOR_RUN_FACTOR]\n" +
                "      ,[CREW_SIZE]\n" +
                "      ,[MOVE_HRS]\n" +
                "      ,[FACTOR_UNIT]\n" +
                "      ,[PARALLET_OPERATION]\n" +
                "      ,[OVERLAP]\n" +
                "      ,[OVERLAP_UNIT]\n" +
                "      ,[EFFICIENCY_FACTOR]\n" +
                "      ,[OUTSIDE_OP_SUPPLY_ITEM]\n" +
                "      ,[OUTSIDE_OP_SUPPLY_ITEM_COST]\n" +
                "      ,[OUTSIDE_OP_SUPPLY_TYPE]\n" +
                "      ,[NOTE]\n" +
                "      ,[VALIDATE_MATERAIL]\n" +
                "      ,[SF_SPLIT]";
        List<Map> cols=new ArrayList<>();
        for (String line : s.split("\n")) {
            String tmp= StringUtils.strip(line.trim(),",");
            tmp= StringUtils.strip(tmp,"[");
            tmp=StringUtils.stripEnd(tmp,"]");
            Map map=new HashMap();
            map.put("field",tmp);

            map.put("title",   toPascal(toCamelCase(tmp.toLowerCase())));
            cols.add(map);
        }


        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

        ve.init();

        Template t = ve.getTemplate("/vm/sql/gen_table_column.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("cols",cols);

        StringWriter sw = new StringWriter();

        t.merge(ctx, sw);

        System.out.println(sw);
    }

    private static void genTanTableSql(String table){
        List<TableColumnSchema> columns = getColumns(table,false);
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

        ve.init();

        Template t = ve.getTemplate("/vm/sql/gen_table_sql.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("table",table);
        ctx.put("className",toPascal(table));
        ctx.put("cols", columns);

        StringWriter sw = new StringWriter();

        t.merge(ctx, sw);

        System.out.println(sw);
    }

    private static void genPOJO(String sql){
        List<TableColumnSchema> columns = getColumns(sql,true);
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

        ve.init();

        Template t = ve.getTemplate("/vm/sql/pojo.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("cols", columns);

        StringWriter sw = new StringWriter();

        t.merge(ctx, sw);

        System.out.println("--------------------");
        System.out.println(sw);
        System.out.println("--------------------");
    }

    public static class TableColumnSchema{
        private String name;
        private String type;
        private String comment;
        private int pk;

        public int getPk() {
            return pk;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            int idx=type.indexOf('(');
            if(idx>0){
                this.type=type.substring(0,idx);
            }else{
                this.type = type;
            }

        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
        public void updatePk(String key) {
            pk = "PRI".equalsIgnoreCase(key) ? 1 : 0;
        }

        public String getJavaName(){
            return toCamelCase(name);
        }
        public String getJavaType(){
            if(type.equalsIgnoreCase("bigint")){
                return "Long";
            }
            if(type.equalsIgnoreCase("int")){
                return "Integer";
            }
            if(type.equalsIgnoreCase("varchar")){
                return "String";
            }
            if(type.equalsIgnoreCase("tinyint")){
                return "Short";
            }
            if(type.equalsIgnoreCase("smallint")){
                return "Integer";
            }
            if(type.equalsIgnoreCase("timestamp")){
                return "LocalDateTime";
            }
            if(type.equalsIgnoreCase("datetime")){
                return "LocalDateTime";
            }
            return "String";
        }
        @Override
        public String toString() {
            return "TableColumnSchema{" +
                    "name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", comment='" + comment + '\'' +
                    ", isPk=" + pk +
                    '}';
        }
    }
}
