package com.ztesoft.iom.common.util;

import java.io.*;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.apache.poi.xwpf.converter.core.BasicURIResolver;
import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.w3c.dom.Document;

/**
 * @Description: work文档工具类
 * @author: huang.jing
 * @Date: 2018/3/20 0020 - 16:12
 */
public class WordUtils {

    private static Logger log = LogManager.getLogger(WordUtils.class);

    /**
     * doc文件转html
     *
     * @param wordFile
     * @param htmlPath
     * @return
     */
    public String docToHtml(File wordFile, String htmlPath) {
        String fileName = "";
        try {
            // 系统当前时间
            String currentTimeMillis = System.currentTimeMillis() + "";
            // 生成html文件名
            String htmlName = currentTimeMillis + ".html";
            // 生成html文件上级文件夹
            File folder = new File(htmlPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            InputStream input = new FileInputStream(wordFile);
            HWPFDocument wordDocument = new HWPFDocument(input);
            WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
                    DocumentBuilderFactory.newInstance().newDocumentBuilder()
                            .newDocument());
            wordToHtmlConverter.setPicturesManager(new PicturesManager() {
                public String savePicture(byte[] content, PictureType pictureType,
                                          String suggestedName, float widthInches, float heightInches) {
                    return suggestedName;
                }
            });
            wordToHtmlConverter.processDocument(wordDocument);
            List pics = wordDocument.getPicturesTable().getAllPictures();
            if (pics != null) {
                for (int i = 0; i < pics.size(); i++) {
                    Picture pic = (Picture) pics.get(i);
                    try {
                        pic.writeImageContent(new FileOutputStream(htmlPath
                                + pic.suggestFullFileName()));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            Document htmlDocument = wordToHtmlConverter.getDocument();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            DOMSource domSource = new DOMSource(htmlDocument);
            StreamResult streamResult = new StreamResult(outStream);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer serializer = tf.newTransformer();
//            serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            serializer.setOutputProperty(OutputKeys.ENCODING, "GB2312");
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty(OutputKeys.METHOD, "html");
            serializer.transform(domSource, streamResult);
            outStream.close();
            String content = new String(outStream.toByteArray());
//            FileUtils.write(new File(htmlPath, htmlName), content, "utf-8");
            FileUtils.write(new File(htmlPath, htmlName), content, "GB2312");
            fileName = htmlName;
        } catch (Exception e) {
            log.error("doc文件转html异常", e);
        }

        return fileName;
    }

    /**
     * docx文件转html
     *
     * @param wordFile
     * @param htmlPath
     * @return
     */
    public String docxToHtml(File wordFile, String htmlPath) {
        String fileName = "";
        try {
            // 系统当前时间
            String currentTimeMillis = System.currentTimeMillis() + "";
            // 生成html文件名
            String htmlName = currentTimeMillis + ".html";
            // 判断html文件是否存在
            File htmlFile = new File(htmlPath + htmlName);
            // 图片路径
            String imageRelaPath = "image";
            String imagePath = htmlPath + imageRelaPath + File.separator;

            // 1) 加载word文档生成 XWPFDocument对象
            InputStream in = new FileInputStream(wordFile);
            XWPFDocument document = new XWPFDocument(in);

            // 2) 将 XWPFDocument转换成XHTML
            // 生成html文件上级文件夹
            File folder = new File(htmlPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // 3) 解析 XHTML配置 (这里设置IURIResolver来设置图片存放的目录)
            File imgFolder = new File(imagePath);
            XHTMLOptions options = XHTMLOptions.create();
            options.setExtractor(new FileImageExtractor(imgFolder));
            // html中图片的路径 相对路径
            options.URIResolver(new BasicURIResolver(imageRelaPath));
            options.setIgnoreStylesIfUnused(false);
            options.setFragment(true);

            OutputStream out = new FileOutputStream(htmlFile);
            XHTMLConverter.getInstance().convert(document, out, options);

            fileName = htmlName;
        } catch (Exception e) {
            log.error("docx文件转html异常", e);
        }

        return fileName;
    }

    /**
     * 替换文件内容
     *
     * @param filePath      文件路径
     * @param replaceTarget 替换目标
     * @param replacement   替换内容
     * @throws IOException
     */
    public void autoReplace(String filePath, String replaceTarget, String replacement) throws IOException {
        // 读取文件内容
        File file = new File(filePath);
        Long fileLength = file.length();
        byte[] fileContext = new byte[fileLength.intValue()];
        FileInputStream in = new FileInputStream(filePath);
        in.read(fileContext);
        in.close();
        String str = new String(fileContext);
        // 替换文件内容
        str = str.replaceAll(replaceTarget, replacement);
        // 将替换后的文件内容输出到原文件
        PrintWriter out = new PrintWriter(filePath);
        out.write(str.toCharArray());
        out.flush();
        out.close();
    }

    /**
     * 获取文件内容
     * @param filePath
     * @return
     * @throws IOException
     */
    public String getFileContent(String filePath) throws IOException {
        // 读取文件内容
        File file = new File(filePath);
        Long fileLength = file.length();
        byte[] fileContext = new byte[fileLength.intValue()];
        FileInputStream in = new FileInputStream(filePath);
        in.read(fileContext);
        in.close();
        String str = new String(fileContext);
        return str;
    }
}
