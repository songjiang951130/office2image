import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.haibian.material.service.AsposeService;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

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
}
