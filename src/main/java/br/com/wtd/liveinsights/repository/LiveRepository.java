package br.com.wtd.liveinsights.repository;

import br.com.wtd.liveinsights.model.Live;
import br.com.wtd.liveinsights.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LiveRepository extends JpaRepository<Live, Long> {
    List<Live> findByUserId(Long userId);
    Optional<Live> findByLiveIdAndUserId(String liveId, Long userId);
    Optional<Live> findByLiveId(String liveId);
    long countByTag(Tag tag);

}

