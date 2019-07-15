import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.haibian.material.service.AsposeService;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class PoiTest {
    @Test
    public void testPdf() throws Exception {
        File file = new File("file/pdf/资料站整体优化.pdf");
        AsposeService service = new AsposeService();
        String result = service.handle("pdf", new FileInputStream(file));
        System.out.println(result);
    }

    @Test
    public void testUploadQiNiu() throws Exception {
        File file = new File("file/pdf/资料站整体优化.pdf");
        AsposeService service = new AsposeService();
        String result = service.handle("pdf", new FileInputStream(file));
        JsonArray jsonArray = (JsonArray) new JsonParser().parse(result);
        for (JsonElement element : jsonArray) {
            String url = element.getAsJsonObject().get("url").toString();
            Assert.assertTrue(url.startsWith("\"http"));
        }
    }


    @Test
    public void testDoc() throws Exception {
        File file = new File("file/doc/2018年5月福州质检英语听力材料.docx");
        AsposeService service = new AsposeService();
        List<String> fileList = service.word2Image(new FileInputStream(file));
        for (String filePath : fileList) {
            String type = getFileHeader(filePath);
            Assert.assertEquals("ffd8ffe0", type);
//            FileUtil.delete(filePath);
        }

        // 仅一页的文件
    }

    public static String getFileHeader(String filePath) {
        FileInputStream is = null;
        String value = null;
        try {
            is = new FileInputStream(filePath);
            byte[] b = new byte[4];
            is.read(b, 0, b.length);
            value = bytesToHexString(b);
        } catch (Exception e) {

        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return value;
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
