package com.haibian.material.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.haibian.material.service.AsposeService;
import com.haibian.material.tools.FileUtil;
import com.haibian.material.tools.Log4JUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsposeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("content-type", "application/json;charset=UTF-8");
        String fileUrl = request.getParameter("fileUrl");
        if (null == fileUrl) {
            outputJson(response, 10000, "参数错误");
            return;
        }
        String localPath = FileUtil.save(fileUrl);
        request.setAttribute("file", localPath);
        AsposeService asposeService = new AsposeService();
        String extendName = FileUtil.getExtendName(fileUrl);
        if (asposeService.getType(extendName) == -1) {
            FileUtil.delete(localPath);
            outputJson(response, 10003, "暂不支持该类型 type:" + extendName);
            return;
        }
        InputStream inputStream = new FileInputStream(localPath);
        try {
            String json = asposeService.handle(extendName, inputStream);
            outputJson(response, 0, "success", new JsonParser().parse(json));
            return;
        } catch (Exception e) {
            e.printStackTrace();
            Log4JUtil.logger.error(e.getMessage(), e);
        } finally {
            inputStream.close();
            FileUtil.delete(localPath);
        }
        outputJson(response, 5000, "系统错误");
        return;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long start = System.currentTimeMillis();
        //防止返回中文乱码
        response.setHeader("content-type", "application/json;charset=UTF-8");
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        System.out.println(isMultipart);
        if (!isMultipart) {
            outputJson(response, 10001, "未获取到文件");
            return;
        }

        //使用Apache文件上传组件处理文件上传步骤：
        //1、创建一个DiskFileItemFactory工厂
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //2、创建一个文件上传解析器
        ServletFileUpload upload = new ServletFileUpload(factory);
        //解决上传文件名的中文乱码
        upload.setHeaderEncoding("UTF-8");

        List<FileItem> list = null;
        try {
            list = upload.parseRequest(request);
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
        if (null == list) {
            outputJson(response, 10002, "请求错误");
            return;
        }
        //循环处理form表单
        for (FileItem item : list) {
            //如果fileitem中封装的是普通输入项的数据
            if (item.isFormField()) {
                String name = item.getFieldName();
                //解决普通输入项的数据的中文乱码问题
                String value = item.getString("UTF-8");
                System.out.println(name + "=" + value);
            } else {
                AsposeService asposeService = new AsposeService();
                String filename = item.getName();
                //注意：不同的浏览器提交的文件名是不一样的，有些浏览器提交上来的文件名是带有路径的，如：  c:\a\b\1.txt，而有些只是单纯的文件名，如：1.txt
                //处理获取到的上传文件的文件名的路径部分，只保留文件名部分
                String relativeFileName = filename.substring(filename.lastIndexOf("\\") + 1);
                //文件后缀
                String extendName = relativeFileName.substring(relativeFileName.lastIndexOf(".") + 1).toLowerCase();
                if (asposeService.getType(extendName) == -1) {
                    outputJson(response, 10003, "暂不支持该类型 type:" + extendName);
                    return;
                }
                //获取item中的上传文件的输入流
                InputStream in = item.getInputStream();
                Log4JUtil.logger.info("getStream time:" + (System.currentTimeMillis() - start));
                try {
                    String json = asposeService.handle(extendName, in);
                    Log4JUtil.logger.info("return json string:" + json);
                    Log4JUtil.logger.info("request time:" + (System.currentTimeMillis() - start));
                    outputJson(response, 0, "success", new JsonParser().parse(json));
                    return;
                } catch (java.lang.Exception e) {
                    e.printStackTrace();
                    Log4JUtil.logger.error(e.getMessage(), e);
                } finally {
                    in.close();
                }
            }
        }
        outputJson(response, 5000, "系统错误");
        return;
    }

    private void outputJson(HttpServletResponse response, int errcode, String errmsg, Object object) throws IOException {
        Gson gson = new Gson();
        Map<String, Object> result = new HashMap<>();
        result.put("data", object);
        result.put("errcode", errcode + "");
        result.put("errmsg", errmsg);
        response.getWriter().println(gson.toJson(result));
        return;
    }

    private void outputJson(HttpServletResponse response, int errcode, String errmsg) throws IOException {
        Gson gson = new Gson();
        Map<String, Object> result = new HashMap<>();
        result.put("errcode", errcode + "");
        result.put("errmsg", errmsg);
        PrintWriter out = response.getWriter();
        out.println(gson.toJson(result));
        out.flush();
        out.close();
        return;
    }
}
