package com.haibian.material.tools;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.haibian.material.tools.Log4JUtil.logger;


public class QiNiuUtil {


    /**指定保存到七牛的文件名--同名上传会报错  {"error":"file exists"}*/
    /**
     * {"hash":"FrQF5eX_kNsNKwgGNeJ4TbBA0Xzr","key":"aa1.jpg"} 正常返回 key为七牛空间地址 http:/xxxx.com/aa1.jpg
     */
    private String domain;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    public UploadManager uploadManager;

    public QiNiuUtil() {
        Properties properties = new Properties();
        InputStream in = QiNiuUtil.class.getClassLoader().getResourceAsStream("qiniu.properties");
        try {
            properties.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        domain = properties.getProperty("DOMAIN");
        accessKey = properties.getProperty("ACCESS_KEY");
        secretKey = properties.getProperty("SECRET_KEY");
        bucketName = properties.getProperty("BUCKET_NAME");
        Configuration configuration = new Configuration();
        configuration.connectTimeout = 2;
        configuration.writeTimeout = 10;
        configuration.readTimeout = 10;
        uploadManager = new UploadManager(configuration);
    }


    public String getUpToken() {
        Auth auth = Auth.create(accessKey, secretKey);
        return auth.uploadToken(bucketName);
    }

    public String upload(String fromFilePath, String toFilePath) {
        Gson gson = new Gson();
        Map<String, String> map = new HashMap<>();
        try {
            //调用put方法上传
            logger.info("from path:" + fromFilePath + " toFilePath:" + toFilePath);
            Response res = uploadManager.put(fromFilePath, toFilePath, getUpToken());
            logger.info("上传结束" + fromFilePath + " toFilePath:" + toFilePath);
            // 删除本地临时目录
            FileUtil.delete(fromFilePath);
            if (res.statusCode == 200) {
                //{"hash":"FsFL4yz4bUyYkDTHUbFB5KYEUiqE","key":"example.php"}
                String json = res.bodyString();
                logger.info("七牛云结果：" + json);
                JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
                String val = domain + "/" + jsonObject.get("key").getAsString();
                map.put("url", val);
                jsonObject.remove("key");
                jsonObject.remove("hash");
            }
        } catch (QiniuException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
        return gson.toJson(map);
    }

}
