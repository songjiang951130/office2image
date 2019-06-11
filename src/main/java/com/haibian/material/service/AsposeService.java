package com.haibian.material.service;

import com.aspose.cells.*;
import com.aspose.words.Document;
import com.aspose.words.FontSettings;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.haibian.material.service.thread.WordRunnable;
import com.haibian.material.tools.Log4JUtil;
import com.haibian.material.tools.QiNiuUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class AsposeService {

    private Map<String, Integer> typeMapping;
    public static final int PREVIEW_COUNT = 3;

    public AsposeService() {
        this.typeMapping = new HashMap<>();
        this.setTypeMapping();

    }

    private void setTypeMapping() {
        //0
        typeMapping.put("ppt", com.aspose.slides.SaveFormat.Ppt);
        //6
        typeMapping.put("xlsx", com.aspose.cells.SaveFormat.XLSX);
        typeMapping.put("xls", com.aspose.cells.SaveFormat.XLSX);
        //10
        typeMapping.put("doc", 2);
        //14
        typeMapping.put("pptx", 14);
        //20
        typeMapping.put("docx", 20);
        //40
        typeMapping.put("pdf", 40);
    }

    public int getType(String extendName) {
        return null == this.typeMapping.get(extendName) ? -1 : this.typeMapping.get(extendName);
    }

    public String handle(String extendName, InputStream inputStream) throws Exception {
        if (-1 == this.getType(extendName)) {
            return "";
        }
        getLicense();

        switch (extendName) {
            case "xls":
            case "xlsx":
                return excelImage(inputStream);
            case "pdf":
                return this.pdfImage(inputStream);
            case "doc":
            case "docx":
                return this.wordImage(inputStream);
            case "ppt":
                return this.pptImage(inputStream);
            case "pptx":
                return this.pptxImage(inputStream);
        }
        return "file type is not support";
    }

    private String upload(String path) {
        long start = System.currentTimeMillis();
        QiNiuUtil util = new QiNiuUtil();
        String qiNiuPath = "material_preview" + path;
        String result = util.upload(path, qiNiuPath);
        Log4JUtil.logger.info("qiniu upload time:" + (System.currentTimeMillis() - start));
        return result;
    }

    //上传预览图
    private String upload(List<String> pathList) {
        JsonArray jsonArray = new JsonArray();
        for (String tmpPath : pathList) {
            String result = this.upload(tmpPath);
            JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
            jsonArray.add(jsonObject);
        }
        return jsonArray.toString();
    }

    private String wordImage(InputStream inputStream) throws Exception {
        Document document = new Document(inputStream);
        //设置字体防止中文乱码
        FontSettings fontSettings = new FontSettings();
        if (!System.getProperty("os.name").equals("Mac OS X")) {
            fontSettings.setFontsFolder("/home/work/local/share/fonts", true);
        }
        document.setFontSettings(fontSettings);
        com.aspose.words.ImageSaveOptions options = new com.aspose.words.ImageSaveOptions(com.aspose.words.SaveFormat.JPEG);
        options.setJpegQuality(80);
        //处理3页一下的预览图
        int cap = Integer.min(document.getPageCount(), AsposeService.PREVIEW_COUNT);
        List<String> list = new ArrayList<>();
        for (int index = 0; index < cap; index++) {
            System.out.println("word index:"+index);
            long nanoTime = System.nanoTime();
            String tmpPath = "/tmp/word_" + nanoTime + ".jpeg";
            document.save(tmpPath, options);
            list.add(tmpPath);
        }
        return this.upload(list);
    }

    private String wordImageLine(InputStream inputStream){
        return "ss";
    }


    private String pdfImage(InputStream inputStream) throws IOException {
        return pdf2Image(inputStream);
    }

    /**
     * 替换为开源实现 pdfbox
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    private String pdf2Image(InputStream inputStream) throws IOException {
        JsonArray jsonArray = new JsonArray();
        PDDocument doc = PDDocument.load(inputStream);
        PDFRenderer renderer = new PDFRenderer(doc);
        int pageCount = doc.getNumberOfPages();
        for (int index = 0; index < pageCount && index < PoiService.PREVIEW_COUNT; index++) {
            BufferedImage image = renderer.renderImageWithDPI(index, 144);
            long nanoTime = System.currentTimeMillis();
            String tmpPath = "/tmp/pdf_" + nanoTime + ".jpeg";
            OutputStream imageStream = new FileOutputStream(tmpPath);
            ImageIO.write(image, "jpeg", imageStream);

            imageStream.close();
            String result = this.upload(tmpPath);
            JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
            jsonArray.add(jsonObject);
        }
        doc.close();
        return jsonArray.toString();
    }

    public String excelImage(InputStream inputStream) throws Exception {
        JsonArray jsonArray = new JsonArray();

        Workbook workbook = new Workbook(inputStream);
        Worksheet sheet = workbook.getWorksheets().get(0);
        ImageOrPrintOptions imgOptions = new ImageOrPrintOptions();
        imgOptions.setQuality(80);
        SheetRender sheetRender = new SheetRender(sheet, imgOptions);
        for (int index = 0; index < sheetRender.getPageCount() && index < 3; index++) {
            long start = System.currentTimeMillis();
            long nanoTime = System.nanoTime();
            String tmpPath = "/tmp/excel_" + nanoTime + ".jpeg";
            sheetRender.toImage(index, tmpPath);
            Log4JUtil.logger.info("handle time:" + (System.currentTimeMillis() - start));
            String result = this.upload(tmpPath);
            JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
            jsonArray.add(jsonObject);
        }
        return jsonArray.toString();
    }

    /**
     * 先转 pdf 再转 img
     *
     * @param inputStream InputStream
     * @return String
     */
    private String pptImage(InputStream inputStream) throws Exception {
        PoiService poiService = new PoiService();
        List<String> fileList = poiService.ppt2Image(inputStream);
        return this.upload(fileList);
    }

    private String pptxImage(InputStream inputStream) throws Exception {
        PoiService poiService = new PoiService();
        List<String> fileList = poiService.pptx2Image(inputStream);
        return this.upload(fileList);
    }

    public static boolean getLicense() {
        boolean result = false;
        try {
            InputStream is = AsposeService.class.getClassLoader().getResourceAsStream("license.xml");
            License aposeLic = new License();
            aposeLic.setLicense(is);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return result;
    }
}
