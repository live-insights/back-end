package br.com.wtd.liveinsights.repository;

import br.com.wtd.liveinsights.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
    Optional<Tag> findByNameIgnoreCase(String name);

}