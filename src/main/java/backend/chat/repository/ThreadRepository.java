package backend.chat.repository;

import backend.domain.Member;
import backend.domain.Thread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ThreadRepository extends JpaRepository<Thread, Long>{

    @Query("SELECT t FROM Thread t WHERE t.member = :member AND t.isActive = true")
    Optional<Thread> findActiveThread(Member member);

    @Query("SELECT t FROM Thread t WHERE t.member = :member order by t.createdTime ")
    List<Thread> findAllByMember(Member member);

}
