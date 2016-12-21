package org.apache.thrift;

/**
 * Created by absir on 2016/12/21.
 */
public class ThriftVisitor {

    public static boolean isOneWay(ProcessFunction processFunction) {
        return processFunction.isOneway();
    }

}
