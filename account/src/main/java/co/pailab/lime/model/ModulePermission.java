package co.pailab.lime.model;

import java.util.List;

public class ModulePermission {
    public String moduleName;
    public Integer moduleId;
    public String moduleDescription;
    public List<String> permissions;

    public ModulePermission() {
    }

    public ModulePermission(Module module, List<String> permissions) {
        this.moduleName = module.getName();
        this.moduleDescription = module.getDescription();
        this.moduleId = module.getId();
        this.permissions = permissions;
    }
}
