package com.haibian.material.service.thread;


import com.aspose.words.Document;
import com.aspose.words.ImageSaveOptions;

import java.util.concurrent.CountDownLatch;

public class WordRunnable implements Runnable{
    public static CountDownLatch countDownLatch;
    //用安全类
    public static String[] tempImage;

    private Document document;
    private ImageSaveOptions options;
    private int index;
    public WordRunnable(Document document,ImageSaveOptions options,int index){
        this.document = document;
        this.options = options;
        this.index = index;
    }
    @Override
    public void run() {
        options.setPageIndex(index);
        System.out.println("word index:"+index);
        long nanoTime = System.nanoTime();
        String tmpPath = "/tmp/word_" + nanoTime + ".jpeg";
        try {
            //预览图有先后关系
            synchronized (WordRunnable.class){
                document.save(tmpPath, options);
                tempImage[index] = tmpPath;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {

        }
        countDownLatch.countDown();
    }

    public static boolean setConfig(int cap){
        countDownLatch = new CountDownLatch(cap);
        tempImage = new String[cap];
        return  true;
    }
}
