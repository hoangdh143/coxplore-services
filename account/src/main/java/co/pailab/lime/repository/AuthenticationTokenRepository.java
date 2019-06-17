package co.pailab.lime.repository;

import co.pailab.lime.model.AuthenticationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import javax.transaction.Transactional;
import java.util.List;


public interface AuthenticationTokenRepository extends JpaRepository<AuthenticationToken, Long> {
    List<AuthenticationToken> findByUserId(int userId);

    AuthenticationToken findByUserIdAndDeviceToken(int userId, String deviceToken);

    @Modifying
    @Transactional
    void deleteByUserId(int userId);
}
