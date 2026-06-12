package ra.demo.service.impl;

import ra.demo.service.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

@Service
public class StorageServiceImpl implements StorageService {
    private final Path uploadDir;
    private final String publicBaseUrl;
    private static final Set<String> ALLOWED = Set.of("application/pdf", "image/png", "image/jpeg", "image/jpg");

    public StorageServiceImpl(@Value("${file.upload-dir}") String uploadDir, @Value("${file.public-base-url}") String publicBaseUrl) {
        this.uploadDir = Path.of(uploadDir);
        this.publicBaseUrl = publicBaseUrl;
    }

    @Override
    public String upload(MultipartFile file) {
        if (file.isEmpty()) throw new IllegalArgumentException("File không được để trống");
        if (!ALLOWED.contains(file.getContentType())) throw new IllegalArgumentException("Chỉ hỗ trợ PDF, PNG, JPG, JPEG");
        try {
            Files.createDirectories(uploadDir);
            String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "record" : file.getOriginalFilename());
            String storedName = UUID.randomUUID() + "-" + original.replaceAll("\\s+", "_");
            Files.copy(file.getInputStream(), uploadDir.resolve(storedName));
            return publicBaseUrl + "/" + storedName;
        } catch (IOException ex) {
            throw new IllegalStateException("Không thể upload hồ sơ bệnh án", ex);
        }
    }
}
