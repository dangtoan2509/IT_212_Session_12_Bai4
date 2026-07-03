# Danh sách Test Case Đăng ký Tài khoản (Account Registration)

Dựa trên các annotation validation trong `AccountRegistrationRequest`:
- `fullName`: `@NotBlank`
- `phone`: `@NotBlank`, `@Pattern(regexp = "^(0[3|5|7|8|9])+([0-9]{8})$")`
- `email`: `@Email`
- `citizenId`: `@NotBlank`, `@Pattern(regexp = "^\\d{12}$")`

## 1. Positive Test Cases (Trường hợp hợp lệ)
| Test Case ID | Description | fullName | phone | email | citizenId | Expected Result |
|---|---|---|---|---|---|---|
| TC_POS_01 | Đăng ký thành công với đầy đủ thông tin hợp lệ | Nguyễn Văn A | 0912345678 | nguyen.vana@gmail.com | 001090123456 | HTTP 201 Created, trả về accountId và accountNumber |
| TC_POS_02 | Đăng ký thành công với email null (vì email không bắt buộc, chỉ yêu cầu @Email nếu có) | Trần Thị B | 0387654321 | null | 079192837465 | HTTP 201 Created, trả về accountId và accountNumber |
| TC_POS_03 | Đăng ký thành công với tên chứa khoảng trắng ở đầu/cuối (trim trước khi xử lý nếu có) | " Lê Văn C " | 0511223344 | levanc@gmail.com | 001099112233 | HTTP 201 Created |

## 2. Negative Test Cases (Trường hợp lỗi Validation và Business)
| Test Case ID | Description | fullName | phone | email | citizenId | Expected Result |
|---|---|---|---|---|---|---|
| TC_NEG_01 | Họ và tên để trống hoặc null | "" / null | 0912345678 | a@b.com | 001090123456 | HTTP 400 Bad Request, "Họ và tên không được để trống" |
| TC_NEG_02 | Số điện thoại để trống hoặc null | Nguyễn Văn A | "" / null | a@b.com | 001090123456 | HTTP 400 Bad Request, "Số điện thoại không được để trống" |
| TC_NEG_03 | Số điện thoại sai đầu số VN (VD: đầu 04) | Nguyễn Văn A | 0412345678 | a@b.com | 001090123456 | HTTP 400 Bad Request, "Số điện thoại không đúng định dạng" |
| TC_NEG_04 | Số điện thoại chứa chữ cái / ký tự đặc biệt | Nguyễn Văn A | 091234abcd | a@b.com | 001090123456 | HTTP 400 Bad Request, "Số điện thoại không đúng định dạng" |
| TC_NEG_05 | Email sai định dạng (thiếu @ hoặc domain) | Nguyễn Văn A | 0912345678 | invalid_email | 001090123456 | HTTP 400 Bad Request, "Email không hợp lệ" |
| TC_NEG_06 | CCCD để trống hoặc null | Nguyễn Văn A | 0912345678 | a@b.com | "" / null | HTTP 400 Bad Request, "Số CCCD không được để trống" |
| TC_NEG_07 | CCCD có chứa chữ cái | Nguyễn Văn A | 0912345678 | a@b.com | 001090123abc | HTTP 400 Bad Request, "Số CCCD không hợp lệ..." |
| TC_NEG_08 | SĐT đã tồn tại trên hệ thống | Nguyễn Văn A | 0912345678 (trùng) | a@b.com | 001090123456 | HTTP 409 Conflict, "Số điện thoại đã được đăng ký!" |
| TC_NEG_09 | CCCD đã tồn tại trên hệ thống | Nguyễn Văn A | 0812345678 | a@b.com | 001090123456 (trùng) | HTTP 409 Conflict, "Số CCCD đã tồn tại trên hệ thống!" |

## 3. Boundary Test Cases (Trường hợp biên)
| Test Case ID | Description | fullName | phone | email | citizenId | Expected Result |
|---|---|---|---|---|---|---|
| TC_BND_01 | Số điện thoại thiếu 1 số (9 số) | Nguyễn Văn A | 091234567 | a@b.com | 001090123456 | HTTP 400 Bad Request, "Số điện thoại không đúng định dạng" |
| TC_BND_02 | Số điện thoại thừa 1 số (11 số) | Nguyễn Văn A | 09123456789 | a@b.com | 001090123456 | HTTP 400 Bad Request, "Số điện thoại không đúng định dạng" |
| TC_BND_03 | Số CCCD thiếu 1 số (11 số) | Nguyễn Văn A | 0912345678 | a@b.com | 00109012345 | HTTP 400 Bad Request, "Số CCCD không hợp lệ..." |
| TC_BND_04 | Số CCCD thừa 1 số (13 số) | Nguyễn Văn A | 0912345678 | a@b.com | 0010901234567 | HTTP 400 Bad Request, "Số CCCD không hợp lệ..." |
