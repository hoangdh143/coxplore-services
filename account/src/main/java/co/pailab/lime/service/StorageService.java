package co.pailab.lime.service;

import co.pailab.lime.helper.ErrorHttpResponse;
import co.pailab.lime.helper.HttpResponse;
import co.pailab.lime.helper.SuccessHttpResponse;
import co.pailab.lime.helper.storage.S3Handler;
import co.pailab.lime.model.storage.Storage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service("storageService")
public class StorageService {
    private S3Handler s3Handler;

    @Autowired
    public StorageService(S3Handler s3Handler) {
        this.s3Handler = s3Handler;
    }

    public void init() {
    }

    public String store(File tmpFile, HttpServletResponse res, String s3Dir) {
        String s3Url;
        try {
            s3Url = s3Handler.uploadOneFile(tmpFile, s3Dir, res, true);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            s3Url = null;
            e.printStackTrace();
        }
        return s3Url;
    }

    @SuppressWarnings("deprecation")
    public HttpResponse storeMultiFile(List<String> files, HttpServletResponse res, String s3Dir) {

        List<String> successFiles = new ArrayList<>();
        List<Integer> errorFiles = new ArrayList<>();

        List<File> fileList = new ArrayList<>();
        for (String file : files) {
            try {
                byte[] bI = org.apache.commons.codec.binary.Base64.decodeBase64(file);
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(bI));

                File tmpFile = new File(System.getProperty("java.io.tmpdir") + "fileName" + ".png");

                ImageIO.write(img, "png", tmpFile);
                fileList.add(tmpFile);

                String s3Url = s3Handler.uploadOneFileNoRes(tmpFile, s3Dir, true);
                successFiles.add(s3Url);
            } catch (Exception e) {
                errorFiles.add(files.indexOf(file));
                e.printStackTrace();
            }
        }

        // prepare data for http response
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.createObjectNode();

        JsonNode node = mapper.valueToTree(successFiles);
        ((ObjectNode) rootNode).put("successFiles", node);

        JsonNode node1 = mapper.valueToTree(errorFiles);
        ((ObjectNode) rootNode).put("errorFiles", node1);

        return new SuccessHttpResponse(true, 200, "Uploaded files info", res, rootNode);
    }

    @SuppressWarnings("deprecation")
    public HttpResponse deleteMultiFile(List<String> files, HttpServletResponse res, String s3Dir) {

        List<String> successFiles = new ArrayList<>();
        List<Integer> errorFiles = new ArrayList<>();

        for (String file : files) {
            try {
                byte[] bI = org.apache.commons.codec.binary.Base64.decodeBase64(file);
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(bI));

                File tmpFile = new File(System.getProperty("java.io.tmpdir") + "fileName" + ".png");

                ImageIO.write(img, "png", tmpFile);
                String s3Url = s3Handler.uploadOneFileNoRes(tmpFile, s3Dir, true);
                successFiles.add(s3Url);
            } catch (Exception e) {
                errorFiles.add(files.indexOf(file));
                e.printStackTrace();
            }

        }

        // prepare data for http response
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.createObjectNode();

        JsonNode node = mapper.valueToTree(successFiles);
        ((ObjectNode) rootNode).put("successFiles", node);

        JsonNode node1 = mapper.valueToTree(errorFiles);
        ((ObjectNode) rootNode).put("errorFiles", node1);

        return new SuccessHttpResponse(true, 200, "Uploaded files info", res, rootNode);
    }

    @SuppressWarnings("deprecation")
    public HttpResponse getAllFile(HttpServletResponse res, String s3Dir, int page, int limit) {
        try {
            Storage storage = s3Handler.getAllFile(s3Dir, page, limit);

            // prepare data for http response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();

            JsonNode node = mapper.valueToTree(storage.getTotalFiles());
            ((ObjectNode) rootNode).put("totalFiles", node);

            JsonNode node1 = mapper.valueToTree(storage.getFileUrls());
            ((ObjectNode) rootNode).put("files", node1);

            return new SuccessHttpResponse(true, 200, "Uploaded files in pailab" + s3Dir + "  info", res, rootNode);
        } catch (Exception e) {
            e.printStackTrace();
            HttpResponse response = new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
            return response;
        }
    }


    public Stream<Path> loadAll() {
        return null;
    }

    public Path load(String filename) {
        return null;
    }

    public Resource loadAsResource(String filename) {
        return null;

    }

    public void deleteOne(String objectKey, String s3Dir, HttpServletResponse res) {
        try {
            s3Handler.deleteOneFile(objectKey, s3Dir, res);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void deleteAll() {
    }
}
