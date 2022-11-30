package net.ion.cloudrobot.model.dto;

import lombok.Data;
import net.ion.cloudrobot.model.File;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class RoOTSFile {

    private MultipartFile file;
    private String description;
    private String regUser;
    private String dispatcherId;

    public File toEntity(String name, String link) {
        return File.builder()
                .name(name)
                .link(link)
                .regUser(regUser)
                .dispatcherId(dispatcherId)
                .description(description).build();
    }
}
