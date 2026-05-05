# Quy Tắc Code Clean theo SOLID — Dự án Library Manager

Tài liệu ngắn gọn các quy tắc tổ chức mã nguồn, đóng gói theo package, trách nhiệm theo layer và quy ước logging. Viết bằng tiếng Việt, áp dụng cho dự án trong workspace (`com.example.demo`).

## Mục tiêu
- Tuân thủ nguyên tắc SOLID.
- Chia module theo package đã chỉ định (controller, service, repository, domain, dto, enums, exceptions, helper, util, config, security).
- Mỗi package chỉ làm đúng một việc.
- Các hàm helper chung được đặt trong package `helper` hoặc `util`.
- Chỉ dùng `log.info` và `log.error` với một định dạng cố định để dễ truy vết hàm đang thực thi.

## 1. Cấu trúc package (bắt buộc)
- `com.example.demo.controller` — Chỉ định nghĩa các REST API, xử lý HTTP request/response, mapping tới DTO. Không chứa logic nghiệp vụ.
- `com.example.demo.service` — Chứa dịch vụ (business logic). Các phương thức service phải thuần xử lý nghiệp vụ, orchestration giữa repository, validation, gọi helper.
- `com.example.demo.repository` — Giao tiếp với database, chỉ chứa các phương thức truy vấn (Spring Data JPA repositories hoặc custom DAO).
- `com.example.demo.domain` — Entity/JPA models.
- `com.example.demo.dto` — Data Transfer Objects cho request/response; Controller ↔ Service dùng DTO, không expose entity trực tiếp.
- `com.example.demo.enums` — Tất cả các enum (trường static/constant phải là enum ở đây).
- `com.example.demo.exceptions` — Các exception tùy chỉnh, exception handler (global exception handling) cũng nằm tại đây hoặc `config`.
- `com.example.demo.helper` hoặc `com.example.demo.util` — Các hàm tiện ích, helper chung (string utils, date utils, validation helpers, mapping helpers nếu không dùng MapStruct).
- `com.example.demo.config` — Cấu hình app, datasource, security beans, swagger, v.v.
- `com.example.demo.security` — Các lớp liên quan bảo mật (filters, providers, configs).

## 2. Nguyên tắc rõ ràng cho từng layer
- Controller: chỉ nhận `DTO` -> validate (bean validation) -> gọi `Service` -> trả `DTO` hoặc `ResponseEntity`.
- Service: public methods phải nhỏ, 1 hành động rõ ràng; xử lý transaction tại service (annotate `@Transactional` khi cần). Không gọi `System.out` hay truy vấn DB trực tiếp bằng JDBC (trừ repository).
- Repository: chỉ có interface/impl cho truy vấn; đặt tên rõ ràng theo chức năng.
- Helper/Util: tất cả method phải là `static` và thuần chức năng, không giữ state. Tên method rõ ràng, tài liệu (JavaDoc) nếu phức tạp.

## 3. SOLID — Áp dụng cụ thể
- Single Responsibility (SRP): mỗi class/bean chỉ 1 lý do để thay đổi (Controller không xử lý validation phức tạp, Service không format HTTP response).
- Open/Closed (OCP): mở rộng bằng interface/abstract (ví dụ: `PaymentStrategy`, `NotificationSender`), không sửa code cũ.
- Liskov Substitution (LSP): implementations phải tôn trọng contract interface; tránh ném unchecked exceptions khác loại.
- Interface Segregation (ISP): tách interface lớn thành nhiều interface nhỏ nếu cần.
- Dependency Inversion (DIP): phụ thuộc vào abstraction (interface), inject qua constructor; tránh `new` trực tiếp ngoại trừ DTO/VO.

## 4. Quy ước đặt tên
- Controller: `XxxController` (ví dụ `ReaderController`).
- Service: `XxxService` + interface `XxxService` (nếu cần mock/test). Implementation `XxxServiceImpl`.
- Repository: `XxxRepository` (extends JpaRepository<Entity, Long>).
- DTO: `XxxRequest`, `XxxResponse`.

