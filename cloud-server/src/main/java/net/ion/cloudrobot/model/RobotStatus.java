package net.ion.cloudrobot.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import net.ion.cloudrobot.model.enums.StatusType;
import net.ion.mdk.jql.jpa.JQLRepositoryBase;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Data
public class RobotStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @NotNull
    private String robotId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private StatusType robotStatus;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Timestamp updateTime;

    @Repository
    public static class JQLRepository extends JQLRepositoryBase<RobotStatus, Long> {
        @Override
        public Long getEntityId(RobotStatus status) {
            return status.getSeq();
        }
    }
}
