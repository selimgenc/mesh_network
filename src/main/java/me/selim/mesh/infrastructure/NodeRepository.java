package me.selim.mesh.infrastructure;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import me.selim.mesh.domain.Node;

import java.util.List;
import java.util.Optional;

public interface NodeRepository {

    Node save(@NotNull Node node);

    List<Node> saveAll(@NotEmpty Iterable<Node> Node);

    Optional<Node> findById(@NotNull Long id);

    List<Node> findAll();

    boolean deleteById(@NotNull Long id);

}
