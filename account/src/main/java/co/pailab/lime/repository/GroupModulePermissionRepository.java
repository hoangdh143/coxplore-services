package co.pailab.lime.repository;

import co.pailab.lime.model.GroupModulePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupModulePermissionRepository extends JpaRepository<GroupModulePermission, Long> {

    GroupModulePermission findByGroupIdAndModuleIdAndPermission(int groupId, int moduleId, String permission);

    List<GroupModulePermission> findByGroupIdAndModuleId(int groupId, int moduleId);

    List<GroupModulePermission> findByModuleId(int moduleId);

    GroupModulePermission findById(int id);

    void deleteByGroupId(int groupId);

    @Query(value = "SELECT module_id FROM group_module_permission where group_id = :groupId group by module_id", nativeQuery = true)
    List<Integer> listModuleOfAGroup(@Param("groupId") int groupId);
}
