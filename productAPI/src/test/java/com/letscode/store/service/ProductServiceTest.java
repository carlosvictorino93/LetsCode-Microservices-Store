package com.letscode.store.service;

import com.letscode.store.dto.ProductDTO;
import com.letscode.store.exception.AlreadyExistException;
import com.letscode.store.exception.NotFoundException;
import com.letscode.store.model.Product;
import com.letscode.store.model.QProduct;
import com.letscode.store.repository.ProductRepository;
import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Test
    public void productSaveSuccessfully(){
        ProductDTO productDTO = ProductDTO.
                builder()
                .productCode("1")
                .price(100D)
                .quantity(100)
                .build();

        Mockito.when(productRepository.findProductByProductCode(Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(productRepository.save(Mockito.any())).thenReturn(Product.convert(productDTO));

        ProductDTO returnedProductDTO = productService.saveProduct(productDTO);

        Assertions.assertEquals("1", returnedProductDTO.getProductCode());
    }

    @Test
    public void productSaveAlreadyOnDB(){
        ProductDTO productDTO = ProductDTO.
                builder()
                .productCode("1")
                .price(100D)
                .quantity(100)
                .build();

        Product product = Product.convert(productDTO);

        Mockito.when(productRepository.findProductByProductCode(Mockito.any())).thenReturn(Optional.of(product));
        Mockito.when(productRepository.save(Mockito.any())).thenReturn(Product.convert(productDTO));

        Assertions.assertThrows(AlreadyExistException.class, () ->  productService.saveProduct(productDTO));

    }

    @Test
    public void listAllProducts(){
        List<Product> productList = new ArrayList<>();
        ProductDTO productDTO = ProductDTO.
                builder()
                .productCode("1")
                .price(100D)
                .quantity(100)
                .build();

        productList.add(Product.convert(productDTO));

        Page<Product> productPage = new PageImpl<>(productList);

        Pageable page = PageRequest.of(0,20);
        Predicate predicate = null;

        Mockito.when(productRepository.findAll(predicate, page)).thenReturn(productPage);

        Page<ProductDTO> returnedProductPage = productService.listProduct(predicate, page);

        Assertions.assertEquals(1, returnedProductPage.getSize());
        Assertions.assertTrue(returnedProductPage.stream().findAny().isPresent());
        Assertions.assertEquals("1", returnedProductPage.stream().findAny().get().getProductCode());
    }

    @Test
    public void listSpecificProduct(){
        Product product = Product.
                builder()
                .productCode("1")
                .price(200D)
                .quantity(200)
                .build();


        Mockito.when(productRepository.findProductByProductCode(Mockito.any())).thenReturn(Optional.of(product));
        Mockito.when(productRepository.save(Mockito.any())).thenReturn(product);

        ProductDTO returnedProductDTO = productService.updateProduct(ProductDTO.convert(product));

        Assertions.assertEquals("1", returnedProductDTO.getProductCode());
    }

    @Test
    public void getProductThatNotExist(){

        Mockito.when(productRepository.findProductByProductCode(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> productService.getProduct("1"));

    }
}
