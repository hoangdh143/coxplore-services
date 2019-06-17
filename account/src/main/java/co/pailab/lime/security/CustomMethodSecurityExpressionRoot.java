package co.pailab.lime.security;

import co.pailab.lime.model.Group;
import co.pailab.lime.model.GroupModulePermission;
import co.pailab.lime.model.Module;
import co.pailab.lime.model.User;
import co.pailab.lime.repository.GroupModulePermissionRepository;
import co.pailab.lime.repository.ModuleRepository;
import co.pailab.lime.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot
        implements MethodSecurityExpressionOperations {
    private UserRepository userRepository;
    private GroupModulePermissionRepository groupModulePermissionRepository;
    private ModuleRepository moduleRepository;

    @Autowired
    public CustomMethodSecurityExpressionRoot(Authentication authentication, UserRepository userRepository,
                                              GroupModulePermissionRepository groupModulePermissionRepository, ModuleRepository moduleRepository) {
        super(authentication);
        this.userRepository = userRepository;
        this.groupModulePermissionRepository = groupModulePermissionRepository;
        this.moduleRepository = moduleRepository;
    }

    public boolean hasAccessRight(String moduleName, String permission) {
        User user = userRepository.findById(Integer.parseInt((String) this.getPrincipal()));

        Group group = user.getGroup();
        if (group == null)
            throw new AccessDeniedException("cannot find group");

        if (group.getName().equals("admin"))
            return true;

        Module module = moduleRepository.findByName(moduleName);
        if (module == null)
            throw new AccessDeniedException("cannot find module");

        GroupModulePermission groupModulePermissionExist = groupModulePermissionRepository
                .findByGroupIdAndModuleIdAndPermission(group.getId(), module.getId(), permission);
        if (groupModulePermissionExist == null)
            throw new AccessDeniedException("no permission");

        return true;
    }

    @Override
    public Object getFilterObject() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setFilterObject(Object filterObject) {
        // TODO Auto-generated method stub

    }

    @Override
    public Object getReturnObject() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setReturnObject(Object returnObject) {
        // TODO Auto-generated method stub

    }

    @Override
    public Object getThis() {
        // TODO Auto-generated method stub
        return null;
    }
}
