package com.ztesoft.iom.manage.http.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * @Description:
 * @author: huang.jing
 * @Date: 2018/5/17 0017 - 9:49
 */
public class HttpBase
{

    private static Logger log = LogManager.getLogger(HttpBase.class);
    public static final String POST = "POST";
    public static final String GET = "GET";
    public static enum HttpMethod {GET, POST}

    public static class APNNet{
        public static String CMWAP = "cmwap";  //中国移动cmwap
        public static String CMNET = "cmnet";  //中国移动cmnet
        public static String GWAP_3 = "3gwap";  //3G wap 中国联通3gwap APN
        public static String GNET_3="3gnet";  //3G net 中国联通3gnet APN
        public static String UNIWAP="uniwap";  //uni wap 中国联通uni wap APN
        public static String UNINET="uninet";  //uni net 中国联通uni net APN
        public static String CTWAP="ctwap"; //中国电信的WAP接入名称（China Telecom WAP）
    }


    public static String postRequest(String destUrl,final Map<String, String> putparams, String mothed,
                                     BasicHeader[] aheader) throws ClientProtocolException, IOException
    {
        log.info("tag", destUrl);
        if (destUrl.equals(""))
        {
            return "";
        }
        return postRequest(destUrl, "", putparams,mothed, aheader);
    }

    public static String postRequest(String destUrl, String requestContent,final Map<String, String> putparams,
                                     String mothed, BasicHeader[] aheader)
            throws ClientProtocolException, IOException,UnsupportedEncodingException
    {
        if (destUrl.equals("")){
            return "";
        }
        InputStream responseContent = postRequestStream(destUrl,requestContent,putparams, mothed, aheader);
        return convertStreamToString(responseContent);
    }
    public static String postRequestDirect(String destUrl,final Map<String, String> putparams, String mothed,
                                           BasicHeader[] aheader) throws ClientProtocolException, IOException
    {
        log.info("tag", destUrl);
        if (destUrl.equals(""))
        {
            return "";
        }
        return postRequestDirect(destUrl, "", putparams,mothed, aheader);
    }

