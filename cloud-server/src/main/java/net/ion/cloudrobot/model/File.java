package net.ion.cloudrobot.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import net.ion.mdk.jql.jpa.JQLRepositoryBase;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * File JPA Model
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@RequiredArgsConstructor
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    @NotNull
    private String name;

    private String description;

    @NotNull
    private String link;

    @NotNull
    private String regUser;

    @NotNull
    @CreatedDate
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    @Column(updatable = false)
    private Timestamp regDate;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Timestamp lastDownloaded;

    @NotNull
    @Column(columnDefinition = "boolean default false")
    private Boolean del = false;

    private String dispatcherId;

    @Builder
    public File(String name, String description, String link, String regUser, String dispatcherId, Timestamp lastDownloaded) {
        this.name = name;
        this.description = description;
        this.link = link;
        this.regUser = regUser;
        this.dispatcherId = dispatcherId;
        this.lastDownloaded = lastDownloaded;
    }

    @Repository
    public static class JQLRepository extends JQLRepositoryBase<File, Long> {
        @Override
        public Long getEntityId(File file) {
            return file.getFileId();
        }
    }

}
