package co.pailab.lime.repository;

import co.pailab.lime.model.Credential;
import co.pailab.lime.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    User findByActivationToken(String object);

    User findByUsername(String username);

    User findById(int id);

    List<User> findAllByOrderByIdDesc(Pageable pageable);

    @Query(value = "Select count(*) from user where group_id = :groupId", nativeQuery = true)
    Integer countAllByGroupId(@Param("groupId") int groupId);

    @Query(value = "Select count(*) from user", nativeQuery = true)
    Integer countAll();

    @Query(value = "SELECT * FROM user WHERE LOWER(username) LIKE CONCAT('%', :keyword, '%') OR LOWER(email) LIKE CONCAT('%', :keyword, '%') OR LOWER(first_name) LIKE CONCAT('%', :keyword, '%') OR LOWER(last_name) LIKE CONCAT('%', :keyword, '%') ", nativeQuery = true)
    List<User> findUserByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "SELECT count(*) FROM user WHERE LOWER(username) LIKE CONCAT('%', :keyword, '%') OR LOWER(email) LIKE CONCAT('%', :keyword, '%') OR LOWER(first_name) LIKE CONCAT('%', :keyword, '%') OR LOWER(last_name) LIKE CONCAT('%', :keyword, '%') ", nativeQuery = true)
    Integer countUserByKeyword(@Param("keyword") String keyword);

    @Transactional
    @Modifying
    void deleteById(int id);

    @Query("Select NEW User(a.id as id, a.username as user_name, a.email) from User a order by id desc ")
    List<User> findAllWithBasicInfo(Pageable pageable);

    @Query("Select New co.pailab.lime.model.Credential(u.email, u.password, u.activated) from User u where u.email = :email ")
    Credential findCredsByEmail(@Param("email") String email);

}