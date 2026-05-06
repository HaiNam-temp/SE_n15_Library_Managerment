package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.example.demo.domain.BookCategory;
import com.example.demo.enums.CategoryStatus;
import com.example.demo.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(CategoryRepository categoryRepository) {
		return args -> {
			// Kiểm tra xem đã có danh mục nào is_default = true chưa
			boolean hasDefault = categoryRepository.findByIsDefaultTrueAndStatus(CategoryStatus.ACTIVE).isPresent();

			if (!hasDefault) {
				System.out.println("====== ĐANG TẠO DANH MỤC MẶC ĐỊNH... ======");
				BookCategory defaultCategory = new BookCategory();
				defaultCategory.setCategoryName("Chưa phân loại");
				defaultCategory.setDescription("Hệ thống tự động gán");
				defaultCategory.setStatus(CategoryStatus.ACTIVE);
				defaultCategory.setIsDefault(true);

				categoryRepository.save(defaultCategory);
				System.out.println("====== TẠO THÀNH CÔNG! ======");
			}
		};
	}
}
