package com.example.demo.service;

import com.example.demo.domain.BorrowingRule;
import com.example.demo.domain.Reader;
import com.example.demo.dto.ReaderDto;
import com.example.demo.repository.BorrowingRuleRepository;
import com.example.demo.repository.ReaderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ReaderService extends BaseService {

    private final ReaderRepository readerRepository;
    private final BorrowingRuleRepository borrowingRuleRepository;

    public ReaderService(ReaderRepository readerRepository, BorrowingRuleRepository borrowingRuleRepository) {
        this.readerRepository = readerRepository;
        this.borrowingRuleRepository = borrowingRuleRepository;
    }

    public List<ReaderDto> findAll() {
        return readerRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public ReaderDto findById(Long id) {
        return toDto(findReader(id));
    }

    public ReaderDto create(ReaderDto readerDto) {
        Reader reader = new Reader();
        applyDto(reader, readerDto);
        return toDto(readerRepository.save(reader));
    }

    public ReaderDto update(Long id, ReaderDto readerDto) {
        Reader reader = findReader(id);
        applyDto(reader, readerDto);
        return toDto(readerRepository.save(reader));
    }

    public void delete(Long id) {
        Reader reader = findReader(id);
        readerRepository.delete(reader);
    }

    private Reader findReader(Long id) {
        return readerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reader not found"));
    }

    private void applyDto(Reader reader, ReaderDto readerDto) {
        reader.setFullName(readerDto.getFullName());
        reader.setDateOfBirth(readerDto.getDateOfBirth());
        reader.setGender(readerDto.getGender());
        reader.setAddress(readerDto.getAddress());
        reader.setEmail(readerDto.getEmail());
        reader.setCardCreatedDate(readerDto.getCardCreatedDate());
        reader.setCardExpiredDate(readerDto.getCardExpiredDate());
        reader.setCardStatus(readerDto.getCardStatus());
        reader.setCurrentBorrowedCount(
                readerDto.getCurrentBorrowedCount() == null ? 0 : readerDto.getCurrentBorrowedCount()
        );
        reader.setPhone(readerDto.getPhone());
        reader.setStudentCodeOrCitizenId(readerDto.getStudentCodeOrCitizenId());
        reader.setBorrowingRule(findBorrowingRule(readerDto.getBorrowingRuleId()));
    }

    private BorrowingRule findBorrowingRule(Long borrowingRuleId) {
        if (borrowingRuleId == null) {
            return null;
        }

        return borrowingRuleRepository.findById(borrowingRuleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Borrowing rule not found"));
    }

    private ReaderDto toDto(Reader reader) {
        ReaderDto readerDto = new ReaderDto();
        readerDto.setId(reader.getId());
        readerDto.setFullName(reader.getFullName());
        readerDto.setDateOfBirth(reader.getDateOfBirth());
        readerDto.setGender(reader.getGender());
        readerDto.setAddress(reader.getAddress());
        readerDto.setEmail(reader.getEmail());
        readerDto.setCardCreatedDate(reader.getCardCreatedDate());
        readerDto.setCardExpiredDate(reader.getCardExpiredDate());
        readerDto.setCardStatus(reader.getCardStatus());
        readerDto.setCurrentBorrowedCount(reader.getCurrentBorrowedCount());
        readerDto.setPhone(reader.getPhone());
        readerDto.setStudentCodeOrCitizenId(reader.getStudentCodeOrCitizenId());
        if (reader.getBorrowingRule() != null) {
            readerDto.setBorrowingRuleId(reader.getBorrowingRule().getRuleId());
        }
        return readerDto;
    }
}
