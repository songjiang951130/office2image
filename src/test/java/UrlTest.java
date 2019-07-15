import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.*;

import static com.haibian.material.tools.Log4JUtil.logger;

public class UrlTest {
    @Test
    public void testBlank() throws IOException {
        String fileUrl = "http://playback.haibian.com/upload_tc/5YyX5Lqs5rW35reA5Yy65biI6L6-5bCP5a2m5bCP5Y2H5Yid5rWL6K-V55yf6aKY77yI5LiA77yJIOino-aekOeJiA==1551422436764/北京海淀区师达小学小升初测试真题（一） 解析版.pdf";
//        String fileUrl = "http://playback.haibian.com/upload_tc/56ysMeWRqCBb54mp55CGXeasp-WnhuWumuW-i-S4juKAnOS8j-WuieazleKAneWunumqjA==1554253373586/第1周 [物理]欧姆定律与“伏安法”实验.pdf";
//        String fileUrl = "http://playback.haibian.com/upload_tc/wqAyMDE55bm05Lit5bGx5biC6auY5Lit6Zi25q615a2m5qCh6ICD6K-V5oub55Sf5pS_562W5oCn54Wn6aG-4oCc5pys5biC5oi357GN55Sf5b6F6YGH4oCd5a6h5qC46YCa6L-H5ZCN5Y2V1559038151877/ 2019年中山市高中阶段学校考试招生政策性照顾“本市户籍生待遇”审核通过名单.pdf";
        URI uri = null;
        String tempFileUrl = fileUrl.replaceAll(" |\\[|\\]", "");
//        System.out.println(fileUrl.charAt(197)+tempFileUrl);
        try {
            uri = new URI(tempFileUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        assert uri != null;
        String host = uri.getHost();
        String scheme = uri.getScheme();

        String rightUrl = fileUrl.substring(fileUrl.indexOf(host) + host.length() + 1);
        String newFileUrl = scheme + "://" + host + "/" + URLEncoder.encode(rightUrl, "utf-8");
        logger.info("newFileUrl:" + newFileUrl);
        newFileUrl = newFileUrl.replaceAll("\\+", "%20");
        URL url = new URL(newFileUrl);
        logger.info("file url:" + newFileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        connection.setConnectTimeout(3 * 1000);
        Assert.assertEquals(200, connection.getResponseCode());
    }

}
