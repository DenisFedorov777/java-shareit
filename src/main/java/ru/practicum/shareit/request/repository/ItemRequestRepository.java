package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("SELECT ir " +
            "FROM ItemRequest ir " +
            "WHERE ir.requestor.id = ?1 " +
            "ORDER BY ir.created DESC")
    List<ItemRequest> findByRequestorId(Long requestorId);

    @Query("SELECT ir " +
            "FROM ItemRequest ir " +
            "WHERE ir.requestor.id <> :requestorId " +
            "ORDER BY ir.created DESC")
    Page<ItemRequest> findItemRequestsByExcludingRequestorId(
            @Param("requestorId") Long requestorId, Pageable pageable);

    @Query("SELECT ir " +
            "FROM ItemRequest ir " +
            "WHERE ir.requestor.id <> :requestorId " +
            "ORDER BY ir.created DESC")
    List<ItemRequest> findItemRequestsByExcludingRequestorId(
            @Param("requestorId") Long requestorId);
}