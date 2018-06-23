package com.ztesoft.iom.common.util;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.DefaultCookieSpecProvider;
import org.apache.http.impl.cookie.RFC6265CookieSpecProvider;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

/**
 * <p>操作HTTP</p>
 * <p>Copyright (c) 2015-2020, http://team.oschina.net/zsgxoss </p>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @author lu.zhou 
 * @since  Feb 1, 2016
 */

public class HttpKit {

	public static String post(String url, Map<String, Object> params) throws Exception {
		CloseableHttpClient client = createSSLClientDefault();
		return post(url, params, client);
	}
	
	public static String post(String url, Map<String, Object> params, CloseableHttpClient client) throws Exception {
		HttpPost postMethod = createPost(url); 
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		if(params != null) {
			for (String key : params.keySet()) {
				list.add(new BasicNameValuePair(key, params.get(key).toString()));
			}
		}
		postMethod.setEntity(new UrlEncodedFormEntity(list));
		CloseableHttpResponse response = client.execute(postMethod);
		HttpEntity entity = response.getEntity();
		return EntityUtils.toString(entity);
	}
	
	public static String post(String url, String requestBody) throws Exception {
		CloseableHttpClient client = createSSLClientDefault();
		return post(url, requestBody, client);
	}
	
	public static String post(String url, String requestBody, CloseableHttpClient client) throws Exception {
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(60000).setConnectTimeout(10000).build();
		HttpPost postMethod = createPost(url); 
		postMethod.setEntity(new StringEntity(requestBody));
		postMethod.setConfig(requestConfig);
		CloseableHttpResponse response = client.execute(postMethod);
		HttpEntity entity = response.getEntity();
		return EntityUtils.toString(entity);
	}
	
	public static HttpPost createPost(String url) {
		HttpPost postMethod = new HttpPost(url); 
		postMethod.setHeader("Accept-Encoding", "gzip, deflate");    
		postMethod.setHeader("Accept-Language", "zh-CN,zh;q=0.8");    
		postMethod.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"); 
		postMethod.setHeader("Accept", "*/*"); 
		postMethod.setHeader("Connection", "keep-alive"); 
		postMethod.setHeader("X-Requested-With", "XMLHttpRequest");
		postMethod.setHeader("User-Agent", "FuckEtl");
		return postMethod;
	}
	
	public static CloseableHttpClient createSSLClientDefault() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(
					null, new TrustStrategy() {
						// 信任所有
						@Override
						public boolean isTrusted(
								java.security.cert.X509Certificate[] arg0,
								String arg1)
								throws java.security.cert.CertificateException {
							return true;
						}
					}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					sslContext);
			PublicSuffixMatcher publicSuffixMatcher = PublicSuffixMatcherLoader.getDefault();

			Registry<CookieSpecProvider> registry = RegistryBuilder.<CookieSpecProvider>create()
			        .register(CookieSpecs.DEFAULT,
			                new DefaultCookieSpecProvider(publicSuffixMatcher))
			        .register(CookieSpecs.STANDARD,
			                new RFC6265CookieSpecProvider(publicSuffixMatcher))
			        .build();
			return HttpClients.custom().setSSLSocketFactory(sslsf)
					//.setDefaultCookieStore(cookieStore)
					.setDefaultCookieSpecRegistry(registry).build();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		return HttpClients.createDefault();
	}
}
