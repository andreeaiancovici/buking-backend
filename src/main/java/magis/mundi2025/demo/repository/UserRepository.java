package magis.mundi2025.demo.repository;
import magis.mundi2025.demo.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<User, Long> {
}
