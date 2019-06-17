package co.pailab.lime.model.storage;

import java.net.URL;
import java.util.List;

public class Storage {
    private String file;
    private List<String> files;
    private List<URL> fileUrls;
    private int totalFiles;

    public Storage() {
        super();
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public List<URL> getFileUrls() {
        return fileUrls;
    }

    public void setFileUrls(List<URL> fileUrls) {
        this.fileUrls = fileUrls;
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(int totalFiles) {
        this.totalFiles = totalFiles;
    }

}
