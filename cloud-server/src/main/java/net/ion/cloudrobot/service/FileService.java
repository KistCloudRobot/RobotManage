package net.ion.cloudrobot.service;

import net.ion.cloudrobot.model.File;
import net.ion.cloudrobot.model.dto.RoOTSFile;
import net.ion.cloudrobot.repository.FileRepository;
import net.ion.cloudrobot.s3.AwsS3Utils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * File Service
 */
@Service
public class FileService {
    private final FileRepository repository;

    private final AwsS3Utils utils;

    public FileService(FileRepository repository, AwsS3Utils utils) {
        this.repository = repository;
        this.utils = utils;
    }

    /**
     * @param file
     * @return
     * @throws IOException
     */
    public Long newFile(RoOTSFile file) throws IOException {
        // aws s3 upload
        String link = utils.uploadFile("file", file.getFile());

        // file 저장
        return repository.save(file.toEntity(file.getFile().getOriginalFilename(), link)).getFileId();
    }

    /**
     * @param fId
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public ResponseEntity<?> downloadFile(Long fId) throws IOException, URISyntaxException {
        Optional<File> file = repository.findById(fId);
        InputStreamResource s3File = utils.download(file.get().getLink());

        if (s3File.exists()) {
            File f = file.get();
            f.setLastDownloaded(Timestamp.valueOf(LocalDateTime.now()));
            repository.save(f);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_OCTET_STREAM_VALUE));
        headers.add("Content-Disposition", "inline; filename=\"" +
                new String(file.get().getName().getBytes("UTF-8"), "ISO-8859-1") +
                "\"");
        headers.add("ETag", new String(file.get().getName().getBytes("UTF-8"), "ISO-8859-1"));

        return new ResponseEntity<>(s3File, headers, HttpStatus.OK);
    }

}
