package shareMe.ShareMe.controller;

import shareMe.ShareMe.model.FileInfo;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

@RestController
public class FileController {

    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();

            File savedFile = new File(UPLOAD_DIR + file.getOriginalFilename());
            file.transferTo(savedFile);

            return "File uploaded successfully: " + file.getOriginalFilename();
        } catch (Exception e) {
            return "Upload failed: " + e.getMessage();
        }
    }


    @GetMapping("/files")
    public List<FileInfo> getFiles() {
        File folder = new File(UPLOAD_DIR);

        if (!folder.exists()) return new ArrayList<>();

        File[] files = folder.listFiles();

        List<FileInfo> fileList = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                fileList.add(new FileInfo(file.getName(), file.length()));
            }
        }
        return fileList;
    }

    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) throws IOException {

        File file = new File("uploads/" + filename);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .contentLength(file.length())
                .body(resource);
    }

    public String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "localhost";
        }
    }

    @GetMapping("/server-url")
    public String getServerUrl() {
        String ip = getLocalIp();
        return "http://" + ip + ":8080";
    }

    @GetMapping(value = "/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] generateQR() throws Exception {

        String url = getServerUrl();

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix matrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 250, 250);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", pngOutputStream);

        return pngOutputStream.toByteArray();
    }

    @DeleteMapping("/delete/{filename:.+}")
    public String deleteFile(@PathVariable String filename) {
        File file = new File(UPLOAD_DIR + filename);

        if (file.exists()) {
            file.delete();
            return "Deleted";
        }
        return "File not found";
    }

    @GetMapping("/test")
    public String testt(){
        return "after deploy";
    }

}