    public static String postRequestDirect(String destUrl, String requestContent,final Map<String, String> putparams,
                                           String mothed, BasicHeader[] aheader)
            throws ClientProtocolException, IOException,UnsupportedEncodingException
    {
        if (destUrl.equals("")){
            return "";
        }
        InputStream responseContent = postRequestStream(destUrl,requestContent,putparams, mothed, aheader);
        String returnContent = inputStream2String(responseContent);
        return returnContent;
    }
    public static String inputStream2String(InputStream is) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i=-1;
        while((i=is.read())!=-1){
            baos.write(i);
        }
        return baos.toString();
    }

    public static InputStream postRequestStream(String destUrl,
                                                String requestContent,final Map<String, String> putparams, String amothed, BasicHeader[] aheader)
            throws ClientProtocolException, IOException
    {
        BasicHttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);

        HttpRequestBase httpPost = null;
        StringEntity requestEntity = new StringEntity(requestContent);
        HttpUriRequest request = null;
        if (amothed.equals(POST)){
            request = getRequest(destUrl, putparams, HttpBase.HttpMethod.POST);
        } else{
            String temp = URLEncoder.encode(requestContent);
            httpPost = new HttpGet(destUrl + "?param=" + temp);
        }

        if (aheader != null)
        {
            for (BasicHeader ah : aheader)
            {
                httpPost.addHeader(ah);
            }
        }

        DefaultHttpClient httpClient = new DefaultHttpClient(params);
        HttpResponse httpResponse = httpClient.execute(request);

        int rescode = httpResponse.getStatusLine().getStatusCode();
        HttpEntity responseEntity = httpResponse.getEntity();
        InputStream responseContent = responseEntity.getContent();
        return responseContent;
    }

    public static HttpURLConnection openConnection(URL arul) throws IOException
    {

//		Context mContext = GlobalVariable.currentCONTEXT;
//		ConnectivityManager manager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//		NetworkInfo activeNetInfo = manager.getActiveNetworkInfo();//获取网络的连接情况
//		Log.i("info", "检测到网络 -- >> "+ activeNetInfo.getExtraInfo());
//		String type = activeNetInfo.getTypeName();
//		Log.e("tag", "NetworkType=" + type);
//		if(!type.equals("WIFI")){       //不是WIFI需要设置不同的代理
//			if(activeNetInfo.getExtraInfo().equals(APNNet.CMWAP) || activeNetInfo.getExtraInfo().equals(APNNet.UNIWAP)){
//				//HttpHost proxy = new HttpHost( "10.0.0.172", 80, "http");
//				InetSocketAddress pxy = new InetSocketAddress("10.0.0.172", 80);
//			}else if(activeNetInfo.getExtraInfo().equals(APNNet.CTWAP)){
//				InetSocketAddress pxy = new InetSocketAddress("10.0.0.200", 80);
//			}else {
//				InetSocketAddress pxy = getProxy();
//				if (pxy != null)
//				{
//					HttpHost proxy = new HttpHost(pxy.getHostName(), pxy.getPort());
//
//				}
//			}
//		}
        HttpURLConnection urlConnection = null;
            urlConnection = (HttpURLConnection) arul.openConnection();
        return urlConnection;
    }

    private static String convertStreamToString(InputStream is)throws UnsupportedEncodingException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try
        {
            GZIPInputStream gunzip = new GZIPInputStream(is);  //对数据流进行解压
            byte[] buffer = new byte[256];
            int n;
            while ((n = gunzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } catch (IOException e)
        {
            log.error(HttpBase.class.getName(), "", e);
        } finally
        {
            try
            {
                is.close();
            } catch (IOException e)
            {
                log.error(HttpBase.class.getName(), "", e);
            }
        }

        String responsestr = new String(out.toString("ISO-8859-1").getBytes("ISO-8859-1"),"gbk"); //解压后需进行转码,gbk
        return responsestr;
    }

    private static HttpUriRequest getRequest(String url, Map<String, String> params, HttpMethod method) {
        if (method.equals(HttpMethod.POST)) {
            List<NameValuePair> listParams = new ArrayList<NameValuePair>();
            if (params != null) {
                for (String name : params.keySet()) {
                    listParams.add(new BasicNameValuePair(name, params.get(name)));
                }
            }
            try {
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(listParams,HTTP.UTF_8);
                    HttpPost request = new HttpPost(url);
                    request.setEntity(entity);
                    return request;
            } catch (UnsupportedEncodingException e) {
                // Should not come here, ignore me.
                throw new java.lang.RuntimeException(e.getMessage(), e);
            }
        } else {
            if (url.indexOf("?") < 0) {
                url += "?";
            }
            if (params != null) {
                for (String name : params.keySet()) {
                    url += "&" + name + "=" + URLEncoder.encode(params.get(name));
                }
            }
            HttpGet request = new HttpGet(url);
            return request;
        }
    }



    /**
     * httpClient的get请求方式2
     *
     * @return
     * @throws Exception
     */
    public static String doGet(String url, String charset) throws Exception {
        /*
         * 使用 GetMethod 来访问一个 URL 对应的网页,实现步骤: 1:生成一个 HttpClinet 对象并设置相应的参数。
         * 2:生成一个 GetMethod 对象并设置响应的参数。 3:用 HttpClinet 生成的对象来执行 GetMethod 生成的Get
         * 方法。 4:处理响应状态码。 5:若响应正常，处理 HTTP 响应内容。 6:释放连接。
         */
        /* 1 生成 HttpClinet 对象并设置参数 */
        HttpClient httpClient = new HttpClient();
        // 设置 Http 连接超时为5秒
        httpClient.getHttpConnectionManager().getParams()
                .setConnectionTimeout(10000);
        /* 2 生成 GetMethod 对象并设置参数 */
        GetMethod getMethod = new GetMethod(url);
        // 设置 get 请求超时为 5 秒
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
        // 设置请求重试处理，用的是默认的重试处理：请求三次
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler());
        String response = "";
        /* 3 执行 HTTP GET 请求 */
        try {
            int statusCode = httpClient.executeMethod(getMethod);
            /* 4 判断访问的状态码 */
            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("请求出错: " + getMethod.getStatusLine());
            }
            /* 5 处理 HTTP 响应内容 */
            // HTTP响应头部信息，这里简单打印
            Header[] headers = getMethod.getResponseHeaders();
            for (Header h : headers)
                System.out
                        .println(h.getName() + "------------ " + h.getValue());
            // 读取 HTTP 响应内容，这里简单打印网页内容
            byte[] responseBody = getMethod.getResponseBody();// 读取为字节数组
            response = new String(responseBody, charset);
            System.out.println("----------response:" + response);
            // 读取为 InputStream，在网页内容数据量大时候推荐使用
            // InputStream response = getMethod.getResponseBodyAsStream();
        } catch (HttpException e) {
            // 发生致命的异常，可能是协议不对或者返回的内容有问题
            System.out.println("请检查输入的URL!");
            e.printStackTrace();
        } catch (IOException e) {
            // 发生网络异常
            System.out.println("发生网络异常!");
            e.printStackTrace();
        } finally {
            /* 6 .释放连接 */
            getMethod.releaseConnection();
        }
        return response;
    }

    public static void main(String[] args) throws Exception {

//        for (int i = 0; i < 1; i++) {
//            long begin = System.currentTimeMillis();
//            System.out.println(i%2 + "第" + i + "次开始" + begin);
//            Map params = new HashMap();
////            params.put("params", "{\"device\":{\"screen\":\"720*1280\",\"model\":\"hlteuc\",\"version\":\"1.0.0\"},\"isPhoneAutoLogin\":false,\"username\":\"iom\",\"serialImis\":\"310260271293328\",\"password\":\"123456\"}");
//            params.put("params", "{\"passData\":{\"paramXml\":\"<?xml version=\\\"1.0\\\" encoding=\\\"GBK\\\" ?><SERVICE><IDA_COMMON><CALL_METHOD>ZYD_QUERYREADLIST_ALL<\\/CALL_METHOD><INPUT_XMLDATA><productType>ALL<\\/productType><serviceType>ALL<\\/serviceType><sortFieldDeal>DESC<\\/sortFieldDeal><CALL_METHOD>ZYD_QUERYREADLIST_ALL<\\/CALL_METHOD><STAFFID>1<\\/STAFFID><sortField>ALL<\\/sortField><isPerOrGridSyn>perSyn<\\/isPerOrGridSyn><bokState>ALL<\\/bokState><ROWNUM>10<\\/ROWNUM><\\/INPUT_XMLDATA><\\/IDA_COMMON><\\/SERVICE>\"}}");
////            System.out.println(HttpBase.postRequest("http://211.138.252.146:18060/MOBILE/rest/mobileService/logonmob", params, HttpBase.POST, null));
//            System.out.println("返回：" + HttpBase.postRequestDirect("http://211.138.252.146:18060/MOBILE/rest/mobileService/directserv/SERVICE_CODE_620", params, HttpBase.POST, null));
//
//            long end = System.currentTimeMillis();
//            System.out.println("第" + i + "次结束" + end);
//            System.out.println("第" + i + "次耗时" + (end - begin));
//        }

//        http://10.184.24.9:8090/portal/gxSso?method=getTicket&UserName=huangjing6&Password=DLabc@889

        Map params = new HashMap();
        params.put("method", "getTicket");
        params.put("UserName", "账号");
        params.put("Password", "密码");
        System.out.println("返回：" + HttpBase.postRequestDirect("http://10.184.24.9:8090/portal/gxSso", params, HttpBase.POST, null));

//        String relatedBossSerialnumber = "1234|234";
//        // RELATED_BOSS_SERIALNUMBER字段不为空时，代表有同装单，按|分隔后计算同装单数量
//        if(relatedBossSerialnumber != null && !"".equals(relatedBossSerialnumber)) {
//            String[] relatedBossSerialnumberArr = relatedBossSerialnumber.split("\\|");
//            System.out.println(relatedBossSerialnumberArr.length + 1);
//        }
    }
}

