package com.tagreff.lab31;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {
    public static void main(String[] args) throws IOException {
        long m = System.currentTimeMillis();
        //считываем адрес сайта и местоположение папки из файла
        Properties properties = new Properties();
        InputStream propertiesStream = Main.class.getClassLoader().getResourceAsStream("config.properties");

        if (propertiesStream != null) {
            try {
                properties.load(propertiesStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else System.out.println("file not found");

        String url = properties.getProperty("cnf.url");
        String targetDirectory = properties.getProperty("cnf.target_directory");

        //Подключаемся к сайту и сохраняем его в качестве Jsoup-документа
        Document document = Jsoup
                .connect(url)
                .userAgent("Chrome")
                .timeout(10 * 1000)
                .get();

        //выбираем элементы по html-тегу <img>
        Elements imageElements = document.select("img");

        ExecutorService executor= Executors.newCachedThreadPool();
        //проходим в цикле по каждому изображению
        int id = 0;
        try {
            for (Element imageElement : imageElements) {
                //получаем абсолютный путь до изображения
                String imageUrl = imageElement.attr("abs:src");
                //загружаем изображение
                executor.execute(new ImageDownloader(id, imageUrl, targetDirectory));
                id++;
            }
        }catch(Exception err){
        err.printStackTrace();
    }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            System.out.println((double) (System.currentTimeMillis() - m));
        } catch (InterruptedException e) {
        e.printStackTrace();
        }
    }
}

class ImageDownloader implements Runnable{
    int id;
    String imageUrl, targetDirectory;
    public ImageDownloader(int i, String image_url, String target_diretory){
        id = i;
        imageUrl = image_url;
        targetDirectory = target_diretory;
    }
    public  void run(){

        //получаем название файла
        String imgName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1 );
        System.out.println("Поток " + id + ", Сохранение: " + imgName + ", из: " + imageUrl);
        try {
            //открытие потока
            URL urlImage = new URL(imageUrl);
            InputStream in = urlImage.openStream();

            byte[] buffer = new byte[4096];
            int n = -1;

            OutputStream outputStreams = new FileOutputStream( targetDirectory + "/" + imgName );

            //записываем байты в поток
            while ( (n = in.read(buffer)) != -1 )
                outputStreams.write(buffer, 0, n);

            //закрытие потока
            outputStreams.close();

            System.out.println("Поток " + id + ", Изображение сохранено");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
