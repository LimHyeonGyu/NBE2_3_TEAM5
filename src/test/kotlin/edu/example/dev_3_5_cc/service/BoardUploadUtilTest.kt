package edu.example.dev_3_5_cc.service

import edu.example.dev_3_5_cc.util.BoardUploadUtil
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Commit

@SpringBootTest
class BoardUploadUtilTest {

    @Autowired
    lateinit var boardUploadUtil: BoardUploadUtil

    @Test
    @Transactional
    @Commit
    fun testUpload(){

    }
}