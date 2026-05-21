package com.qnocks.shorty.analytics_service.repository;

import com.qnocks.shorty.analytics_service.entity.ClickEventEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClickEntityRepository extends CrudRepository<ClickEventEntity, Long> {
}