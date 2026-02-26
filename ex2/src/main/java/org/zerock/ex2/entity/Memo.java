package org.zerock.ex2.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity //엔티티 클래스
@Table(name = "tbl_memo") //테이블명
@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Memo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mno;

    @Column(length = 200, nullable = false)
    private String memoText;
}
