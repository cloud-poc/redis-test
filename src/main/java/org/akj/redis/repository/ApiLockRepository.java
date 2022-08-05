package org.akj.redis.repository;

import org.akj.redis.entity.ApiLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiLockRepository extends JpaRepository<ApiLock, String> {
}
