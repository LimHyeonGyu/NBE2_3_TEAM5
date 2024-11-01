package edu.example.dev_3_5_cc.exception

enum class ProductException(val message: String, val code: Int) {
    NOT_FOUND("Product NOT_FOUND", 404),
    NOT_CREATED("Product Not Created", 400),
    NOT_UPDATED("Product Not Updated", 400),
    NOT_DELETED("Product Not Deleted", 400),
    IMAGE_NOT_FOUND("No Product Image", 400),
    CREATE_ERR("No Authenticated user", 403);

    private val productTaskException: ProductTaskException = ProductTaskException(message, code)
    fun get(): ProductTaskException {
        return productTaskException
    }
}