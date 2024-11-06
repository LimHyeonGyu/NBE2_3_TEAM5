package edu.example.dev_3_5_cc.repository.search

import com.querydsl.core.types.Projections
import com.querydsl.jpa.JPQLQuery
import edu.example.dev_3_5_cc.dto.product.ProductListDTO
import edu.example.dev_3_5_cc.dto.product.ProductRequestDTO
import edu.example.dev_3_5_cc.entity.Product
import edu.example.dev_3_5_cc.entity.QProduct
import edu.example.dev_3_5_cc.entity.QProductImage
import edu.example.dev_3_5_cc.entity.QProductImage.productImage
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport

class ProductSearchImpl: QuerydslRepositorySupport(Product::class.java), ProductSearch {
    override fun list(pageable: Pageable): Page<ProductListDTO> {
        val product: QProduct = QProduct.product
        val productImage: QProductImage = QProductImage.productImage

        val query: JPQLQuery<Product> = from(product)
            .leftJoin(product.images, productImage)  // 조인
            .on(productImage.ino.eq(0))  // WHERE 조건 = ino 가 0인 이미지 파일

        // Projections.fields 를 사용하여 매핑
        val dtoQuery: JPQLQuery<ProductListDTO> = query.select(
            Projections.fields(
                ProductListDTO::class.java,
                product.productId,
                product.pname,
                product.price,
                product.stock,
                productImage.filename.`as`("pimage")
            )
        )
        querydsl?.applyPagination(pageable, dtoQuery)  //페이징
        val productList: List<ProductListDTO> = dtoQuery.fetch()  // 쿼리 실행
        val count: Long = dtoQuery.fetchCount()  // 레코드 수 조회

        return PageImpl(productList, pageable, count)
    }

    override fun listWithAllImages(pageable: Pageable): Page<ProductRequestDTO> {
        val product = QProduct.product
        val query: JPQLQuery<Product> = from(product)
            .leftJoin(product.images, productImage).fetchJoin()

        querydsl?.applyPagination(pageable, query)  // 페이징

        val products: List<Product> = query.fetch()  // 쿼리 실행
        val count: Long = query.fetchCount()  // 레코드 수 조회

        val dtoList: List<ProductRequestDTO> = products.map { ProductRequestDTO(it)}

        return PageImpl(dtoList, pageable, count)
    }
}
