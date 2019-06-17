package co.pailab.lime.repository;

import co.pailab.lime.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModuleRepository extends JpaRepository<Module, Long> {

    Module findByName(String name);

    Module findById(int id);
}
