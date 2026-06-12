package ra.demo.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ra.demo.exception.FileStorageException;
import ra.demo.service.CloudStorageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class CloudStorageServiceImpl implements CloudStorageService {
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf", "image/png", "image/jpeg", "image/jpg"
    );

    private final Cloudinary cloudinary;
    private final boolean cloudinaryEnabled;
    private final String folder;
    private final Path localUploadDir;
    private final String localPublicBaseUrl;

    public CloudStorageServiceImpl(
            @Value("${cloudinary.cloud-name:}") String cloudName,
            @Value("${cloudinary.api-key:}") String apiKey,
            @Value("${cloudinary.api-secret:}") String apiSecret,
            @Value("${cloudinary.folder:hospital-management/medical-records}") String folder,
            @Value("${file.upload-dir:uploads/medical-records}") String localUploadDir,
            @Value("${file.public-base-url:http://localhost:8080/files/medical-records}") String localPublicBaseUrl) {
        this.folder = folder;
        this.localUploadDir = Path.of(localUploadDir);
        this.localPublicBaseUrl = localPublicBaseUrl;
        this.cloudinaryEnabled = StringUtils.hasText(cloudName)
                && StringUtils.hasText(apiKey)
                && StringUtils.hasText(apiSecret);
        this.cloudinary = cloudinaryEnabled ? new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        )) : null;
    }

    @Override
    public String uploadMedicalRecord(MultipartFile file) {
        validate(file);
        if (cloudinaryEnabled) {
            return uploadToCloudinary(file);
        }
        return uploadToLocalStorage(file);
    }

    private String uploadToCloudinary(MultipartFile file) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", "auto"
            ));
            Object secureUrl = result.get("secure_url");
            if (secureUrl == null || secureUrl.toString().isBlank()) {
                throw new FileStorageException("Cloudinary did not return secure_url");
            }
            log.info("Uploaded medical record to Cloudinary: public_id={}", result.get("public_id"));
            return secureUrl.toString();
        } catch (IOException e) {
            throw new FileStorageException("Cannot upload medical record file to Cloudinary");
        } catch (RuntimeException e) {
            throw new FileStorageException("Cloudinary upload failed: " + e.getMessage());
        }
    }

    private String uploadToLocalStorage(MultipartFile file) {
        try {
            Files.createDirectories(localUploadDir);
            String originalName = StringUtils.cleanPath(
                    file.getOriginalFilename() == null ? "medical-record" : file.getOriginalFilename()
            );
            String storedName = UUID.randomUUID() + "-" + originalName.replaceAll("\\s+", "_");
            Files.copy(file.getInputStream(), localUploadDir.resolve(storedName), StandardCopyOption.REPLACE_EXISTING);
            log.info("Cloudinary credentials not configured. Stored medical record locally: {}", storedName);
            return localPublicBaseUrl + "/" + storedName;
        } catch (IOException e) {
            throw new FileStorageException("Cannot store medical record file locally");
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new FileStorageException("File is required");
        if (file.getSize() > 10 * 1024 * 1024) throw new FileStorageException("File exceeds 10MB limit");
        String contentType = file.getContentType();
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new FileStorageException("Only PDF, PNG, JPG or JPEG medical records are allowed");
        }
    }
}
