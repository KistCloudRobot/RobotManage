package net.ion.cloudrobot.repository;

import net.ion.cloudrobot.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository
 */
@Repository
public interface FileRepository extends JpaRepository<File, Long> {
}
