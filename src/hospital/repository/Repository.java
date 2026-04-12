package hospital.repository;

import hospital.model.Person;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Repository<T extends Person> {
    private final List<T> store = new ArrayList<>();

    public void add(T entity)               { store.add(entity); }
    public List<T> findAll()                { return List.copyOf(store); }
    public List<T> getAll()                 { return List.copyOf(store); }
    public boolean existsById(int id)       { return store.stream().anyMatch(e -> e.getId() == id); }

    public Optional<T> findById(int id) {
        return store.stream().filter(e -> e.getId() == id).findFirst();
    }

    public void remove(int id) {
        store.removeIf(e -> e.getId() == id);
    }
}
