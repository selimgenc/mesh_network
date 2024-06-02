package me.selim.mesh.infrastructure;

import me.selim.mesh.domain.Node;
import me.selim.mesh.service.IdGenerator;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.when;

class InMemoryNodeRepositoryTest {

    private static InMemoryNodeRepository repository;
    private static ApplicationEventPublisher publisher;
    private static IdGenerator<Long> idGenerator;

    @BeforeAll
    static void setUp() {
        idGenerator = Mockito.mock(IdGenerator.class);
        publisher = Mockito.mock(ApplicationEventPublisher.class);
        repository = new InMemoryNodeRepository(idGenerator, publisher);
    }

    @BeforeEach
    void setUpEach() {
        for (Node node : repository.findAll()) {
            repository.deleteById(node.getId());
        }
    }

    @Test
    @DisplayName("save() should save node and add id to it")
    void save_shouldSaveNode() {
        //given only with name
        when(idGenerator.next()).thenReturn(1L);
        Node node = new Node("Node1");
        //when
        Node saveNode = repository.save(node);
        //then
        Assertions.assertNotNull(saveNode.getId());
        Assertions.assertTrue(repository.findAll().contains(node));
    }


    @Test
    @DisplayName("saveAll() should save all nodes")
    void saveAll() {
        //given
        when(idGenerator.next()).thenReturn(new Random().nextLong());
        Node node1 = new Node("Node1");
        Node node2 = new Node("Node2");
        //when
        List<Node> savedNodes = repository.saveAll(List.of(node1, node2));
        //then
        for (Node savedNode : savedNodes) {
            Assertions.assertNotNull(savedNode.getId());
            Assertions.assertTrue(repository.findById(savedNode.getId()).isPresent());
        }
    }

    @Test
    @DisplayName("findById() should return node by id")
    void findById() {
        //given
        when(idGenerator.next()).thenReturn(1L);
        Node node = new Node("Node1");
        repository.save(node);
        //when
        Node foundNode = repository.findById(1L).get();
        //then
        Assertions.assertEquals(node, foundNode);
    }

    @Test
    @DisplayName("findAll() should return all nodes")
    void findAll() {
        //given
        when(idGenerator.next()).thenReturn(new Random().nextLong());
        Node node1 = new Node("Node1");
        Node node2 = new Node("Node2");
        repository.save(node1);
        repository.save(node2);
        //when
        List<Node> nodes = repository.findAll();
        //then
        Assertions.assertTrue(nodes.contains(node1));
        Assertions.assertTrue(nodes.contains(node2));
    }

    @Test
    @DisplayName("deleteById() should delete node by id")
    void deleteById() {
        //given
        when(idGenerator.next()).thenReturn(1L);
        Node node = new Node("Node1");
        repository.save(node);
        //when
        repository.deleteById(1L);
        //then
        Assertions.assertTrue(repository.findAll().isEmpty());

    }
}