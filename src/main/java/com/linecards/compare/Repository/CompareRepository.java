package com.linecards.compare.Repository;

import com.linecards.compare.Entity.Compare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompareRepository extends JpaRepository<Compare, Integer> {
}
