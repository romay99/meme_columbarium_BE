package com.romay.meme.columbarium.member.repository;

import com.romay.meme.columbarium.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findMemberById(String id);

  boolean existsMemberById(String id);

  boolean existsMemberByNickname(String nickname);
}
