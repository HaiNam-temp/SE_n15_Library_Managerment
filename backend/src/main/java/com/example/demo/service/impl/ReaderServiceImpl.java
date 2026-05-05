package com.example.demo.service.impl;

import com.example.demo.domain.Account;
import com.example.demo.domain.Reader;
import com.example.demo.domain.LoanTicket;
import com.example.demo.domain.ActivityLog;
import com.example.demo.dto.ReaderRequest;
import com.example.demo.dto.ReaderResponse;
import com.example.demo.dto.ReaderDetailResponse;
import com.example.demo.dto.ReaderUpdateRequest;
import com.example.demo.dto.DeleteReaderResponse;
import com.example.demo.enums.AccountRole;
import com.example.demo.enums.AccountStatus;
import com.example.demo.exceptions.DuplicateResourceException;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.exceptions.BusinessException;
import com.example.demo.helper.MapperUtils;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.ReaderRepository;
import com.example.demo.repository.LoanTicketRepository;
import com.example.demo.repository.ActivityLogRepository;
import com.example.demo.service.ReaderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.stream.Collectors;
import java.util.List;
import com.example.demo.dto.ReaderPageResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReaderServiceImpl implements ReaderService {

    private final ReaderRepository readerRepository;
    private final AccountRepository accountRepository;
    private final LoanTicketRepository loanTicketRepository;
    private final ActivityLogRepository activityLogRepository;

    @Override
    @Transactional
    public ReaderResponse createReader(ReaderRequest request) {
        log.info("start createReader - studentCodeOrCitizenId={} email={}", request.getStudentCodeOrCitizenId(), request.getEmail());

        if (readerRepository.existsByEmail(request.getEmail())) {
            log.error("createReader failed - email already exists={}", request.getEmail());
            throw new DuplicateResourceException("email already exists");
        }

        if (readerRepository.existsByStudentCodeOrCitizenId(request.getStudentCodeOrCitizenId())) {
            log.error("createReader failed - studentCode already exists={}", request.getStudentCodeOrCitizenId());
            throw new DuplicateResourceException("studentCodeOrCitizenId already exists");
        }

        // create account
        Account account = new Account();
        // choose username as email
        account.setUsername(request.getEmail());
        account.setPassword("change-me");
        account.setRole(AccountRole.READER);
        account.setStatus(AccountStatus.ACTIVE);

        account = accountRepository.save(account);

        Reader reader = MapperUtils.toReaderEntity(request);
        reader.setAccount(account);

        Reader saved = readerRepository.save(reader);

        log.info("end createReader - readerId={}", saved.getId());
        return MapperUtils.toReaderResponse(saved);
    }

    @Override
    public ReaderPageResponse getReaders(int pageIndex, int pageSize) {
        log.info("start getReaders - pageIndex={} pageSize={}", pageIndex, pageSize);
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<Reader> page = readerRepository.findAll(pageable);

        ReaderPageResponse resp = new ReaderPageResponse();
        resp.setContent(page.getContent().stream().map(MapperUtils::toReaderResponse).collect(Collectors.toList()));
        resp.setTotalElements(page.getTotalElements());
        resp.setTotalPages(page.getTotalPages());
        resp.setPageIndex(page.getNumber());
        resp.setPageSize(page.getSize());

        log.info("end getReaders - returnedElements={} totalElements={}", resp.getContent().size(), resp.getTotalElements());
        return resp;
    }

    @Override
    @Transactional(readOnly = true)
    public ReaderDetailResponse getReaderDetail(Long readerId) {
        log.info("start getReaderDetail - readerId={}", readerId);
        
        Reader reader = readerRepository.findById(readerId)
                .orElseThrow(() -> new NotFoundException("Reader not found with id: " + readerId));
        
        List<LoanTicket> loans = loanTicketRepository.findByReaderOrderByBorrowDateDesc(reader);
        
        ReaderDetailResponse resp = MapperUtils.toReaderDetailResponse(reader, loans);
        log.info("end getReaderDetail - readerId={} loansCount={}", readerId, loans.size());
        return resp;
    }

    @Override
    @Transactional
    public ReaderResponse updateReader(Long readerId, ReaderUpdateRequest request) {
        log.info("start updateReader - readerId={} email={} studentCode={}", readerId, request.getEmail(), request.getStudentCodeOrCitizenId());
        
        Reader reader = readerRepository.findById(readerId)
                .orElseThrow(() -> new NotFoundException("Reader not found with id: " + readerId));
        
        // Check if email is being changed and if new email is unique
        if (!reader.getEmail().equals(request.getEmail()) && readerRepository.existsByEmail(request.getEmail())) {
            log.error("updateReader failed - email already exists={}", request.getEmail());
            throw new DuplicateResourceException("email already exists");
        }
        
        // Check if studentCode is being changed and if new code is unique
        if (!reader.getStudentCodeOrCitizenId().equals(request.getStudentCodeOrCitizenId()) && 
            readerRepository.existsByStudentCodeOrCitizenId(request.getStudentCodeOrCitizenId())) {
            log.error("updateReader failed - studentCode already exists={}", request.getStudentCodeOrCitizenId());
            throw new DuplicateResourceException("studentCodeOrCitizenId already exists");
        }
        
        // Update reader fields
        reader.setFullName(request.getFullName());
        reader.setEmail(request.getEmail());
        reader.setStudentCodeOrCitizenId(request.getStudentCodeOrCitizenId());
        reader.setPhone(request.getPhone());
        reader.setDateOfBirth(request.getDateOfBirth());
        reader.setGender(request.getGender());
        reader.setAddress(request.getAddress());
        reader.setCardStatus(request.getCardStatus());
        
        Reader saved = readerRepository.save(reader);
        log.info("end updateReader - readerId={}", readerId);
        return MapperUtils.toReaderResponse(saved);
    }

    @Override
    @Transactional
    public DeleteReaderResponse deleteReader(Long readerId) {
        log.info("start deleteReader - readerId={}", readerId);
        
        Reader reader = readerRepository.findById(readerId)
                .orElseThrow(() -> new NotFoundException("Reader not found with id: " + readerId));
        
        // Check if reader has borrowed books
        if (reader.getCurrentBorrowedCount() != null && reader.getCurrentBorrowedCount() > 0) {
            log.error("deleteReader failed - reader has borrowed books, readerId={} count={}", readerId, reader.getCurrentBorrowedCount());
            throw new BusinessException("Cannot delete reader with outstanding borrowed books");
        }
        
        // Soft delete: set account status to INACTIVE
        Account account = reader.getAccount();
        if (account != null) {
            account.setStatus(AccountStatus.INACTIVE);
            accountRepository.save(account);
        }
        
        // Log the deletion action
        ActivityLog log_entry = new ActivityLog();
        log_entry.setAction("DELETE_READER");
        log_entry.setActionTime(java.time.LocalDateTime.now());
        log_entry.setDescription("Deleted reader: " + reader.getFullName() + " (id: " + readerId + ")");
        if (account != null) {
            log_entry.setAccount(account);
        }
        activityLogRepository.save(log_entry);
        
        log.info("end deleteReader - readerId={}", readerId);
        
        DeleteReaderResponse resp = new DeleteReaderResponse();
        resp.setReaderId(readerId);
        resp.setMessage("Reader deleted successfully");
        return resp;
    }
}
