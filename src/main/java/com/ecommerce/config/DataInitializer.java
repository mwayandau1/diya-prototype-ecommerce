
package com.ecommerce.config;

import com.ecommerce.model.Category;
import com.ecommerce.model.Product;
import com.ecommerce.model.Role;
import com.ecommerce.model.User;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.RoleRepository;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        // Create roles
        if (roleRepository.count() == 0) {
            Role userRole = new Role();
            userRole.setName(Role.ERole.ROLE_USER);
            roleRepository.save(userRole);

            Role adminRole = new Role();
            adminRole.setName(Role.ERole.ROLE_ADMIN);
            roleRepository.save(adminRole);

            System.out.println("Roles created successfully!");
        }

        // Create admin user
        if (userRepository.count() == 0) {
            User adminUser = new User();
            adminUser.setName("Admin User");
            adminUser.setUsername("admin");
            adminUser.setEmail("admin@example.com");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setRoles(new HashSet<>(Collections.singletonList(roleRepository.findByName(Role.ERole.ROLE_ADMIN).get())));
            userRepository.save(adminUser);

            User normalUser = new User();
            normalUser.setName("Normal User");
            normalUser.setUsername("user");
            normalUser.setEmail("user@example.com");
            normalUser.setPassword(passwordEncoder.encode("user123"));
            normalUser.setRoles(new HashSet<>(Collections.singletonList(roleRepository.findByName(Role.ERole.ROLE_USER).get())));
            userRepository.save(normalUser);

            System.out.println("Users created successfully!");
        }

        // Create categories and products
        if (categoryRepository.count() == 0) {
            // Create Electronics category
            Category electronics = new Category();
            electronics.setName("Electronics");
            electronics.setDescription("Electronic devices and gadgets");
            electronics = categoryRepository.save(electronics);

            Product laptop = new Product();
            laptop.setName("Laptop Pro X");
            laptop.setDescription("Powerful laptop with high performance");
            laptop.setPrice(new BigDecimal("1299.99"));
            laptop.setStockQuantity(50);
            laptop.setImageUrl("https://example.com/laptop.jpg");
            laptop.setCategory(electronics);
            productRepository.save(laptop);

            Product smartphone = new Product();
            smartphone.setName("Smartphone Ultra");
            smartphone.setDescription("Latest smartphone with advanced features");
            smartphone.setPrice(new BigDecimal("899.99"));
            smartphone.setStockQuantity(100);
            smartphone.setImageUrl("https://example.com/smartphone.jpg");
            smartphone.setCategory(electronics);
            productRepository.save(smartphone);

            // Create Clothing category
            Category clothing = new Category();
            clothing.setName("Clothing");
            clothing.setDescription("All types of clothing and accessories");
            clothing = categoryRepository.save(clothing);

            Product tshirt = new Product();
            tshirt.setName("Premium T-Shirt");
            tshirt.setDescription("Comfortable cotton t-shirt");
            tshirt.setPrice(new BigDecimal("29.99"));
            tshirt.setStockQuantity(200);
            tshirt.setImageUrl("https://example.com/tshirt.jpg");
            tshirt.setCategory(clothing);
            productRepository.save(tshirt);

            Product jeans = new Product();
            jeans.setName("Classic Jeans");
            jeans.setDescription("Durable denim jeans");
            jeans.setPrice(new BigDecimal("59.99"));
            jeans.setStockQuantity(150);
            jeans.setImageUrl("https://example.com/jeans.jpg");
            jeans.setCategory(clothing);
            productRepository.save(jeans);

            System.out.println("Categories and products created successfully!");
        }
    }
}
