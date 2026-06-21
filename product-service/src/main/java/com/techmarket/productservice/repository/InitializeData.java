package com.techmarket.productservice.repository;

import com.techmarket.productservice.model.entities.Category;
import com.techmarket.productservice.model.entities.Product;
import com.techmarket.productservice.model.entities.Promotion;
import com.techmarket.productservice.model.entities.Variation;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class InitializeData {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final PromotionRepository promotionRepository;

  @PostConstruct
  public void loadData() {

    productRepository.deleteAll();
    categoryRepository.deleteAll();
    promotionRepository.deleteAll();

    List<Variation> variationList = createVariations();

    Promotion promotion = Promotion.builder()
            .promotionCode("PHO300")
            .name("Phone discounts")
            .description("Phone discounts")
            .discountRate(BigDecimal.valueOf(0.15))
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusMonths(1))
            .build();

    promotion = promotionRepository.save(promotion);

    Category phoneCategory = categoryRepository.save(
            Category.builder().id(new ObjectId("5f2d7c3b1e2f4a6b8c9d0e1a")).name("phone").promotion(promotion).build()
    );

    Category notebookCategory = categoryRepository.save(
            Category.builder().id(new ObjectId("5f2d7c3b1e2f4a6b8c9d0e1b")).name("notebook").build()
    );

    Category headphonesCategory = categoryRepository.save(
            Category.builder().id(new ObjectId("5f2d7c3b1e2f4a6b8c9d0e1c")).name("headphones").build()
    );

    List<Product> products = List.of(

            // PHONES
            p("Iphone 13", "Apple iPhone 13 (245 GB) - Negro", phoneCategory.getId(), 999.99, "iphone13", variationList),
            p("Iphone 12", "Apple iPhone 12 (200 GB) - Negro", phoneCategory.getId(), 799.99, "iphone12", variationList),
            p("Iphone 11", "Apple iPhone 11 (128 GB) - Blanco", phoneCategory.getId(), 687.99, "iphone11", variationList),

            p("Samsung S22 Ultra", "Samsung S22 Ultra (245 GB) - Negro", phoneCategory.getId(), 855.99, "samsung1", variationList),
            p("Samsung A54", "Samsung A54 (158 GB)", phoneCategory.getId(), 356.99, "samsung2", variationList),
            p("Samsung A34", "Samsung A34 (128 GB) - Negro", phoneCategory.getId(), 299.99, "samsung3", variationList),

            p("Motorola 1", "Motorola 1 (128 GB) - Negro", phoneCategory.getId(), 150.99, "motorola1", variationList),
            p("Motorola 2", "Motorola 2 (128 GB) - Negro", phoneCategory.getId(), 199.99, "motorola2", variationList),
            p("Motorola 3", "Motorola 3 (128 GB) - Negro", phoneCategory.getId(), 159.99, "iphone14", variationList),

            // NOTEBOOKS
            p("Notebook Dell", "Notebook Dell (500 GB) 8 cores - Negro", notebookCategory.getId(), 1099.99, "notebook1", variationList),
            p("Notebook HP", "Notebook HB (500 GB) 8 cores - Negro", notebookCategory.getId(), 1199.99, "notebook2", variationList),
            p("Notebook 3", "Notebook 3 (500 GB) 8 cores - Negro", notebookCategory.getId(), 999.99, "notebook3", variationList),
            p("Notebook 4", "Notebook 4 (500 GB) 8 cores - Negro", notebookCategory.getId(), 899.99, "notebook4", variationList),

            // HEADPHONES
            p("Headphones 1", "Headphones 1 - Negro", headphonesCategory.getId(), 143.99, "auri1", variationList),
            p("Headphones 2", "Headphones 2 - Negro", headphonesCategory.getId(), 245.99, "auri2", variationList),
            p("Headphones 3", "Headphones 3 - Negro", headphonesCategory.getId(), 367.99, "auri3", variationList),
            p("Headphones 4", "Headphones 4 - Negro", headphonesCategory.getId(), 134.99, "auri4", variationList)
    );

    productRepository.saveAll(products);
  }

  private Product p(String name,
                    String detail,
                    ObjectId categoryId,
                    double price,
                    String img,
                    List<Variation> variations) {

    return Product.builder()
            .name(name)
            .nameWithDetail(detail)
            .category(categoryId)
            .stock(25)
            .price(BigDecimal.valueOf(price))
            .img(img)
            .variations(variations)
            .build();
  }

  private List<Variation> createVariations() {
    return List.of(
            Variation.builder().color("black").build(),
            Variation.builder().color("red").build(),
            Variation.builder().color("white").build()
    );
  }
}