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
        IntStream.rangeClosed(1, 300).forEach(i->{
            Guestbook guestbook = Guestbook.builder()
                    .title("Title...." + i)
                    .content("Content..." + i)
                    .writer("user" + (i % 10))
                    .build();

            System.out.println(guestbookRepository.save(guestbook));
        });
    }

    @Test
    public void updateTest() {

        Optional<Guestbook> result = guestbookRepository.findById(299L); //존재하는 번호로 테스트

        if(result.isPresent()){

            Guestbook guestbook = result.get();

            guestbook.changeTitle("Changed Title....");
            guestbook.changeContent("Changed Content...");

            guestbookRepository.save(guestbook);
        }
    }

    // gno기준 10개 항목씩 페이징 설정 후, dsl을 활용하여 title에 키워드 1이 포함된 항목들을 페이징 설정에 맞추어 조회
    @Test
    public void testQuery1(){
        Pageable pageable = PageRequest.of(1,10, Sort.by("gno").descending());
        QGuestbook qGuestbook = QGuestbook.guestbook;
        String keyword = "1";
        BooleanBuilder builder = new BooleanBuilder();
        BooleanExpression expression = qGuestbook.title.contains(keyword);
        builder.and(expression);
        Page<Guestbook> result = guestbookRepository.findAll(builder, pageable);

        result.stream().forEach(guestbook -> {
            System.out.println(guestbook);
        });
    }

    // 페이징 설정 후, dsl을 활용하여 title or content에 키워드 23이 포함된 항목들을 페이징 설정에 맞추어 조회
    @Test
    public void testQuery2(){
        Pageable pageable = PageRequest.of(0,10, Sort.by("gno").descending());
        QGuestbook qGuestbook = QGuestbook.guestbook;
        String keyword = "23";
        BooleanBuilder builder = new BooleanBuilder();
        BooleanExpression exTitle = qGuestbook.title.contains(keyword); // 키워드가 타이틀에 포함되는 조건
        BooleanExpression exContent = qGuestbook.content.contains(keyword); // 키워드가 콘텐트에 포함되는 조건
        BooleanExpression exAll = exTitle.or(exContent); // 키워드가 타이틀 or 콘텐트에 포함되는 조건
        builder.and(exAll); // 빌더에 조건문 추가
        builder.and(qGuestbook.gno.gt(235L)); // gno의 최솟값 설정
        Page<Guestbook> result = guestbookRepository.findAll(builder, pageable); // 최종 쿼리문

        result.stream().forEach(guestbook -> {
            System.out.println(guestbook);
        });
    }




}