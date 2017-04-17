package com.alienlab.db.table;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.common.TypeUtils;
import com.alienlab.db.DAO;
import com.alienlab.db.DbPoolConnection;

public class DataTable {
    private static Logger logger = Logger.getLogger(DataTable.class);
    private String tableName = "";
    private String[] tableHeader = null;
    private List<String[]> tableRows = null;

    public List<String[]> getTableRows() {
        return tableRows;
    }

    public void setTableRows(List<String[]> tableRows) {
        this.tableRows = tableRows;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String[] getTableHeader() {
        return tableHeader;
    }

    public void setTableHeader(String[] tableHeader) {
        this.tableHeader = tableHeader;
    }

    public static DataTable getDataTable(String sql, String tablename) {
        ResultSet resSet = null;
        DruidPooledConnection conn = null;
        Statement stmt = null;
        DataTable dt = new DataTable();
        try {
            conn = DbPoolConnection.getInstance().getConnection();
            if (!TypeUtils.isEmpty(sql) && !TypeUtils.isEmpty(conn)) {
                stmt = conn.createStatement();
                resSet = stmt.executeQuery(sql);
                ResultSetMetaData rsmd = resSet.getMetaData();
                int columnCount = rsmd.getColumnCount();
                String[] header = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    header[i - 1] = rsmd.getColumnLabel(i).toUpperCase();
                }
                dt.setTableHeader(header);
                List<String[]> rows = new ArrayList<String[]>();
                while (resSet.next()) {
                    String[] row = new String[header.length];
                    for (int i = 0; i < header.length; i++) {
                        row[i] = TypeUtils.getString(resSet.getObject(header[i]));
                    }
                    rows.add(row);
                }
                dt.setTableRows(rows);
                dt.setTableName(tablename);
                return dt;
            } else {
                logger.info("SQL不能为空");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return null;
        } finally {
            DAO.closeResultSet(resSet);
            DAO.closeStatement(stmt);
            DAO.closeConnection(conn);
        }
    }

    public static void main(String[] args) {
        DataTable dt = DataTable.getDataTable("select * from tb_op_info", "tb_op_info");
        String json = JSONObject.toJSONString(dt);
        System.out.println(json);
    }

}
