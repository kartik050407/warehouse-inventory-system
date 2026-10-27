package com.warehouse.inventory.service;

import com.warehouse.inventory.dto.ProductRequest;
import com.warehouse.inventory.dto.ProductResponse;
import com.warehouse.inventory.entity.Product;
import com.warehouse.inventory.exception.DuplicateResourceException;
import com.warehouse.inventory.exception.ResourceNotFoundException;
import com.warehouse.inventory.repository.CategoryRepository;
import com.warehouse.inventory.repository.ProductRepository;
import com.warehouse.inventory.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;
    private final SupplierRepository supplierRepo;

    public ProductService(ProductRepository productRepo,
                          CategoryRepository categoryRepo,
                          SupplierRepository supplierRepo) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
        this.supplierRepo = supplierRepo;
    }

    @Transactional
    public ProductResponse create(ProductRequest r) {
        if (productRepo.existsBySku(r.sku())) {
            throw new DuplicateResourceException("SKU already exists: " + r.sku());
        }
        Product p = new Product();
        p.setSku(r.sku());
        p.setName(r.name());
        p.setBarcode(r.barcode());
        p.setPrice(r.price());
        p.setLowStockThreshold(r.lowStockThreshold());
        if (r.categoryId() != null) {
            p.setCategory(categoryRepo.findById(r.categoryId()).orElseThrow(
                    () -> new ResourceNotFoundException("Category not found: " + r.categoryId())));
        }
        if (r.supplierId() != null) {
            p.setSupplier(supplierRepo.findById(r.supplierId()).orElseThrow(
                    () -> new ResourceNotFoundException("Supplier not found: " + r.supplierId())));
        }
        return toResponse(productRepo.save(p));
    }
    @Transactional
    public ProductResponse updateThreshold(Long id, int threshold) {
        Product p = productRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Product not found: " + id));
        p.setLowStockThreshold(threshold);
        return toResponse(p);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepo.findAll().stream().map(this::toResponse).toList();
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(p.getId(), p.getSku(), p.getName(), p.getBarcode(),
                p.getPrice(), p.getLowStockThreshold(),
                p.getCategory() == null ? null : p.getCategory().getName(),
                p.getSupplier() == null ? null : p.getSupplier().getName());
    }
}