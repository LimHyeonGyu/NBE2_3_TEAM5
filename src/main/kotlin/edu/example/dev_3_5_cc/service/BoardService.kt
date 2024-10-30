package edu.example.dev_3_5_cc.service

import edu.example.dev_3_5_cc.repository.BoardRepository
import jakarta.transaction.Transactional
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Service

@Service
@Transactional
class BoardService(
    private val boardRepository: BoardRepository,
    private val modelMapper: ModelMapper
) {


}