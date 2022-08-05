package org.akj.redis.service;

import lombok.extern.slf4j.Slf4j;
import org.akj.redis.entity.ApiLock;
import org.akj.redis.entity.LockStatus;
import org.akj.redis.repository.ApiLockRepository;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ApiLockService {

    private final ApiLockRepository apiLockRepository;

    public ApiLockService(ApiLockRepository apiLockRepository) {
        this.apiLockRepository = apiLockRepository;
    }

    public ApiLock tryLock(ApiLock apiLock) {
        log.debug("Inserting api lock.");
        return apiLockRepository.saveAndFlush(apiLock);
    }

    public void unlock(ApiLock apiLock) {
        Optional<ApiLock> lockOptional = apiLockRepository.findOne(Example.of(apiLock));

        if (lockOptional.isPresent()) {
            log.debug("Updating api lock status to completed.");
            apiLockRepository.delete(lockOptional.get());
        }
    }

    public void update(ApiLock apiLock) {
        if (null != apiLock) {
            apiLockRepository.save(apiLock);
        }
    }
}
