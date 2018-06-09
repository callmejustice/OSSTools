package com.ztesoft.iom.manage.kbs.service;

import java.io.*;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.apache.poi.xwpf.converter.core.BasicURIResolver;
import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.core.XWPFConverterException;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.w3c.dom.Document;

/**
 * @Description:
 * @author: huang.jing
 * @Date: 2018/3/16 0016 - 16:53
 */
public class DocUntils {

    public static void main(String[] args) throws Throwable {
//        final String path = "D:\\";
//        final String file = "aa.doc";
//        InputStream input = new FileInputStream(path + file);
//        HWPFDocument wordDocument = new HWPFDocument(input);
//        WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
//                DocumentBuilderFactory.newInstance().newDocumentBuilder()
//                        .newDocument());
//        wordToHtmlConverter.setPicturesManager(new PicturesManager() {
//            public String savePicture(byte[] content, PictureType pictureType,
//                                      String suggestedName, float widthInches, float heightInches) {
//                return suggestedName;
//            }
//        });
//        wordToHtmlConverter.processDocument(wordDocument);
//        List pics = wordDocument.getPicturesTable().getAllPictures();
//        if (pics != null) {
//            for (int i = 0; i < pics.size(); i++) {
//                Picture pic = (Picture) pics.get(i);
//                try {
//                    pic.writeImageContent(new FileOutputStream(path
//                            + pic.suggestFullFileName()));
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        Document htmlDocument = wordToHtmlConverter.getDocument();
//        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//        DOMSource domSource = new DOMSource(htmlDocument);
//        StreamResult streamResult = new StreamResult(outStream);
//        TransformerFactory tf = TransformerFactory.newInstance();
//        Transformer serializer = tf.newTransformer();
//        serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
//        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
//        serializer.setOutputProperty(OutputKeys.METHOD, "html");
//        serializer.transform(domSource, streamResult);
//        outStream.close();
//        String content = new String(outStream.toByteArray());
//        FileUtils.write(new File(path, "1.html"), content, "utf-8");
//
//        autoReplace(path + "1.html", path + "1.html");

//        Word2007ToHtml("d://");
        String sd = "<head><title>sdf21</title><body></body>";
        System.out.println(sd.replaceAll("<head><title>.+</title>", "<head><title>测试</title>"));
    }

    /**
     * docx文件转html
     * @param tempContextUrl 项目访问名
     * @return
     */
    public static int Word2007ToHtml(String tempContextUrl) {
        int rv = 0;
        try {
//            String path =  "d://aa.docx";
            //word路径
            String wordPath = "d://";
            //word文件名
            String wordName = "aaa";
            //后缀
            String suffix = ".docx";
            //生成html路径
            String htmlPath = wordPath + File.separator + System.currentTimeMillis() + "_show" + File.separator;
            //生成html文件名
            String htmlName = System.currentTimeMillis() + ".html";
            //图片路径
            String imagePath = htmlPath + "image" + File.separator;

            //判断html文件是否存在
            File htmlFile = new File(htmlPath + htmlName);

            //word文件
            File wordFile = new File(wordPath + File.separator + wordName + suffix);

            // 1) 加载word文档生成 XWPFDocument对象
            InputStream in = new FileInputStream(wordFile);
            XWPFDocument document = new XWPFDocument(in);

            // 2) 解析 XHTML配置 (这里设置IURIResolver来设置图片存放的目录)
            File imgFolder = new File(imagePath);
            XHTMLOptions options = XHTMLOptions.create();
            options.setExtractor(new FileImageExtractor(imgFolder));
            //html中图片的路径 相对路径
            options.URIResolver(new BasicURIResolver("image"));
            options.setIgnoreStylesIfUnused(false);
            options.setFragment(true);

            // 3) 将 XWPFDocument转换成XHTML
            //生成html文件上级文件夹
            File folder = new File(htmlPath);
            if(!folder.exists()){
                folder.mkdirs();
            }
            OutputStream out = new FileOutputStream(htmlFile);
            XHTMLConverter.getInstance().convert(document, out, options);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return rv;
        } catch (XWPFConverterException e) {
            e.printStackTrace();
            return rv;
        } catch (IOException e) {
            e.printStackTrace();
            return rv;
        }
        rv = 1;
        return rv;
    }

    private static void autoReplace(String filePath, String outPath) throws IOException{
        File file=new File(filePath);
        Long fileLength=file.length();
        byte[] fileContext=new byte[fileLength.intValue()];
        FileInputStream in=new FileInputStream(filePath);
        in.read(fileContext);
        in.close();
        String str=new String(fileContext);

        str=str.replace("<head>","<head><title>测试标题</title>");

        PrintWriter out=new PrintWriter(outPath);
        out.write(str.toCharArray());
        out.flush();
        out.close();
    }
}
