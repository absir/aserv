/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-5-27 下午7:26:55
 */
package com.absir.aserv.jdbc;

import com.absir.core.kernel.KernelString;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author absir
 */
public class JdbcUtils {

    /**
     * JDBC_DRIVER_CLASS_PREFIX
     */
    public static final String JDBC_DRIVER_CLASS_PREFIX = JdbcDriver.class.getPackage().getName() + ".driver.";

    /**
     * Driver
     */
    private static JdbcDriver Driver = null;

    /**
     * Product_Version_Map_JdbcDriver
     */
    private static Map<String, JdbcDriver> Product_Version_Map_JdbcDriver = new HashMap<String, JdbcDriver>();

    /**
     * @return
     */
    public static JdbcDriver Driver() {
        if (Driver == null) {
            Driver = new JdbcDriver();
        }

        return Driver;
    }

    /**
     * @param connection
     * @return
     * @throws SQLException
     */
    public static JdbcDriver forProduct(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        return JdbcUtils.forProduct(KernelString.capitalize(metaData.getDatabaseProductName().toLowerCase()), metaData.getDatabaseProductVersion().toLowerCase());
    }

    /**
     * @param productName
     * @param version
     * @return
     */
    public static JdbcDriver forProduct(String productName, String version) {
        String productVersion = productName;
        int length = version.length();
        if (length > 0) {
            productVersion += "_" + version;
        }

        JdbcDriver jdbcDriver = Product_Version_Map_JdbcDriver.get(productVersion);
        if (jdbcDriver != null) {
            return jdbcDriver;
        }

        try {
            jdbcDriver = (JdbcDriver) Class.forName(JDBC_DRIVER_CLASS_PREFIX + productVersion + "_driver").newInstance();

        } catch (ClassNotFoundException e) {
            if (productName != null) {
                if (productVersion != productName) {
                    jdbcDriver = forProduct(productName, version.substring(0, length - 1));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (jdbcDriver == null) {
            throw new RuntimeException("JdbcDriver for " + productName + ":" + version + " not found!");

        } else {
            Product_Version_Map_JdbcDriver.put(productVersion, jdbcDriver);
        }

        return jdbcDriver;
    }
}
