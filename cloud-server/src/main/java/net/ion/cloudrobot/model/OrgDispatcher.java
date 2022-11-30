package net.ion.cloudrobot.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import net.ion.mdk.jql.SecuredEntity;
import net.ion.mdk.jql.jpa.JQLRepositoryBase;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
public class OrgDispatcher {

    @Id
    private String dispatcherId;

    private String dispatcherName;

    private String description;

    @CreatedDate
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Timestamp regDate;

    @SecuredEntity.AuthorID
    private String regUser;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "orgDispatcher")
    private List<Robot> robots;


    @Repository
    public static class JQLRepository extends JQLRepositoryBase<OrgDispatcher, String> {

        @Override
        public String getEntityId(OrgDispatcher orgDispatcher) {
            return orgDispatcher.getDispatcherId();
        }
    }

}
