package com.example.imageuploadtest;

public interface FileUploadObserver {
	
	public void uploadStatusUpdate(int error, long totalBytesRead, long totalBytes);

}
