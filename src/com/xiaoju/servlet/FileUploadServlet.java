package com.xiaoju.servlet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xiaoju.utils.HttpClientUploadUtils;

public class FileUploadServlet extends HttpServlet{
	public void doGet(HttpServletRequest request,HttpServletResponse response) {
		doPost(request,response);
	}
	public void doPost(HttpServletRequest request,HttpServletResponse response) {
		String result = "";
		String url = "";
		List<File> fileList = new ArrayList<>();
		result = HttpClientUploadUtils.uploadMultiFile(url, fileList);
	}
}
