package com.company.salestracker.util;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.company.salestracker.dto.response.PaginationResponse;
import com.company.salestracker.exception.ResourceNotFoundException;

public class PaginationUtil {

	 /**
     * Generic pagination method.
     *
     * @param <E>  Entity type
     * @param <D>  DTO type
     * @param repositoryRepo repository that extends PagingAndSortingRepository
     * @param pageNumber  current page (0-based)
     * @param pageSize    page size
     * @param sortBy      field to sort by
     * @param sortDir     "asc" or "desc"
     * @param mapper      function to convert entity -> DTO
     * @return PaginationResponse<D>
     */
	
    public static <E, D> PaginationResponse<D> getPaginated(
            PagingAndSortingRepository<E, ?> repositoryRepo,
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDir,
            Function<E, D> mapper
    ) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<E> page = repositoryRepo.findAll(pageable);
        List<D> dtoList = page.map(mapper).toList();

        if (dtoList.isEmpty()) {
            throw new ResourceNotFoundException("No records found");
        }

        return new PaginationResponse<>(
                dtoList,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}


