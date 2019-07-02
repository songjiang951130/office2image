package com.haibian.material.service;

import com.aspose.cells.SaveFormat;
import com.haibian.material.tools.Log4JUtil;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.model.TextRun;
import org.apache.poi.hslf.usermodel.RichTextRun;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.apache.poi.xslf.usermodel.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PoiService {
    public static final int PREVIEW_COUNT = 3;
    private Map<String, Integer> typeMapping;

    public PoiService() {
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
        typeMapping.put("pdf", SaveFormat.PDF);
    }

    public int getType(String extendName) {
        return null == this.typeMapping.get(extendName) ? -1 : this.typeMapping.get(extendName);
    }

    public String handle(String extendName, InputStream inputStream) throws Exception {
        if (-1 == this.getType(extendName)) {
            return "";
        }
        switch (extendName) {
            case "ppt":
                return this.ppt2Image(inputStream).toString();
            case "pptx":
                return this.pptx2Image(inputStream).toString();
        }
        return "file type is not support";
    }

    public List<String> word2Pdf() {
        return null;
    }

    /**
     * POI-XSLF  pptx 2007
     *
     * @param inputStream
     * @return
     */
    public List<String> pptx2Image(InputStream inputStream) throws IOException {
        List<String> fileList = new ArrayList<>();
        XMLSlideShow xmlSlideShow = new XMLSlideShow(inputStream);
        inputStream.close();
        XSLFSlide[] slides = xmlSlideShow.getSlides();
        Dimension onePPTPageSize = xmlSlideShow.getPageSize();
        int cap = slides.length < PoiService.PREVIEW_COUNT ? slides.length : PoiService.PREVIEW_COUNT;
        System.out.println("cap:" + cap + " length:" + slides.length);
        for (int index = 0; index < cap; index++) {
            XSLFShape[] shapes = slides[index].getShapes();
            for (XSLFShape shape : shapes) {
                if (shape instanceof XSLFTextShape) {
                    XSLFTextShape sh = (XSLFTextShape) shape;
                    List<XSLFTextParagraph> textParagraphs = sh.getTextParagraphs();
                    for (XSLFTextParagraph xslfTextParagraph : textParagraphs) {
                        List<XSLFTextRun> textRuns = xslfTextParagraph.getTextRuns();
                        for (XSLFTextRun xslfTextRun : textRuns) {
                            Log4JUtil.logger.info("font:" + xslfTextRun.getFontFamily());
                            xslfTextRun.setFontFamily("微软雅黑");
                        }
                    }
                }
            }

            //根据幻灯片大小生成图片
            BufferedImage img = new BufferedImage(onePPTPageSize.width, onePPTPageSize.height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = img.createGraphics();
            graphics.setPaint(Color.black);
            graphics.fill(new Rectangle2D.Float(200, 200, onePPTPageSize.width, onePPTPageSize.height));
            // 最核心的代码
            slides[index].draw(graphics);
            long nano = System.currentTimeMillis();
            //图片将要存放的路径
            String absolutePath = "/tmp/pptx_" + (index + 1) + "_" + nano + ".jpeg";
            File jpegFile = new File(absolutePath);
            fileList.add(absolutePath);
            if (jpegFile.exists()) {
                continue;
            }
            FileOutputStream out = new FileOutputStream(jpegFile);
            javax.imageio.ImageIO.write(img, "jpeg", out);
            xmlSlideShow.write(out);
            out.close();
        }
        return fileList;
    }

    /**
     * ppt HSLF文件
     *
     * @throws IOException
     */
    public List<String> ppt2Image(InputStream inputStream) throws IOException {

        List<String> fileList = new ArrayList<>();

        SlideShow ppt = new SlideShow(inputStream);
        inputStream.close();

        Dimension dimension = ppt.getPageSize();
        //获取每一张ppt
        Slide[] slide = ppt.getSlides();
        int cap = slide.length < PoiService.PREVIEW_COUNT ? slide.length : PoiService.PREVIEW_COUNT;

        for (int index = 0; index < cap; index++) {
            TextRun[] textRun = slide[index].getTextRuns();
            for (TextRun text : textRun) {
                RichTextRun[] richTextRuns = text.getRichTextRuns();
                for (RichTextRun rich : richTextRuns) {
                    Log4JUtil.logger.info("font:" + rich.getFontName());
                    rich.setFontName("微软雅黑");
                }
            }
            //根据幻灯片大小生成图片
            BufferedImage img = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = img.createGraphics();
            graphics.setPaint(Color.white);
            graphics.fill(new Rectangle2D.Float(0, 0, dimension.width, dimension.height));

            slide[index].draw(graphics);
            long nano = System.currentTimeMillis();
            //图片将要存放的路径
            String absolutePath = "ppt_" + (index + 1) + "_" + nano + ".jpeg";
            File jpegFile = new File(absolutePath);
            fileList.add(absolutePath);
            if (jpegFile.exists()) {
                continue;
            }
            FileOutputStream out = new FileOutputStream(jpegFile);
            javax.imageio.ImageIO.write(img, "jpeg", out);
            ppt.write(out);
            out.close();
        }
        return fileList;
    }
}
