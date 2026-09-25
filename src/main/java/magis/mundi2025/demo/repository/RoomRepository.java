package magis.mundi2025.demo.repository;

import magis.mundi2025.demo.model.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}