# Quy tắc Code Clean theo SOLID — Library Manager

## Mục tiêu
- Tuân thủ nguyên tắc SOLID.
- Chia module theo package đã chỉ định.
- Mỗi package chỉ chịu trách nhiệm đúng một việc.
- Hàm helper chung đặt trong `helper` hoặc `util`.
- Chỉ dùng `log.info` và `log.error` với định dạng cố định để dễ truy vết.

## 1. Cấu trúc package (bắt buộc)
- `com.example.demo.controller`: chỉ định nghĩa REST API, xử lý HTTP request/response, mapping tới DTO.
- `com.example.demo.service`: chứa nghiệp vụ, orchestration giữa repository và validation.
- `com.example.demo.repository`: giao tiếp với DB, chỉ chứa phương thức truy vấn.
- `com.example.demo.domain`: entity/JPA models.
- `com.example.demo.dto`: DTO request/response; controller và service trao đổi qua DTO.
- `com.example.demo.enums`: enum, không dùng static constant kiểu chuỗi.
- `com.example.demo.exceptions`: exception tùy chỉnh và global exception handler.
- `com.example.demo.helper` / `com.example.demo.util`: hàm tiện ích, helper chung, tất cả method static, không giữ state.
- `com.example.demo.config`: cấu hình app, bean, datasource, swagger, security.
- `com.example.demo.security`: lớp bảo mật, filter, provider, cấu hình.

## 2. Nguyên tắc rõ ràng cho từng layer
- Controller: nhận DTO → validate (Bean Validation) → gọi Service → trả DTO/ResponseEntity.
- Service: public methods nhỏ, 1 hành động rõ ràng; xử lý transaction tại service với `@Transactional` khi cần.
- Repository: interface/impl cho truy vấn; đặt tên rõ ràng theo chức năng.
- Helper/Util: method static, thuần chức năng, không giữ state.

## 3. SOLID — Áp dụng cụ thể
- SRP: mỗi class chỉ một lý do để thay đổi.
- OCP: mở rộng bằng interface/abstract, không sửa code cũ.
- LSP: implementation tôn trọng contract.
- ISP: tách interface lớn thành nhiều interface nhỏ.
- DIP: phụ thuộc vào abstraction, inject qua constructor.

## 4. Quy ước đặt tên
- Controller: `XxxController`.
- Service: `XxxService` và implementation `XxxServiceImpl` nếu cần.
- Repository: `XxxRepository`.
- DTO: `XxxRequest`, `XxxResponse`.

## 5. Logging (bắt buộc)
- Chỉ dùng `log.info(...)` và `log.error(...)`.
- Mục tiêu: biết hàm nào đang xử lý.
- Mẫu log nên giống:
  - `log.info("start createCategory - request={}", categoryRequest);`
  - `log.info("end createCategory - categoryId={}", categoryId);`
  - `log.error("createCategory error - message={}", message, exception);`

> Tài liệu này áp dụng trực tiếp cho dự án `com.example.demo` trong workspace.
