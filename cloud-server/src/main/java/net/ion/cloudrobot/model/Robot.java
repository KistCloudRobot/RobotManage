package net.ion.cloudrobot.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import net.ion.cloudrobot.model.enums.RobotType;
import net.ion.cloudrobot.model.enums.StatusType;
import net.ion.mdk.jql.SecuredEntity;
import net.ion.mdk.jql.jpa.JQLRepositoryBase;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Robot JPA Model
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
public class Robot {

    @Id
    private String robotId;

    @NotNull
    private String robotName;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RobotType robotType;

    @NotNull
    private String robotPlace;

    @Enumerated(EnumType.STRING)
    private StatusType robotStatus;

    @SecuredEntity.AuthorID
    private String regUser;

    @NotNull
    @CreatedDate
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    @Column(updatable = false)
    private Timestamp regDate;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Timestamp updateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatcherId")
    private OrgDispatcher orgDispatcher;

    @Repository
    public static class JQLRepository extends JQLRepositoryBase<Robot, String> {
        @Override
        public String getEntityId(Robot robot) {
            return robot.getRobotId();
        }
    }
}