## 5. Logging (bắt buộc)
- Chỉ dùng `log.info(...)` và `log.error(...)`.
- Mục tiêu: mọi log phải trả lời được câu hỏi "hàm nào đang xử lý" và có format cố định.
- Định dạng log khuyến nghị (logback pattern):

  `%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%logger{36}.%M] - %msg%n`

  - `%logger{36}` trả về class, `%M` trả về method — (Lưu ý: `%M` có thể ảnh hưởng hiệu năng; cân nhắc chỉ bật trong dev hoặc dùng MDC).

- Quy ước viết log trong code (ví dụ trong `ReaderService.createReader`):

  - Khi bắt đầu method:
    `log.info("start createReader - readerRequest={}", readerRequestSummary);`
  - Khi kết thúc thành công:
    `log.info("end createReader - readerId={}", readerId);`
  - Khi lỗi:
    `log.error("createReader failed - cause= {}", ex.getMessage(), ex);`

- KHÔNG dùng `log.debug` hoặc `log.trace` cho thông tin bắt buộc, chỉ dùng `info`/`error` theo qui định.

## 6. Exception handling
- Tạo hierarchy exception trong `exceptions` (ví dụ `BaseException extends RuntimeException`, `NotFoundException`, `ValidationException`).
- Dùng `@ControllerAdvice` để map exception -> HTTP status + body (DTO lỗi chuẩn).

## 7. Transactions
- Đánh dấu `@Transactional` ở service layer methods thực hiện nhiều thao tác DB.
- Nếu method chỉ đọc, dùng `@Transactional(readOnly = true)`.

## 8. Entity / DTO / Mapping
- Entities: chỉ chứa mapping JPA, no business logic. Dùng `@ManyToOne`, `@OneToMany` rõ ràng và lazy fetch mặc định.
- DTOs: response/request object, không chứa annotation JPA.
- Mapping: dùng MapStruct hoặc helper mapper trong `helper` package. Mapper cần tách riêng thành `com.example.demo.helper.mapper`.

## 9. Enums & Constants
- Tất cả trường static/constant phải dùng enum trong package `enums` (ví dụ `AccountStatus`, `LoanStatus`).

## 10. Tests
- Unit tests: chỉ test Service logic bằng Mock repository.
- Integration tests: test repository + Flyway migrations using testcontainers (khuyến nghị).

## 11. Migrations
- Dùng Flyway; migration SQL nằm trong `src/main/resources/db/migration` với prefix `V1__...`.

## 12. Ví dụ nhanh cấu trúc thư mục

- `com/example/demo/controller/ReaderController.java`
- `com/example/demo/service/ReaderService.java`
- `com/example/demo/service/impl/ReaderServiceImpl.java`
- `com/example/demo/repository/ReaderRepository.java`
- `com/example/demo/domain/Reader.java`
- `com/example/demo/dto/ReaderRequest.java`
- `com/example/demo/dto/ReaderResponse.java`
- `com/example/demo/enums/AccountStatus.java`
- `com/example/demo/helper/MapperUtils.java`
- `com/example/demo/exceptions/NotFoundException.java`

## 13. Kiểm tra tuân thủ (Checklist trước khi commit)
- [ ] Controller không chứa business logic.
- [ ] Service có `@Transactional` đúng chỗ.
- [ ] Helper functions nằm trong `helper`/`util` và không giữ state.
- [ ] Enum cho các trường cố định.
- [ ] Log chỉ dùng `info`/`error` và tuân theo format.
- [ ] Có migration Flyway cho thay đổi schema.
- [ ] Unit tests cover service logic; integration test cover repository/migrations.

---

Nếu bạn muốn, tôi sẽ tạo file cấu hình log (logback-spring.xml) với pattern đề xuất, hoặc tạo template `Controller`/`Service`/`Repository` đầu tiên theo chuẩn này. Bạn muốn bắt đầu với phần nào tiếp theo? 
