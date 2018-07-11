package com.xiaoju.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpClientUploadUtils {
	/**
	 * 多文件上传
	 * @param url
	 * @param fileList
	 * @return
	 */
	public static String uploadMultiFile(String url,List<File> fileList) {
		String result = "";
		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse httpResponse = null;
				
		HttpPost post = new HttpPost(url);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		for(File file: fileList) {
			builder.addBinaryBody(file.getName(), file);
		}
		post.setEntity(builder.build());
		try {
			httpResponse = client.execute(post);
			int status = httpResponse.getStatusLine().getStatusCode();
			if(status == HttpStatus.SC_OK) {
				HttpEntity entity = httpResponse.getEntity();
				result = EntityUtils.toString(entity);
			}
		} catch (IOException e) {
			e.printStackTrace();
			result = "error:" + e.getMessage();
		} finally {
			HttpClientUtils.closeQuietly(httpResponse);
			HttpClientUtils.closeQuietly(client);
		}
		return result;
	}
	
	public static String download(HttpServletRequest request,String savePath) {
		String result = "ok";
		if(savePath == null || savePath.length()<1) {
			savePath = request.getServletContext().getRealPath("/file");
		}
		File toFile = new File(savePath);
		if(!toFile.exists()) {
			File parentFile = new File(toFile.getParent());
			if(!parentFile.exists()) {
				parentFile.mkdirs();
			}
			try {
				toFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				result ="error";
			}
		}
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload servletFileUpload = new ServletFileUpload(factory);
		try {
			List<FileItem> items = servletFileUpload.parseRequest(request);
			Iterator<FileItem> itr = items.iterator();
			while(itr.hasNext()) {
				FileItem fileItem = itr.next();
				if(!fileItem.isFormField()) {
					try {
						String fileName = URLDecoder.decode(fileItem.getName(),"UTF-8").replaceAll("\\", "/");
						try {
							fileItem.write(toFile);
						} catch (Exception e) {
							e.printStackTrace();
							result ="error";
						}
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						result ="error";
					}
				}
			}
		} catch (FileUploadException e) {
			e.printStackTrace();
			result ="error";
		}
		
		return result;
	}
}
