package com.techmarket.productservice.repository;

import com.techmarket.productservice.model.entities.Category;
import com.techmarket.productservice.model.entities.Product;
import com.techmarket.productservice.model.entities.Promotion;
import com.techmarket.productservice.model.entities.Variation;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class InitializeData {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final PromotionRepository promotionRepository;

  @PostConstruct
  public void loadDate() {
    List<Variation> variationList = createVariations();
    productRepository.deleteAll();
    categoryRepository.deleteAll();
    List<Product> productList = new ArrayList<>();

    Promotion promotion = Promotion.builder()
        .promotionCode("PHO300")
        .name("Phone discounts")
        .description("Phone discounts")
        .discountRate(BigDecimal.valueOf(0.15))
        .startDate(LocalDate.now())
        .endDate(LocalDate.now().plusMonths(1))
        .build();

    categoryRepository.saveAll(List.of(Category.builder()
            .name("phone")
            .promotion(promotion)
            .build(),
        Category.builder()
            .name("notebook")
            .build(),
        Category.builder()
            .name("headphones")
            .build()
    ));

    Optional<Category> phoneCategory = categoryRepository.findByName("phone");
    Optional<Category> notebookCategory = categoryRepository.findByName("notebook");
    Optional<Category> headphonesCategory = categoryRepository.findByName("headphones");


    productList.add(Product.builder()
        .name("Iphone 13")
        .nameWithDetail("Apple iPhone 13 (245 GB) - Negro")
        .category(phoneCategory.get().getId())
        .stock(25)
        .img("iphone13")
        .price(BigDecimal.valueOf(999.99))
        .variations(variationList)
        .build());

    productList.add(Product.builder()
        .name("Iphone 12")
        .nameWithDetail("Apple iPhone 12 (200 GB) - Negro")
        .category(phoneCategory.get().getId())
        .stock(25)
        .img("iphone12")
        .price(BigDecimal.valueOf(799.99))
        .variations(variationList)
        .build());

    productList.add(Product.builder()
        .name("Iphone 11")
        .nameWithDetail("Apple iPhone 11 (200 GB) - Negro")
        .category(phoneCategory.get().getId())
        .stock(25)
        .img("iphone11")
        .price(BigDecimal.valueOf(699.99))
        .variations(variationList)
        .build());

    productList.add(Product.builder()
        .name("Samsung A54")
        .nameWithDetail("Samsung A54 (158 GB)")
        .category(phoneCategory.get().getId())
        .stock(25)
        .img("samsung2")
        .price(BigDecimal.valueOf(356.99))
        .variations(variationList)
        .build());

    productList.add(Product.builder()
        .name("Samsung S22")
        .nameWithDetail("Samsung S22 (158 GB)")
        .category(phoneCategory.get().getId())
        .stock(25)
        .img("samsung1")
        .price(BigDecimal.valueOf(356.99))
        .variations(variationList)
        .build());

    productList.add(Product.builder()
        .name("Samsung A54")
        .nameWithDetail("Samsung A54 (158 GB)")
        .category(phoneCategory.get().getId())
        .stock(25)
        .img("samsung2")
        .price(BigDecimal.valueOf(356.99))
        .variations(variationList)
        .build());

    productList.add(Product.builder()
        .name("Samsung A34")
        .nameWithDetail("Samsung A34 (128 GB) - Negro")
        .category(phoneCategory.get().getId())
        .stock(25)
        .img("samsung3")
        .price(BigDecimal.valueOf(299.99))
        .variations(variationList)
        .build());

    productList.add(Product.builder()
        .name("Motorola 1")
        .nameWithDetail("Motorola 1 (128 GB) - Negro")
        .category(phoneCategory.get().getId())
        .stock(25)
        .img("motorola1")
        .price(BigDecimal.valueOf(150.99))
        .variations(variationList)
        .build());

    productList.add(Product.builder()
        .name("Motorola 2")
        .nameWithDetail("Motorola 2 (128 GB) - Negro")
        .category(phoneCategory.get().getId())
        .stock(25)
        .img("motorola2")
        .price(BigDecimal.valueOf(199.99))
        .variations(variationList)
        .build());

    productList.add(Product.builder()
        .name("Motorola 3")
        .nameWithDetail("Motorola 3 (128 GB) - Negro")
        .category(phoneCategory.get().getId())
        .stock(25)
        .img("iphone14")
        .price(BigDecimal.valueOf(159.99))
        .variations(variationList)
        .build());

    productList.add(Product.builder()
        .name("Notebook Dell")
        .nameWithDetail("Notebook Dell (500 GB) 8 cores - Negro")
        .category(notebookCategory.get().getId())
        .stock(25)
        .img("notebook1")
        .price(BigDecimal.valueOf(1099.99))
        .variations(variationList)
        .build());

    productList.add(Product.builder()
        .name("Notebook HP")
        .nameWithDetail("Notebook HB (500 GB) 8 cores - Negro")
        .category(notebookCategory.get().getId())
        .stock(25)
        .img("notebook2")
        .price(BigDecimal.valueOf(1199.99))
        .variations(variationList)
        .build());

    productList.add(Product.builder()
        .name("Notebook 3")
        .nameWithDetail("Notebook 3 (500 GB) 8 cores - Negro")
        .category(notebookCategory.get().getId())
        .stock(25)
        .img("notebook3")
        .price(BigDecimal.valueOf(999.99))
        .variations(variationList)
        .build());

    productList.add(Product.builder()
        .name("Notebook 4")
        .nameWithDetail("Notebook 4 (500 GB) 8 cores - Negro")
        .category(notebookCategory.get().getId())
        .stock(25)
        .img("notebook4")
        .price(BigDecimal.valueOf(899.99))
        .variations(variationList)
        .build());

    productList.add(Product.builder()
        .name("Headphones 1")
        .nameWithDetail("Headphones 1 - Negro")
        .category(headphonesCategory.get().getId())
        .stock(25)
        .img("auri1")
        .price(BigDecimal.valueOf(143.99))
        .variations(variationList)
        .build());

    productList.add(Product.builder()
        .name("Headphones 2")
        .nameWithDetail("Headphones 2 - Negro")
        .category(headphonesCategory.get().getId())
        .stock(25)
        .img("auri2")
        .price(BigDecimal.valueOf(245.99))
        .variations(variationList)
        .build());

    productList.add(Product.builder()
        .name("Headphones 3")
        .nameWithDetail("Headphones 3 - Negro")
        .category(headphonesCategory.get().getId())
        .stock(25)
        .img("auri3")
        .price(BigDecimal.valueOf(367.99))
        .variations(variationList)
        .build());
    productRepository.saveAll(productList);
  }

  private List<Category> createCategories() {
    return List.of(Category.builder()
            .name("phone")
            .build(),
        Category.builder()
            .name("notebook")
            .build(),
        Category.builder()
            .name("headphones")
            .build()
    );
  }

  private List<Variation> createVariations() {
    return List.of(Variation.builder()
            .color("black")
            .build(),
        Variation.builder()
            .color("red")
            .build(),
        Variation.builder()
            .color("white")
            .build()
    );
  }
}
