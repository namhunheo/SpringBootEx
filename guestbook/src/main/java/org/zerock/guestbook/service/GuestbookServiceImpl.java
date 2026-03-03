package org.zerock.guestbook.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zerock.guestbook.dto.GuestbookDTO;
import org.zerock.guestbook.dto.PageRequestDTO;
import org.zerock.guestbook.dto.PageResultDTO;
import org.zerock.guestbook.entity.Guestbook;
import org.zerock.guestbook.entity.QGuestbook;
import org.zerock.guestbook.repository.GuestbookRepository;

import java.util.Optional;
import java.util.function.Function;

@Service
@Log4j2
@RequiredArgsConstructor
public class GuestbookServiceImpl implements GuestbookService {

    private final GuestbookRepository repository;

    @Override
    public Long register(GuestbookDTO dto) {
        Guestbook entity = dtoToEntity(dto);
        log.info(entity);
        repository.save(entity);
        return entity.getGno();
    }

    @Override
    public PageResultDTO<GuestbookDTO, Guestbook> getList(PageRequestDTO requestDTO) {
        // requestDTO에 있는 page, size로 Pageable객체 생성
        Pageable pageable = requestDTO.getPageable(Sort.by("gno").descending());
        // 조회
        // BooleanBuilder: Querydsl에서 제공하는 동적 where 조건 빌더
        BooleanBuilder booleanBuilder = getSearch(requestDTO);
        Page<Guestbook> result = repository.findAll(booleanBuilder, pageable);
        // 조회한 엔티티 -> DTO 변환하는 기능 정의
        // fn은 function 구조 객체
        Function<Guestbook, GuestbookDTO> fn = (entity -> entityToDto(entity));
        // PageResultDTO 생성자로 조회결과, 기능 넘겨주고 객체 생성해서 리턴
        return new PageResultDTO<>(result,fn);
    }

    @Override
    public GuestbookDTO read(Long gno) {
        Optional<Guestbook> result = repository.findById(gno);

        return result.isPresent()? entityToDto(result.get()): null;
    }

    @Override
    public void remove(Long gno) {
        repository.deleteById(gno);
    }

    @Override
    public void modify(GuestbookDTO dto) {
        Optional<Guestbook> result = repository.findById(dto.getGno());

        if(result.isPresent()){
            Guestbook entity = result.get();

            entity.changeTitle(dto.getTitle());
            entity.changeContent(dto.getContent());

            repository.save(entity);
        }
    }

    private BooleanBuilder getSearch(PageRequestDTO requestDTO){
        String type = requestDTO.getType();

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QGuestbook qGuestbook = QGuestbook.guestbook;
        String keyword = requestDTO.getKeyword();
        // gno > 0 (무조건 포함)
        BooleanExpression expression = qGuestbook.gno.gt(0L);
        booleanBuilder.and(expression);

        // 여러 경우의 수
        // 검색안한 경우
        if (type == null || type.trim().length() == 0) {
            return booleanBuilder;
        }

        BooleanBuilder conditionBuilder = new BooleanBuilder();

        if(type.contains("t")){ // 제목
            conditionBuilder.or(qGuestbook.title.contains(keyword));
        }
        if(type.contains("c")){ // 내용
            conditionBuilder.or(qGuestbook.content.contains(keyword));
        }
        if(type.contains("w")){ // 작성자
            conditionBuilder.or(qGuestbook.writer.contains(keyword));
        }
        // type == 'tcw' -> 제목 or 내용 or 작성자

        // gno > 0 and (title or content...: 동적)
        booleanBuilder.and(conditionBuilder);
        return booleanBuilder;
    }
}
