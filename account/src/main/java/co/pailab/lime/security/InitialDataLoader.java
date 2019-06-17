package co.pailab.lime.security;

import co.pailab.lime.helper.HttpRequest.HttpRequest;
import co.pailab.lime.model.*;
import co.pailab.lime.model.Module;
import co.pailab.lime.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;


    @Value("${numberGenUserName}")
    private String numberGenUserName;

    @Value("${userNamePrefix}")
    private String userNamePrefix;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private GroupModulePermissionRepository groupModulePermissionRepository;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            if (alreadySetup)
                return;
            //create module list
            Module module_group = createModuleIfNotFound("module_group");
            Module module_user = createModuleIfNotFound("module_user");
            Module module_module = createModuleIfNotFound("module_module");
            Module module_bmi_type = createModuleIfNotFound("module_bmi_type");
            Module module_career = createModuleIfNotFound("module_career");
            Module module_career_mbti = createModuleIfNotFound("module_career_mbti");
            Module module_career_sector = createModuleIfNotFound("module_career_sector");
            Module module_career_skill = createModuleIfNotFound("module_career_skill");
            Module module_company = createModuleIfNotFound("module_company");
            Module module_course = createModuleIfNotFound("module_course");
            Module module_course_skill = createModuleIfNotFound("module_course_skill");
            Module module_module_job_offer = createModuleIfNotFound("module_module_job_offer");
            Module module_education_major = createModuleIfNotFound("module_education_major");
            Module module_permission = createModuleIfNotFound("module_permission");
            Module module_job = createModuleIfNotFound("module_job");
            Module module_job_location = createModuleIfNotFound("module_job_location");
            Module module_job_offer = createModuleIfNotFound("module_job_offer");
            Module module_major_university = createModuleIfNotFound("module_major_university");
            Module module_mbti_famous_people = createModuleIfNotFound("module_mbti_famous_people");
            Module module_mtbi_strength_weakness = createModuleIfNotFound("module_mtbi_strength_weakness");
            Module module_mbti_type = createModuleIfNotFound("module_mbti_type");
            Module module_question = createModuleIfNotFound("module_question");
            Module module_skill = createModuleIfNotFound("module_skill");
            Module module_skill_answer = createModuleIfNotFound("module_skill_answer");
            Module module_skill_question = createModuleIfNotFound("module_skill_question");
            Module module_skill_test_career = createModuleIfNotFound("module_skill_test_career");
            Module module_test = createModuleIfNotFound("module_test");
            Module module_university = createModuleIfNotFound("module_university");
            Module module_expert_service = createModuleIfNotFound("module_expert_service");
            Module module_user_expert_profile = createModuleIfNotFound("module_user_expert_profile");
            Module module_user_expert_career_sector = createModuleIfNotFound("module_user_expert_career_sector");

            Group groupAdmin = createGroupIfNotFound("admin");
            Group groupUser = createGroupIfNotFound("user");
            Group groupExpert = createGroupIfNotFound("expert");
            Group groupRecruiter = createGroupIfNotFound("recruiter");
            Group groupExpertRecruiter = createGroupIfNotFound("expert & recruiter");

            //create admin user
            User user = null;
            user = createUserIfNotFound("vitae.admin@gmail.com", "vitae.admin.2019", groupAdmin);

            List<GroupModulePermission> groupModulePermissions = new ArrayList<>();

            //create groupModulePermission for groupExpert
            groupModulePermissions.add(new GroupModulePermission(groupExpert, module_expert_service, "read"));
            groupModulePermissions.add(new GroupModulePermission(groupExpert, module_expert_service, "create"));
            groupModulePermissions.add(new GroupModulePermission(groupExpert, module_expert_service, "update"));
            groupModulePermissions.add(new GroupModulePermission(groupExpert, module_expert_service, "delete"));

            groupModulePermissions.add(new GroupModulePermission(groupExpert, module_user_expert_profile, "read"));
            groupModulePermissions.add(new GroupModulePermission(groupExpert, module_user_expert_profile, "create"));
            groupModulePermissions.add(new GroupModulePermission(groupExpert, module_user_expert_profile, "update"));
            groupModulePermissions.add(new GroupModulePermission(groupExpert, module_user_expert_profile, "delete"));

            groupModulePermissions.add(new GroupModulePermission(groupExpert, module_user_expert_career_sector, "read"));
            groupModulePermissions.add(new GroupModulePermission(groupExpert, module_user_expert_career_sector, "create"));
            groupModulePermissions.add(new GroupModulePermission(groupExpert, module_user_expert_career_sector, "update"));
            groupModulePermissions.add(new GroupModulePermission(groupExpert, module_user_expert_career_sector, "delete"));

            //create groupModulePermission for groupRecruiter
            groupModulePermissions.add(new GroupModulePermission(groupRecruiter, module_job_offer, "read"));
            groupModulePermissions.add(new GroupModulePermission(groupRecruiter, module_job_offer, "create"));
            groupModulePermissions.add(new GroupModulePermission(groupRecruiter, module_job_offer, "update"));
            groupModulePermissions.add(new GroupModulePermission(groupRecruiter, module_job_offer, "delete"));

            groupModulePermissions.add(new GroupModulePermission(groupRecruiter, module_company, "read"));
            groupModulePermissions.add(new GroupModulePermission(groupRecruiter, module_company, "create"));
            groupModulePermissions.add(new GroupModulePermission(groupRecruiter, module_company, "update"));
            groupModulePermissions.add(new GroupModulePermission(groupRecruiter, module_company, "delete"));

            //create groupModulePermissions for groupExpertRecruiter
            groupModulePermissions.add(new GroupModulePermission(groupExpertRecruiter, module_expert_service, "read"));
            groupModulePermissions.add(new GroupModulePermission(groupExpertRecruiter, module_expert_service, "create"));
            groupModulePermissions.add(new GroupModulePermission(groupExpertRecruiter, module_expert_service, "update"));
            groupModulePermissions.add(new GroupModulePermission(groupExpertRecruiter, module_expert_service, "delete"));

            groupModulePermissions.add(new GroupModulePermission(groupExpertRecruiter, module_user_expert_profile, "read"));
            groupModulePermissions.add(new GroupModulePermission(groupExpertRecruiter, module_user_expert_profile, "create"));
            groupModulePermissions.add(new GroupModulePermission(groupExpertRecruiter, module_user_expert_profile, "update"));
            groupModulePermissions.add(new GroupModulePermission(groupExpertRecruiter, module_user_expert_profile, "delete"));

            groupModulePermissions.add(new GroupModulePermission(groupExpertRecruiter, module_user_expert_career_sector, "read"));
            groupModulePermissions.add(new GroupModulePermission(groupExpertRecruiter, module_user_expert_career_sector, "create"));
            groupModulePermissions.add(new GroupModulePermission(groupExpertRecruiter, module_user_expert_career_sector, "update"));
            groupModulePermissions.add(new GroupModulePermission(groupExpertRecruiter, module_user_expert_career_sector, "delete"));

            groupModulePermissions.add(new GroupModulePermission(groupExpertRecruiter, module_job_offer, "read"));
            groupModulePermissions.add(new GroupModulePermission(groupExpertRecruiter, module_job_offer, "create"));
            groupModulePermissions.add(new GroupModulePermission(groupExpertRecruiter, module_job_offer, "update"));
            groupModulePermissions.add(new GroupModulePermission(groupExpertRecruiter, module_job_offer, "delete"));

            groupModulePermissions.add(new GroupModulePermission(groupExpertRecruiter, module_company, "read"));
            groupModulePermissions.add(new GroupModulePermission(groupExpertRecruiter, module_company, "create"));
            groupModulePermissions.add(new GroupModulePermission(groupExpertRecruiter, module_company, "update"));
            groupModulePermissions.add(new GroupModulePermission(groupExpertRecruiter, module_company, "delete"));

            createMultiGroupModulePermissionIfNotFound(groupModulePermissions);

            alreadySetup = true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Transactional
    Group createGroupIfNotFound(String name) {
        Group group = groupRepository.findByName(name);
        if (group == null) {
            group = new Group(name);
            groupRepository.save(group);
        }
        return group;
    }

    @Transactional
    User createUserIfNotFound(String email, String password, Group group) throws IOException, JSONException {

        User user = userRepository.findByEmail(email);
        String defaultUserName = userNamePrefix + (Integer.parseInt(numberGenUserName) + 1);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setPassword(bCryptPasswordEncoder.encode(password));
            user.setGroup(group);
            user.setActivated(1);
            user.setGender(2);
            user.setUsername(defaultUserName);

            userRepository.save(user);

        }
        return user;
    }

    @Transactional
    Module createModuleIfNotFound(String name) {

        Module module = moduleRepository.findByName(name);
        if (module == null) {
            module = new Module(name);
            moduleRepository.save(module);
        }
        return module;
    }

    @Transactional
    void createGroupModulePermissionIfNotFound(Group group, Module module) {

        GroupModulePermission groupModulePermissionCreate = groupModulePermissionRepository
                .findByGroupIdAndModuleIdAndPermission(group.getId(), module.getId(), "create");
        if (groupModulePermissionCreate == null) {
            groupModulePermissionCreate = new GroupModulePermission(group, module, "create");
            groupModulePermissionRepository.save(groupModulePermissionCreate);
        }

        GroupModulePermission groupModulePermissionRead = groupModulePermissionRepository
                .findByGroupIdAndModuleIdAndPermission(group.getId(), module.getId(), "read");
        if (groupModulePermissionRead == null) {
            groupModulePermissionRead = new GroupModulePermission(group, module, "read");
            groupModulePermissionRepository.save(groupModulePermissionRead);
        }

        GroupModulePermission groupModulePermissionUpdate = groupModulePermissionRepository
                .findByGroupIdAndModuleIdAndPermission(group.getId(), module.getId(), "update");
        if (groupModulePermissionUpdate == null) {
            groupModulePermissionUpdate = new GroupModulePermission(group, module, "update");
            groupModulePermissionRepository.save(groupModulePermissionUpdate);
        }

        GroupModulePermission groupModulePermissionDelete = groupModulePermissionRepository
                .findByGroupIdAndModuleIdAndPermission(group.getId(), module.getId(), "delete");
        if (groupModulePermissionDelete == null) {
            groupModulePermissionDelete = new GroupModulePermission(group, module, "delete");
            groupModulePermissionRepository.save(groupModulePermissionDelete);
        }

    }

    @Transactional
    void createMultiGroupModulePermissionIfNotFound(List<GroupModulePermission> groupModulePermissions) {

        List<GroupModulePermission> groupModulePermissionFilters = groupModulePermissions.stream().map(groupModulePermission -> {
            GroupModulePermission groupModulePermissionCreate = groupModulePermissionRepository
                    .findByGroupIdAndModuleIdAndPermission(groupModulePermission.getGroup().getId(), groupModulePermission.getModule().getId(), groupModulePermission.getPermission());
            if(groupModulePermissionCreate != null) return null;
            return groupModulePermission;
        }).collect(Collectors.toList());

        groupModulePermissionFilters.removeAll(Collections.singleton(null));

        groupModulePermissionRepository.saveAll(groupModulePermissionFilters);

    }

}