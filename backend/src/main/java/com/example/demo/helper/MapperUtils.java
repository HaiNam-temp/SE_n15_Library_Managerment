package com.example.demo.helper;

import com.example.demo.domain.Reader;
import com.example.demo.domain.LoanTicket;
import com.example.demo.dto.ReaderRequest;
import com.example.demo.dto.ReaderResponse;
import com.example.demo.dto.ReaderDetailResponse;
import com.example.demo.dto.LoanTicketResponse;
import java.util.List;
import java.util.stream.Collectors;

public final class MapperUtils {
    private MapperUtils() {}

    public static Reader toReaderEntity(ReaderRequest req) {
        Reader r = new Reader();
        r.setFullName(req.getFullName());
        r.setEmail(req.getEmail());
        r.setStudentCodeOrCitizenId(req.getStudentCodeOrCitizenId());
        r.setPhone(req.getPhone());
        r.setDateOfBirth(req.getDateOfBirth());
        r.setAddress(req.getAddress());
        return r;
    }

    public static ReaderResponse toReaderResponse(Reader r) {
        if (r == null) return null;
        ReaderResponse resp = new ReaderResponse();
        resp.setId(r.getId());
        resp.setFullName(r.getFullName());
        resp.setEmail(r.getEmail());
        resp.setStudentCodeOrCitizenId(r.getStudentCodeOrCitizenId());
        resp.setPhone(r.getPhone());
        resp.setDateOfBirth(r.getDateOfBirth());
        if (r.getAccount() != null) {
            resp.setAccountRole(r.getAccount().getRole() == null ? null : r.getAccount().getRole().name());
            resp.setAccountStatus(r.getAccount().getStatus() == null ? null : r.getAccount().getStatus().name());
        }
        return resp;
    }

    public static ReaderDetailResponse toReaderDetailResponse(Reader r, List<LoanTicket> loans) {
        if (r == null) return null;
        ReaderDetailResponse resp = new ReaderDetailResponse();
        resp.setId(r.getId());
        resp.setFullName(r.getFullName());
        resp.setEmail(r.getEmail());
        resp.setStudentCodeOrCitizenId(r.getStudentCodeOrCitizenId());
        resp.setPhone(r.getPhone());
        resp.setDateOfBirth(r.getDateOfBirth());
        resp.setGender(r.getGender());
        resp.setAddress(r.getAddress());
        resp.setCardCreatedDate(r.getCardCreatedDate());
        resp.setCardExpiredDate(r.getCardExpiredDate());
        resp.setCardStatus(r.getCardStatus());
        resp.setCurrentBorrowedCount(r.getCurrentBorrowedCount());
        if (r.getAccount() != null) {
            resp.setAccountRole(r.getAccount().getRole() == null ? null : r.getAccount().getRole().name());
            resp.setAccountStatus(r.getAccount().getStatus() == null ? null : r.getAccount().getStatus().name());
        }
        if (loans != null) {
            resp.setRecentLoans(loans.stream().map(MapperUtils::toLoanTicketResponse).collect(Collectors.toList()));
        }
        return resp;
    }

    public static LoanTicketResponse toLoanTicketResponse(LoanTicket loan) {
        if (loan == null) return null;
        LoanTicketResponse resp = new LoanTicketResponse();
        resp.setLoanId(loan.getLoanId());
        resp.setBorrowDate(loan.getBorrowDate());
        resp.setDueDate(loan.getDueDate());
        resp.setStatus(loan.getStatus() == null ? null : loan.getStatus().name());
        if (loan.getLoanDetails() != null) {
            resp.setItemCount(loan.getLoanDetails().size());
        }
        return resp;
    }
}
