package com.romay.meme.columbarium.meme.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.romay.meme.columbarium.meme.entity.Meme;
import com.romay.meme.columbarium.meme.entity.QMeme;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class MemeRepositoryImpl implements MemeRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  public MemeRepositoryImpl(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Override
  public Page<Meme> findBySearchAndSort(String keyword, String sortOption, Pageable pageable) {
    QMeme meme = QMeme.meme;

    // 1️⃣ ID만 조회 (페이징 + 조건 모두 적용)
    var idQuery = queryFactory.select(meme.code)
        .from(meme)
        .where(
            meme.latest.isTrue(), // 최신 글만
            keyword != null && !keyword.isEmpty() ? meme.title.containsIgnoreCase(keyword) : null
        );

    // 정렬
    if ("likes".equals(sortOption)) {
      idQuery.orderBy(meme.likesCount.desc());
    } else {
      idQuery.orderBy(meme.createdAt.desc());
    }

    List<Long> ids = idQuery
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    if (ids.isEmpty()) {
      return new PageImpl<>(List.of(), pageable, 0);
    }

    // 2️⃣ 실제 엔티티 조회 (fetchJoin)
    List<Meme> content = queryFactory.selectFrom(meme)
        .leftJoin(meme.category).fetchJoin()
        .leftJoin(meme.member).fetchJoin()
        .where(meme.code.in(ids)) // ID 기준 조회만
        .orderBy(
            "likes".equals(sortOption) ? meme.likesCount.desc() : meme.createdAt.desc()
        )
        .fetch();

    // 3️⃣ 전체 카운트 조회
    long total = queryFactory.selectFrom(meme)
        .where(
            meme.latest.isTrue(),
            keyword != null && !keyword.isEmpty() ? meme.title.containsIgnoreCase(keyword) : null
        )
        .fetchCount();

    return new PageImpl<>(content, pageable, total);
  }
}
