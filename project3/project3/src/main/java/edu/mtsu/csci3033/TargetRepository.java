package edu.mtsu.csci3033;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TargetRepository extends CrudRepository<Targets, Long> {
    Targets findByTarget(String target);
    Targets findByWordIdAndLanguageId(long id, long languageId);
}

