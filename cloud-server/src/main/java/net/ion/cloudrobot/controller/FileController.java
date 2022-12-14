package net.ion.cloudrobot.controller;

import net.ion.cloudrobot.model.File;
import net.ion.cloudrobot.model.dto.RoOTSFile;
import net.ion.cloudrobot.service.FileService;
import net.ion.mdk.jql.JQLController;
import net.ion.mdk.jql.JQLRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * File Api
 */
@RestController
@RequestMapping("/api/file")
public class FileController extends JQLController<File,Long> {

    private final FileService service;

    /**
     * @param jqlRepository
     * @param service
     */
    protected FileController(JQLRepository<File, Long> jqlRepository, FileService service) {
        super(jqlRepository);
        this.service = service;
    }

    /**
     * Save File
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/upload")
    public ResponseEntity<Object> saveNewFile(@ModelAttribute RoOTSFile file) throws IOException {
        Long res = service.newFile(file);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    /**
     * Download File By fileId
     * @param fileId
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<?> downLoadFile(@PathVariable Long fileId) throws IOException, URISyntaxException {
        return service.downloadFile(fileId);
    }

}
