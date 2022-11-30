package net.ion.mdk.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import net.ion.mdk.jql.jpa.JQLRepositoryBase;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@MappedSuperclass
@DynamicInsert
public abstract class CyclicTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = READ_ONLY)
    @Getter
    Long id;

    @Schema(description = "Task 반복 실행 주기 (분 단위).\n 0인 경우, 매일 1회 수행.", example = "ex) 0")
    @Getter @Setter
    int intervalMinute;

    @Column(insertable = false)
    @Schema(accessMode = READ_ONLY)
    @Getter @Setter
    LocalDateTime lastExecutionTs;

    @JsonIgnore
    public Duration calcInterval() {
        return Duration.ofMinutes(intervalMinute);
    }

    @Schema(accessMode = READ_ONLY)
    public boolean isDailyTask() {
        return intervalMinute <= 0;
    }

    public static abstract class JQLRepository<TASK extends CyclicTask>
            extends JQLRepositoryBase<TASK, Long> {

        @Override
        public TASK insert(TASK entity) {
            // validation 을 위해 호출.
            entity.calcInterval();
            return super.insert(entity);
        }
    }
}
