package com.hzgc.hbase.staticrepo;

import com.hzgc.dubbo.staticrepo.ObjectInfoHandler;
import com.hzgc.dubbo.staticrepo.ObjectSearchResult;
import com.hzgc.hbase.util.HBaseHelper;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class ObjectInfoHandlerImpl implements ObjectInfoHandler {
    private static Logger logger = Logger.getLogger(String.valueOf(ObjectInfoHandlerImpl.class));
    @Override
    public byte addObjectInfo(String platformId, Map<String, Object> person) {
        Set<String> fieldset = person.keySet();
        List<String> fieldlist = new ArrayList<String>();
        Iterator<String> it = fieldset.iterator();
        while (it.hasNext()){
            fieldlist.add(it.next());
        }
        String rowkey = UUID.randomUUID().toString().replace("-","");
        // 获取table 对象，通过封装HBaseHelper 来获取
        HBaseHelper helper = new HBaseHelper();
        Table objectinfo = helper.getTable("objectinfo");
        //构造Put 对象
        Put put = new Put(Bytes.toBytes(rowkey));
        // 添加列族属性
        for (String field:fieldlist){
            if ("photo".equals(field)){
                put.addColumn(Bytes.toBytes("person"), Bytes.toBytes(field),
                        (byte[])person.get(field));
            } else {
                put.addColumn(Bytes.toBytes("person"), Bytes.toBytes(field),
                        Bytes.toBytes((String) person.get(field)));
            }
        }
        // 给表格添加两个时间的字段，一个是创建时间，一个是更新时间
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = format.format(date);
        put.addColumn(Bytes.toBytes("person"), Bytes.toBytes("createtime"), Bytes.toBytes(dateString));
        put.addColumn(Bytes.toBytes("person"), Bytes.toBytes("updatetime"), Bytes.toBytes(dateString));
        put.addColumn(Bytes.toBytes("person"), Bytes.toBytes("platformId"), Bytes.toBytes(platformId));
        // 执行Put 操作，往表格里面添加一行数据
        try {
            objectinfo.put(put);
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        } finally {
            // 关闭表格和连接对象。
            try {
                objectinfo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int deleteObjectInfo(List<String> rowkeys) {
        // 获取table 对象，通过封装HBaseHelper 来获取
        HBaseHelper helper = new HBaseHelper();
        Table table = helper.getTable("objectinfo");
        List<Delete> deletes = new ArrayList<Delete>();
        Delete delete = null;
        for (String rowkey: rowkeys){
            delete = new Delete(Bytes.toBytes(rowkey));
            deletes.add(delete);
        }
        // 执行删除操作
        try {
            table.delete(deletes);
            return  0;
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        } finally {
            try {
                table.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int updateObjectInfo(Map<String, Object> person)  {
        // 获取table 对象，通过封装HBaseHelper 来获取
        HBaseHelper helper = new HBaseHelper();
        Table table = helper.getTable("objectinfo");
        String id = (String) person.get("id");
        Set<String> fieldset = person.keySet();
        Iterator<String> it = fieldset.iterator();
        List<String> fieldlist = new ArrayList<String>();
        while (it.hasNext()) {
            fieldlist.add(it.next());
        }
        Put put = new Put(Bytes.toBytes(id));
        for (String field : fieldlist) {
            if ("photo".equals(field)){
                put.addColumn(Bytes.toBytes("person"), Bytes.toBytes(field),
                        (byte[])person.get(field));
            } else {
                put.addColumn(Bytes.toBytes("person"), Bytes.toBytes(field),
                        Bytes.toBytes((String) person.get(field)));
            }
        }
        try {
            table.put(put);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                table.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public ObjectSearchResult getObjectInfo(String platformId, String name, String idCard,
                                            String rowkey, byte[] image,
                                            int threshold, List<String> pkeys,
                                            String creator, String cphone,
                                            long start, long pageSize,
                                            int serachId, String serachType,
                                            boolean moHuSearch) {
        return null;
    }

    @Override
    public ObjectSearchResult searchByPlatFormIdAndIdCard(String platformId, String IdCard,
                                                          boolean moHuSearch, long start, long pageSize) {
        return null;
    }

    @Override
    public ObjectSearchResult searchByRowkey(String rowkey) {
        return null;
    }

    @Override
    public ObjectSearchResult serachByCphone(String cphone) {
        return null;
    }

    @Override
    public ObjectSearchResult searchByCreator(String creator, boolean moHuSearch,
                                              long start, long pageSize) {
        return null;
    }

    @Override
    public ObjectSearchResult searchByName(String name, boolean moHuSearch,
                                           long start, long pageSize) {
        return null;
    }

    @Override
    public ObjectSearchResult serachByPhotoAndThreshold(String platformId, byte[] photo,
                                                        int threshold, String feature,
                                                        long start, long pageSize) {
        return null;
    }

    @Override
    public String getFeature(String tag, byte[] photo) {
        return null;
    }

    @Override
    public byte[] getPhotoByKey(String rowkey) {
        HBaseHelper helper = new HBaseHelper();
        Table table = helper.getTable("objectinfo");
        Scan scan = new Scan(Bytes.toBytes(rowkey));
        ResultScanner rs = null;
        try {
            rs = table.getScanner(scan);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Iterator<Result> it = rs.iterator();
        byte[] photo = null;
        while (it.hasNext()){
            Result r = it.next();
            photo = r.getValue(Bytes.toBytes("person"),Bytes.toBytes("photo"));
        }
        return photo;
    }
}
