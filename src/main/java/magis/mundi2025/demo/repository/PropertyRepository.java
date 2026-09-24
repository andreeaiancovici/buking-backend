package magis.mundi2025.demo.repository;

import magis.mundi2025.demo.model.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    @Query("SELECT DISTINCT p FROM Property p LEFT JOIN p.rooms r WHERE " +
            "(:city IS NULL OR LOWER(p.address) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
            "(:minRating IS NULL OR p.starRating >= :minRating) AND " +
            "(:minPrice IS NULL OR r.pricePerNight >= :minPrice) AND " +
            "(:maxPrice IS NULL OR r.pricePerNight <= :maxPrice) AND " +
            "(:minCapacity IS NULL OR r.capacity >= :minCapacity)")
    List<Property> search(@Param("city") String city,
                          @Param("minRating") Integer minRating,
                          @Param("minPrice") BigDecimal minPrice,
                          @Param("maxPrice") BigDecimal maxPrice,
                          @Param("minCapacity") Integer minCapacity);
}