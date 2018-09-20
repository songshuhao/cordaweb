package ats.blockchain.web.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class JdbcTest{
    public static void main(String[] args) {
        String driver = "com.mysql.jdbc.Driver";    //驱动标识符
        String url = "jdbc:mysql://127.0.0.1:3306/mysql"; //链接字符串
        // url ="jdbc:oracle:thin:@10.0.30.64:1521:orcl";  // 连接远程的数据库可以这么写
        String user = "root";         //数据库的用户名
        String password = "123456";     //数据库的密码
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        boolean flag = false;

        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url,user, password);
            String sql = "SELECT * FROM basketinfo";
            pstm =con.prepareStatement(sql);
            rs = pstm.executeQuery();
            while(rs.next()) {
            	String empno = rs.getString("basket_no");
                String ename =rs.getString("product_code");
                String sal = rs.getString("Supplier_Code");
                String hiredate =rs.getString("Supplier_name");
                int deptno = rs.getInt(("diamonds_number"));
                System.out.println(empno +"\t"+ ename +"\t"+ sal +"\t"+ hiredate +"\t"+ deptno);
            }

            flag = true;
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            // 关闭执行通道
            if(pstm !=null) {
                try {
                    pstm.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            // 关闭连接通道
            try {
                if(con != null &&(!con.isClosed())) {
                    try {
                        con.close();
                    } catch (SQLException e) {
                       e.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(flag) {
            System.out.println("执行成功！");
        } else {
            System.out.println("执行失败！");
        }
    }
}