package co.pailab.lime.helper.storage;

import co.pailab.lime.helper.ErrorHttpResponse;
import co.pailab.lime.helper.HttpResponse;
import co.pailab.lime.model.storage.Storage;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class S3Handler {
    private static String clientRegion = "ap-southeast-1";
    private static String bucketName = "pailab";

    private AmazonS3 s3Client;

    public S3Handler() {
        this.s3Client = AmazonS3ClientBuilder.standard().withRegion(clientRegion)
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance()).build();

    }

    public String uploadOneFileNoRes(File tmpFile, String s3Dir, boolean publicAccess) {
        String fileObjKeyName = new Date().getTime() + "_" + ".png";
        try {
            s3Client.putObject(bucketName + s3Dir, fileObjKeyName, tmpFile);
            if (publicAccess == true)
                s3Client.setObjectAcl(bucketName + s3Dir, fileObjKeyName, CannedAccessControlList.PublicRead);
            return s3Client.getUrl(bucketName + s3Dir, fileObjKeyName).toString();
        } catch (AmazonServiceException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Storage getAllFile(String s3Dir, int start, int limit) {
        List<URL> files = new ArrayList<>();
        ListObjectsV2Result objList = s3Client.listObjectsV2(new ListObjectsV2Request().
                withBucketName("pailab").withPrefix(s3Dir + "/").withDelimiter("/")
        );
        List<S3ObjectSummary> s3ObjectSummaries = objList.getObjectSummaries();
        int offset = start + limit + 1;
        for (S3ObjectSummary obj : s3ObjectSummaries) {
            if (s3ObjectSummaries.indexOf(obj) >= (start + 1) && s3ObjectSummaries.indexOf(obj) < offset) {
                URL objUrl = s3Client.getUrl(bucketName, obj.getKey());
                files.add(objUrl);
            }
        }
        Storage storage = new Storage();
        storage.setFileUrls(files);
        storage.setTotalFiles(s3ObjectSummaries.size() - 1);
        return storage;
    }

    public int countAllFile(String s3Dir) {
        ListObjectsV2Result objList = s3Client.listObjectsV2(new ListObjectsV2Request().
                withBucketName("pailab").withPrefix(s3Dir + "/").withDelimiter("/")
        );
        List<S3ObjectSummary> s3ObjectSummaries = objList.getObjectSummaries();

        return s3ObjectSummaries.size() - 1;
    }

    public String uploadOneFile(File tmpFile, String s3Dir, HttpServletResponse res, boolean publicAccess)
            throws IOException {
        String fileObjKeyName = new Date().getTime() + "_" + "avatar.png";
        try {

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/png");
            metadata.addUserMetadata("x-amz-meta-title", "someTitle");

            s3Client.putObject(bucketName + s3Dir, fileObjKeyName, tmpFile);
            if (publicAccess == true)
                s3Client.setObjectAcl(bucketName + s3Dir, fileObjKeyName, CannedAccessControlList.PublicRead);

            return s3Client.getUrl(bucketName + s3Dir, fileObjKeyName).toString();
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
            HttpResponse response = new ErrorHttpResponse(false, 500, "IMAGE_UPLOAD_ERROR", e.getErrorMessage(), res);
            ObjectMapper mapper = new ObjectMapper();
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            PrintWriter out = res.getWriter();
            String httpResInString = mapper.writeValueAsString(response);
            out.print(httpResInString);
            out.flush();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
            HttpResponse response = new ErrorHttpResponse(false, 500, "IMAGE_PROCESSING_ERROR",
                    "Image processing error", res);
            ObjectMapper mapper = new ObjectMapper();
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            PrintWriter out = res.getWriter();
            String httpResInString = mapper.writeValueAsString(response);
            out.print(httpResInString);
            out.flush();
        }

        return null;
    }

    public Boolean deleteOneFile(String objectKey, String s3Dir, HttpServletResponse res) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out;

        try {
            s3Client.deleteObject(new DeleteObjectRequest(bucketName + s3Dir, objectKey));
            return null;
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
            HttpResponse response = new ErrorHttpResponse(false, 500, "IMAGE_PROCESSING_ERROR",
                    "Image processing error", res);
            String httpResInString = mapper.writeValueAsString(response);
            out = res.getWriter();
            out.print(httpResInString);
            out.flush();
            return null;
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
            HttpResponse response = new ErrorHttpResponse(false, 500, "IMAGE_PROCESSING_ERROR",
                    "Image processing error", res);
            out = res.getWriter();
            String httpResInString = mapper.writeValueAsString(response);
            out.print(httpResInString);
            out.flush();
            return null;
        }
    }
}
