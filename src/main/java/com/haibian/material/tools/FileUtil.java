package com.haibian.material.tools;

import java.io.*;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.haibian.material.tools.Log4JUtil.logger;

public class FileUtil {
    private static String chinesePattern = "[\\u4e00-\\u9fa5]";

    public static void delete(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    public static String save(String fileUrl) throws IOException {
        /**
         * 此处有个巨坑，下载地址上带着中文，会出现404
         * 只有进行部分转码 ，不得全部转码
         */
        URI uri = null;
        try {
            uri = new URI(fileUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        String host = uri.getHost();
        String rightUrl = fileUrl.substring(fileUrl.indexOf(host) + host.length()+1);
        logger.info("rightUrl:" + rightUrl);
        String newFileUrl = "http://" + host +"/"+ URLEncoder.encode(rightUrl, "utf-8");
        URL url = new URL(newFileUrl);
        logger.info("file url:" + newFileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        connection.setConnectTimeout(3 * 1000);

        logger.info("response code:" + connection.getResponseCode());
        if (connection.getResponseCode() != 200) {
            return "";
        }

        InputStream inputStream = connection.getInputStream();
        //java 1.9 可以用以下方法
        long nano = System.currentTimeMillis();
        File file = new File("/tmp/save_" + nano + "." + getExtendName(fileUrl));
        FileOutputStream fos = new FileOutputStream(file);
        int length = 0;
        byte buffer[] = new byte[1024];
        while ((length = inputStream.read(buffer, 0, buffer.length)) != -1) {
            fos.write(buffer, 0, length);
        }
        fos.close();
        inputStream.close();
        connection.disconnect();
        return file.getAbsolutePath();
    }


    public static String encode(String str, String charset) throws UnsupportedEncodingException {
        Pattern p = Pattern.compile(chinesePattern);
        Matcher m = p.matcher(str);
        StringBuffer b = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(b, URLEncoder.encode(m.group(0), charset));
        }
        m.appendTail(b);
        return b.toString();
    }


    public static String getExtendName(String fileName) {
        String extendName = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return extendName;
    }
}
