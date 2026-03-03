package org.zerock.guestbook.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.zerock.guestbook.entity.Guestbook;
import org.zerock.guestbook.entity.QGuestbook;

import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
public class GuestbookRepositoryTests {
    @Autowired
    private GuestbookRepository guestbookRepository;

    @Test
    public void insertDummies() {
        IntStream.rangeClosed(1,300).forEach(i->{
            Guestbook guestbook = Guestbook.builder()
                    .title("Title...."+i)
                    .content("Content..."+i)
                    .writer("user"+(i%10))
                    .build();
            System.out.println(guestbookRepository.save(guestbook));
        });
    }

    @Test
    public void updateTest() {
        Optional<Guestbook> result = guestbookRepository.findById(300L);
        if (result.isPresent()) {
            Guestbook guestbook = result.get();
            guestbook.changeTitle("Changed Title...");
            guestbook.changeContent("Changed Content...");

            guestbookRepository.save(guestbook);
        }
    }

    // 제목에 '1'이 들어있는데 데이터 검색
    @Test
    public void testQuery1() {
        Pageable pageable = PageRequest.of(1,10, Sort.by("gno").descending());

        // QueryDSL
        QGuestbook qGuestbook = QGuestbook.guestbook;
        String keyword = "1"; // 검색어 (사용자가 입력한 검색어)
        BooleanBuilder builder = new BooleanBuilder(); // 검색(필터링) 객체
        // 조건
        BooleanExpression expression = qGuestbook.title.contains(keyword);

        builder.and(expression); // 빌더에 추가

        // 실행
        Page<Guestbook> result = guestbookRepository.findAll(builder, pageable);

        result.forEach(g-> {
            System.out.println(g);
        });
    }

    @Test
    public void testQuery2() {
        Pageable pageable = PageRequest.of(1,10, Sort.by("gno").descending());

        // QueryDSL
        QGuestbook qGuestbook = QGuestbook.guestbook;
        String keyword = "1"; // 검색어 (사용자가 입력한 검색어)
        BooleanBuilder builder = new BooleanBuilder(); // 검색(필터링) 객체
        // 조건
        BooleanExpression exTitle = qGuestbook.title.contains(keyword);
        BooleanExpression exContent = qGuestbook.content.contains(keyword);
        BooleanExpression exAll = exTitle.or(exContent);

        builder.and(exAll); // 빌더에 추가
        // gno가 0보다 큰
        builder.and(qGuestbook.gno.gt(0L));

        // 실행
        Page<Guestbook> result = guestbookRepository.findAll(builder, pageable);

        result.forEach(g-> {
            System.out.println(g);
        });
    }
}
