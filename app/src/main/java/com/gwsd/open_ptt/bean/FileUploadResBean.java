package com.gwsd.open_ptt.bean;

public class FileUploadResBean {

    int status;
    String message;
    int timestamp;
    FileResult data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public FileResult getData() {
        return data;
    }

    public void setData(FileResult data) {
        this.data = data;
    }

    public static class FileResult {
        FileUrl file1;
        FileUrl file2;

        public FileUrl getFile1() {
            return file1;
        }

        public void setFile1(FileUrl file1) {
            this.file1 = file1;
        }

        public FileUrl getFile2() {
            return file2;
        }

        public void setFile2(FileUrl file2) {
            this.file2 = file2;
        }
    }

    public static class FileUrl {
        String url;
        String fileId;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getFileId() {
            return fileId;
        }

        public void setFileId(String fileId) {
            this.fileId = fileId;
        }
    }

}
