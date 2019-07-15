package com.haibian.material.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;

import static com.haibian.material.tools.Log4JUtil.logger;

public class FileUtil {
    public static void delete(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    public static String save(String fileUrl) throws IOException {
        URI uri = null;
        String tempFileUrl = fileUrl.replaceAll(" |\\[|\\]|#|、", "");
        String newFileUrl = "";
        try {
            uri = new URI(tempFileUrl);
            String host = uri.getHost();
            String scheme = uri.getScheme();
            String rightUrl = fileUrl.substring(fileUrl.indexOf(host) + host.length() + 1);
            newFileUrl = scheme + "://" + host + "/" + URLEncoder.encode(rightUrl, "utf-8");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        logger.info("newFileUrl:" + newFileUrl);
        newFileUrl = newFileUrl.replaceAll("\\+", "%20");
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
        File file = new File("/tmp/save_" + nano + "." + getExtendName(tempFileUrl));
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

    public static String getExtendName(String fileName) {
        String extendName = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        System.out.println("扩展名:" + extendName);
        return extendName;
    }
}
