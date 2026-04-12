package hospital.repository;

import hospital.model.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Generic in-memory repository for any Person subtype.
 * Demonstrates: Generics, Optional, Streams.
 */
public class Repository<T extends Person> {
    private final List<T> store = new ArrayList<>();

    public void add(T entity) {
        store.add(entity);
    }

    public Optional<T> findById(int id) {
        return store.stream().filter(e -> e.getId() == id).findFirst();
    }

    public List<T> findAll() {
        return List.copyOf(store);
    }

    public boolean existsById(int id) {
        return store.stream().anyMatch(e -> e.getId() == id);
    }
}
