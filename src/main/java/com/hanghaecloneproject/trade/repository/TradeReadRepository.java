package com.hanghaecloneproject.trade.repository;

import com.hanghaecloneproject.trade.domain.Trade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeReadRepository extends JpaRepository<Trade, Long>, TradeReadRepositoryCustom {

    Page<Trade> findAllByIdLessThanAndTitleContainingAndAddressContains(Long lastId, String title, String address, Pageable pageable);
}
