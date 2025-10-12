package com.romay.meme.columbarium.meme.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.romay.meme.columbarium.meme.entity.Meme;
import com.romay.meme.columbarium.meme.entity.QMeme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MemeRepositoryImpl implements MemeRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  public MemeRepositoryImpl(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Override
  public Page<Meme> findBySearchAndSort(String keyword, String sortOption, Pageable pageable) {
    QMeme meme = QMeme.meme;

    // 1️⃣ 먼저 ID만 조회
    var idQuery = queryFactory.select(meme.code)
        .from(meme)
        .where(
            keyword != null && !keyword.isEmpty() ? meme.title.containsIgnoreCase(keyword) : null);

    // 정렬
    if ("likes".equals(sortOption)) {
      idQuery.orderBy(meme.likesCount.desc());
    } else { // 최신순 기본
      idQuery.orderBy(meme.createdAt.desc());
    }

    List<Long> ids = idQuery.offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    if (ids.isEmpty()) {
      return new PageImpl<>(List.of(), pageable, 0);
    }

    // 2️⃣ fetchJoin으로 실제 엔티티 조회 (N+1 방지)
    List<Meme> content = queryFactory.selectFrom(meme)
        .leftJoin(meme.category).fetchJoin()
        .leftJoin(meme.member).fetchJoin()
        .where(meme.code.in(ids))
        .where(meme.latest.isTrue())
        .orderBy(
            "likes".equals(sortOption) ? meme.likesCount.desc() : meme.createdAt.desc()
        )
        .fetch();

    // 전체 갯수
    long total = queryFactory.selectFrom(meme)
        .where(
            keyword != null && !keyword.isEmpty() ? meme.title.containsIgnoreCase(keyword) : null)
        .fetchCount();

    return new PageImpl<>(content, pageable, total);
  }

}
