package co.pailab.lime.repository;

import co.pailab.lime.model.Group;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {

    Group findByName(String name);

    Group findById(int id);

    List<Group> findAllByOrderById(Pageable pageable);

    @Query(value = "Select count(*) from `group`", nativeQuery = true)
    Integer countAll();
}
