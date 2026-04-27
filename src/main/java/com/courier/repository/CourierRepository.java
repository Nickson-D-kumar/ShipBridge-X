package com.courier.repository;

import com.courier.entity.CourierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourierRepository extends JpaRepository<CourierEntity, Integer> {
    List<CourierEntity> findBySenderUsername(String senderUsername);
    List<CourierEntity> findByEmpUsername(String empUsername);
}
