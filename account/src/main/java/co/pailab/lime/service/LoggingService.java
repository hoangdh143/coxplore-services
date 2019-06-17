package co.pailab.lime.service;

import co.pailab.lime.helper.ErrorHttpResponse;
import co.pailab.lime.helper.HttpResponse;
import co.pailab.lime.helper.ResponseBody;
import co.pailab.lime.helper.SuccessHttpResponse;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoggingService {
    private String logFilePath = "logs/app.log";

    @SuppressWarnings("deprecation")
    public HttpResponse getLast1000Lines(HttpServletResponse res) {
        try {
            List<String> list = getLastLines(1000);
            ResponseBody responseBody = new ResponseBody();
            responseBody.put("logs", list);
            return new SuccessHttpResponse(true, 200, "Recent logs", res,
                    responseBody.get());
        } catch (IOException e) {
            e.printStackTrace();
            return new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
        }
    }

    @SuppressWarnings("deprecation")
    public HttpResponse getRequestResponseOnly(HttpServletResponse res) {
        try {
            List<String> list = getLastLines(2000).stream().filter(this::logFilter).collect(Collectors.toList());
            ResponseBody responseBody = new ResponseBody();
            responseBody.put("logs", list);
            return new SuccessHttpResponse(true, 200, "Recent logs", res,
                    responseBody.get());
        } catch (IOException e) {
            e.printStackTrace();
            return new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
        }
    }

    @SuppressWarnings("deprecation")
    public ResponseEntity<Resource> getFullLogs() throws IOException {
        Path path = Paths.get(logFilePath);
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.TEXT_PLAIN);
        httpHeaders.setContentDispositionFormData("attachment", "app.log");

        return ResponseEntity.ok()
                .headers(httpHeaders)
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource);
    }

    private boolean logFilter(String p) {
        return p.contains("CustomRequestLoggingFilter") || p.contains("CustomLogger") || p.contains("WARN");
    }

    @SuppressWarnings("deprecation")
    private List<String> getLastLines(int numberOfLines) throws IOException {
        ReversedLinesFileReader fileReader = new ReversedLinesFileReader(new File(logFilePath));
        List<String> list = new ArrayList<>();
        int i = 0;
        while (i < numberOfLines) {
            list.add(fileReader.readLine());
            i++;
        }
        Collections.reverse(list);
        return list;
    }
}
